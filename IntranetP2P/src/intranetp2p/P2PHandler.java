package intranetp2p;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class P2PHandler {

	private static final String IS_FILE_AVAILABLE = "isFileAvailable";

	CacheMgr mgr;
	Socket clientSocket;
	Server server = new Server(7777);
	ArrayList<Socket> clntSocket = new ArrayList();
	byte[] b;
	int sizeofFile;
	HashMap<Socket, Properties> h = new HashMap();

	public static ArrayList<Socket> availablePeers = new ArrayList();

	public P2PHandler() {
		mgr = new CacheMgr();
		// Started the LocalServer

		server.startServer();

		listenPeersRequest();
	}

	/**
	 * Reads the Datagram listening to the port searches the file returns the
	 * outputstream Mediator listens to Peers Request TODO : Thread
	 * Implementation may be Required
	 */
	public void listenPeersRequest() {

		Thread t = new Thread() {
			InputStream in = null;
			OutputStream out = null;
			BufferedReader pbr = null;
			boolean flag = false;
			String response = new String();

			public void run() {
				try {

					ArrayList<String> request = new ArrayList();

					while (true) {

						/**
						 * ClientSocket Responsibilities are 
						 * 	1.  Listen to the
						 * 	2.  ClientRequests 
						 *  3.  Sends the Responses
						 */
/**
* ***************************************************************************************************
*/						

						clientSocket = server.getClientSocket();

/**
* ***************************************************************************************************
*/						
						if (clientSocket != null) {

							in = clientSocket.getInputStream();
							out = clientSocket.getOutputStream();
							pbr = new BufferedReader(new InputStreamReader(in));

						} else {
							continue;
						}
/**
* ***************************************************************************************************
*/						

						/**
						 * FirstLine : isFileAvailable SecondLine : FileName
						 */
						String str = new String();
						request.clear();

/**
 * **************************Adding the Parameters to the "request" ArrayList*************************************************************************
 */						
						
						while (pbr.ready() && (str = pbr.readLine()) != null) {
							request.add(str);
						}

						
/**
* ***************************************************************************************************
*/						

						if (request.size() > 0) {

							if (request.get(0).equals(
									"isFileURLAvailable".trim())) {

								response = mgr.isFileURLAvailable(request
										.get(1).trim(), null);


								if (response != null) {
									StringTokenizer st = new StringTokenizer(
											response);

									out.write(("Available \n" + st.nextToken()
											+ "\n" + st.nextToken() + "\n")
											.getBytes());
									out.flush();

								} else {
									out.write(("NA" + "\n").getBytes());
									out.flush();

								}

							} else if (request.get(0).trim().equals(
									"getFile".trim())) {
								// FileName is after getFile String
								System.out.println(" IN GET FILE ");
								/**
								 * FirstList : getFile Second : url Third :
								 * FileName
								 */

								out.write(mgr.searchAndGetFile(CacheMgr
										.getFileNameFromURL(request.get(1))));
								out.write("\n".getBytes());
								// out.write((byte) -1);
								out.flush();
								request.clear();

							} else if (request.get(0).trim().equals("partFile")) {
								// processing the response
								/**
								 * First Line : getFile+"FileName" Second Line :
								 * int offset Third Line : int length
								 */
								out.write(mgr.getFile(request.get(1).substring(
										"getFile".length()), Integer
										.parseInt(request.get(2)), Integer
										.parseInt(request.get(3))));

								out.write("\n".getBytes());
								// out.write((byte) -1);
								out.flush();
								request.clear();

							}
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					new RuntimeException(e);
				} finally {
					try {
						out.flush();

						// out.close();
						// in.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						new RuntimeException(e);
					}
				}

			}
		};
		t.start();
	}

	/**
	 * Based on the request from proxy , notifies all peers to search for a
	 * given fileName ApplicationServer sends the Notification for all peers,to
	 * search for a file with the given FileName
	 * 
	 * MY LOCAL IS ACTING AS A CLIENT
	 * 
	 * TODO : Can we rename this method as getFileFromPeers File Location :
	 * C:\Documents and Settings\nprabha
	 */
	public void requestPeers(String fileurl) throws UnknownHostException {

		ByteArrayInputStream bis = null;
		BufferedWriter bw = null;
		String ipAddress = null;

		try {

			// write a method getAllPeersAddress
			// Iterate each Peer and Send the Info
/**
* ***************************************************************************************************
*/						

			
			byte[] infoBytes = searchAndGetFile("PeerList.txt");

/**
* ***************************************************************************************************
*/						

			
			bis = new ByteArrayInputStream(infoBytes);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));

			assert (infoBytes != null);
/**
* ***************************************************************************************************
*/						

			/**
			 * Sending Request PART
			 */
/**
* ***************************************************************************************************
*/						

			while ((ipAddress = br.readLine()) != null) {

				System.out.println(" Printing the IP Address "
						+ ipAddress.trim());

				Socket s = new Socket(ipAddress.trim(), 7777);
				clntSocket.add(s);


				P2PURLAvailableHandler p = new P2PURLAvailableHandler(s,
						fileurl);

				p.currentThread = Thread.currentThread();
				Thread t = new Thread(p);
				t.start();

			}
			
/**
* ***************************************************************************************************
*/						
			
			/**
			 * Receving RESPONSE PART
			 */

			System.out.println(" Going to Sleep :)");

		} catch (Exception e) {

			throw new RuntimeException(e);
		}

		System.out.println(" Bye ");
	}

	public class P2PURLAvailableHandler implements Runnable {
		Socket sock;
		OutputStream os;
		InputStream is;
		String fileurl;
		Thread currentThread;

		public P2PURLAvailableHandler(Socket s, String url) {
			// TODO Auto-generated constructor stub
			this.sock = s;
			this.fileurl = url;
		}

		public void run() {
			/**
			 * Format :
			 * 
			 * FirstLine : isFileAvailable SecondLine : FileName
			 */
			try {

				sendRequestForURL();

				Thread.sleep(1000);

				addToPeerListOfAvailable();

			} catch (IOException io) {
				throw new RuntimeException(io);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * @throws IOException
		 * @throws InterruptedException
		 */
		private void sendRequestForURL() throws IOException,
				InterruptedException {
			String sb = "isFileURLAvailable".toString() + "\n".toString()
					+ fileurl.toString() + "\n";

			os = sock.getOutputStream();
			os.write(sb.getBytes());
			os.flush();
			Thread.sleep(200);
		}

		public void addToPeerListOfAvailable() {

			try {
				is = sock.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				while (!br.ready()) {
					break;
				}

				String response = br.readLine();
				// sizeofFile = Integer.parseInt(br.readLine());

				System.out.println(" Printing the Response of the Peers :"
						+ response);
				if (br.ready() && response.trim().equals("Available")) {

					availablePeers.add(sock);
					System.out.println(" Available Peers List Size "
							+ availablePeers.size());

				} else {
					// TODO Intimate the Stub
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}

		}

	}
//todo set the fileName as that of thread name
	public class P2PFileDownloadHandler implements Runnable {
		String fileurl , fileName;
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br;
		int offset, length;

		public void setParameters(String url, int offset, int length) {
			this.fileurl = url;
			this.offset = offset;
			this.length = length;
		}

		public void setStreams(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}

		public void run() {

			try {
				br = new BufferedReader(new InputStreamReader(is));
				final BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(os));

				System.out
						.println("IN P2PFILEHANDLER!!PRINTING THE FILE URL : "
								+ fileurl);

				bw.write("get" + fileurl + "\n" + offset + "\n" + length
								+ "\n");
				bw.flush();
				Thread.sleep(1000);
				// TODO Again should recheck Number of Available
				// Peers Responded

				CacheMgr c = new CacheMgr();
				c.saveFileByPart(is, fileurl , Integer.parseInt(Thread.currentThread().getName()));

			} catch (InterruptedException e) {
				new RuntimeException(e);
			}

			catch (IOException e) {
				// TODO Auto-generated catch block
				new RuntimeException(e);
			}
		}
	}

	/**
	 * @param fileurl
	 * @throws IOException
	 */
	private byte[] downloadFile(final String fileurl) throws IOException {

		// getting the count of AVAILABLE SERVER PEERS
		InputStream is = null;
		OutputStream os = null;

		int offset = 0, length = 0 ,incr = 0;
		

		try {
			System.out.println(" Dowloading initiated....");
			if (availablePeers.size() > 1) {

				for (final Socket peer : availablePeers) {

					// TODO logic of getting the bytes from each
					/**
					 * 1. Get the size of the file 2. Compute the bytes for each
					 * Peer 3. Request the Bytes Assuming each peer has COMPLETE
					 * FILE FOR SAMPLE , GET THE FILE SIZE FROM THE FIRST PEER
					 */

					is = peer.getInputStream();
					os = peer.getOutputStream();

					offset = length;

					if (availablePeers.lastIndexOf(peer) != -1) {
						length = sizeofFile;
					} else {
						length = length + (sizeofFile / availablePeers.size());
					}

					P2PFileDownloadHandler downloader = new P2PFileDownloadHandler();
					downloader.setParameters(fileurl, offset, length);
					downloader.setStreams(is, os);
					Thread t = new Thread(downloader);
					t.setName(Integer.toString(incr++));
					t.start();
					
				}

			}
			/**
			 * construct a packet 1. GET 2. FILENAME
			 * 
			 */

			else if (availablePeers.size() == 1) {

				try {

					is = availablePeers.get(0).getInputStream();
					os = availablePeers.get(0).getOutputStream();

					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(os));

					bw.write("getFile" + "\n" + fileurl);
					bw.write("\n");

					bw.flush();
					os.flush();

					Thread.sleep(1000);

					System.out.println(" Downloading ......." + fileurl);

					CacheMgr c = new CacheMgr();
					c.saveFile(is, fileurl);

					System.out.println(" Congratulations!! Downloaded");

				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					// availablePeers.get(0).shutdownInput();
					// availablePeers.get(0).shutdownOutput();
					// os.close();
				}
			} else {
				// NO SERVER PEER HAS THE FILE
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return null;

	}

	/**
	 * @return
	 * @throws IOException
	 */
	private ArrayList<Socket> getCountOfFileAvailablePeers() throws IOException {
		InputStream is;
		BufferedReader br;
		// Get the list of PEERS who has the Required FileName
		ArrayList<Socket> availablePeers = new ArrayList();

		for (Socket peer : clntSocket) {

			System.out
					.println(" Waked UP!! Getting the List of Available Peers");

			is = peer.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));

			System.out.println(br.ready());

			while (!br.ready()) {
				break;
			}

			String response = br.readLine();
			sizeofFile = Integer.parseInt(br.readLine());

			System.out.println(" Printing the Response of the Peers :"
					+ response);
			if (br.ready() && response.trim().equals("Available")) {

				availablePeers.add(peer);
				System.out.println(" Available Peers List Size "
						+ availablePeers.size());

			} else {
				// TODO Intimate the Stub
			}

		}
		return availablePeers;
	}

	public byte[] getFileFromPeers(final String url) throws IOException,
			InterruptedException {
		final Thread currentThread = Thread.currentThread();

		requestPeers(url);

		Thread.sleep(5000);

		downloadFile(url);

		System.out.println(" TATAT");
		return b;

	}

	// Local api for p2phandler to return the peer list file
	public byte[] searchAndGetFile(String fileName) {

		String path = System.getProperty("user.home") + File.separator
				+ "LANP2P";
		File dir = new File(path);

		DataInputStream dis = null;

		System.out.println(System.getProperty("user.home"));

		if (!dir.exists()) {
			System.out
					.println("Directory Doesnot exist...!Creating the Directory");
			dir.mkdir();
		}

		for (File search : dir.listFiles()) {

			System.out.println("Printing the Files in the Directory "
					+ search.getName());
			if (search.getName().equals(fileName)) {
				try {
					dis = new DataInputStream(new FileInputStream(search));
					byte[] b1 = new byte[dis.available()];

					dis.readFully(b1);

					System.out.println(" Printing the bytes : " + b1.length);
					return b1;

				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				} catch (EOFException eof) {
					throw new RuntimeException(eof);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					try {
						dis.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

			}
		}
		return null;
	}

}
