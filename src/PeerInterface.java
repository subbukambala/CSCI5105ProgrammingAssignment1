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
     * @return Return the key of the owner of the input key.
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
     * @todo Everything.
     */
    public void notify(Key key) throws Exception;

    /**
     * This method is called by peer to update it's predecessor
     */
    public void updatePredecessor(Key key) throws RemoteException;
    
    /**
     * This method is called by peer to update it's predecessor
     */
    public void updateSuccessor(Key key) throws RemoteException;
    
    /**
     * This method returns the successor node of a key
     */
    public Key getSuccessor(Key key) throws RemoteException;
    
    /**
     * This method returns the fingerEntry of a node
     */
    public FingerEntry getFingerEntry() throws RemoteException;
    
    public void updateFingerTable(int mbits) throws RemoteException;
        
    /**
     * This method is used to print node's internal data such as Key, Finger Table, 
     * No of words.
     */
    public void printData() throws Exception;
}
