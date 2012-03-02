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

	/**
	 * The words and definitions stored at this 
	 * peer.
	 */
        private Map<String, String> dict;

	/**
	 * This peers predeccsor.
	 */
        private Key pred;
	
	/**
	 * This peers successor.
	 */
        private Key succ;
	
	/**
	 * Used signal the end of a notify cycle.
	 */
        private boolean lock;


        /**
         * The peer constructor.
         */
        public Peer(String sp) throws Exception {
                try {
			// Initialize of various members
                        pred = null;
                        succ = null;
                        lock = false;



                        // Find the SuperPeer
                        superpeer = (SuperPeerInterface) Naming
                                .lookup("//" + sp + "/SuperPeer");

                        // Initialize the node cache
                        peercache = new HashMap<Key, PeerInterface>();

                        // Initialize word map
                        dict = new HashMap<String, String>();

			////////////////////////////////////////////////
			// BEGIN CRITICAL SECTION
			// only one peer will be in this code at a time.
                        superpeer.lock();
                        // Get this Peer's NodeID
                        nodeid = superpeer.join();
                        //Start logger
                        lg = new Logger("Peer:" + nodeid.toString());
                        lg.log(Level.FINER, "Peer started.");

                        // Register with the RMI Registry.
                        lg.log(Level.FINER, "Binding to local RMI registry with name "
                               + nodeid.toString());
                        Naming.rebind(nodeid.toString(), this);

                        // Get Successor
                        succ = superpeer.getSuccessor(nodeid);

                        // Get hasher
                        lg.log(Level.FINER, "Retrieving Hash object");
                        hasher = superpeer.getHasher();
                        // Set mbits
                        int mbits = hasher.getBitSize();
                        // Initialize finger table
                        ft = new FingerTable(nodeid.succ(),nodeid);
                        // Update finger table
                        constructFingerTable(mbits);
                        lock = true;
			// If we have a successor, start the notify cycle
                        if(succ != null ) {
                                lg.log(Level.FINER,"Calling " + succ.toString());
                                getPeer(succ).notify(nodeid);
                        }
                        lock = false;
                        superpeer.unlock();
			// END CRITICAL SECTION
			////////////////////////////////////////////////
                        lg.log(Level.FINER, "Exit Critical Section");


			lg.log(Level.INFO, "!!!! Peer "+nodeid+" at "+superpeer.getAddress(nodeid)+" started !!!!"); 
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
        }
        /**
	 * Used with trying to get a remote object to a peer.
	 * It will check a local cache for a the object and if it does exist
	 * it will ask the super peer for the location of the peer and store it.
	 * @return PeerInterface or null if some error occurs.
	 */
        private PeerInterface getPeer(Key node) throws Exception {
		lg.log(Level.FINEST, "getPeer Entry");
                PeerInterface peer = peercache.get(node);
                try {
                        if (peer == null) {
				lg.log(Level.FINER, "Peer "+node+" not found in cache asking superpeer.");
                                String addy = superpeer.getAddress(node);
                                lg.log(Level.FINER,"//" + addy + "/" + node.toString());
                                if (addy != null) {
                                        peer = (PeerInterface) Naming.lookup("//" + addy + "/"
                                                                             + node.toString());

                                        peercache.put(node, peer);
                                }

                        }
			else
				lg.log(Level.FINER, "Peer "+node+" found in cache.");


                } catch (Exception e) {
                        e.printStackTrace();
                }
		if (peer == null)
			lg.log(Level.WARNING, "getPeer attempt on "+node+" unsuccessful.");


		lg.log(Level.FINEST, "getPeer Exit");
                return peer;
        }

        @Override 
		public Key getSuccessor(Key key) throws Exception {
		lg.log(Level.FINEST, "getSuccessor Entry");


                lg.log(Level.FINER,"getSuccessor Calling succ:" + succ + " from peer:" + nodeid + " with key:" + key);

		// Ensure this peers successor is up to date 
                succ = superpeer.getSuccessor(nodeid);
		
		// If we have no successor, this peer is the successor
                if (succ == null) {
                        return nodeid;
                }
		
		// Ensure this peers predecessor is up to date
                pred = superpeer.getPredecessor(nodeid);

		// Get the max key value 
                Key max = new Key(BigInteger.valueOf((int)Math.pow(2,hasher.getBitSize()))).pred();


		// If this peer knows the which peer that key belongs to ...
                if (
		    // Normal increasing range case
                    (nodeid.compare(key)<0 && key.compare(succ)<=0)
		    // Modulo case
		    ||(pred.compare(nodeid)>0 && (key.compare(pred)>0 && key.compare(max)<=0) || (key.compare(nodeid)<=0))
                    ){
			lg.log(Level.FINER,"getSuccessor - Known successor.");
			lg.log(Level.FINEST, "getSuccesssor Exit");
			return succ;
		} 
		// ... else ask this peers successor
		else {
			lg.log(Level.FINER,"getSuccessor - Unknown successor.");
                        try {
				lg.log(Level.FINEST, "getSuccesssor Exit");
                                return getPeer(succ).getSuccessor(key);
                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }

		lg.log(Level.WARNING, "getSuccessor returning null");
		lg.log(Level.FINEST, "getSuccesssor Exit");
                return null;
        }


        @Override 
		public String lookup(String word, Level logLevel) throws Exception {
		lg.log(Level.FINEST, "lookup Entry");

		// Get the hash for this word
                Key key = hasher.getHash(word);
                lg.log(Level.FINER, " Hashed word "+word+" has key " + key);

		// Get the max key value
                Key max = new Key(BigInteger.valueOf((int)Math.pow(2,hasher.getBitSize()))).pred();

		
		// If this peer knows the which peer that key belongs to ...
                if(
		   // Normal ascending range
		   pred == null || (key.compare(pred)>0 && key.compare(nodeid)<=0)
		   // Modulor case
                   ||(pred.compare(nodeid)>0 && (key.compare(pred)>0 && key.compare(max)<=0) || (key.compare(nodeid)<=0))
                   ) {
                        lg.log(logLevel, " Peer "+nodeid+" should have word "+word+" with key "+key);

			// Lookup keey 
                        if (dict.get(word) != null) {
				lg.log(Level.FINEST, "lookup Exit");
                                return dict.get(word);
                        } else {
				lg.log(Level.FINEST, "lookup Exit");
                                return "Meaning is not found";
                        }
                }
		// ... else find next success through finger key.
		else {
                        Key closestNode = ft.getClosestSuccessor(key);

                        lg.log(logLevel, " Peer "+nodeid+" should NOT have word "+word+" with key "+key + " ... calling insert on the best finger table match "+closestNode);
                        PeerInterface peer = getPeer(closestNode);
			lg.log(Level.FINEST, "lookup Exit");
                        return peer.lookup(word, logLevel);
                }
        }


	@Override
		public boolean insert(String word, String def, Level logLevel) throws Exception {
		lg.log(Level.FINEST, "insert Entry");

                // Max key value
		Key max = new Key(BigInteger.valueOf((int)Math.pow(2,hasher.getBitSize()))).pred();

		// Get key hash 
                Key key = hasher.getHash(word);
		
		lg.log(Level.FINER, " Hashed word "+word+" with definition "+def+" has key " + key);

		// If this peer knows the which peer that key belongs to ...
                if(
		   // Normal ascending rande
		   pred == null || (key.compare(pred)>0 && key.compare(nodeid)<=0)
		   // Modulo range 
                   ||(pred.compare(nodeid)>0 && (key.compare(pred)>0 && key.compare(max)<=0) || (key.compare(nodeid)<=0))
                   ) {
                        lg.log(logLevel, " Peer "+nodeid+" should have word "+word+"("+def+") with key "+key);
                        dict.put(word, def);
			lg.log(Level.FINEST, "insert Exit");
                        return true;
                }

		// ... else find the successor through the finer table
                Key closestNode = ft.getClosestSuccessor(key);

		lg.log(logLevel, " Peer "+nodeid+" should NOT have word "+word+"("+def+") with key "+key + " ... calling insert on the best finger table match "+closestNode);

                PeerInterface peer = getPeer(closestNode);
		lg.log(Level.FINEST, "insert Exit");
                return peer.insert(word, def, logLevel);
        }
	@Override
		public synchronized void notify(Key _pred) throws Exception
        {
		lg.log(Level.FINEST, "notify entry");
                pred = _pred;
                lg.log(Level.FINER,"Notified by " + _pred.toString());
                succ = superpeer.getSuccessor(nodeid);

		// Start the notify cycle ... end the cycle with the initiator.
                PeerInterface peer = getPeer(succ);
                if ( !lock) {
                        lock = true;
                        constructFingerTable(hasher.getBitSize());
                        peer.notify(nodeid);
                        lock = false;
                        return;
                }
                else {
                        lock = false;
                        return;
                }
        }

	/**
	 * Construct a peers finger table using getSuccessor.
	 */
        public void constructFingerTable(int mbits)
        {
		lg.log(Level.FINEST, "constructFingerTable entry");
		// clear the current finger table

                ft.clear();
                try {
			// For each mbit ...
                        for (int i = 1; i <= mbits; i++) {
				// make a new finger entry 
                                FingerEntry fe = new FingerEntry();
				
				// Calculate (nodeid+2^(i-1))%max_key
                                Key fingerid = new Key().add(nodeid).add(new Key(BigInteger.valueOf((int)Math.pow(2,i-1)))).mod(new Key(BigInteger.valueOf((int)Math.pow(2,mbits))));
				// Adding a new finger entry
                                fe.setId(fingerid);
				lg.log(Level.FINER,"Peer "+nodeid+" Initiating getSuccessor on key "+fingerid);
                                fe.setNodeId(getSuccessor(fingerid));
                                ft.addFingerEntry(fe);
                        }


                }
                catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
		lg.log(Level.FINEST, "constructFingerTable exit");

        }

	
	// Begin utility functions.
	
	@Override
		public String getName() throws Exception {
                return ("Peer-" + nodeid.toString());

        }

	@Override
		public Key myPred() throws Exception {
                return pred;
        }

	@Override
		public Key mySucc() throws Exception {
                return succ;
        }

	@Override
        public String[][] getEntries() throws Exception
        {
                String[][] rv = new String[dict.size()][2];
                Iterator it = dict.entrySet().iterator();
                Map.Entry<String,String> pairs = null;
                int i = 0;
                for (Map.Entry<String, String> entry : dict.entrySet()) {
                        rv[i][0] = entry.getKey();
                        rv[i][1] = entry.getValue();
                        i++;
                }
                return rv;
        }

	@Override
		public FingerTable getFingerTable() {return ft;}


	// Main -- This is where the magic happens ;^)
        public static void main(String[] argv) {

		// Default superper
                String superpeeraddy = "localhost";

		
		ArgumentHandler cli = new ArgumentHandler
			(
	     "Peer [-h] [superpeer address]"
	     ,"Run a peer and attached to the specified superpeer. If none is provied localhost is assumed."
	     ,"Bala Subrahmanyam Kambala, Daniel William DaCosta - GPLv3 (http://www.gnu.org/copyleft/gpl.html)"
			 );
		cli.addOption("h", "help", false, "Print this usage information.");

		// parse command line
		CommandLine commandLine = cli.parse(argv);
		if (commandLine.hasOption('h')) {
			cli.usage("");
			System.exit(0);
                }
		
		if(commandLine.getArgs().length != 0) superpeeraddy  = commandLine.getArgs()[0];
                try {
                        Peer peer = new Peer(superpeeraddy);
                } catch (Exception e) {
                        System.out.println("Peer exception: " + e);
                }
        }




}
