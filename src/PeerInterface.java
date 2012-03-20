/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion The RMI interface for a peer.
 */
import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;

/**
 * @todo Everything
 */
public interface PeerInterface extends Remote
{

        /********** THESE FUNCTIONS ARE USED FOR DHT OPERATION *****************/
        /**
         * This method looks for a key in the whole DHT recursively.
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
         * Notify a peer that a the node associated with key has
         * added this node as a successor.
         */
        public void notify(Key key) throws Exception;


        /**
         * Get the successor of this key.
         * @return The key of the node that is the successor of
         * the input key.
         */
        public Key getSuccessor(Key key) throws Exception;




        /********** THESE FUNCTIONS ARE USED FOR DIAGNOSIS AND CLIENT INTERACTION *****************/
        /**
         * Mainly a debugging function used to retrieve the
         * finger table from a given node.
         * @return The FingerTable for this peer.
         */
        public FingerTable getFingerTable() throws Exception;


        /**
         * @return This peers predecessor. Null if none exixts.
         */
        public Key myPred() throws Exception;

        /**
         * @return This peers successor. Null if none exixts.
         */
        public Key mySucc() throws Exception;


        /**
         * @return A table of all words and definitions stored in this
         * peer.
         */
        public String[][] getEntries() throws Exception;


        /**
         * @return The name of this peer as a String.
         */
        public String getName() throws Exception;
}

