/*
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.dcache.chimera;

/*
 * @Immutable
 */
public class UnixPermission {

    private final int _mode;

    static public final int S_IRUSR = 00400; // owner has read permission
    static public final int S_IWUSR = 00200; // owner has write permission
    static public final int S_IXUSR = 00100; // owner has execute permission

    static public final int S_IRGRP = 00040; // group has read permission
    static public final int S_IWGRP = 00020; // group has write permission
    static public final int S_IXGRP = 00010; // group has execute permission

    static public final int S_IROTH = 00004; // others have read permission
    static public final int S_IWOTH = 00002; // others have write permission
    static public final int S_IXOTH = 00001; // others have execute
    // permission

    /**
     * permission mask
     */
    static public final int S_PERMS = 00777; // permission mask

    static public final int s_ISTICKY = 01000; // sticky bit

    /**
     * Unix domain socket
     */
    static public final int S_IFSOCK = 0140000;

    /**
     * Symbolic link
     */
    static public final int S_IFLNK = 0120000;

    /**
     * Regular file
     */
    static public final int S_IFREG = 0100000;

    /**
     * BLock device
     */
    static public final int S_IFBLK = 0060000;

    /**
     * Directory
     */
    static public final int S_IFDIR = 0040000;

    /**
     * Character device
     */
    static public final int S_IFCHR = 0020000;

    /**
     * Named pipe
     */
    static public final int S_IFIFO = 0010000;

    /**
     * file type mask
     */
    static public final int S_TYPE = 0770000; // type mask
    static public final int F_TYPE = 0170000;

    public UnixPermission(int mode) {
        _mode = mode;
    }

    @Override
    public String toString() {
        char[] modeString = new char[]{'-', '-', '-', '-', '-', '-', '-', '-', '-', '-',};

        switch (getType(_mode)) {
            case S_IFLNK:
                modeString[0] = 'l';
                break;
            case S_IFREG:
                /*
                 * this is default any way
                 * modeString[0] = '-';
                 */
                break;
            case S_IFBLK:
                modeString[0] = 'b';
                break;
            case S_IFDIR:
                modeString[0] = 'd';
                break;
            case S_IFCHR:
                modeString[0] = 'c';
                break;
            case S_IFIFO:
                modeString[0] = 'F';
                break;
            case S_IFSOCK:
                modeString[0] = 's';
                break;
            default:
                modeString[0] = '?';
        }

        // OWNER
        if ((_mode & S_IRUSR) == S_IRUSR) {
            modeString[1] = 'r';
        }
        if ((_mode & S_IWUSR) == S_IWUSR) {
            modeString[2] = 'w';
        }
        if ((_mode & S_IXUSR) == S_IXUSR) {
            modeString[3] = 'x';
        }

        // GROUP
        if ((_mode & S_IRGRP) == S_IRGRP) {
            modeString[4] = 'r';
        }
        if ((_mode & S_IWGRP) == S_IWGRP) {
            modeString[5] = 'w';
        }
        if ((_mode & S_IXGRP) == S_IXGRP) {
            modeString[6] = 'x';
        }

        // OTHERS
        if ((_mode & S_IROTH) == S_IROTH) {
            modeString[7] = 'r';
        }
        if ((_mode & S_IWOTH) == S_IWOTH) {
            modeString[8] = 'w';
        }
        if ((_mode & S_IXOTH) == S_IXOTH) {
            modeString[9] = 'x';
        }

        return String.valueOf(modeString);
    }

    public boolean isSymLink() {
        return getType(_mode) == S_IFLNK;
    }

    public boolean isCharDev() {
        return getType(_mode) == S_IFCHR;
    }

    public boolean isBlockDev() {
        return getType(_mode) == S_IFBLK;
    }

    public boolean isDir() {
        return getType(_mode) == S_IFDIR;
    }

    public boolean isReg() {
        return getType(_mode) == S_IFREG;
    }

    public static int getType(int mode) {
        return mode & F_TYPE;
    }
}
