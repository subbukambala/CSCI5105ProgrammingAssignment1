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
public class FingerEntry implements Serializable{
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
}