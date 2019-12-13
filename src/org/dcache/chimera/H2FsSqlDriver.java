/*
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.dcache.chimera;

import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.EnumSet;

import org.dcache.acl.enums.AceFlags;
import org.dcache.acl.enums.RsType;

/**
 * H2 database specific dialect.
 */
public class H2FsSqlDriver extends FsSqlDriver {

    public H2FsSqlDriver(DataSource dataSource) throws ChimeraFsException
    {
        super(dataSource);
    }


    /**
     * copy all directory tags from origin directory to destination. New copy marked as inherited.
     *
     * @param orign
     * @param destination
     */
    @Override
    void copyTags(FsInode orign, FsInode destination) {
        int n = _jdbc.update("INSERT INTO t_tags (inumber,itagid,isorign,itagname) (SELECT " + destination.ino() + ",itagid,0,itagname from t_tags WHERE inumber=?)",
                     orign.ino());
        if (n > 0) {
            // if tags was copied, then bump the reference counts.
            _jdbc.update("UPDATE t_tags_inodes SET inlink = inlink + 1 WHERE itagid IN (SELECT itagid from t_tags where inumber=?)", destination.ino());
        }
    }

    @Override
    void copyAcl(FsInode source, FsInode inode, RsType type, EnumSet<AceFlags> mask, EnumSet<AceFlags> flags) {
        int msk = mask.stream().mapToInt(AceFlags::getValue).reduce(0, (a, b) -> a | b);
        int flgs = flags.stream().mapToInt(AceFlags::getValue).reduce(0, (a, b) -> a | b);
        _jdbc.update("INSERT INTO t_acl (inumber,rs_type,type,flags,access_msk,who,who_id,ace_order) " +
                     "SELECT ?, ?, type, BITXOR(BITOR(flags, ?), ?), access_msk, who, who_id, ace_order " +
                     "FROM t_acl WHERE inumber = ? AND BITAND(flags, ?) > 0",
                     ps -> {
                         ps.setLong(1, inode.ino());
                         ps.setInt(2, type.getValue());
                         ps.setInt(3, msk);
                         ps.setInt(4, msk);
                         ps.setLong(5, source.ino());
                         ps.setInt(6, flgs);
                     });
    }

    @Override
    public boolean isForeignKeyError(SQLException e) {
        return "23506".endsWith(e.getSQLState());
    }
}
