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
package org.dcache.chimera.examples.cli;

import com.mchange.v2.c3p0.DataSources;

import java.io.File;

import javax.sql.DataSource;

import org.dcache.chimera.DbConnectionInfo;
import org.dcache.chimera.FileSystemProvider;
import org.dcache.chimera.FsInode;
import org.dcache.chimera.JdbcFs;
import org.dcache.chimera.XMLconfig;

public class Lstag {


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage :" + Lstag.class.getName() + " <chimera.config> <chimera path>");
            System.exit(4);
        }

        XMLconfig config = new XMLconfig(new File("config.xml"));

        DbConnectionInfo connectionInfo = config.getDbInfo(0);
        Class.forName(connectionInfo.getDBdrv());

        DataSource dataSource = DataSources.unpooledDataSource(connectionInfo.getDBurl(), connectionInfo.getDBuser(), connectionInfo.getDBpass());

        FileSystemProvider fs = new JdbcFs(DataSources.pooledDataSource(dataSource), connectionInfo.getDBdialect());

        FsInode inode = fs.path2inode(args[1]);

        String[] tags = fs.tags(inode);

        System.out.println("Total: " + tags.length);
        for (String tag : tags) {
            System.out.println(tag);
        }


    }

}
