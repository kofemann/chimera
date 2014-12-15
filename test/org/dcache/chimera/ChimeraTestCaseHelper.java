package org.dcache.chimera;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.io.FileReader;
import java.util.Properties;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.After;
import org.junit.Before;

public abstract class ChimeraTestCaseHelper {

    protected FileSystemProvider _fs;
    protected FsInode _rootInode;
    private HikariDataSource _dataSource;

    @Before
    public void setUp() throws Exception {

        Properties dbProperties = new Properties();
        dbProperties.load(new FileReader("chimera-test.properties"));

        _dataSource = ChimeraFsHelper.getDataSource(
                dbProperties.getProperty("chimera.db.url"),
                dbProperties.getProperty("chimera.db.driver"),
                dbProperties.getProperty("chimera.db.user"),
                dbProperties.getProperty("chimera.db.password"));

        try (Connection conn = _dataSource.getConnection()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("org/dcache/chimera/changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        }

        _fs = new JdbcFs(_dataSource, dbProperties.getProperty("chimera.db.dialect"));
        _rootInode = _fs.path2inode("/");
    }

    @After
    public void tearDown() throws Exception {
        Connection conn = _dataSource.getConnection();
        conn.createStatement().execute("SHUTDOWN;");
        _dataSource.shutdown();
    }
}
