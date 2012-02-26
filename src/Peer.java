import java.rmi.*;
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
 * @descriptrion Implements Peer Interface
 */

/**
 * @todo Everything
 */
public class Peer implements PeerInterface
{
    /**
     * Logger for Peer.
     */
    private Logger lg;

    /**
     * This peer's NodeID.
     */
    NodeID nodeid;

    /**
     * @todo Everything
     */
    public Peer (String sp) throws Exception
    {
	
	SuperPeerInterface superpeer = 
	    (SuperPeerInterface) Naming.lookup ("//"+sp+"/SuperPeer");
	nodeid = superpeer.join();
	lg = new Logger("Peer:"+nodeid.toString());
        lg.log(Level.FINEST,"Peer started.");
	superpeer.getAddress(nodeid);
	superpeer.getPeers();
    }

    


    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {

	String superpeeraddy = "localhost";

	//Setup command line options
	CommandLineParser parser = new BasicParser( );
	Options options = new Options( );
	options.addOption("h", "help", false, "Print this usage information.");
	options.addOption("P", "superpeer", false, "Address of SuperPeer.");

	// Parse the program arguments
	CommandLine commandLine;
	try 
        {
	    commandLine = parser.parse( options, argv );
	    if( commandLine.hasOption('h') ) {
		HelpFormatter helpFormatter = new HelpFormatter( );
		helpFormatter.setWidth( 80 );
		helpFormatter.printHelp( "Peer Usage:", "", options, "");
		System.exit(0);
	    }
	    if( commandLine.hasOption('P') ) {
		//XXX:uncaught exception!
		superpeeraddy = commandLine.getOptionValue('s');
	    }
	}
        catch( Exception e ) 
	{
	    System.out.println( "Improper Usage" );
	    HelpFormatter helpFormatter = new HelpFormatter( );
	    helpFormatter.setWidth( 80 );
	    helpFormatter.printHelp( "Peer Usage:", "", options, "");
	    System.exit(1);
	}


	try 
	{
	    Peer peer = new Peer(superpeeraddy);
	} 
	catch (Exception e) 
	{
	    System.out.println ("Peer exception: " + e);
	}
    }


}
