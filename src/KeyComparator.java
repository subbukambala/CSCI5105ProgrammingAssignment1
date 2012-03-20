/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Comparator for Keys.
 */
import java.util.Comparator;


public class KeyComparator implements Comparator<Key> {

        public KeyComparator() {}
        public int compare(Key a,Key b) {return a.compare(b);}

}
