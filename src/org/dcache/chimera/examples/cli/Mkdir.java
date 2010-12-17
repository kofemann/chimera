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
import org.dcache.chimera.JdbcFs;
import org.dcache.chimera.XMLconfig;

public class Mkdir {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String path = "";
        boolean createParents = false;

        if (args.length == 2) {
            path = args[1];
            createParents = false;
        } else if ((args.length == 3) && (args[1].equals("-p"))) {
            path = args[2];
            createParents = true;
        } else {
            System.err.println("Usage :" + Mkdir.class.getName() + " <chimera.config> [-p] <chimera path>");
            System.exit(4);
        }

        XMLconfig config = new XMLconfig(new File("config.xml"));

        DbConnectionInfo connectionInfo = config.getDbInfo(0);
        Class.forName(connectionInfo.getDBdrv());

        DataSource dataSource = DataSources.unpooledDataSource(connectionInfo.getDBurl(), connectionInfo.getDBuser(), connectionInfo.getDBpass());

        FileSystemProvider fs = new JdbcFs(DataSources.pooledDataSource(dataSource), connectionInfo.getDBdialect());

        if (createParents) {
            fs.mkdirs(path);
        } else {
            fs.mkdir(path);
        }

    }

}
