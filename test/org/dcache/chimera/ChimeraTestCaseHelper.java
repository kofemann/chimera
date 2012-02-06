package org.dcache.chimera;

import java.sql.Connection;
import java.io.FileReader;
import java.sql.DriverManager;
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
    private Connection _conn;

    @Before
    public void setUp() throws Exception {

        Properties dbProperties = new Properties();
        dbProperties.load(new FileReader("chimera-test.properties"));

        Class.forName(dbProperties.getProperty("chimera.db.driver"));
        _conn = DriverManager.getConnection(dbProperties.getProperty("chimera.db.url"),
                dbProperties.getProperty("chimera.db.user"), dbProperties.getProperty("chimera.db.password"));

        _conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(_conn));
        Liquibase liquibase = new Liquibase("org/dcache/chimera/changelog/changelog-master.xml",
                new ClassLoaderResourceAccessor(), database);
        // Uncomment the following line when testing with mysql database
      /*
         * Liquibase liquibase = new Liquibase(changeLogFile, new
         * ClassLoaderResourceAccessor(), new JdbcConnection(conn));
         */

        liquibase.update("");
        _fs = ChimeraFsHelper.getFileSystemProvider(
                dbProperties.getProperty("chimera.db.url"),
                dbProperties.getProperty("chimera.db.driver"),
                dbProperties.getProperty("chimera.db.user"),
                dbProperties.getProperty("chimera.db.password"),
                dbProperties.getProperty("chimera.db.dialect") );
        _rootInode = _fs.path2inode("/");
    }

    @After
    public void tearDown() throws Exception {
        _conn.createStatement().execute("SHUTDOWN;");
        _conn.close();
    }
}
