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

/**
 * @todo A command line interface into our DHT.
 */
public class CLI {

   
    public CLI () {}
    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	//Setup command line options
	CommandLineParser parser = new BasicParser( );
	Options options = new Options( );
	options.addOption("h", "help", false, "Print this usage information.");
	options.addOption("l", "listpeers", false, "List peers");

	// Parse the program arguments
	CommandLine commandLine;
	try 
        {
	    commandLine = parser.parse( options, argv );
	    if( commandLine.hasOption('h') ) {
		HelpFormatter helpFormatter = new HelpFormatter( );
		helpFormatter.setWidth( 80 );
		helpFormatter.printHelp( "CLI Usage:", "", options, "");
		System.exit(0);
	    }
	    if( commandLine.hasOption('l') ) {
		try 
		{
		    
		    // Find the SuperPeer
		    SuperPeerInterface superpeer = (SuperPeerInterface) Naming.lookup("//" + commandLine.getArgs()[0] + "/SuperPeer");
		    String[][] rv = superpeer.getPeers();
		    for(int i = 0 ; i < rv.length ; i++ )
		    {
			System.out.println(Integer.toString(i)+") "+rv[i][0]+" = "+rv[i][1]);
		    }
		} catch (Exception e) 
		{
		    System.out.println ("SuperPeer failed: " + e);
		}
	    }
	    else
	    {
		System.out.println( "Improper Usage" );
		HelpFormatter helpFormatter = new HelpFormatter( );
		helpFormatter.setWidth( 80 );
		helpFormatter.printHelp( "SuperPeer Usage:", "", options, "");
		System.exit(1);
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