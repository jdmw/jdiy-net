package jd.net.ftp.server.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jd.net.protocol.app.ftp.datagram.cst.FTPReturnCode;
import jd.util.io.IOUt;

public class UserProtocolInterpreter {

	public void connect(InputStream is ,OutputStream os) throws IOException {
		IOUt.write(os, FTPReturnCode.OK + "-" + "OK\r\r\n");
		os.flush();
		while(true) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b = -1 ;
			while((b = is.read()) != -1) {
				baos.write(b);
			}
			String cmd = new String(baos.toByteArray());
			System.out.println("input: " + cmd);
			//byte[] bytes = IOUtils.toByteArray(is); // IOUt.readToByteArray(is);
			//Console.ln(new String(bytes));
			
			IOUt.write(os,FTPReturnCode.OK + "-" + "OK\r\r\n");
			os.flush();
			//os.close();
		}
		
	}

}
