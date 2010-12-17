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
package org.dcache.chimera.examples.touch;

import com.mchange.v2.c3p0.DataSources;

import java.io.File;

import javax.sql.DataSource;

import org.dcache.chimera.DbConnectionInfo;
import org.dcache.chimera.JdbcFs;
import org.dcache.chimera.FsInode;
import org.dcache.chimera.XMLconfig;
import org.dcache.chimera.FileNotFoundHimeraFsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Touch {

    private static final Logger _log = LoggerFactory.getLogger(Touch.class);

    public static void main(String args[]) {

        if (args.length != 3) {
            _log.error("Usage: Touch <config> <root> <n>");
            System.exit(1);
        }


        String rootPath = args[1];
        int loopCount = Integer.parseInt(args[2]);
        try {

            XMLconfig config = new XMLconfig(new File(args[0]));
            DbConnectionInfo connectionInfo = config.getDbInfo(0);
            Class.forName(connectionInfo.getDBdrv());

            DataSource dataSource = DataSources.unpooledDataSource(connectionInfo.getDBurl(), connectionInfo.getDBuser(), connectionInfo.getDBpass());

            JdbcFs fs = new JdbcFs(DataSources.pooledDataSource(dataSource), connectionInfo.getDBdialect());

            FsInode rootInode = fs.path2inode(rootPath);
            if (!rootInode.isDirectory()) {
                _log.error(rootPath + " is not a directory");
                System.exit(1);
            }

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < loopCount; i++) {
                // do the same as NFS does: lookup, create, settime

                FsInode newInode = null;
                String name = Integer.toString(i);
                long transactionStartTime = System.currentTimeMillis();
                try {
                    newInode = rootInode.inodeOf(name);
                } catch (FileNotFoundHimeraFsException fnf) {
                    newInode = rootInode.create(name, 0, 0, 644);
                }

                newInode.setMTime(System.currentTimeMillis());
                newInode.stat();

                if (_log.isDebugEnabled()) {
                    _log.debug(i + " " + (System.currentTimeMillis() - transactionStartTime));
                }
            }

            long delta = (System.currentTimeMillis() - startTime) / 1000;
            _log.info(loopCount + " processed in " + delta + ". speed: " + (double) loopCount / (double) delta + " fps");

        } catch (Exception e) {
            _log.error("Touch ", e);
        }

    }
}
