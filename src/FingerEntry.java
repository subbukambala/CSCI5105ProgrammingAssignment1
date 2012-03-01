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
	private Integer startWordKey;
	private Integer endWordKey;

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

	public Integer getStartWordKey() {
		return startWordKey;
	}

	public void setStartWordKey(Integer startWordKey) {
		this.startWordKey = startWordKey;
	}

	public Integer getEndWordKey() {
		return endWordKey;
	}

	public void setEndWordKey(Integer endWordKey) {
		this.endWordKey = endWordKey;
	}

}