/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.math.BigInteger;
import java.io.Serializable;

public class Key implements Serializable
{

    /**
     * Internal respesentation of a Key is an integer.
     */
    private BigInteger id;

    /**
     * Intialize the Key
     */
    public Key (BigInteger _id) 
    {
	id = _id;
    }

    
    /**
     * Check if two Keys are equal.
     */
    public boolean equals(Key b)
    {
	return (id == b.id);
    }

    /**
     * Check if this Key is less than another.
     */
    public boolean less(Key b)
    {
	return (id == b.id);
    }

    /**
     * Convert this Key to a String.
     */
    public String toString()
    {
	return id.toString();
    }

}