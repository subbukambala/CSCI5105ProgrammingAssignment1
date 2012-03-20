/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Comparators for FingerEntries.
 */
import java.util.Comparator;
import java.io.Serializable;


public class FingerEntryComparator implements Comparator<FingerEntry>, Serializable {

        public FingerEntryComparator() {}
        public int compare(FingerEntry a,FingerEntry b) {return a.compare(b);}

}
