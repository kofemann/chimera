package org.dcache.chimera;

import org.junit.Test;
import static org.junit.Assert.*;

public class PnfsBackwardCompatibilityTest extends ChimeraTestCaseHelper {

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
