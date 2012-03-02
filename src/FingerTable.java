/**
 * @authors  Daniel William DaCosta, Bala Subrahmanyam Kambala
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD.
 */
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.PriorityQueue;
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
	public FingerTable(Key id, String ipAddress)
	{
		myFingerEntry = new FingerEntry();
		myFingerEntry.setId(id);
		myFingerEntry.setIpAddress(ipAddress);
		table = new PriorityQueue<FingerEntry>(5,new FingerEntryComparator());
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
    			+ fe.getIpAddress() + "\t" + fe.getStartWordKey() + "\t" + fe.getEndWordKey() + "\n");
    	}
    } 
    	
    public Iterator<FingerEntry> iterator () { return table.iterator(); } 

    public int size() {return table.size();}


    public Key getClosestSuccessor(Key key) {
	Iterator<FingerEntry> it = table.iterator();
	while(it.hasNext()) {
	    FingerEntry fe = it.next();
        		
	    // If key lies in range
	    if (fe.getId().compare(key)==-1 && key.compare(fe.getId())==-1)
		return fe.getId();
	}
	if( table.size() == 0 ) return null;
	else {
	    it = table.iterator();
	    return it.next().getId();
	}
    }

}
