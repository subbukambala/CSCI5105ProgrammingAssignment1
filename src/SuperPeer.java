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
        private TreeSet<PeerInfo> peertable;

        /**
         * Random number generator for Node IDs.
         */
        private SecureRandom prng;

        /**
         * SHA1 Hasher.
         */
        private HasherInterface hasher;

	/**
	 * Used to lock the critical section on peer startup.
	 */
        private boolean lock;

        /**
         * The m-bits
         */
        private int mbits;


        /**
         * SuperPeer Constructor. It takes the number of bits in the key.
         */
        SuperPeer (int _mbits) throws RemoteException, NoSuchAlgorithmException
        {
                lock = false;
                lg = new Logger("SuperPeer");
                mbits = _mbits;
                peertable = new TreeSet<PeerInfo>(new PeerInfoComparator());
                prng = SecureRandom.getInstance("SHA1PRNG");
                hasher = new SHA1Hasher(mbits);
                lg.log(Level.FINER,"SuperPeer started.");
        }


	@Override
		public void lock() throws Exception{
                lg.log(Level.FINEST,"SuperPeer lock engaged.");
                while(lock) {Thread.sleep(1000);}
                lock = true;
        }

	@Override
		public synchronized void unlock() throws Exception{
                lock = false;
                lg.log(Level.FINEST,"SuperPeer lock disengaged.");
        }

	@Override
		public synchronized Key getSuccessor(Key nodeid)  throws Exception
        {
                lg.log(Level.FINEST,"getSuccessor Entry.");
                Iterator<PeerInfo> it = peertable.iterator();
                PeerInfo fe =  null;

		// Find the first peer key greater than the provided one.
                while(it.hasNext()) {
                        fe = it.next();
                        if(fe.getNodeId().compare(nodeid)>0) break;
                        fe = null;
                }

		// This means that either the peertable is empty or
		// nodeid provided is higher than all other peers.
                if(fe == null) {
                        lg.log(Level.FINER,"getSuccessor - Highest nodeid or peertable empty try the first entry.");
                        it = peertable.iterator();
                        if(it.hasNext()) {
                                fe = it.next();
                                if(fe.getNodeId().compare(nodeid)==0) fe = null;
                        }
                }
                Key rv = null;
                if(fe!=null) rv = fe.getNodeId();
		else lg.log(Level.FINER,"getSuccessor - No successor found, table must be empty..");
                lg.log(Level.FINEST,"getSuccessor Exit.");
                return rv;
        }

	@Override
		public synchronized Key getPredecessor(Key nodeid)  throws Exception
        {
                lg.log(Level.FINEST,"getPredecessor Entry.");
                Iterator<PeerInfo> it = peertable.iterator();
                Iterator<PeerInfo> it2 = peertable.iterator();
                PeerInfo fe =  null;
                PeerInfo pfe =  it2.next();

                while(it.hasNext()) {
                        fe = it.next();
                        if(fe.getNodeId().compare(nodeid)>=0) break;
                        pfe = fe;
                        fe = null;
                }
                Key rv = pfe.getNodeId();
                lg.log(Level.FINEST,"getPredecessor Exit.");
                if(pfe.getNodeId().equals(nodeid)) return peertable.last().getNodeId();
                else return rv;
        }


	@Override
		public synchronized Key join() throws Exception {
                lg.log(Level.FINEST,"join Entry.");
                Key rv;
		String ip = RemoteServer.getClientHost();
                // XXX: ID collisions need to be detected using peertable!
		rv = hasher.getHash(new Integer(prng.nextInt()).toString());
		PeerInfo pi = new PeerInfo(rv,ip);
		while (peertable.contains(pi)) { 
			rv = hasher.getHash(new Integer(prng.nextInt()).toString());
			pi = new PeerInfo(rv,ip);
			lg.log(Level.WARNING, " Random Peer Key Collision, is your key space big enough?");
		}
                peertable.add(pi);
                lg.log(Level.INFO, "!!!! Allocating Node ID " + rv + " to client at "+ip+" !!!!");
                lg.log(Level.FINEST,"join Exit.");
                return rv;
        }


	@Override
		public synchronized HasherInterface getHasher() throws Exception {
                lg.log(Level.FINEST,"getHasher Entry.");
                lg.log(Level.FINEST,"getHasher Exit.");
                return new SHA1Hasher(mbits);
        }

	@Override
		public synchronized String getAddress(Key id) throws Exception {
                lg.log(Level.FINEST, "getAddress Entry.");
                PeerInfo fe = null;
                Iterator<PeerInfo> it = peertable.iterator();
                while(it.hasNext()) {
                        fe = it.next();
                        lg.log(Level.FINER, "getAddress - Checking " + fe.getNodeId().toString()+" for match ...");
                        if (fe.getNodeId().equals(id)) {
                                lg.log(Level.FINER, "getAddress - match found.");
                                lg.log(Level.FINEST, "getAddress Exit.");
                                return fe.getIP();
                        }
                }

                lg.log(Level.WARNING, "getAddress failed on " + id.toString()
                       + ", returning null!");
                lg.log(Level.FINEST, "getAddress Entry.");
                return null;
        }

	@Override
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
         * The good stuff.
         */
        public static void main (String[] argv)
        {
                int mbits = 5;
		ArgumentHandler cli = new ArgumentHandler
			(
			 "SuperPeer [-h]"
			 ,"Run a DHT SuperPeer and attached to the specified superpeer."
			 ,"Bala Subrahmanyam Kambala, Daniel William DaCosta - GPLv3 (http://www.gnu.org/copyleft/gpl.html)"
			 );
		cli.addOption("h", "help", false, "Print this usage information.");
                cli.addOption("m", "mbits", true, "The maximum number of unique keys in terms of 2^m (Default is "+Integer.toString(mbits)+").");

		CommandLine commandLine = cli.parse(argv);
		if( commandLine.hasOption('h') ) {
			cli.usage("");
			System.exit(0);
                }
		if( commandLine.hasOption('m') ) {
			//XXX:uncaught exception!
			mbits = Integer.parseInt((commandLine.getOptionValue('m')));
		}
		try{
			Naming.rebind ("SuperPeer", new SuperPeer (mbits));
		} catch (Exception e) {
			System.out.println ("SuperPeer failed: " + e);
		}
	}
}
