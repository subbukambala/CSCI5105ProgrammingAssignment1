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
 * @todo Everything..
 */
public class FingerEntry implements Serializable, Comparable<FingerEntry> {
	private Key id;
	private String ipAddress;
	private Key startWordKey;
	private Key endWordKey;

	public FingerEntry() {	}
	
	public FingerEntry(Key _id, String _ipAddress)
	{
		id = _id;
		ipAddress = _ipAddress;
	}
	
	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setStartWordKey(Key startWordKey) {
		this.startWordKey = startWordKey;
	}

	public void setEndWordKey(Key endWordKey) {
		this.endWordKey = endWordKey;
	}
	
	public Key getStartWordKey() {
		return startWordKey;
	}

	public Key getEndWordKey() {
		return endWordKey;
	}

	@Override
	public int hashCode(){
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ hash $$$$$$$$$$$$$$$$");
		return id.getId().intValue();
	}
	
	@Override
	public boolean equals(Object o){
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ equals $$$$$$$$$$$$$$$$");
		if (o instanceof FingerEntry) {
			FingerEntry fe = (FingerEntry) o;
			if (this.id.equals(fe.id)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public int compareTo(FingerEntry o) {
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ compare $$$$$$$$$$$$$$$$");
		if (id == null || o.id == null) {
			return -1;
		}
		
		if (id.equals(o.id)) {
			return 0;
		}
		else if (id.leq(o.id)) {
			
			return -1;
		} 
		else {
			return 1;
		}
	}
}