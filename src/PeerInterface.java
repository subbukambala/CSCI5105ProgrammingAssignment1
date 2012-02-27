/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Peer Interface
 */
import java.rmi.*;
import java.rmi.server.*;

/**
 * @todo Everything
 */
public interface PeerInterface extends Remote
{

    public String getName() throws Exception;
}
