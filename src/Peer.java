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

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import java.util.TimerTask;

/**
 * @todo Everything
 */
public class Peer extends UnicastRemoteObject implements PeerInterface {


	private class Stabilize extends TimerTask {

		private Peer peer;

		public Stabilize(Peer _peer) {
			peer = _peer;
		}

		public void run() {
			peer.stabilize();
		}
	}

	private class FixFinger extends TimerTask {

		private Peer peer;

		public FixFinger(Peer _peer) {
			peer = _peer;
		}

		public void run() {
			peer.fixFinger();
		}
	}

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
	 * My finger entry.
	 */
	private FingerEntry myFingerEntry;
	
    /**
	 * Finger table.
	 */
	private FingerTable ft;
	
	private Map<String, String> dict;

    private Key pred;
    private Key succ;
    private Stabilize stabilizer;
    private FixFinger fingerFixer;
    private java.util.Timer timer;
    
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
		// Initialize finger table
		lg.log(Level.FINEST, "Getting initial finger table from superpeer");
		ft = superpeer.getInitialFingerTable(nodeid);
		
		succ = getSuccessor(nodeid);

		// Notify Successor.
		if(succ != null) {
			getPeer(succ).notify(nodeid);
		}
		
		int mbits = superpeer.getChordKeyBitsSize();
		
		System.out.println("++++++++++ size of mbits" + mbits);
		updateFingerTable(mbits);
		System.out.println("2.4");
		
		/*timer = new java.util.Timer();
		stabilizer = new Stabilize(this);
		timer.schedule(stabilizer, 0, 20000);

		fingerFixer = new FixFinger(this);
		timer.schedule(fingerFixer, 0, 5000);
		 */		

		// TEST
//		lg.log(Level.FINEST, "Testing RMI self call of getName 3- " + getPeer(nodeid).getName());
		
		lg.log(Level.FINEST,
				"Done...");
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
			if (superpeer.getNodeServiceAddress(node) != null) {
				peer = (PeerInterface) Naming.lookup("//"
						+ superpeer.getNodeServiceAddress(node));
			
				peercache.put(node, peer);
			}
		}
		}
		catch (Exception e) {
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
		
		if (nodeid.leq(key) && key.leq(succ) && !key.equals(succ)) {
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
     * @todo Everything.
     */
    public void notify(Key key) throws Exception
    {
	lg.log(Level.FINEST, "Peer-"+key.toString()+" is now our predecssor.");
	if(pred == null || (pred.leq(key) && key.leq(nodeid))) {
	    pred = key;
	}
    }


    public void stabilize() {
	lg.log(Level.FINEST, "stabilize called.");
	return;
    }

    public void fixFinger() {
	lg.log(Level.FINEST, "fixFinger called.");
	return;
    }
    
    public void updatePredecessor(Key key) throws RemoteException
    {
    	pred = key;
    }
    
    public void updateSuccessor(Key key) throws RemoteException
    {
    	succ = key;
    }    	
    
	public FingerEntry getFingerEntry() throws RemoteException
	{
		FingerEntry fe = null;
		try {

			if (myFingerEntry != null) {
				return myFingerEntry;
			}
			fe = new FingerEntry();

			fe.setId(nodeid);

			fe.setIpAddress(RemoteServer.getClientHost());

			BigInteger prevEndId = BigInteger.ZERO;

			prevEndId = getPeer(pred).getFingerEntry().getEndWordKey().getId();

			Key endKey = new Key(prevEndId.add(BigInteger.ONE));

			fe.setStartWordKey(endKey);

			fe.setEndWordKey(nodeid);

		} catch (ServerNotActiveException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fe;
	}
    
    /**
     * New node calls this method to update notify successor
     * 
     * @throws RemoteException
     */
    private void notifySuccessor() throws RemoteException 
    {
    	try {
			PeerInterface peer = (PeerInterface) Naming.lookup("//"
					+ superpeer.getNodeServiceAddress(succ));
			
			peer.updatePredecessor(nodeid);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * New node calls this method to update notify predecessor
     * 
     * @throws RemoteException
     */
    private void notifyPredecessor() throws RemoteException 
    {
    	try {
			PeerInterface peer = (PeerInterface) Naming.lookup("//"
					+ superpeer.getNodeServiceAddress(pred));
			
			peer.updateSuccessor(nodeid);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
	public void updateFingerTable(int mbits) throws RemoteException
	{
    	// crude way of constructing finger table for each node again.
		try {
			constructFingerTable(mbits);

			if (succ != null) {
				getPeer(succ).updateFingerTable(mbits);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("1");
		}
	}

	public void constructFingerTable(int mbits) 
	{
		try {
			for (int i = 1; i < mbits; i++) {
				System.out.println("1...");
				FingerEntry fe = new FingerEntry();
				fe.setId(nodeid);
				int fingerId = (nodeid.getId().intValue() + 
						(int) Math.pow(2, i - 1)) % (int) (Math.pow(2, mbits));
				fe.setStartWordKey(new Key(BigInteger.ZERO));
				fe.setEndWordKey(new Key(BigInteger.valueOf((long)Math.pow(2, mbits) - 1)));
				fe.setIpAddress(superpeer.getAddress(nodeid));

				Key key = new Key(BigInteger.valueOf(fingerId));
				Key succNode = null;

				succNode = getSuccessor(key);

				if (succNode == null) {
					ft.AddFingerEntry(fe);
				} 
				else {
					ft.AddFingerEntry(getPeer(succNode).getFingerEntry());
				}
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
    	
    	if (getSuccessor(key) == null) {
    		lg.log(Level.FINEST, " Successor of key is requested node: " + nodeid);
        	return dict.get(word);
    	}
    	else {
    		for (Integer i = 0; i < ft.table.size(); i++) {
        		FingerEntry fe = ft.table.get(i);
        		
        		// If key lies in range
        		if ((fe.getStartWordKey().leq(key) &&
            			key.leq(fe.getEndWordKey())) || (i == ft.table.size() -1)) 
        		{
        			PeerInterface peer = (PeerInterface) Naming.lookup("//"
        					+ superpeer.getNodeServiceAddress(ft.table.get(i).getId()));

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
    	lg.log(Level.FINEST, "In insert method");
    	
    	
    	Key key = hasher.getHash(word);
    	
    	lg.log(Level.FINEST, " Hashed word key: " + key);
    	
    	if (getSuccessor(key) == null) {
    		lg.log(Level.FINEST, " Successor of key is requested node: " + nodeid);
        	dict.put(word, def);
    		return true;
    	}
    	
    	
    	for (Integer i = 0; i < ft.table.size(); i++) {
    		// If key lies in range
    		FingerEntry fe = ft.table.get(i); 
    		
    		if ((fe.getStartWordKey().leq(key) &&
    			key.leq(fe.getEndWordKey())) || (i == ft.table.size() -1)) 
    		{
    			
    			PeerInterface peer = (PeerInterface) Naming.lookup("//"
    					+  superpeer.getNodeServiceAddress(ft.table.get(i).getId()));
    			
    			// XXX:: Based on log level, prints path
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
