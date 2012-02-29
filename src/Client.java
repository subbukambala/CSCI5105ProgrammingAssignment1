import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * @authors Daniel William DaCosta, Bala Subrahmanyam Kambala
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD.
 */
public class Client {

	private Map<String, String> dict = new HashMap<String, String> (); 
	
	public static void main(String[] argv) {

		//Setup command line options
		CommandLineParser parser = new BasicParser( );
		Options options = new Options( );
		options.addOption("h", "help", false, "Print this usage information.");
		options.addOption("l", "lookup", false, "input a word to find a meaning");
		options.addOption("f", "filename", false, "path of input file");
		
		// Parse the program arguments
		CommandLine commandLine;
		try 
	        {
			for (int i = 0; i < argv.length; i++) {
				System.out.println(argv[i]);
				
			}
			
		    commandLine = parser.parse( options, argv );
		    
		    if( commandLine.hasOption('h') ) {
		    	HelpFormatter helpFormatter = new HelpFormatter( );
		    	helpFormatter.setWidth( 80 );
		    	helpFormatter.printHelp( "Client Usage:", "", options, "");
		    	System.exit(0);
		    }
		    if( commandLine.hasOption('l') ) {
		    	
		    	String word = commandLine.getOptionValue( "lookup" );
		    	// call lookup RMI service to find a meaning of word.
		    	System.out.println(word + "...word");
			
		    }
		    
		    if( commandLine.hasOption('f') ) {
		    	
		    	String filePath = commandLine.getOptionValue( 'f' );
		    	// XXX:: call lookup RMI service to find a meaning of a word.
			
		    	System.out.println(filePath + "... file path");
		    	Client cli = new Client();
				cli.readFile(filePath);
				
				Iterator it = cli.dict.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        System.out.println(pairs.getKey() + " = " + pairs.getValue());
			        // XXX::  call insert RMI service to insert a meaning of a word
			    }
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
	
	public void readFile(String fileName) {
		try {
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
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

}
