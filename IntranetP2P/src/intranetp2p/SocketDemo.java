package intranetp2p;

import java.io.*;
import java.net.*;
public class SocketDemo extends Thread{ 
private ServerSocket server ;
public SocketDemo(){
try{
server = new ServerSocket(5000);
}catch(Exception e)
{
System.out.println(e+"dddddddd");
}
}
public static void main(String []args){
try{
 Socket s = new Socket("mninprabha.modeln.com",5000);
System.out.println("Connected to " + s.getRemoteSocketAddress());
}catch(Exception e){
System.out.println(e+"dddd");
}

//	Thread t = new SocketDemo();
//t.start();
}
/*public void run(){
while(true){
try{
System.out.println("Waiting"+server.getLocalPort());
Socket acceptConn = server.accept();
System.out.println("Just Connected to"+acceptConn.getRemoteSocketAddress());
acceptConn.close();
}catch(Exception e){
System.out.println(e);
e.printStackTrace();
    }
  }
 }
*/}
