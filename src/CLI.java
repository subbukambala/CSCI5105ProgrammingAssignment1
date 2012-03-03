/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Command line interface to this DHT system.
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
 * A command line interface into our DHT.
 */
public class CLI {


	// logger
        private static final Logger LOG = new Logger("CLI");


        /**
         * Pick the lowest peer with a certain IP.
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
         * Yep!
         */
        public static void main (String[] argv)
        {
		// peer ip
                String pIP = null;
		// peer id
                String pID = null;

		// verbose
		boolean verbose = false;

                ArgumentHandler cli = new ArgumentHandler
                        (
                         "CLI [-hLd] [-l word] [-f file] [superpeer address]  [-P peer address] [-p peer ID])"
                         ,"CLI provides remote access to peers and superpeers within this CHORD implementation."
                         ,"Bala Subrahmanyam Kambala, Daniel William DaCosta - GPLv3 (http://www.gnu.org/copyleft/gpl.html)"
                         );
		
                cli.addOption("v", "verbose", false, "When set insert and lookup will output log messages.");
                cli.addOption("h", "help", false, "Print this usage information.");
                cli.addOption("L", "listpeers", false, "Calls the RMI getPeer on the provided host. It will list all known peers.");
                cli.addOption("l", "lookup", true, "Lookup a word from a particular peer.");
                cli.addOption("f", "filename", true, "Load all definitions from a file from a particular peer.");
                cli.addOption("P", "peerIP", true, "Specify the peer IP address. If none is provided, one will be choosen if required by the directive.");
                cli.addOption("p", "peerId", true, "Specify the peer Id address.");
                cli.addOption("F", "fingertable", false, "Output the finger table, predecessor and success of a particular peer. Peer can be specified with the -P and -p options or will the first node will be chosen.");
                cli.addOption("d", "data", false, "Output the data for a particular peer. Peer can be specified with the -P and -p options or will the first node will be chosen.");

                CommandLine commandLine = cli.parse(argv);

		try {
			SuperPeerInterface superpeer = (SuperPeerInterface) Naming
				.lookup("//" + commandLine.getArgs()[0] + "/SuperPeer");
		} catch(Exception e) {
			cli.usage("\nNo Superpeer specified, no SuperPeer runnning, or bad SuperPeer address.\n");
                        System.exit(1);
		}

		// Help option
                if( commandLine.hasOption('h') ) {
                        cli.usage("");
                        System.exit(0);
                }


		// Peer ip specified
                if (commandLine.hasOption('P')) {
                        String ip = commandLine.getOptionValue('P');
                        if (ip == null) {
                                cli.usage("\nNo peer IP provided!\n");
                                System.exit(1);
                        }
                        pIP = ip;
                }

		// verbosity
                if (commandLine.hasOption('v')) {
			verbose=true;
                }


		// Peer id specified
                if (commandLine.hasOption('p')) {
                        String id = commandLine.getOptionValue('p');
                        if (id == null) {
                                cli.usage("\nNo peer ID provided!\n");
                                System.exit(1);
                        }
                        if( pIP == null ) {
                                cli.usage("\nID provided without IP!\n");
                                System.exit(1);
                        }
                        pID = id;
                }

		// List all peers
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
				Level lv = Level.FINE;
				if(verbose) lv = Level.INFO;
                                String meaning = peer.lookup(word,lv);
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
		// Get the finger table of a peer
                if (commandLine.hasOption('F')) {
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
                                String pred = peer.myPred().toString();
                                String succ = peer.mySucc().toString();

                                System.out.println("Predecessor:"+pred);
                                System.out.println("Successor:"+succ);

                                FingerTable ft = peer.getFingerTable();
                                String[][] rv = ft.getTable();
                                for(int i = 0 ; i < rv.length ; i++ ) {
                                        System.out.println(Integer.toString(i)+") "+rv[i][0]+" = "+rv[i][1]);
                                }

                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        System.exit(0);
                }

		// Get data from a peer
                if (commandLine.hasOption('d')) {
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

                                String[][] rv = peer.getEntries();
                                for(int i = 0 ; i < rv.length ; i++ ) {
                                        System.out.println(Integer.toString(i)+") "+rv[i][0]+" = "+rv[i][1]);
                                }

                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        System.exit(0);
                }

		// Read data from a file and insert it into DHT.
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

                                LOG.log(Level.FINEST, "//"+pIP+"/"+pID);
                                PeerInterface peer = (PeerInterface) Naming.lookup("//"+pIP+"/"+pID);
                                Iterator it = dict.entrySet().iterator();
                                while (it.hasNext()) {
                                        Map.Entry pairs = (Map.Entry) it.next();
                                        System.out.println(pairs.getKey() + " = "
                                                           + pairs.getValue());

					Level lv = Level.FINE;
					if(verbose) lv = Level.INFO;
                                        peer.insert(pairs.getKey().toString(), pairs.getValue()
                                                    .toString(), lv);
                                }
                                System.exit(0);

                        } catch (FileNotFoundException fne) {

                                fne.printStackTrace();
                                cli.usage("\nError opening file!\n");
                                System.exit(1);
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