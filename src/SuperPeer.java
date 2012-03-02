/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements the Super Peer Interface.
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.lang.Math.*;

public class SuperPeer extends UnicastRemoteObject implements
		SuperPeerInterface {
	/**
	 * Logger for SuperPeer.
	 */
	private Logger lg;

	/**
	 * We would like node ID's to be unique. This table will be used to ensure
	 * this.
	 */
	private List<FingerEntry> peertable;

	/**
	 * Random number generator for Node IDs.
	 */
	private SecureRandom prng;

	/**
	 * SHA1 Hasher.
	 */
	private HasherInterface hasher;

    /**
     * The m-bits
     */
    private int mbits;
    
    public Integer getChordKeyBitsSize()  throws RemoteException
    {
    	return mbits;
    }
    
    /**
     * This methods inserts into peertable in the increasing order key.
     */
    private void insertNewNode(FingerEntry fe)
    {
    	System.out.println("insert new node. calleld...............++++++++++==============");
    	// Search for the non-existent item
    	int index = Collections.binarySearch(peertable, fe);
    	
    	// Add the non-existent item to the list
    	if (index < 0) {
    		peertable.add(-index - 1, fe);
    	}
    }
    
    public Key getSuccessor(Key nodeid)  throws RemoteException
    {
    	System.out.println(" get successor in superpeer");
    	
    	FingerEntry fe = new FingerEntry();
    	fe.setId(nodeid);
    	
    	// Search for the non-existent item
    	int index = Collections.binarySearch(peertable, fe);
    	
    	// Add the non-existent item to the list
    	if (index > 0) {
    		return peertable.get(index - 1).getId();
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * @todo Everything
     */
    SuperPeer (int _mbits) throws RemoteException, NoSuchAlgorithmException
    {
	lg = new Logger("SuperPeer");
	mbits = _mbits;
	peertable = new ArrayList<FingerEntry>();
	prng = SecureRandom.getInstance("SHA1PRNG");
	hasher = new SHA1Hasher((int)Math.pow(2,mbits));
	lg.log(Level.FINEST,"SuperPeer started.");
    }


	/**
	 * @return A Key.
	 * @exception RemoteException
	 *                if the remote invocation fails.
	 */
	public Key join() throws RemoteException, ServerNotActiveException,
			NoSuchAlgorithmException {
		lg.log(Level.FINEST, "Join Called.");
		Key rv;
		synchronized (this) {
			// XXX: ID collisions need to be detected using peertable!
			rv = hasher.getHash(new Integer(prng.nextInt()).toString());
			insertNewNode(new FingerEntry(rv, RemoteServer.getClientHost()));
			lg.log(Level.FINEST, "Allocating Node ID " + rv.toString() + ".");
		}
		return rv;
	}

	/**
	 * @return The Hasher class
	 */
	public HasherInterface getHasher() throws RemoteException {

	    return new SHA1Hasher((int)Math.pow(2,mbits));
	}

	/**
	 * Retrieves an Address from a Key.
	 * 
	 * @return A string containing an IP String Address.
	 * @exception RemoteException
	 *                if the remote invocation fails.
	 */
	public String getAddress(Key id) throws RemoteException {
		lg.log(Level.FINER, "getAddress Called.");
		FingerEntry fe = null;
		synchronized (this) {
		    for (Integer i = 0; i < peertable.size(); i++) {
			lg.log(Level.FINEST, "getAddress - Checking " + peertable.get(i).getId().toString()+" for match ...");
			if (peertable.get(i).getId().equals(id)) {
			    lg.log(Level.FINEST, "getAddress - match found.");
			    fe = peertable.get(i);
			    return fe.getIpAddress();
			}
		    }
		}
		lg.log(Level.WARNING, "getAddress failed on " + id.toString()
		       + ", returning null!");
		return null;
	}


	/**
	 * @todo Document
	 */
    public FingerTable getInitialFingerTable(Key key) throws RemoteException,ServerNotActiveException {
		FingerTable table = new FingerTable(key, RemoteServer.getClientHost());
		if (peertable.size() == 1) {
		    table.InitFingerTable();
		}
		else {
		    synchronized(this) {
			FingerEntry fe = peertable.get(peertable.size()-1);
			table.InitFingerTable(fe);
		    }
		}
		return table;
    }




    /**
     * @todo Document
     */
    public String[][] getPeers() throws Exception
    {
	String [][] rv = new String[peertable.size()][2];
	for (int i = 0; i < peertable.size(); i++) {
	    lg.log(Level.FINEST, "getAddress - Checking " + peertable.get(i).getId().toString()+" for match ...");
	    rv[i][0] = peertable.get(i).getId().toString();
	    rv[i][1] = peertable.get(i).getIpAddress();
	}
	return rv;
    }

    @Override
    public String getNodeServiceAddress()  throws RemoteException
    {
    	if(peertable.size() != 0) {
    		int random = 0;
	    	if (peertable.size() == 1) {
	    		random = 0;
	    	}
	    	else {
	    		random = (int)(peertable.size() + (Math.random() * 37)) % peertable.size();
	    	}
	    	
	    	//Service is uniquely identified by IP address and node ID
	    	return peertable.get(random).getIpAddress() + "/" + peertable.get(random).getId();
    	}
    	else {
    		throw new RemoteException("No node is available to serve the request");
    	}
    }
    
    @Override
    public String getNodeServiceAddress(Key id)  throws RemoteException
    {
    	System.out.println("Entered in serviceaddr____ " + id.getId() + "\n\n");
    	
    	FingerEntry fe = new FingerEntry();
    	fe.setId(id);
    	int index = Collections.binarySearch(peertable, fe);
    	System.out.println(".... index... " + index + "-- peer size" + peertable.size());
    	
    	for (int i = 0; i < peertable.size(); i++) {
    		System.out.println(peertable.get(i).getId() + "=== id");
    	}
    	if (index >= 0) {
    		fe = peertable.get(index);
    		return fe.getIpAddress() + "/" + fe.getId();
    	}
    	
    	return null;
    }

    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	int mbits = 8;
	//Setup command line options
	CommandLineParser parser = new BasicParser( );
	Options options = new Options( );
	options.addOption("h", "help", false, "Print this usage information.");
	options.addOption("m", "mbits", false, "The maximum number of unique keys in terms of 2^m (Default is "+Integer.toString(mbits)+").");

	// Parse the program arguments
	CommandLine commandLine;
	try 
        {
	    commandLine = parser.parse( options, argv );
	    if( commandLine.hasOption('h') ) {
		HelpFormatter helpFormatter = new HelpFormatter( );
		helpFormatter.setWidth( 80 );
		helpFormatter.printHelp( "SuperPeer Usage:", "", options, "");
		System.exit(0);
	    }
	    if( commandLine.hasOption('m') ) {
		//XXX:uncaught exception!
		mbits = Integer.parseInt((commandLine.getOptionValue('m')));
	    }
	    try 
	    {
		Naming.rebind ("SuperPeer", new SuperPeer (mbits));
	    } catch (Exception e) 
	    {
		System.out.println ("SuperPeer failed: " + e);
	    }
	}
        catch( Exception e ) 
	{
	    System.out.println( "Improper Usage" );
	    HelpFormatter helpFormatter = new HelpFormatter( );
	    helpFormatter.setWidth( 80 );
	    helpFormatter.printHelp( "SuperPeer Usage:", "", options, "");
	    System.exit(1);
	}
    }
}
