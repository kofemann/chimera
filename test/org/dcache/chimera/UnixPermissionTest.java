package org.dcache.chimera;

import org.junit.Test;
import static org.junit.Assert.*;

public class UnixPermissionTest {

    @Test
    public void testSymLink() {

        UnixPermission p = new UnixPermission(UnixPermission.S_IFLNK | 00777 );

        assertTrue("SymLink not detected", p.isSymLink());
        assertFalse("SymLink detected as CHAR device", p.isCharDev());
        assertFalse("SymLink detected as BLOCK device", p.isBlockDev());
        assertFalse("SymLink detected as DIR device", p.isDir());
        assertFalse("SymLink detected as REG device", p.isReg());
    }


    @Test
    public void testDir() {

        UnixPermission p = new UnixPermission(UnixPermission.S_IFDIR | 00777);

        assertTrue("Dir not detected", p.isDir());
        assertFalse("Dir detected as CHAR device", p.isCharDev());
        assertFalse("Dir detected as BLOCK device", p.isBlockDev());
        assertFalse("Dir detected as DIR device", p.isSymLink());
        assertFalse("Dir detected as REG device", p.isReg());
    }

}
