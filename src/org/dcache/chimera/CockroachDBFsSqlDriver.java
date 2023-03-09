package org.dcache.chimera;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

/**
 *
 * @author tigran
 */
public class CockroachDBFsSqlDriver extends FsSqlDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CockroachDBFsSqlDriver.class);

    public CockroachDBFsSqlDriver(DataSource dataSource) throws ChimeraFsException {
        super(dataSource);
        LOGGER.info("Running CockroachDB specific Driver");
    }


    @Override
    void createEntryInParent(FsInode parent, String name, FsInode inode) {
        int n = _jdbc.update("INSERT INTO t_dirs (iparent, iname, ichild) VALUES(?,?,?) ON CONFLICT (iparent,iname) DO NOTHING",
                ps -> {
                    ps.setLong(1, parent.ino());
                    ps.setString(2, name);
                    ps.setLong(3, inode.ino());
                });
        if (n == 0) {
            /*
             * no updates as such entry already exists.
             * To be compatible with others, throw corresponding
             * DataAccessException.
             */
            throw new DuplicateKeyException("Entry already exists");
        }
    }

    @Override
    void copyTags(FsInode orign, FsInode destination) {
        // NOP for now
    }

    @Override
    void removeInodeIfUnlinked(FsInode inode)
    {
        _jdbc.update("DELETE FROM t_inodes WHERE inumber=?", inode.ino());
    }

    void removeTag(FsInode dir, String tag) {
    }

    void removeTag(FsInode dir) {
    }
}
