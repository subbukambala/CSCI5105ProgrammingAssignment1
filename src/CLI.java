/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Implements the Super Peer Interface.
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	ArgumentHandler cli = new ArgumentHandler
	    (
	     "CLI [-hL] [-l word] [-f file] peer/super"
	     ,"CLI provides remote access to peers and superpeers within this CHORD implementation."
	     ,"Bala Subrahmanyam Kambala, Daniel William DaCosta - GPLv3 (http://www.gnu.org/copyleft/gpl.html)"
	     );
	cli.addOption("h", "help", false, "Print this usage information.");
	cli.addOption("L", "listpeers", false, "Calls the RMI getPeer on the provided host. It will list all known peers.");
	cli.addOption("l", "lookup", true, "Lookup a word from a particular peer.");
	cli.addOption("f", "filename", true, "Load all definitions from a file from a particular peer.");

	CommandLine commandLine = cli.parse(argv);
	if( commandLine.hasOption('h') ) {
	    cli.usage("");
	    System.exit(0);
	}

	if( commandLine.getArgs().length == 0) {
	    cli.usage("\nNo peer provided!\n");
	    System.exit(1);
	}

	if( commandLine.hasOption('L') ) {
	    try {   
		// Find the SuperPeer
		SuperPeerInterface superpeer = (SuperPeerInterface) Naming.lookup("//" + commandLine.getArgs()[0] + "/SuperPeer");
		String[][] rv = superpeer.getPeers();
		for(int i = 0 ; i < rv.length ; i++ ) {
		    System.out.println(Integer.toString(i)+") "+rv[i][0]+" = "+rv[i][1]);
		}
	    } catch (Exception e) {
		System.out.println ("SuperPeer failed: " + e);
		System.exit(1);
	    }
	    System.exit(0);
	}
	if( commandLine.hasOption('l') ) {
	    String word = commandLine.getOptionValue('l');
	    if ( word == null ) {
		cli.usage("\nNo word provided!\n");
		System.exit(1);
	    }
	    // call lookup RMI service to find a meaning of word.
	    System.out.println(word + "...word");
	    System.exit(0);
	}
	if( commandLine.hasOption('f') ) {
	    String filePath = commandLine.getOptionValue( 'f' );
	    if (filePath == null ) {
		cli.usage("\nNo file provided!\n");
		System.exit(1);
	    }
		
	    // XXX:: call lookup RMI service to find a meaning of a word.
	    System.out.println(filePath + "... file path");
	    Map<String, String> dict = null;
	    try {
		dict = CLI.readFile(filePath);
	    } catch (Exception e) {
		cli.usage("\nError opening file!\n");
		System.exit(1);
	    }

	    Iterator it = dict.entrySet().iterator();
	    while (it.hasNext()) {
		Map.Entry pairs = (Map.Entry)it.next();
		System.out.println(pairs.getKey() + " = " + pairs.getValue());
		// XXX::  call insert RMI service to insert a meaning of a word
	    }
	    System.exit(0);
	}
	
	cli.usage("\nNo directive provided!\n");
	System.exit(1);
	
	
    }

    public static Map<String, String> readFile(String fileName) throws Exception {

	Map<String, String> dict = new HashMap<String, String> (); 
	// Open the file that is the first
	// command line parameter
	FileInputStream fstream = new FileInputStream(fileName);
	// Get the object of DataInputStream
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String strLine;
	// Read File Line By Line
	while ((strLine = br.readLine()) != null) {
	    // Print the content on the console
	    String words[] = strLine.split(":");
	    dict.put(words[0].trim(), words[1].trim());
	    System.out.println(strLine);
	}
	// Close the input stream
	in.close();
	return dict;
    }

}