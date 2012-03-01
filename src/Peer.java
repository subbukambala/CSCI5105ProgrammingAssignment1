/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements Peer Interface
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;

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
	private static Logger lg;

	/**
	 * This peer's NodeID.
	 */
	private Key nodeid;

	/**
	 * SuperPeer
	 */
	private SuperPeerInterface superpeer;

	/**
	 * The hash function that all peers use.
	 */
	private HasherInterface hasher;

	/**
	 * A cache to map Node ID's to IP.
	 */
	private HashMap<Key, PeerInterface> peercache;

        /**
	 * Finger table.
	 */
	private FingerTable ft;
	
	private Map<String, String> dict;
    
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
		
		dict = new HashMap<String, String>();
		
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
    
    @Override
    public String lookup(String word, Level logLevel) throws Exception
    {
    	if (dict.get(word) != null) {
    		return dict.get(word); 
    	}
    	else {
    		Key key = hasher.getHash(word);
        	
        	for (Integer i = 0; i < ft.table.size(); i++) {
        		FingerEntry fe = ft.table.get(i);
        		
        		// If key lies in range
        		if ((fe.getStartWordKey().less(key) &&
            			key.less(fe.getEndWordKey())) || (i == ft.table.size() -1)) 
        		{
        			PeerInterface peer = (PeerInterface) Naming.lookup("//"
        					+ ft.table.get(i).getIpAddress() + "/" + ft.table.get(i).getId());

        			// Based on log level, prints path.
        			lg.log(logLevel, nodeid + " ");
        			
        			return peer.lookup(word, logLevel);
        		}
        	}
    	}
    	
    	
    	return "";
    }
    /**
     * @return True if this peer has added the word successfully.
     * False otherwise.
     */
    public boolean insert(String word, String def, Level logLevel) throws Exception
    {
    	Key key = hasher.getHash(word);
    	
    	for (Integer i = 0; i < ft.table.size(); i++) {
    		// If key lies in range
    		FingerEntry fe = ft.table.get(i); 
    		if ((fe.getStartWordKey().less(key) &&
    			key.less(fe.getEndWordKey())) || (i == ft.table.size() -1)) 
    		{
    			PeerInterface peer = (PeerInterface) Naming.lookup("//"
    					+ ft.table.get(i).getIpAddress() + "/" + ft.table.get(i).getId());
    			
    			// Based on log level, prints path
    			lg.log(logLevel, nodeid + " ");
    			
    			return peer.insert(word, def, logLevel);
    		}
    	}
    	
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

    @Override
    public void printData() throws Exception
    {
    	System.out.println("Node id: " + nodeid);
    	
    	System.out.println("Size of dictionary: " + dict.size());
    	
    	System.out.println("***  Finger Table *** ");
    	ft.PrintFingerTable();
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
