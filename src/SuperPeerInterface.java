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
    public Key join() throws RemoteException,ServerNotActiveException,NoSuchAlgorithmException;
    /**
     * Retrieves an Address from a NodeID.
     * @return An Address.
     * @exception RemoteException if the remote invocation fails.
     */

    /**
     * @return The Hasher class
     */
    public HasherInterface getHasher() throws RemoteException;


    /**
     * @todo Document
     */
    public String getAddress(Key id) throws RemoteException;


    /**
     * @todo Document
     */
    public Key getSuccessor(Key key)  throws RemoteException;


    /**
     * @todo Document
     */
    public FingerTable getInitialFingerTable(Key key) throws RemoteException,ServerNotActiveException;


}