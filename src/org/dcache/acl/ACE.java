package org.dcache.acl;

import java.io.Serializable;
import org.dcache.acl.enums.AccessMask;
import org.dcache.acl.enums.AceType;
import org.dcache.acl.enums.Who;

/**
 * An access control list (ACL) is an array of access control entries (ACE).
 *
 * @author David Melkumyan, DESY Zeuthen
 *
 */
public class ACE implements Serializable {

    static final long serialVersionUID = -7088617639500399472L;
    public static final String DEFAULT_ADDRESS_MSK = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    /**
     * Type of ACE (ALLOW / DENY)
     */
    private final AceType _type;
    /**
     * The ACE flags (combination of values from AceFlags enumeration)
     */
    private final int _flags;
    /**
     * The access mask (combination of values from AccessMask enumeration)
     */
    private final int _accessMsk;
    /**
     * The subject (combination of values from Who enumeration)
     */
    private final Who _who;
    /**
     * Virtual user or group ID (equals to -1 if who is special subject)
     */
    private final int _whoID;
    /**
     * The request origin address mask
     */
    private final String _addressMsk;

    /**
     * @param type
     *            Type of ACE (ALLOW / DENY)
     * @param flags
     *            ACE flags
     * @param accessMsk
     *            Access mask
     * @param who
     *            Subject
     * @param whoID
     *            Virtual user or group ID
     * @param addressMsk
     *            Request origin address mask
     * @param order
     *            Defines position of ACE within ACL
     */
    public ACE(AceType type, int flags, int accessMsk, Who who, int whoID, String addressMsk) {
        _type = type;
        _flags = flags;
        _accessMsk = accessMsk;
        _who = who;
        _whoID = whoID;
        _addressMsk = addressMsk;
    }

    public int getAccessMsk() {
        return _accessMsk;
    }

    public String getAddressMsk() {
        return _addressMsk;
    }

    public boolean isDefaultAddressMsk(String addressMsk) {
        return DEFAULT_ADDRESS_MSK.equalsIgnoreCase(addressMsk);
    }

    public boolean isDefaultAddressMsk() {
        return isDefaultAddressMsk(_addressMsk);
    }

    public int getFlags() {
        return _flags;
    }

    public AceType getType() {
        return _type;
    }

    public Who getWho() {
        return _who;
    }

    public int getWhoID() {
        return _whoID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ACE other = (ACE) obj;
        if (_type != other._type) {
            return false;
        }
        if (_flags != other._flags) {
            return false;
        }
        if (_accessMsk != other._accessMsk) {
            return false;
        }
        if (_who != other._who) {
            return false;
        }
        if (_whoID != other._whoID) {
            return false;
        }
        if (!_addressMsk.equals(other._addressMsk)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return _type.hashCode() ^ _flags ^ _accessMsk ^ _who.hashCode()
                ^ _whoID ^ _addressMsk.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%d:%s", _type.getAbbreviation(),
                _who, _whoID, AccessMask.asString(_accessMsk) );
    }
}
