/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.io.Serializable;
import java.lang.Math.*;


public class SHA1Hasher implements HasherInterface, Serializable
{
    private MessageDigest sha;
    private int bitsize;
    private BigInteger ringsize;
    public SHA1Hasher (int _bitsize)
    {
	sha = null;
	bitsize = _bitsize;
	ringsize = new BigInteger(Integer.toString((int)Math.pow(2,bitsize)));
    }
    /**
     * @note MessageDigest is not serializable.  To get around this
     * we ensure to initialize after it has been recieved.
     */
    public Key getHash(String str) throws NoSuchAlgorithmException
    {
	if(sha == null)
	{
	    sha = MessageDigest.getInstance("SHA-1");
	}
        return 
	    new Key
	    (
	     new BigInteger(sha.digest( str.getBytes() )).abs().mod(ringsize)
	    );
    }

    public int getBitSize() {return bitsize;}

}
