/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Entry of the peertable kept by the SuperPeer..
 */

import java.math.BigInteger;

public  class PeerInfo
{

        private Key nodeid;
        private String ip;

        public PeerInfo(Key _nodeid, String _ip) {
                ip = _ip;
                nodeid = _nodeid;
        }

        public String getIP() {
                return ip;
        }

        public Key getNodeId() {
                return nodeid;
        }


}
