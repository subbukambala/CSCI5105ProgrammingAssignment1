/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Remote Interface to the SuperPeer.
 */
import java.rmi.*;
import java.rmi.server.*;
import java.security.NoSuchAlgorithmException;

public interface SuperPeerInterface extends Remote {
        /**
         * @return A NodeID.
         */
        public Key join() throws Exception;


        /**
         * @return The Hasher class
         */
        public HasherInterface getHasher() throws Exception;


	/**
	 * @return The Successor of the nodeid.
	 */
        public Key getSuccessor(Key nodeid)  throws Exception;
	
	/**
	 * @return The predecessor of the nodeid.
	 */
        public Key getPredecessor(Key nodeid)  throws Exception;

        /**
         * Retrieves an Address from a NodeID.
         * @return An Address.
         */
        public String getAddress(Key id) throws Exception;

        /**
         * @returns  A string where the rows are the peer entries and
	 * column 0 is the nodeid and column 1 is the ip address.
         */
        public String[][] getPeers() throws Exception;


	/**
	 * Used be the peers to ensure that peer startup/joins occur
	 * in a synchronized fashion. Check Peer.java.
	 */
        public void lock() throws Exception;
        public void unlock() throws Exception;

}