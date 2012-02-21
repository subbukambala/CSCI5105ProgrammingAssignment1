import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;

/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements the Super Peer Interface.
 */

public class SuperPeer extends UnicastRemoteObject implements SuperPeerInterface 
{
    /**
     * Logger for SuperPeer.
     */
    private Logger lg;

    /**
     * @todo Everything
     */
    SuperPeer () throws RemoteException
    {
	lg = new Logger("SuperPeer");
    }

    /**
     * @return A NodeID.
     * @exception RemoteException if the remote invocation fails.
     */
    public NodeID join() throws RemoteException
    {
	lg.log(Level.FINER,"Join Called.");
	return new NodeID();
    }
    /**
     * Retrieves an Address from a NodeID.
     * @return An Address.
     * @exception RemoteException if the remote invocation fails.
     */
    public Address getAddress(NodeID id) throws RemoteException
    {
	lg.log(Level.FINER,"getAddress Called.");
	return new IPPortAddress();
    }
    /**
     * Retrieves all known nodes.
     * @todo This class needs to returns some mapping between NodeID's and Addresses.
     * @return ???
     * @exception RemoteException if the remote invocation fails.
     */
    public void getPeers() throws RemoteException
    {
	lg.log(Level.FINER,"getPeers Called.");
	return;
    }

    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	try 
	{
	    Naming.rebind ("SuperPeer", new SuperPeer ());
	} catch (Exception e) 
	{
	    System.out.println ("SuperPeer failed: " + e);
	}
    }
}
