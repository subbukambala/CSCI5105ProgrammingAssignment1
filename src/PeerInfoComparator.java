/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion TBD
 */
import java.util.Comparator;


public class PeerInfoComparator implements Comparator<PeerInfo> 
{

	public PeerInfoComparator() {
	}

	public int compare(PeerInfo a, PeerInfo b) {
		return a.getNodeId().compare(b.getNodeId());
	}

}
