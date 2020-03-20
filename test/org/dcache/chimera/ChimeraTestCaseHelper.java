package org.dcache.chimera;

import com.hazelcast.core.Hazelcast;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileReader;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Connection;
import java.util.Properties;

public abstract class ChimeraTestCaseHelper {

    protected FileSystemProvider _fs;
    protected FsInode _rootInode;
    protected HikariDataSource _dataSource;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("hazelcast.logging.type","slf4j");
    }

    @Before
    public void setUp() throws Exception {

        Properties dbProperties = new Properties();
        dbProperties.load(new FileReader("chimera-test.properties"));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbProperties.getProperty("chimera.db.url"));
        config.setUsername(dbProperties.getProperty("chimera.db.user"));
        config.setPassword(dbProperties.getProperty("chimera.db.password"));
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(0);

        _dataSource = new HikariDataSource(config);

        try (Connection conn = _dataSource.getConnection()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("org/dcache/chimera/changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);

            liquibase.update("");
        }

        PlatformTransactionManager txManager =  new DataSourceTransactionManager(_dataSource);
        _fs = new JdbcFs(_dataSource, txManager,  Hazelcast.newHazelcastInstance());
        _rootInode = _fs.path2inode("/");
    }

    @After
    public void tearDown() throws Exception {
        Connection conn = _dataSource.getConnection();
        conn.createStatement().execute("SHUTDOWN;");
        _dataSource.close();
        _fs.close();
        Hazelcast.shutdownAll();
    }

}
