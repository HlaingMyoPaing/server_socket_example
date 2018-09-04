package webapi.nesic.com.socket_server_example_1;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
 
public class Server {
	private String tag = "SimpleTcpServer.java";
	MainActivity activity;
	ServerSocket serverSocket;
	String message = "";
	static final int socketServerPORT = 3030;
 
	public Server(MainActivity activity) {
		this.activity = activity;
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}
 
	public int getPort() {
		return socketServerPORT;
	}
 
	public void onDestroy() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
	private class SocketServerThread extends Thread {
 
		int count = 0;
 
		@Override
		public void run() {
			try {
				// create ServerSocket using specified port
				serverSocket = new ServerSocket(socketServerPORT);
 
				while (true) {
					// block the call until connection is created and return
					// Socket object
					Socket socket = serverSocket.accept();
					count++;
					message += "#" + count + " from "
							+ socket.getInetAddress() + ":"
							+ socket.getPort() + "\n";



                    Log.e("","after accept");
                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    BufferedReader br = new BufferedReader(isr);
                    boolean isDone = false;
                    String s = new String();

                    while(!isDone && ((s=br.readLine())!=null)){
                        final String mes = s;
                        System.out.println("client data is :"+s);   // Printing on Console
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String clientmessage = "";
                                if( activity.msg.getText().toString()!=null ){
                                    clientmessage = activity.msg.getText().toString() + "\n"+mes;
                                }
                                activity.msg.setText(clientmessage);
                            }
                        });


                    }

//                    // 受信ストリームの取得
//                    InputStream inputStream = socket.getInputStream();
//                    BufferedInputStream bis = new BufferedInputStream(inputStream);
//                    DataInputStream dis = new DataInputStream(bis);
//
//                    // ログ日時データ長
//                    ByteBuffer bb = ByteBuffer.allocate(4);
//                    bb.putInt(dis.readInt());
//                    bb.order(ByteOrder.LITTLE_ENDIAN);
//                    int dateLength = bb.getInt(0);
//                    Log.d(tag, "Date Length = " + dateLength);
//
//                    // ログ日時
//                    byte[] logBuff = new byte[dateLength];
//                    dis.readFully(logBuff, 0, dateLength);
//                    String logDate = new String(logBuff, "SJIS");
//                    Log.d(tag, "Log Date = " + logDate);
//
//                    // カメラ名データ長
//                    bb = ByteBuffer.allocate(7);
//                    bb.putInt(dis.readInt());
//                    bb.order(ByteOrder.LITTLE_ENDIAN);
//                    int cameraNameLength = bb.getInt(0);
//                    Log.d(tag, "Camera Name Length = " + cameraNameLength);
//
//                    // カメラ名
//                    byte[] cameraNameBuff = new byte[cameraNameLength];
//                    dis.readFully(cameraNameBuff, 0, cameraNameLength);
//                    String cameraName = new String(cameraNameBuff, "SJIS");
//                    Log.d(tag, "Camera Name = " + cameraName);
//
//                    // ログ内容長
//                    bb = ByteBuffer.allocate(4);
//                    bb.putInt(dis.readInt());
//                    bb.order(ByteOrder.LITTLE_ENDIAN);
//                    int logContentsLength = bb.getInt(0);
//                    Log.d(tag, "Log Contents Length = " + logContentsLength);
//
//                    // ログ内容
//                    byte[] logContentsBuff = new byte[logContentsLength];
//                    dis.readFully(logContentsBuff, 0, logContentsLength);
//                    String logContents = new String(logContentsBuff, "SJIS");
//                    Log.d(tag, "Log Contents = " + logContents);

/*

                    SocketServerReplyThread socketServerReplyThread =
                            new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
*/


 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
	private class SocketServerReplyThread extends Thread {
 
		private Socket hostThreadSocket;
		int cnt;
 
		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			cnt = c;
		}
 
		@Override
		public void run() {
			OutputStream outputStream;
			String msgReply = "Hello from Server, you are #" + cnt;
 
			try {
				outputStream = hostThreadSocket.getOutputStream();
		     	PrintStream printStream = new PrintStream(outputStream);
				printStream.print(msgReply);
				printStream.close();
				activity.runOnUiThread(new Runnable() {
 
					@Override
					public void run() {
						activity.msg.setText(message);
					}
				});
 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}
 
			activity.runOnUiThread(new Runnable() {
 
				@Override
				public void run() {
					activity.msg.setText(message);
				}
			});
		}
 
	}
 
	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();
 
					if (inetAddress.isSiteLocalAddress()) {
						ip += "Server running at : "
								+ inetAddress.getHostAddress();
					}
				}
			}
 
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}
}