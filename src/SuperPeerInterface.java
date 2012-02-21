import java.rmi.*;
/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Remote Interface to the Super Peer.
 */
public interface SuperPeerInterface extends Remote {
    /**
     * @return A NodeID.
     * @exception RemoteException if the remote invocation fails.
     */
    public NodeID join() throws RemoteException;
    /**
     * Retrieves an Address from a NodeID.
     * @return An Address.
     * @exception RemoteException if the remote invocation fails.
     */
    public Address getAddress(NodeID id) throws RemoteException;
    /**
     * Retrieves all known nodes.
     * @todo This class needs to returns some mapping between NodeID's and Addresses.
     * @return ???
     * @exception RemoteException if the remote invocation fails.
     */
    public void getPeers() throws RemoteException;
}