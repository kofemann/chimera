package org.dcache.chimera;

import com.mchange.v2.c3p0.DataSources;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;

public class ChimeraFsHelper {

    private ChimeraFsHelper() {};

    public static FileSystemProvider getFileSystemProvider(String configFile)
            throws IOException, ClassNotFoundException, SQLException {

        XMLconfig config = new XMLconfig(new File(configFile));

        DbConnectionInfo connectionInfo = config.getDbInfo(0);
        Class.forName(connectionInfo.getDBdrv());

        DataSource dataSource = DataSources.unpooledDataSource(connectionInfo.getDBurl(),
                connectionInfo.getDBuser(), connectionInfo.getDBpass());

        return new JdbcFs(DataSources.pooledDataSource(dataSource), connectionInfo.getDBdialect());
    }
}
