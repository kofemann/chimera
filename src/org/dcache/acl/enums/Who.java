package org.dcache.acl.enums;

/**
 * The enumeration Who allows to identify different kind of subjects.
 *
 * @author David Melkumyan, DESY Zeuthen
 */
public enum Who {

    /**
     * The user identified by the virtual user ID.
     */
    USER(0x00000000, "USER"),

    /**
     * The group identified by the virtual group ID.
     */
    GROUP(0x00000001, "GROUP"),

    /**
     * The owner of resource.
     */
    OWNER(0x00000002, "OWNER@"),

    /**
     * The group associated with the owner of resource.
     */
    OWNER_GROUP(0x00000003, "GROUP@"),

    /**
     * The world, including the owner and owning group.
     */
    EVERYONE(0x00000004, "EVERYONE@"),

    /**
     * Accessed without any authentication.
     */
    ANONYMOUS(0x00000005, "ANONYMOUS@"),

    /**
     * Any authenticated user (opposite of ANONYMOUS).
     */
    AUTHENTICATED(0x00000006, "AUTHENTICATED@");

    private final int _value;

    private final String _label;

    private Who(int value, String abbreviation) {
        _value = value;
        _label = abbreviation;
    }

    public int getValue() {
        return _value;
    }

    public static Who valueOf(int value) throws IllegalArgumentException {
        for (Who who : Who.values())
            if ( who._value == value )
                return who;

        throw new IllegalArgumentException("Illegal argument (value of who): " + value);
    }

}
