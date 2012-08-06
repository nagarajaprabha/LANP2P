package intranetp2p.Test;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import intranetp2p.CacheMgr;
import junit.framework.TestCase;

public class TestCacheMgr extends TestCase {
	private CacheMgr mgr = null;
	public TestCacheMgr(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		mgr = new CacheMgr();
	}

	protected void tearDown() throws Exception {
	}
	public void testSaveFile(){
		String fileName = "test";
		Byte[] b = new Byte[1];
		b[0]='l';
		mgr.saveFile(b , fileName);
		String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+File.pathSeparator+"LANP2P";
		byte []bb = mgr.searchAndGetFile(fileName);
		assertNotNull(bb);
		
	}

}
