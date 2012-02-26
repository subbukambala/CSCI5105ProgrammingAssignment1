import java.io.Serializable;
/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Represents node uniqe identifiers.
 */
public class NodeID implements Serializable
{

    /**
     * Internal respesentation of a NodeID is an integer.
     */
    private int id;

    /**
     * Intialize the NodeID
     */
    public NodeID (int _id) 
    {
	id = _id;
    }

    
    /**
     * Check if two NodeIDs are equal.
     */
    public boolean equal(NodeID b)
    {
	return (id == b.id);
    }

    /**
     * Check if this NodeID is less than another.
     */
    public boolean less(NodeID b)
    {
	return (id == b.id);
    }

    /**
     * Convert this NodeID to a String.
     */
    public String toString()
    {
	return Integer.toString(id);
    }

}