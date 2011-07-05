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
package org.dcache.chimera.posix;

/**
 * Access mask field as defined in RFC35661.
 */
public enum AceAccessMask {

    /**
     * Permission to read the data of the file.
     */
    READ_DATA,

    /**
     * Permission to list the entries of a directory.
     */
    LIST_DIRECTORY,

    /**
     * Permission to modify the file's data.
     */
    WRITE_DATA,

    /**
     * Permission to add a new file to a directory.
     */
    ADD_FILE,

    /**
     * Permission to append data to a file.
     */
    APPEND_DATA,

    /**
     * Permission to create a subdirectory in a directory.
     */
    ADD_SUBDIRECTORY,

    /**
     * Permission to read the named attributes of a file.
     */
    READ_NAMED_ATTRS,

    /**
     * Permission to write the named attributes of a file.
     */
    WRITE_NAMED_ATTRS,

    /**
     * Permission to execute a file.
     */
    EXECUTE,

    /**
     * Permission to delete a file or directory within a directory.
     */
    DELETE_CHILD,

    /**
     * The ability to read (non-acl) file attributes.
     */
    READ_ATTRIBUTES,

    /**
     * The ability to write (non-acl) file attributes.
     */
    WRITE_ATTRIBUTES,

    /**
     * Permission to modify the durations of event and non-event-based
     * retention.
     */
    WRITE_RETENTION,

    /**
     * Permission to modify the administration retention holds.
     */
    WRITE_RETENTION_HOLD,

    /**
     * Permission to delete the file.
     */
    DELETE,

    /**
     * Permission to read the ACL attribute.
     */
    READ_ACL,

    /**
     * Permission to write the ACL attribute.
     */
    WRITE_ACL,

    /**
     * Permission to change the owner.
     */
    WRITE_OWNER,

    /**
     * Permission to access file locally at the server with synchronous reads
     * and writes.
     */
    SYNCHRONIZE,
}