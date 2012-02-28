/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.security.NoSuchAlgorithmException;

public interface HasherInterface
{
    public Key getHash(String str) throws NoSuchAlgorithmException;
}
