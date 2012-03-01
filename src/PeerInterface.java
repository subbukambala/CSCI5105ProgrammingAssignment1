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
     * @return Return the key of the owner of the input key.
     */
    public Key getOwner(Key key) throws Exception;
    /**
     * @return If this peer owns the definition for this word 
     * the definition is returned as a string. In any other case null is
     * returned.
     */
    public String getDef(String word) throws Exception;
    /**
     * @return True if this peer has added the word successfully.
     * False otherwise.
     */
    public boolean addDef(String word,String def) throws Exception;
    /**
     * @return A table of all words and definitions stored in this
     * peer.
     */
    public String[][] getEntries() throws Exception;
    /**
     * @todo Everything.
     */
    public void notify(Key key) throws Exception;
}
