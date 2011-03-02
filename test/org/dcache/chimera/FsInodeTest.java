package org.dcache.chimera;

import org.junit.Test;
import static org.junit.Assert.*;

public class FsInodeTest extends ChimeraTestCaseHelper {

    @Test
    public void testFsInodeEquals() {

        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode(_fs, id);
        FsInode inode2 = new FsInode(_fs, id);


        assertTrue("inodes not equal", inode1.equals(inode2));
        assertTrue("inodes not symetricaly equal", inode2.equals(inode1));
        assertTrue("equal, but hashcode not the same", inode1.hashCode() == inode2.hashCode());
    }

    @Test
    public void testFsInodeDifferById() {

        String id1 = InodeId.newID(0);
        String id2 = InodeId.newID(0);

        FsInode inode1 = new FsInode(_fs, id1);
        FsInode inode2 = new FsInode(_fs, id2);


        assertFalse("differ id but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }

    @Test
    public void testFsInodeDifferByType() {

        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode(_fs, id);
        FsInode inode2 = new FsInode(_fs, id, FsInodeType.ID);


        assertFalse("differ type but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }

    @Test
    public void testFsInodeDifferByType2() {

        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode(_fs, id);
        FsInode inode2 = new FsInode_ID(_fs, id);


        assertFalse("differ type but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }

    @Test
    public void testFsInodeDifferByTAG() {

        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode_TAG(_fs, id, "tag1");
        FsInode inode2 = new FsInode_TAG(_fs, id, "tag2");


        assertFalse("differ type but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }



    @Test
    public void testFsInodeDifferPSET() {

        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode_PSET(_fs, id, new String[] { "a" } );
        FsInode inode2 = new FsInode_PSET(_fs, id, new String[] { "b" });


        assertFalse("differ pset but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }

    @Test
    public void testFsInodeDifferPGET() {

        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode_PGET(_fs, id, new String[] { "a" } );
        FsInode inode2 = new FsInode_PGET(_fs, id, new String[] { "b" });


        assertFalse("differ pget but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }

    @Test
    public void testFsInodeDifferCONST() {


        FsInode inode1 = new FsInode_CONST(_fs, "const1");
        FsInode inode2 = new FsInode_CONST(_fs, "const2");


        assertFalse("differ consts but equal", inode1.equals(inode2));
        assertFalse("inodes not symetricaly unequal", inode2.equals(inode1));
    }

    @Test
    public void testFsInodeSameInodeId() {
        String id = InodeId.newID(0);

        FsInode inode1 = new FsInode(_fs, id);
        FsInode inode2 = new FsInode(_fs, id);
        assertEquals( "inode-IDs for two FsInodes with the same Chimera-IDs are the same",
                inode1.id(), inode2.id());
    }

    @Test
    public void testFsInodeDifferentInodeId() {
        String id1 = InodeId.newID(0);
        String id2 = InodeId.newID(0);

        FsInode inode1 = new FsInode(_fs, id1);
        FsInode inode2 = new FsInode(_fs, id2);
        assertTrue( "inode-IDs for two FsInodes with different Chimera-IDs are different",
                inode1.id() != inode2.id());
    }

    @Test
    public void testFsInodeDifferentInodeIdAckwardCase() {
        String id1 = "0000B52D9C939623408E8548969804EDEE06";
        String id2 = "0000F6B5818998044429A76EC091D3AC50C8";

        FsInode inode1 = new FsInode(_fs, id1);
        FsInode inode2 = new FsInode(_fs, id2);
        assertTrue( "inode-IDs for two previously problematic Chimera-IDs are different",
                inode1.id() != inode2.id());
    }

    @Test
    public void testPnfsIdInodeZeroCounter() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000000000000000000000000";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0, inode.id());
    }

    @Test
    public void testPnfsIdInodeZeroCounterLayer1() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000000000000000000000001"; // layer 1 for PNFS-ID 000000000000000000000000
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0, inode.id());
    }

    @Test
    public void testPnfsIdInodeLowestSignificantBitCounter() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000000000000000000000008";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 1, inode.id());
    }

    @Test
    public void testPnfsIdInodeHighestSignificantBitCounter() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000000000000000400000000";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0x80000000L, inode.id());
    }

    @Test
    public void testPnfsIdInodeHighSignificantBitsInCounter() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000000000000000480000000";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0x90000000L, inode.id());
    }

    @Test
    public void testPnfsIdInodeLargestCounter() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "0000000000000007FFFFFFF8";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0xFFFFFFFFL, inode.id());
    }

    @Test
    public void testPnfsIdInodeZeroCounterDatabase1() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000100000000000000000000";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0x80000000L, inode.id());
    }

    @Test
    public void testPnfsIdInodeLargestCounterDatabase1() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "0001000000000007FFFFFFF8";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0x7FFFFFFFL, inode.id());
    }

    @Test
    public void testPnfsIdInodeZeroCounterDatabase2() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000200000000000000000000";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0x40000000L, inode.id());
    }

    @Test
    public void testPnfsIdInodeZeroCounterDatabase13() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "000D00000000000000000000";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0xB0000000L, inode.id());
    }


    @Test
    public void testPnfsIdInodeLargestCounterAndLargestDatabase() {
        //             ddddeeeehhhhhhhhllllllll
        String id =   "FFFF000000000007FFFFFFF8";
        FsInode inode = new FsInode(_fs, id);
        assertEquals( "inode-IDs for PNFS-ID " + id, 0x0000FFFFL, inode.id());
    }
}
