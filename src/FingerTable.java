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
    public void addFingerEntry(FingerEntry fe) throws Exception
    {
	if(fe.getNodeId() == null ) throw new Exception("foo");
    	table.add(fe);
    }
        
    /**
     * Prints finger table
     */
    public String[][] getTable() 
    {
    	Iterator<FingerEntry> it = table.iterator();
	String[][] rv = new String[table.size()][2];
	int i = 0;
    	while(it.hasNext()) {
	    FingerEntry fe = it.next();
	    rv[i][0] = fe.getId().toString();
	    rv[i][1] = fe.getNodeId().toString();
	    i++;
    	}
	return rv;
    } 
    	
    public Iterator<FingerEntry> iterator () { return table.iterator(); } 


    public void clear () {table.clear();}

    public int size() {return table.size();}


	public Key getClosestSuccessor(Key key) {
		Iterator<FingerEntry> it = table.iterator();
		Iterator<FingerEntry> it2 = table.iterator();
		FingerEntry fe = null;
		FingerEntry pfe = it2.next();
		while (it.hasNext()) {
			fe = it.next();

			// If fingerKey greater than key... return 
			if (fe.getId().compare(key)>=0) {
				return pfe.getNodeId();
			}
			pfe = fe;
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
