/**
 * @authors  Daniel William DaCosta, Bala Subrahmanyam Kambala
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD.
 */
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * @todo Everything
 */
public class FingerTable implements Serializable
{
	
	private static final Logger LOG = new Logger("FingerTable");
	
	FingerEntry myFingerEntry;
	

	public Collection<FingerEntry> table;
	
	/**
	 * Initializes with current node details.
	 * 
	 * @param id
	 * @param ipAddress
	 */
	public FingerTable(Key id, Key nodeId)
	{
		myFingerEntry = new FingerEntry();
		myFingerEntry.setId(id);
		myFingerEntry.setNodeId(nodeId);
		table = new TreeSet<FingerEntry>(new FingerEntryComparator());
	}
    /**
	 * Constructs init finger table with previously registered node details.
	 * 
	 * @param fe
	 */
    public void addFingerEntry(FingerEntry fe) 
    {
    	table.add(fe);
    }
        
    /**
     * Prints finger table
     */
    public void PrintFingerTable() 
    {
    	Iterator<FingerEntry> it = table.iterator();
    	while(it.hasNext()) {
    		FingerEntry fe = it.next();
    		LOG.log(Level.INFO, fe.getId() + "\t" 
    			+ fe.getNodeId() + "\n");
    	}
    } 
    	
    public Iterator<FingerEntry> iterator () { return table.iterator(); } 


    public void clear () {table.clear();}

    public int size() {return table.size();}


	public Key getClosestSuccessor(Key key) {
		Iterator<FingerEntry> it = table.iterator();
		FingerEntry fe = null;
		while (it.hasNext()) {
			fe = it.next();
			LOG.log(Level.FINER,"STUFFS " + fe.getNodeId() + " " + fe.getId() + " " +key.toString());

			// If fingerKey greater than key... return 
			if (fe.getId().compare(key)>=0) {
				return fe.getNodeId();
			}
		}

		if (table.size() == 0) {
			return null;
		}
		else {
			it = table.iterator();
			fe = it.next();
			return fe.getNodeId();
		}
	}

}
