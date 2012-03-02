/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Remote Interface to the Super Peer.
 */
import java.rmi.*;
import java.rmi.server.*;
import java.security.NoSuchAlgorithmException;

public interface SuperPeerInterface extends Remote {
    /**
     * @return A NodeID.
     * @exception RemoteException if the remote invocation fails.
     */
    public Key join() throws Exception;
    /**
     * Retrieves an Address from a NodeID.
     * @return An Address.
     * @exception RemoteException if the remote invocation fails.
     */

    /**
     * @return The Hasher class
     */
    public HasherInterface getHasher() throws Exception;


    public Key getSuccessor(Key nodeid)  throws Exception;
    
    /**
     * @todo Document
     */
    public String getAddress(Key id) throws Exception;

    /**
     * @todo Document
     */
    public String[][] getPeers() throws Exception;

    public void lock() throws Exception;
    public void unlock() throws Exception;

}