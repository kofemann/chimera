--
-- find all directories where nlink count do not match to the actual count
--
SELECT t_inodes.ipnfsid, t_inodes.inlink  FROM t_inodes WHERE inlink != (
    SELECT COUNT(*) FROM t_dirs  WHERE t_inodes.ipnfsid = t_dirs.iparent
    ) AND itype = 16384;

--
-- and fix it
--
UPDATE t_inodes SET inlink = (
    SELECT COUNT(*) FROM t_dirs  WHERE t_inodes.ipnfsid = t_dirs.iparent
    ) WHERE itype = 16384;



--
-- find all files where nlink count do not match to the actual count
--
SELECT t_inodes.ipnfsid, t_inodes.inlink  FROM t_inodes WHERE inlink != (
    SELECT COUNT(*) FROM t_dirs  WHERE t_inodes.ipnfsid = t_dirs.ipnfsid
    ) AND itype = 32768;

--
-- and fix it
--
UPDATE t_inodes SET inlink = (
    SELECT COUNT(*) FROM t_dirs  WHERE t_inodes.ipnfsid = t_dirs.ipnfsid
    ) WHERE itype = 32768;


--
-- find lost inodes: no records in dirs structure
--
SELECT t_inodes.ipnfsid FROM t_inodes WHERE (
    SELECT COUNT(*) FROM t_dirs  WHERE t_inodes.ipnfsid = t_dirs.ipnfsid
    ) = 0;

--
-- create lost+found directory
--
BEGIN;
    INSERT INTO t_inodes VALUES ('000000000000000000000000000000000017',    16384, 493, 2,  0,  0,  512, 1, NOW(), NOW(), NOW() );
    INSERT INTO t_dirs VALUES ('000000000000000000000000000000000000',  'lost+found','000000000000000000000000000000000017');
    INSERT INTO t_dirs VALUES ('000000000000000000000000000000000017',  '..','000000000000000000000000000000000000');
    INSERT INTO t_dirs VALUES ('000000000000000000000000000000000017',  '.','000000000000000000000000000000000017');
    UPDATE t_inodes SET inlink = inlink +1 WHERE ipnfsid='000000000000000000000000000000000000';
COMMIT;


--
-- populate lost+found
--
BEGIN;
    INSERT INTO t_dirs SELECT '000000000000000000000000000000000017', t_inodes.ipnfsid, t_inodes.ipnfsid
        FROM t_inodes where (select count(*) FROM t_dirs  WHERE t_inodes.ipnfsid = t_dirs.ipnfsid) = 0;
    UPDATE t_inodes SET inlink = ( SELECT COUNT(*) FROM t_dirs WHERE iparent = '000000000000000000000000000000000017' ) WHERE ipnfsid='000000000000000000000000000000000017';
COMMIT;
