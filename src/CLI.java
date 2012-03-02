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
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

    private static final Logger LOG = new Logger("CLI");
	

    /**
     * @todo Everything
     */
    public static String[] pickPeer(String[][] table, String pip)
    {
	for(int i = 0; i< table.length; i++){
	    if(pip != null && table[i][1] == pip) return table[i];
	    else return table[i];
	}
	return null;
    }

    /**
     * @todo Everything
     */
    public static void main (String[] argv) 
    {
	String pIP = "";
	String pID = "";
	ArgumentHandler cli = new ArgumentHandler
	    (
	     "CLI [-hL] [-l word] [-f file] [superpeer address]  [-P peer address] [-p peer ID])"
	     ,"CLI provides remote access to peers and superpeers within this CHORD implementation."
	     ,"Bala Subrahmanyam Kambala, Daniel William DaCosta - GPLv3 (http://www.gnu.org/copyleft/gpl.html)"
	     );
	cli.addOption("h", "help", false, "Print this usage information.");
	cli.addOption("L", "listpeers", false, "Calls the RMI getPeer on the provided host. It will list all known peers.");
	cli.addOption("l", "lookup", true, "Lookup a word from a particular peer.");
	cli.addOption("f", "filename", true, "Load all definitions from a file from a particular peer.");
	cli.addOption("P", "peerIP", true, "Specify the peer IP address. If none is provided, one will be choosen if required by the directive.");
	
	CommandLine commandLine = cli.parse(argv);
	if( commandLine.hasOption('h') ) {
	    cli.usage("");
	    System.exit(0);
	}


	if (commandLine.hasOption('P')) {
	    String ip = commandLine.getOptionValue('P');
	    if (ip == null) {
		cli.usage("\nNo peer IP provided!\n");
		System.exit(1);
	    }
	    pIP = ip;
	}

	if (commandLine.hasOption('p')) {
	    String id = commandLine.getOptionValue('p');
	    if (id == null) {
		cli.usage("\nNo peer IP provided!\n");
		System.exit(1);
	    }
	    if( pIP == null ) {
		cli.usage("\nID provided without IP!\n");
		System.exit(1);
	    }
	    pID = id;
	}

	if( commandLine.hasOption('L') ) {
	    if( commandLine.getArgs().length == 0) {
		cli.usage("\nNo superpeer provided!\n");
		System.exit(1);
	    }
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
	// call lookup RMI service to find a meaning of word.
	if (commandLine.hasOption('l')) {
	    String word = commandLine.getOptionValue('l');
	    if (word == null) {
		cli.usage("\nNo word provided!\n");
		System.exit(1);
	    }		
	    SuperPeerInterface superpeer;
	    try {
		superpeer = (SuperPeerInterface) Naming
		    .lookup("//" + commandLine.getArgs()[0] + "/SuperPeer");
		if(pID==null) {
		    String[] rv = pickPeer(superpeer.getPeers(),pIP);
		    pIP = rv[1];
		    pID = rv[0];
		}
		PeerInterface peer = (PeerInterface) Naming.lookup("//"+pIP+"/"+pID);	
		String meaning = peer.lookup(word, Level.INFO);
		System.out.println(meaning);
 		
	    } catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (NotBoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
 			}  catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    	
	    System.exit(0);
	}
	if (commandLine.hasOption('f')) {
	    try {
		String filePath = commandLine.getOptionValue('f');
		if (filePath == null) {
		    cli.usage("\nNo file provided!\n");
		    System.exit(1);
		}
		
		LOG.log(Level.FINEST, "File path: " + filePath);
		Map<String, String> dict = null;
		
		dict = CLI.readFile(filePath);
		
		SuperPeerInterface superpeer = (SuperPeerInterface) Naming
		    .lookup("//" + commandLine.getArgs()[0] + "/SuperPeer");

		if(pID==null) {
		    String[] rv = pickPeer(superpeer.getPeers(),pIP);
		    pIP = rv[1];
		    pID = rv[0];
		}
		PeerInterface peer = (PeerInterface) Naming.lookup("//"+pIP+"/"+pID);	
		Iterator it = dict.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pairs = (Map.Entry) it.next();
		    System.out.println(pairs.getKey() + " = "
				       + pairs.getValue());

		    // XXX:: logging option should be provided by user
		    peer.insert(pairs.getKey().toString(), pairs.getValue()
				.toString(), Level.INFO);
		}
		System.exit(0);

	    } catch (FileNotFoundException fne) {

		fne.printStackTrace();
				cli.usage("\nError opening file!\n");
				System.exit(1);
				
	    } catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (NotBoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (Exception e) {
		
		e.printStackTrace();
		cli.usage("\nError in input file processing!\n");
		System.exit(1);
	    }
	}
	
	cli.usage("\nNo directive provided!\n");
	System.exit(1);
	
	
    }

	public static Map<String, String> readFile(String fileName)
			throws Exception {

		Map<String, String> dict = new HashMap<String, String>();
		// Open the file that is the first
		// command line parameter
		FileInputStream fstream = new FileInputStream(fileName);
		
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			
			// Assuming word is always separated with :
			if (strLine.contains(":")) {
				String words[] = strLine.split(":");
				dict.put(words[0].trim(), words[1].trim());
			}
		}
		// Close the input stream
		in.close();
		return dict;
	}

}