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
import java.util.StringTokenizer;

public class P2PHandler {

	private static final String IS_FILE_AVAILABLE = "isFileAvailable";

	CacheMgr mgr;
	Socket clientSocket;
	Server server = new Server(7777);
	ArrayList<Socket> clntSocket = new ArrayList();
	byte[] b;

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
					// System.out.println(clientSocket);

					ArrayList<String> request = new ArrayList();

					while (true) {

						/**
						 * ClientSocket Responsibilities are 1. Listen to the
						 * ClientRequests and Sends the Responses
						 */
						clientSocket = server.getClientSocket();

						// System.out.println(" Listening your Request ....");

						if (clientSocket != null) {

							/*
							 * System.out .println(" ClientSocket requested is
							 * under process ");
							 */
							in = clientSocket.getInputStream();
							out = clientSocket.getOutputStream();
							pbr = new BufferedReader(new InputStreamReader(in));

						} else {
							continue;
						}

						/**
						 * FirstLine : isFileAvailable SecondLine : FileName
						 */
						/*
						 * System.out .println(" Printing the Details of
						 * Requested Message : " + br.readLine());
						 * 
						 * System.out.println(br.readLine().equals(
						 * "isFileURLAvailable".trim()));
						 */

						String str = new String();
						request.clear();
						while (pbr.ready() && (str = pbr.readLine()) != null) {
							request.add(str);
							System.out.println(str + " Reading ... "
									+ request.get(0));

						}
						if (request.size() > 0) {
							if (request.get(0).equals(
									"isFileURLAvailable".trim())) {

								/*
								 * System.out .println(" Yes Got Message :
								 * Please wait ");
								 */
								response = mgr.isFileURLAvailable(request
										.get(1).trim(), null);

								// System.out.println(" Response is " +
								// response);

								// TODO can make it into a single condition
								if (response != null) {
									StringTokenizer st = new StringTokenizer(
											response);
									/*
									 * out.write(("Available \n" +
									 * st.nextToken() + "\n" + st.nextToken() +
									 * "\n" + st.nextToken() + "\n" + st
									 * .nextToken()).getBytes());
									 * 
									 * 
									 * 
									 */
									// TEMPORARAY : ONE PARAMETER IS MISSING
									out
											.write(("Available \n"
													+ st.nextToken() + "\n")
													.getBytes());
									out.flush();

								} else {
									// System.out.println(" Writing the Response
									// ");
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
								System.out.println("Size of the file is "
										+ request.get(1) + "\n"
										+ request.get(1));

								out.write(mgr.searchAndGetFile(CacheMgr.getFileNameFromURL(request.get(1))));
								out.write("\n".getBytes());
								out.write((byte) -1);
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
								out.write((byte) -1);
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
						e.printStackTrace();
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
		// Thread nt = new Thread(){
		// public void run(){

		ByteArrayInputStream bis = null;
		BufferedWriter bw = null;
		String ipAddress = null;

		int i = 0;

		InputStream is = null;
		OutputStream os = null;

		try {

			// write a method getAllPeersAddress
			// Iterate each Peer and Send the Info

			byte[] infoBytes = searchAndGetFile("PeerList.txt");

			bis = new ByteArrayInputStream(infoBytes);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));

			assert (infoBytes != null);

			/**
			 * Sending Request PART
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
				String sb = new String();
				sendRequestForURL();
				Thread.sleep(1000);
				addToPeerListOfAvailable();
				// currentThread.join();
				// notify();

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
			String sb;
			sb = "isFileURLAvailable".toString() + "\n".toString()
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
				e.printStackTrace();
			}

		}

	}

	public class P2PFileDownloadHandler implements Runnable {
		String fileurl;
		public P2PFileDownloadHandler(String fileurl){
			//System.out.println(" IN P2PFILEDOWNLOADER CONSTRUCTOR : "+ fileurl);
			this.fileurl = fileurl;
		}
		public void run() {

			try {
				System.out.println("IN P2PFILEHANDLER!!PRINTING THE FILE URL : "+fileurl);
				downloadFile(fileurl);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			BufferedReader br;


			try {
				System.out.println(" Dowloading initiated....");
				if (availablePeers.size() > 1) {
					
					
					for (final Socket peer : availablePeers) {

						// TODO logic of getting the bytes from each
						/**
						 * 1. Get the size of the file 2. Compute the bytes for
						 * each Peer 3. Request the Bytes Assuming each peer has
						 * COMPLETE FILE FOR SAMPLE , GET THE FILE SIZE FROM THE
						 * FIRST PEER
						 */
						 int size = 0, offset = 0, length = 0 ,i =0;
						

						is = peer.getInputStream();
						os = peer.getOutputStream();

						br = new BufferedReader(new InputStreamReader(is));
						final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));


						offset = length;
						
						if(availablePeers.lastIndexOf(peer)!=-1){
							length = availablePeers.size();
						}else{
							length = length+(size/availablePeers.size());
						}

						final int finallength = length , finaloffset = offset;
						final InputStream finalis = is;
						Thread partthread = new Thread(){

							public void run(){
								
								try {
									bw.write("get" + fileurl + "\n" + finaloffset + "\n"
											+ finallength+ "\n");
									bw.flush();
									Thread.sleep(1000);
									CacheMgr c = new CacheMgr();
									c.saveFileByPart(finalis, fileurl);
								} catch (IOException e) {
								 new RuntimeException(e);
								}catch (InterruptedException e) {
									new RuntimeException(e);
								}

							}
						};
						
						partthread.start();
						Thread.currentThread().sleep(1001);
						
						
						/**
						 * construct a packet 1. GET 2. FILENAME
						 * 
						 */

					}
				}

				else if (availablePeers.size() == 1) {

					try {

						is = availablePeers.get(0).getInputStream();
						os = availablePeers.get(0).getOutputStream();

						br = new BufferedReader(new InputStreamReader(is));
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

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
		/*
		 * final Thread t1 = new Thread() { public void run() { try {
		 * requestPeers(url); // currentThread.join(); } catch
		 * (UnknownHostException e) { throw new RuntimeException(e); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } }; t1.start();
		 * 
		 * Thread t2 = new Thread() {
		 * 
		 * public void run() { try { t1.join(); P2PFileDownloadHandler pd = new
		 * P2PFileDownloadHandler(); pd.fileurl = url; Thread tt1 = new
		 * Thread(pd); System.out.println( " IN NEW BLOCK "); tt1.start(); //
		 * currentThread.join();
		 *  } catch (Exception e) { throw new RuntimeException(e); } }
		 * 
		 * public byte[] getResult() { return b; } }; t2.start(); t2.join();
		 */
		requestPeers(url);
		Thread.sleep(90000);
		P2PFileDownloadHandler pd = new P2PFileDownloadHandler(url);
		pd.fileurl = url;
		Thread tt1 = new Thread(pd);
		tt1.start();
		Thread.sleep(100000);
		System.out.println(" TATAT");
		return b;

	}

	// API to return the peer list file
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
					eof.printStackTrace();
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
