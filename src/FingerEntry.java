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
public class FingerEntry implements Serializable {
	private Key id;
	private Key nodeId;
	
	public FingerEntry() {	}
	
	public FingerEntry(Key _id, Key _nodeId)
	{
		id = _id;
		nodeId = _nodeId;
	}
	
	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public Key getNodeId() {
		return nodeId;
	}

	public void setNodeId(Key nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public int hashCode(){
		return id.getId().intValue();
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof FingerEntry) {
			FingerEntry fe = (FingerEntry) o;
			if (this.id.equals(fe.id)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public int compare(FingerEntry b) {
		return id.compare(b.id);
    }
}