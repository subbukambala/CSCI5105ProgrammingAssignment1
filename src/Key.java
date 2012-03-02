/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.util.Comparator;
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
    	id = new BigInteger(_id.toByteArray());
    }


    public Key(Key k)
    {
	id = new BigInteger(k.id.toByteArray());
    }

    public Key() {

	id = BigInteger.ZERO;
    }

    public BigInteger getId()
    {
    	return id;
    }
    
    /**
     * Check if two Keys are equal.
     */
    public boolean equals(Key b)
    {
    	return (id.equals(b.id));
    }

    /**
     * @return -1 if less than, 0 if equal, 1 if greater
     * NOTE: <=0 is less than or equal, >=0 is greater than or equal.
     */
    public int compare(Key b) {
	return id.compareTo(b.id);
	}

    /**
     * Convert this Key to a String.
     */
    public String toString()
    {
    	return id.toString();
    }

    public Key succ() {

	return new Key(id.add(BigInteger.ONE));
    }

    public Key add(Key k) {

	return new Key(id.add(k.id));
    }

    public Key mod(Key k) {

	return new Key(id.mod(k.id));
    }

}