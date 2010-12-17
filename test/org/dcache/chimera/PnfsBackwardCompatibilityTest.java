package org.dcache.chimera;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.dcache.chimera.util.SqlHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;




public class PnfsBackwardCompatibilityTest {


    private FileSystemProvider _fs;
    private FsInode _rootInode;
    private Connection _conn;

    @Before
    public void setUp() throws Exception {

        Class.forName("org.hsqldb.jdbcDriver");

        _conn = DriverManager.getConnection("jdbc:hsqldb:mem:chimeramem", "sa", "");
        _conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        File sqlFile = new File("sql/create-hsqldb.sql");
        StringBuilder sql = new StringBuilder();

        BufferedReader dataStr = new BufferedReader(new FileReader(sqlFile));
        String inLine = null;

        while ((inLine = dataStr.readLine()) != null) {
            sql.append(inLine);
        }

        String[] statements = sql.toString().split(";");
        for(String statement: statements) {
            Statement st = _conn.createStatement();
            st.executeUpdate(statement);
            SqlHelper.tryToClose(st);
        }

        _fs = ChimeraFsHelper.getFileSystemProvider("test-config.xml");
        _rootInode = _fs.path2inode("/");

    }

    @After
    public void tearDown() throws Exception {
        _conn.createStatement().execute("SHUTDOWN;");
        _conn.close();
    }


    @Test
    public void testGetCursor() throws Exception {

        FsInode inode = _fs.inodeOf(_rootInode, ".(get)(cursor)" );
        assertTrue(".(get)(cursor) have to always return existing file", inode.exists());
    }


    @Test(expected=org.dcache.chimera.FileNotFoundHimeraFsException.class)
    public void testGetAccessNotExist() throws Exception {

        FsInode inode = _rootInode.create("newFile", 0, 0, 0644);
        String accessFile = String.format(".(access)(%s)(1)", inode.toString());
        FsInode accessInode = _fs.inodeOf(_rootInode, accessFile);
        fail("non exising level not catched");
    }

    @Test
    public void testGetAccessLevelExist() throws Exception {

        byte[] data = "bla-bla-bla".getBytes();
        FsInode inode = _rootInode.create("newFile", 0, 0, 0644);
        String accessFile = String.format(".(access)(%s)(1)", inode.toString());
        FsInode level1 = new FsInode(_fs, inode.toString(), 1);
        level1.write(0, data, 0, data.length);
        FsInode accessInode = _fs.inodeOf(_rootInode, accessFile);
        assertTrue(".(access)(x)(n) on existing level-n have to return existing file", accessInode.exists());
    }

    @Test
    public void testCreateAccessFile() throws Exception {

        byte[] data = "bla-bla-bla".getBytes();
        FsInode inode = _rootInode.create("newFile", 0, 0, 0644);
        String accessFile = String.format(".(access)(%s)(1)", inode.toString());
        _rootInode.create(accessFile, 0, 0, 0644);


        FsInode level1 = new FsInode(_fs, inode.toString(), 1);
        assertTrue("create of .(access)(x)(n) did not create a corrsponding level", level1.exists());
    }

    @Test
    public void testCreateAccessFileLevel0() throws Exception {

        byte[] data = "bla-bla-bla".getBytes();
        FsInode inode = _rootInode.create("newFile", 0, 0, 0644);
        String accessFile = String.format(".(access)(%s)(0)", inode.toString());
        FsInode accessInode = _rootInode.create(accessFile, 0, 0, 0644);

        assertEquals("create of .(access)(x)(0) have to return original file", inode, accessInode);
    }

}
