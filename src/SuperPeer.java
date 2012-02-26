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

/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements the Super Peer Interface.
 */

public class SuperPeer extends UnicastRemoteObject implements SuperPeerInterface 
{
    /**
     * Logger for SuperPeer.
     */
    private Logger lg;

    /**
     * Store the next NodeID seed.
     */
    private int nextnodeidseed;


    /**
     * The size of per node finger table.
     */
    private int fsize;

    /**
     * @todo Everything
     */
    SuperPeer (int _fsize) throws RemoteException
    {
	lg = new Logger("SuperPeer");
	nextnodeidseed = 0;
	fsize = _fsize;
	lg.log(Level.FINEST,"SuperPeer started.");
    }

    /**
     * @return A NodeID.
     * @exception RemoteException if the remote invocation fails.
     */
    public NodeID join() throws RemoteException
    {
	lg.log(Level.FINEST,"Join Called.");
	NodeID rv;
	synchronized(this)
	{
	    nextnodeidseed++;
	    rv = new NodeID(nextnodeidseed);
	    lg.log(Level.FINEST,"Allocating Node ID "+Integer.toString(nextnodeidseed)+".");
	}	
	return rv;
    }

    /** 
     * @return The size of the finger table.
     * @exception RemoteException if the remote invocation fails.
     */
    public int getFingerTableSize() throws RemoteException
    {
	return fsize;
    }

    /**
     * Retrieves an Address from a NodeID.
     * @return An Address.
     * @exception RemoteException if the remote invocation fails.
     */
    public Address getAddress(NodeID id) throws RemoteException
    {
	lg.log(Level.FINER,"getAddress Called.");
	return new IPPortAddress();
    }
    /**
     * Retrieves all known nodes.
     * @todo This class needs to returns some mapping between NodeID's and Addresses.
     * @return ???
     * @exception RemoteException if the remote invocation fails.
     */
    public void getPeers() throws RemoteException
    {
	lg.log(Level.FINER,"getPeers Called.");
	return;
    }

    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	int fsize = 5;
	//Setup command line options
	CommandLineParser parser = new BasicParser( );
	Options options = new Options( );
	options.addOption("h", "help", false, "Print this usage information.");
	options.addOption("s", "fingertablesize", false, "The size of each peers finger table.");

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
	    if( commandLine.hasOption('s') ) {
		//XXX:uncaught exception!
		fsize = Integer.parseInt((commandLine.getOptionValue('s')));
	    }
	    try 
	    {
		Naming.rebind ("SuperPeer", new SuperPeer (fsize));
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
