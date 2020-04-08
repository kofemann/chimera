package org.dcache.acl.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * This object consists of a enumeration (implemented as bit mask) of possible
 * access permissions.
 *
 * @author David Melkumyan, DESY Zeuthen
 */
public enum AccessMask {
    /**
     * Permission to read the data of the file.
     */
    READ_DATA(0x00000001, 'r'), // 0000 0000 0000 0001

    /**
     * Permission to list the contents of a directory.
     */
    LIST_DIRECTORY(0x00000001, 'l'), // 0000 0000 0000 0001

    /**
     * Permission to modify a file's data anywhere in the file's offset range.
     */
    WRITE_DATA(0x00000002, 'w'), // 0000 0000 0000 0010

    /**
     * Permission to add a new file in a directory.
     */
    ADD_FILE(0x00000002, 'f'), // 0000 0000 0000 0010

    /**
     * The ability to modify a file's data, but only starting at EOF.
     */
    APPEND_DATA(0x00000004, 'a'), // 0000 0000 0000 0100

    /**
     * Permission to create a sub directory in a directory.
     */
    ADD_SUBDIRECTORY(0x00000004, 's'), // 0000 0000 0000 0100

    /**
     * Permission to read the named attributes of a file or to lookup the named
     * attributes directory.
     */
    READ_NAMED_ATTRS(0x00000008, 'n'), // 0000 0000 0000 1000

    /**
     * Permission to write the named attributes of a file or to create a named
     * attribute directory.
     */
    WRITE_NAMED_ATTRS(0x00000010, 'N'), // 0000 0000 0001 0000

    /**
     * Permission to execute a file or traverse/search a directory.
     */
    EXECUTE(0x00000020, 'x'), // 0000 0000 0010 0000

    /**
     * Permission to delete a file or directory within a directory.
     */
    DELETE_CHILD(0x00000040, 'D'), // 0000 0000 0100 0000

    /**
     * The ability to read basic attributes (non-ACLs) of a file.
     */
    READ_ATTRIBUTES(0x00000080, 't'), // 0000 0000 1000 0000

    /**
     * Permission to change the times associated with a file or directory to an
     * arbitrary value.
     */
    WRITE_ATTRIBUTES(0x00000100, 'T'), // 0000 0001 0000 0000

    /**
     * Permission to delete the file or directory.
     */
    DELETE(0x00010000, 'd'), // 0001 0000 0000 0000 0000

    /**
     * Permission to read the ACL.
     */
    READ_ACL(0x00020000, 'c'), // 0010 0000 0000 0000 0000

    /**
     * Permission to write the ACL and mode attributes.
     */
    WRITE_ACL(0x00040000, 'C'), // 0100 0000 0000 0000 0000

    /**
     * Permission to write the owner and owner_group attributes.
     */
    WRITE_OWNER(0x00080000, 'o'), // 1000 0000 0000 0000 0000

    /**
     * Permission to write the owner and owner_group attributes.
     */
    SYNCHRONIZE(0x00100000, 'y'); // 0001 0000 0000 0000 0000 0000

    // ALL ALLOWED: 11111111111111111111 = FFFFF = 1048575

    private final static Map<Character, AccessMask> _values = new HashMap<>();
    static {
        for(AccessMask mask: AccessMask.values()) {
            _values.put(mask.getLabel(), mask);
        }
    }

    private final int _value;
    private final char _label;

    AccessMask(int value, char abbreviation) {
        _value = value;
        _label = abbreviation;
    }

    public int getValue() {
        return _value;
    }

    public char getLabel() {
        return _label;
    }

    public boolean matches(int accessMask) {
        return (_value & accessMask) == _value;
    }

    /**
     * @param accessMask
     *            ACE access bit mask
     * @return Return string representation of access bit mask
     */
    public static String asString(int accessMask) throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();
        for (AccessMask accessMsk : AccessMask.values())
            if ( accessMsk.matches(accessMask) )
                sb.append(accessMsk.getLabel());

        return sb.toString();
    }

}
