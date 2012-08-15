package intranetp2p.Test;

import java.net.UnknownHostException;

import intranetp2p.P2PHandler;
import junit.framework.TestCase;

public class TestP2PHandler extends TestCase {
	P2PHandler p2p;
	public TestP2PHandler(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		p2p = new P2PHandler();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testListenPeersRequest() {
		fail("Not yet implemented"); // TODO
	}

	public final void testNotifyAllPeers() throws UnknownHostException {
		/**
		 * This test assumes that server is running
		 */
		p2p.notifyAllPeers("https:docs.google.com/document/d/1oVRF2oz5riiWMRTH_7WtKc3xH9Xkzab2KV39ZgTGaXM/");
		//fail("Not yet implemented"); // TODO
	}
	
	public final void testSearchAndGetFile(){
		String fileName = "PeerList.txt";
		byte []b = p2p.searchAndGetFile(fileName);
		assertNotNull(b);
	}


}
