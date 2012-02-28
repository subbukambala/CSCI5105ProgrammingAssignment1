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

public class SHA1Hasher implements HasherInterface, Serializable
{
	private MessageDigest sha;

	public SHA1Hasher() {
		sha = null;
	}

	public Key getHash(String str) throws NoSuchAlgorithmException {
		if (sha == null) {
			https: // subbukambala@github.com/chaosape/CSCI5105ProgrammingAssignment1.git
			sha = MessageDigest.getInstance("SHA-1");
		}
		return new Key(new BigInteger(sha.digest(str.getBytes())).abs());
	}

}
