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

	public void printPeerData() throws Exception;
	
    /**
     * @return The name of this peer as a String.
     */
    public String getName() throws Exception;
    
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
     * @todo Everything.
     */
    public void notify(Key key) throws Exception;

     

    public Key getSuccessor(Key key) throws Exception;


    public FingerTable getFingerTable() throws Exception;


    public Key myPred() throws Exception;
    public Key mySucc() throws Exception;
        
    /**
     * This method is used to print node's internal data such as Key, Finger Table, 
     * No of words.
     */
    public void printData() throws Exception;
    
    
}
