-- $Id$
-- some procedures to push some work to SQL server

CREATE OR REPLACE FUNCTION "public"."inode2path" (varchar) RETURNS varchar AS $$
DECLARE
     inode VARCHAR := $1;
     ipath varchar := '';
     ichain  RECORD;
BEGIN

    LOOP
        SELECT INTO ichain * FROM t_dirs WHERE ipnfsid=inode AND iname != '.' AND iname != '..';
        IF FOUND  AND ichain.iparent != inode
        THEN
            ipath :=   '/' || ichain.iname ||  ipath;
            inode := ichain.iparent;
        ELSE
            EXIT;
        END IF;

        END LOOP;

     RETURN ipath;
END;
$$
LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION path2inode(root varchar, path varchar) RETURNS varchar AS $$
DECLARE
    id varchar := root;
    elements varchar[] := string_to_array(path, '/');
    child varchar;
    itype integer;
    link varchar;
BEGIN
    FOR i IN 1..array_upper(elements,1) LOOP
        SELECT dir.ipnfsid, inode.itype INTO child, itype FROM t_dirs dir, t_inodes inode WHERE dir.ipnfsid = inode.ipnfsid AND dir.iparent=id AND dir.iname=elements[i];
        IF itype=40960 THEN
           SELECT ifiledata INTO link FROM t_inodes_data WHERE ipnfsid=child;
           IF link LIKE '/%' THEN
              child := path2inode('000000000000000000000000000000000000', 
                                   substring(link from 2));
           ELSE
              child := path2inode(id, link);
           END IF;
        END IF;
        IF child IS NULL THEN
           RETURN NULL;
        END IF;
        id := child;
    END LOOP;
    RETURN id;
END;
$$ LANGUAGE plpgsql; 

--
--  store location of deleted  inodes in trash table
--
-- stores a old values into the trash table except last access time,
-- which replaced with a time, when the trigger was running
--

CREATE OR REPLACE FUNCTION f_locationinfo2trash() RETURNS TRIGGER AS $t_inodes_trash$
BEGIN

    IF (TG_OP = 'DELETE') THEN

        INSERT INTO t_locationinfo_trash SELECT
            ipnfsid ,
            itype,
            ilocation ,
            ipriority,
            ictime ,
            iatime ,
            istate FROM t_locationinfo WHERE ipnfsid = OLD.ipnfsid;

    END IF;

    RETURN OLD;
END;

$t_inodes_trash$ LANGUAGE plpgsql;


--
-- trigger to store removed inodes
--

CREATE TRIGGER tgr_locationinfo_trash BEFORE DELETE ON t_inodes FOR EACH ROW EXECUTE PROCEDURE f_locationinfo2trash();




---
--- populate inhereted tags
---
CREATE OR REPLACE FUNCTION f_populate_tags() RETURNS TRIGGER AS $t_populate_tags$
BEGIN
	IF TG_OP = 'INSERT' AND NEW.iname = '..'
    THEN
	    INSERT INTO t_tags ( SELECT NEW.iparent, itagname, itagid, 0 from t_tags WHERE ipnfsid=NEW.ipnfsid );
    END IF;

	RETURN NEW;
END;

$t_populate_tags$ LANGUAGE plpgsql;
--
-- trigger to store removed inodes
--

CREATE TRIGGER tgr_populate_tags AFTER INSERT ON t_dirs FOR EACH ROW EXECUTE PROCEDURE f_populate_tags();
