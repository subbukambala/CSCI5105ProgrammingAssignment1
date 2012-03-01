/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Peer Interface
 */
import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;

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
    
    /**
     * This method looks for a key in whole DHT recursively.
     * 
     * @return meaning of a given word.
     */
    public String lookup(String word, Level logLevel) throws Exception;
    
    /**
     * @return True if this peer has added the word successfully.
     * False otherwise.
     */
    public boolean insert(String word, String def, Level logLevel) throws Exception;
    
    /**
     * @return A table of all words and definitions stored in this
     * peer.
     */
    public String[][] getEntries() throws Exception;
    
    /**
     * This method is used to print node's internal data such as Key, Finger Table, 
     * No of words.
     */
    public void printData() throws Exception;
}
