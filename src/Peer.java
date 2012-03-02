/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements Peer Interface
 */

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.math.BigInteger;
import java.net.MalformedURLException;
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

    private Key pred;
    private Key succ;
    
    private boolean lock = false;
    
	/**
	 * @todo Everything
	 */
	public Peer(String sp) throws Exception {
		try {
	    pred = null;
	    succ = null;
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
		
		succ = superpeer.getSuccessor(nodeid);

		// Notify Successor.
		if(succ != null) {
		    //lock!
			getPeer(succ).notify(nodeid);
		}
		
		int mbits = hasher.getBitSize();
		
		System.out.println("++++++++++ size of mbits" + mbits);
		updateFingerTable(mbits);
		System.out.println("2.4");
		
		// TEST
//		lg.log(Level.FINEST, "Testing RMI self call of getName 3- " + getPeer(nodeid).getName());
		
		lg.log(Level.FINEST,
				"Testing RMI self call of getName - "
						+ getPeer(nodeid).getName());
		// TEST
		// superpeer.getPeers();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private PeerInterface getPeer(Key node) throws Exception {

		PeerInterface peer = peercache.get(node);

		try {
			if (peer == null) {
				String addy = superpeer.getAddress(node);

				if (addy != null) {
					peer = (PeerInterface) Naming.lookup("//" + addy + "/"
							+ node.toString());

					peercache.put(node, peer);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return peer;
	}

	public String getName() throws Exception {
		return ("Peer-" + nodeid.toString());

	}

	@Override
	public Key getSuccessor(Key key) throws RemoteException {
		if (succ == null) {
		    return null;
		}
			
		if (nodeid.compare(key)<=0 && key.compare(succ)<=0 && !key.equals(succ)) {
				return succ;
		} else {
		    try {
			return getPeer(succ).getSuccessor(key);
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		
		return null;
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
		lg.log(Level.FINEST, "In lookup method");
		Key key = hasher.getHash(word);
		lg.log(Level.FINEST, " Hashed word key: " + key);

		if (getSuccessor(key) == null || getSuccessor(key) == nodeid) {
			lg.log(Level.FINEST, " Successor of key is requested node: "
					+ nodeid);
	
			if (dict.get(word) != null) {
				return dict.get(word);
			} else {
				return "Meaning is not found";
			}
			
		} else {
			Key closestNode = ft.getClosestSuccessor(key);
	    	
			// Based on log level, prints path.
			lg.log(logLevel, closestNode + " ");
			
			PeerInterface peer = getPeer(closestNode);
			return peer.lookup(word, logLevel);
		}
	}    	
    	
    	
    /**
     * @return True if this peer has added the word successfully.
     * False otherwise.
     */
    public boolean insert(String word, String def, Level logLevel) throws Exception
    {
    	lg.log(Level.FINEST, "In insert method");
    	
		Key key = hasher.getHash(word);
		 	
    	lg.log(Level.FINEST, " Hashed word key: " + key);    	
    	if (getSuccessor(key) == null) {
    		lg.log(Level.FINEST, " Successor of key is requested node: " + nodeid);
        	dict.put(word, def);
    		return true;
    	}
    	
    	Key closestNode = ft.getClosestSuccessor(key);
    	
		// Based on log level, prints path.
		lg.log(logLevel, closestNode + " ");
		
		PeerInterface peer = getPeer(closestNode);
		peer.insert(word, def, logLevel);
		
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

    @Override
    public void printPeerData() throws RemoteException {
    	try {
			if (succ == null) {
				printData();
			}
			else {
				getPeer(succ).printData();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	/**
	 * New node calls this method to update notify successor
	 * 
	 * @throws RemoteException
	 */
	public void notify(Key _pred) throws Exception 
	{
		pred = _pred;
	    PeerInterface peer = getPeer(succ);
	    updateFingerTable(hasher.getBitSize());
	    peer.notify(nodeid);
	}
      
 	public void updateFingerTable(int mbits) throws RemoteException
 	{
 		if (! lock) {
 			lock = true;

 			 // crude way of constructing finger table for each node again.
	    	try {
	 			constructFingerTable(mbits);
	 			
	 			if (succ != null && succ != nodeid) {
	 				getPeer(succ).updateFingerTable(mbits);
	 			}
	 			else {
	 				lock = false;
	 			}
	 			
	 		} catch (Exception e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 			System.out.println("1");
	 		}
	 	}
 		else {
 			lock = false;
 			return;
 		}
 	}
 
 	public void constructFingerTable(int mbits) 
 	{
 		try {
 			for (int i = 1; i < mbits; i++) {
 				
				FingerEntry fe = new FingerEntry();
 				
				Key fingerid = new Key().add(nodeid).mod(new Key(BigInteger.valueOf((int)Math.pow(2,mbits))));
 	
				fe.setId(fingerid);
				fe.setNodeId(getSuccessor(fingerid));
 
 				ft.addFingerEntry(fe);
 			}
 			
 			lg.log(Level.FINEST, "Logging *************2**" + ft.table.size());
 
 		} catch (RemoteException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 
 		catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 
 	}
     
}
