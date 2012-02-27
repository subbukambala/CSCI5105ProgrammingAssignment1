/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public class SHA1Hasher implements HasherInterface
{
    private MessageDigest sha;
    public SHA1Hasher () throws NoSuchAlgorithmException 
    {
	sha = MessageDigest.getInstance("SHA-1");
    }
    public BigInteger getHash(String str)
    {
        return new BigInteger(sha.digest( str.getBytes() ));
    }

}
