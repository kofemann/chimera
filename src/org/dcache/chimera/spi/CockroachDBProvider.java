package org.dcache.chimera.spi;

import org.dcache.chimera.CockroachDBFsSqlDriver;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.dcache.chimera.ChimeraFsException;
import org.dcache.chimera.FsSqlDriver;

import static org.dcache.chimera.util.SqlHelper.tryToClose;

/**
 * Provider for CockroachDB.
 * See: https://www.cockroachlabs.com
 */
public class CockroachDBProvider implements DBDriverProvider {

    @Override
    public boolean isSupportDB(DataSource dataSource) throws SQLException {
        Connection dbConnection = null;
        try {
            dbConnection = dataSource.getConnection();
            String databaseProductName = dbConnection.getMetaData().getDatabaseProductName();

            if (databaseProductName.equalsIgnoreCase("PostgreSQL")) {
                /*
                 * CockroachDB presents itself as PostgreSQL with special schema.
                 */
                try (ResultSet rs = dbConnection.getMetaData().getSchemas()) {
                    while (rs.next()) {
                        String schema = rs.getString("TABLE_SCHEM");
                        if (schema.equalsIgnoreCase("crdb_internal")) {
                            return true;
                        }
                    }
                }
            }
        } finally {
            tryToClose(dbConnection);
        }
        return false;
    }

    @Override
    public FsSqlDriver getDriver(DataSource dataSource) throws SQLException, ChimeraFsException {
        return new CockroachDBFsSqlDriver(dataSource);
    }

}
