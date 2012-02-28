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

    /**
     * @return The name of this peer as a String.
     */
    public String getName() throws Exception;
    /**
     * @return Null if this peer is the owner, otherwise it returns
     * the next applicable node in the finger table.
     */
    public Key getOwner(Key key) throws Exception;
    public String getDef(String str) throws Excpetion;
    public String addDef() throws Excpetion;
    public String getEntries() throws Exception;
}
