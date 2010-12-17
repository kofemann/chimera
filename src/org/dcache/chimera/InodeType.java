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

/**
 * Supported Inode types
 */
public enum InodeType {

    REG(UnixPermission.S_IFREG),
    DIR(UnixPermission.S_IFDIR),
    FIFO(UnixPermission.S_IFIFO),
    SLINK(UnixPermission.S_IFLNK),
    SOCK(UnixPermission.S_IFSOCK),
    BLOCK(UnixPermission.S_IFBLK),
    CHAR(UnixPermission.S_IFCHR);

    private final int _type;

    private InodeType(int type) {
        _type = type;
    }

    public int intValue() {
        return _type;
    }
}
