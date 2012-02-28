/**
 * @authors  Daniel William DaCosta, Bala Subrahmanyam Kambala
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD.
 */
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @todo Everything
 */
public class FingerTable implements Serializable
{
	
	
	FingerEntry myFingerEntry;
	
	public List<FingerEntry> table;
	
	/**
	 * Initializes with current node details.
	 * 
	 * @param id
	 * @param ipAddress
	 */
	public FingerTable(Key id, String ipAddress)
	{
		myFingerEntry.setId(id);
		myFingerEntry.setIpAddress(ipAddress);
	}
	
	/**
	 * Constructs init finger table with previously registered node details.
	 * 
	 * @param id
	 * @param ipAddress
	 */
    public void InitFingerTable(Key id, String ipAddress) 
    {
    	table = new ArrayList<FingerEntry> ();
    	FingerEntry entry = new FingerEntry();
    	entry.setId(id);
    	entry.setIpAddress(ipAddress);
    } 
    
    /**
	 * Constructs init finger table with previously registered node details.
	 * 
	 * @param fe
	 */
    public void InitFingerTable(FingerEntry fe) 
    {
    	table = new ArrayList<FingerEntry> ();
    	table.add(fe);
    }
    
    /**
	 * Constructs init finger table with previously registered node details.
	 * 
	 * @param fe
	 */
    public void AddFingerEntry(FingerEntry fe) 
    {
    	table.add(fe);
    }
    
    /**
     * Creates empty table in case of no node has been registered.
     */
    public void InitFingerTable() 
    {
    	table = new ArrayList<FingerEntry> ();
    } 
}
