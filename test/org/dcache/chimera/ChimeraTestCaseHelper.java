package org.dcache.chimera;

import com.zaxxer.hikari.HikariDataSource;
import java.io.FileReader;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Connection;
import java.util.Properties;

public abstract class ChimeraTestCaseHelper {

    protected FileSystemProvider _fs;
    protected FsInode _rootInode;
    private HikariDataSource _dataSource;

    @Before
    public void setUp() throws Exception {

        Properties dbProperties = new Properties();
        dbProperties.load(new FileReader("chimera-test.properties"));

        _dataSource = FsFactory.getDataSource(
                dbProperties.getProperty("chimera.db.url"),
                dbProperties.getProperty("chimera.db.user"),
                dbProperties.getProperty("chimera.db.password"));

        try (Connection conn = _dataSource.getConnection()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("org/dcache/chimera/changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);

            liquibase.update("");
        }

        PlatformTransactionManager txManager =  new DataSourceTransactionManager(_dataSource);
        _fs = new JdbcFs(_dataSource, txManager, dbProperties.getProperty("chimera.db.dialect"));
        _rootInode = _fs.path2inode("/");
    }

    @After
    public void tearDown() throws Exception {
        Connection conn = _dataSource.getConnection();
        conn.createStatement().execute("SHUTDOWN;");
        _dataSource.close();
    }

}
