package jd.net.ftp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import jd.net.ftp.server.control.UserProtocolInterpreter;
import jd.util.io.IOUt;
import jd.util.lang.concurrent.CcUt;

public class FTPServer {

	public final static int DEFAULT_PORT = 21 ;
	
	public FTPServer(int port) {
		
	}
	public static void startServer() throws IOException {
		ServerSocket ser = new ServerSocket(80);
		Socket client = null ;
		UserProtocolInterpreter interpreter = new UserProtocolInterpreter();
		while((client = ser.accept()) != null) {
			Socket socket = client ;

			CcUt.start(()->{
				try {
					InputStream is = socket.getInputStream();
					OutputStream os = socket.getOutputStream();
					interpreter.connect(is,os);
					//os.flush();
					//os.close();
				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					if(!socket.isClosed()) {
						IOUt.close(socket);
					}
				}
			});
		}
	}
	
	public static void main(String[] args) throws IOException {
		startServer();

	}

}
