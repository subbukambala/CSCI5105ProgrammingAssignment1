/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;

public class Key implements Serializable
{

    /**
     * Internal representation of a Key is an integer.
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
    	return (id.equals(b.id));
    }
    
    /**
     * Check id is less than b
     */
    public boolean less(Key b)
    {
    	return (id.compareTo(b.id) <= 0);
    }


    public boolean leq(Key b)
    {
	return id.compareTo(b.id)<=0;
	
    }

    /**
     * Convert this Key to a String.
     */
    public String toString()
    {
    	return id.toString();
    }

}