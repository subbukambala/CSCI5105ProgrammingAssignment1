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


import java.util.Collections;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.lang.Math.*;
import java.lang.Thread;
import java.util.concurrent.locks.*;

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
    private Collection<PeerInfo> peertable;

	/**
	 * Random number generator for Node IDs.
	 */
	private SecureRandom prng;

	/**
	 * SHA1 Hasher.
	 */
	private HasherInterface hasher;

    private boolean lock;
    

    /**
     * The m-bits
     */
    private int mbits;
    
    
    /**
     * @todo Everything
     */
    SuperPeer (int _mbits) throws RemoteException, NoSuchAlgorithmException
    {
	lock = false;
	lg = new Logger("SuperPeer");
	mbits = _mbits;
	peertable = new TreeSet<PeerInfo>(new PeerInfoComparator());
	prng = SecureRandom.getInstance("SHA1PRNG");
	hasher = new SHA1Hasher(mbits);
	lg.log(Level.FINEST,"SuperPeer started.");
    }

    
    public void lock() throws Exception{
	lg.log(Level.FINEST,"SuperPeer lock engaged.");
	while(lock) {Thread.sleep(1000);}
	lock = true;
    }


    public synchronized void unlock() throws Exception{
	lock = false;
	lg.log(Level.FINEST,"SuperPeer lock disengaged.");
    }
    public synchronized Key getSuccessor(Key nodeid)  throws Exception
    {	
	lg.log(Level.FINER,"getSuccessor Entry.");
    	Iterator<PeerInfo> it = peertable.iterator();
    	PeerInfo fe =  null;

    	while(it.hasNext()) {
    		fe = it.next();
    		if(fe.getNodeId().compare(nodeid)>0) break;
		fe = null;
    	}
	if(fe == null) {
	    lg.log(Level.FINER,"getSuccessor - still null.");
	    it = peertable.iterator();
	    if(it.hasNext()) {
    		fe = it.next();
		if(fe.getNodeId().compare(nodeid)==0) fe = null;
	    }
    	}
	Key rv = null;
	if(fe!=null) rv = fe.getNodeId();
	lg.log(Level.FINER,"getSuccessor Exit.");
    	return rv;
    	
    }

    /**
     * @return A Key.
     * @exception RemoteException
     *                if the remote invocation fails.
     */
    public synchronized Key join() throws Exception {
	lg.log(Level.FINER,"join Entry.");
	Key rv;
	// XXX: ID collisions need to be detected using peertable!
	rv = hasher.getHash(new Integer(prng.nextInt()).toString());
	peertable.add(new PeerInfo(rv, RemoteServer.getClientHost()));
	lg.log(Level.FINEST, "Allocating Node ID " + rv.toString() + ".");
	lg.log(Level.FINER,"join Exit.");
	return rv;
    }

    /**
     * @return The Hasher class
     */
    public synchronized HasherInterface getHasher() throws Exception {
	lg.log(Level.FINER,"getHasher Entry.");
	lg.log(Level.FINER,"getHasher Exit.");
	return new SHA1Hasher(mbits);
	}

    /**
     * Retrieves an Address from a Key.
     * 
     * @return A string containing an IP String Address.
     * @exception RemoteException
     *                if the remote invocation fails.
     */
    public synchronized String getAddress(Key id) throws Exception {
	lg.log(Level.FINER, "getAddress Entry.");
	PeerInfo fe = null;
	Iterator<PeerInfo> it = peertable.iterator();
	while(it.hasNext()) {
	    fe = it.next();
	    lg.log(Level.FINEST, "getAddress - Checking " + fe.getNodeId().toString()+" for match ...");
	    if (fe.getNodeId().equals(id)) {
		lg.log(Level.FINEST, "getAddress - match found.");
		lg.log(Level.FINER, "getAddress Exit.");
		return fe.getIP();
	    }
	}
    
	lg.log(Level.WARNING, "getAddress failed on " + id.toString()
	       + ", returning null!");
	lg.log(Level.FINER, "getAddress Entry.");
	return null;
    }

    /**
     * @todo Document
     */
    public synchronized String[][] getPeers() throws Exception
    {
	String [][] rv = new String[peertable.size()][2];
	PeerInfo fe = null;
	Iterator<PeerInfo> it = peertable.iterator();
	int i = 0;
	while(it.hasNext()) {
	    fe = it.next();
	    rv[i][0] = fe.getNodeId().toString();
	    rv[i][1] = fe.getIP();

	    i++;
	}
	return rv;
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
