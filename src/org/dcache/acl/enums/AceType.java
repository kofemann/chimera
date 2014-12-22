package org.dcache.acl.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The AceType is an enumeration (to be implemented as a bit mask).
 *
 * @author David Melkumyan, DESY Zeuthen
 */
public enum AceType {
    /**
     * Explicitly grants the access
     */
    ACCESS_ALLOWED_ACE_TYPE(0x00000000, "A"),

    /**
     * Explicitly denies the access
     */
    ACCESS_DENIED_ACE_TYPE(0x00000001, "D"),

    /**
     * Log any access attempt to a file or directory that uses any of the
     * access methods specified in mask.
     */
    ACCESS_AUDIT_ACE_TYPE(0x00000002, "U"),

    /**
     * Generate an alarm when any access attempt to a file or directory that uses any of the
     * access methods specified in mask.
     */
    ACCESS_ALARM_ACE_TYPE(0x00000002, "L");

    private final int _value;

    private final String _abbreviation;

    private static final Map<String, AceType> _valuesByAbb = new HashMap<>();
    private static final Map<Integer, AceType> _valuesByValue = new HashMap<>();
    static {
        for(AceType aceType: AceType.values()){
            _valuesByAbb.put(aceType.getAbbreviation(), aceType);
            _valuesByValue.put(aceType.getValue(), aceType);
        }
    }

    private AceType(int value, String abbreviation) {
        _value = value;
        _abbreviation = abbreviation;
    }

    public int getValue() {
        return _value;
    }

    public String getAbbreviation() {
        return _abbreviation;
    }

    @Override
    public String toString() {
        return String.valueOf(_abbreviation);
    }

    public static AceType fromAbbreviation(String abbreviation) throws IllegalArgumentException {
        AceType aceType = _valuesByAbb.get(abbreviation);
        if(aceType == null)
            throw new IllegalArgumentException("Invalid ACE type abbreviation: " + abbreviation);

        return aceType;
    }

    public static AceType valueOf(int value) throws IllegalArgumentException {
        AceType aceType = _valuesByValue.get(value);
        if(aceType == null)
            throw new IllegalArgumentException("Invalid ACE type value: " + Integer.toHexString(value));

        return aceType;
    }
}
