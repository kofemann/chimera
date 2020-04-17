package org.dcache.chimera.store;

import com.google.common.base.CharMatcher;

import java.io.Serializable;

import static com.google.common.base.Strings.padStart;
import static org.dcache.chimera.store.ChecksumType.ADLER32;
import static java.util.Objects.requireNonNull;

public class Checksum  implements Serializable
{
    private static final long serialVersionUID = 7338775749513974986L;

    private static final String HEX_DIGITS = "0123456789abcdef";

    private static final CharMatcher HEXADECIMAL =
            CharMatcher.anyOf(HEX_DIGITS);

    private static final char DELIMITER = ':';

    private final ChecksumType type;
    private final String value;

    /**
     * Creates a new instance of Checksum.
     * @throws IllegalArgumentException if the number of bytes in value is
     * incorrect for the supplied type
     * @throws NullPointerException if either argument is null
     */
    public Checksum(ChecksumType type, byte[] value)
    {
        this(type, bytesToHexString(value));
    }

    /**
     * Creates a new instance of Checksum based on supplied type and a
     * string of the checksum value in hexadecimal.  If the type is ADLER32
     * then the value may omit any leading zeros.
     * @throws IllegalArgumentException if the value is inappropriate for
     * the supplied checksum type.
     * @throws NullPointerException if either argument is null
     */
    public Checksum(ChecksumType type, String value)
    {
        requireNonNull(type, "type may not be null");
        requireNonNull(value, "value may not be null");

        this.type = type;
        this.value = normalise(value);
    }

    private String normalise(String original)
    {
        String normalised = original.trim().toLowerCase();

        /**
         * Due to bug in checksum calculation module, some ADLER32
         * sums are stored without leading zeros.
         */
        if (type == ADLER32) {
            normalised = padStart(normalised, type.getNibbles(), '0');
        }

        if (!HEXADECIMAL.matchesAllOf(normalised)) {
            throw new IllegalArgumentException("checksum value \"" +
                    original + "\" contains non-hexadecimal digits");
        }

        if (normalised.length() != type.getNibbles()) {
            throw new IllegalArgumentException(type.getName() + " requires " +
                    type.getNibbles() + " hexadecimal digits but \"" +
                    original + "\" has " + normalised.length());
        }

        return normalised;
    }

    public ChecksumType getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public byte[] getBytes()
    {
        return stringToBytes(value);
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == null){
            return false;
        }

        if (other == this) {
            return true;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        Checksum that = (Checksum) other;
        return ((this.type == that.type) && this.value.equals(that.value));
    }

    @Override
    public int hashCode()
    {
        return value.hashCode() ^ type.hashCode();
    }

    @Override
    public String toString()
    {
        return toString(false);
    }

    public String toString(boolean useStringKey)
    {
        return (useStringKey ? type.getName() : String.valueOf(type.getType())) + ":" + value;
    }

    public static String bytesToHexString(byte[] bytes)
    {
        requireNonNull(bytes, "byte array may not be null");

        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(HEX_DIGITS.charAt((aByte >> 4) & 0xf));
            sb.append(HEX_DIGITS.charAt(aByte & 0xf));
        }
        return sb.toString();
    }

    private static byte[] stringToBytes(String str)
    {
        if ((str.length() % 2) != 0) {
            str = "0" + str;
        }

        byte[] r = new byte[str.length() / 2];

        for (int i = 0, l = str.length(); i < l; i += 2) {
            r[i / 2] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
        }
        return r;
    }

    /**
     * Create a new checksum instance for an already computed digest
     * of a particular type.
     *
     * @param digest the input must have the following format:
     *            <type>:<hexadecimal digest>
     * @throws IllegalArgumentException if argument has wrong form
     * @throws NullPointerException if argument is null
     */
    public static Checksum parseChecksum(String digest)
    {
        requireNonNull(digest, "value may not be null");

        int del = digest.indexOf(DELIMITER);
        if (del < 1) {
            throw new IllegalArgumentException("Not a dCache checksum: " + digest);
        }

        String type = digest.substring(0, del);
        String checksum = digest.substring(del + 1);

        return new Checksum(ChecksumType.getChecksumType(type), checksum);
    }
}
