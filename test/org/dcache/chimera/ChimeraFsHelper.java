package org.dcache.chimera;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.SQLException;

public class ChimeraFsHelper {

    private ChimeraFsHelper() {};

    public static HikariDataSource getDataSource(String url, String drv, String user, String pass)
            throws IOException, ClassNotFoundException, SQLException {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setDriverClassName(drv);
        config.setMaximumPoolSize(3);

        return new HikariDataSource(config);
    }
}
