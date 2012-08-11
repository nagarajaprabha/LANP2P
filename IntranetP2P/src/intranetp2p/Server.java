/**
 * 
 */
package intranetp2p;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

/**
 * @author nprabha
 * 
 */
public class Server {
	private ServerSocket serverSocket = null;
	Socket client = null;
	private int port;
	CacheMgr cacheMgr;

	public Server(int port) {
		this.port = port;
		cacheMgr = new CacheMgr();
	}

	public void startServer() {
		try {
			System.out.println("Creating Server Socket");
			System.out.println(port);
			serverSocket = new ServerSocket(port);

			Thread t = new Thread() {
				public void run() {
					while (true) {
						try {
							System.out.println("Accept Client Sockets");
							client = serverSocket.accept();

							// handle each client seperately
						} catch (IOException e) {
							// TODO Auto-generated catch block
							new RuntimeException(e);
						}

					}

				}

			};
			t.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public Socket getClientSocket() {
		// TODO Auto-generated method stub
		return client;
	}

	public static void main(String[] args) {
		new Server(7777).startServer();
	}

}
