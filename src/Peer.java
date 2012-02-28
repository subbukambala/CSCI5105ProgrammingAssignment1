/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements Peer Interface
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import java.util.HashMap;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

/**
 * @todo Everything
 */
public class Peer extends UnicastRemoteObject implements PeerInterface {
	/**
	 * Logger for Peer.
	 */
	private Logger lg;

	/**
	 * This peer's NodeID.
	 */
	Key nodeid;

	/**
	 * SuperPeer
	 */
	SuperPeerInterface superpeer;

	/**
	 * The hash function that all peers use.
	 */
	HasherInterface hasher;

	/**
	 * A cache to map Node ID's to IP.
	 */
	HashMap<Key, PeerInterface> peercache;

        /**
	 * Finger table.
	 */
    FingerTable ft;
    
	/**
	 * @todo Everything
	 */
	public Peer(String sp) throws Exception {
		// Find the SuperPeer
		superpeer = (SuperPeerInterface) Naming
				.lookup("//" + sp + "/SuperPeer");

		// Get this Peer's NodeID
		nodeid = superpeer.join();

		// Initialize the Logger
		lg = new Logger("Peer:" + nodeid.toString());
		lg.log(Level.FINEST, "Peer started.");

		// Get Hasher object
		lg.log(Level.FINEST, "Retrieving Hash object");
		hasher = superpeer.getHasher();

		// Register with the RMI Registry.
		lg.log(Level.FINEST, "Binding to local RMI registry with name "
				+ nodeid.toString());
		Naming.rebind(nodeid.toString(), this);

		// Initialize the node cache
		peercache = new HashMap<Key, PeerInterface>();


		// Initialize finger table
		lg.log(Level.FINEST, "Getting initial finger table from superpeer");
		ft = superpeer.getInitialFingerTable(nodeid);
		
		// TEST
		lg.log(Level.FINEST,
				"Testing RMI self call of getName - "
						+ getPeer(nodeid).getName());
		// TEST
		// superpeer.getPeers();
	}

	private PeerInterface getPeer(Key node) throws Exception {
		PeerInterface peer = peercache.get(node);
		if (peer == null) {
			peer = (PeerInterface) Naming.lookup("//"
					+ superpeer.getAddress(node) + "/" + node.toString());
			peercache.put(node, peer);
		}
		return peer;
	}

	public String getName() throws Exception {
		return ("Peer-" + nodeid.toString());

	}


    /** 
     * @return Null if this peer is the owner, otherwise it returns
     * the next applicable node in the finger table.
     */
    public Key getOwner(Key key) throws Exception
    {
	//XXX: implement
	return null;
    }
    /**
     * @return If this peer owns the definition for this word 
     * the definition is returned as a string. In any other case null is
     * returned.
     */
    public String getDef(String word) throws Exception
    {
	//XXX: implement
	return null;
    }
    /**
     * @return True if this peer has added the word successfully.
     * False otherwise.
     */
    public boolean addDef(String word,String def) throws Exception
    {
	//XXX: implement
	return false;
    }
    /**
     * @return A table of all words and definitions stored in this
     * peer.
     */
    public String[][] getEntries() throws Exception
    {
	//XXX: implement
	return null;
    }


	/**
	 * @todo Everything
	 */
	public static void main(String[] argv) {

		String superpeeraddy = "localhost";

		// Setup command line options
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("h", "help", false, "Print this usage information.");
		options.addOption("P", "superpeer", false, "Address of SuperPeer.");

		// Parse the program arguments
		CommandLine commandLine;
		try {
			commandLine = parser.parse(options, argv);
			if (commandLine.hasOption('h')) {
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.setWidth(80);
				helpFormatter.printHelp("Peer Usage:", "", options, "");
				System.exit(0);
			}
			if (commandLine.hasOption('P')) {
				// XXX:uncaught exception!
				superpeeraddy = commandLine.getOptionValue('s');
			}
		} catch (Exception e) {
			System.out.println("Improper Usage");
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.setWidth(80);
			helpFormatter.printHelp("Peer Usage:", "", options, "");
			System.exit(1);
		}

		try {
			Peer peer = new Peer(superpeeraddy);
		} catch (Exception e) {
			System.out.println("Peer exception: " + e);
		}
	}

}
