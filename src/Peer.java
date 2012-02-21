import java.rmi.*;
/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements Peer Interface
 */

/**
 * @todo Everything
 */
public class Peer implements PeerInterface
{

    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	try 
	{
	    SuperPeerInterface superpeer = 
		(SuperPeerInterface) Naming.lookup ("//localhost/SuperPeer");
	    superpeer.join();
	    superpeer.getAddress(new NodeID());
	    superpeer.getPeers();
	} 
	catch (Exception e) 
	{
	    System.out.println ("Peer exception: " + e);
	}
    }


}
