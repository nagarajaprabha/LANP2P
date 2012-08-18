package intranetp2p.Test;

import java.io.IOException;
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

	public final void testNotifyAllPeers() throws IOException {
		/**
		 * This test assumes that server is running
		 */
		final String url = "http://java.sun.com/docs/books/tutorialNB/download/tutorial-5.0.zip";
		//p2p.notifyAllPeers(url);
		
		assertNotNull(p2p.getFileFromPeers(url));
		//fail("Not yet implemented"); // TODO
	}
	
	public final void testSearchAndGetFile(){
		String fileName = "PeerList.txt";
		byte []b = p2p.searchAndGetFile(fileName);
		assertNotNull(b);
	}


}
