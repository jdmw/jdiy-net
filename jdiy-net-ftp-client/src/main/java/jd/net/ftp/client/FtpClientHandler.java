package jd.net.ftp.client;

import jd.net.protocol.app.ftp.datagram.FtpRequestDatagram;
import jd.net.protocol.app.ftp.datagram.FtpResponseDatagram;
import jd.net.protocol.app.ftp.datagram.cst.FtpCommands;
import jd.net.protocol.common.connection.SocketConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class FtpClientHandler {

    static final String user = "ftp" ;
    static final String pass = "@jdmw1234" ;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLoopbackAddress(),21);
        FtpRequestDatagram req = new FtpRequestDatagram();
        FtpResponseDatagram res = new FtpResponseDatagram();
        SocketConnection conn = new SocketConnection(socket);

        res.attach(conn);
        res.read();
        System.out.println("receive: " + res.getLine());
        req.setRequestCommand(FtpCommands.USER);
        req.setArgument(user);
        req.attach(conn);
        req.write();
        //conn.flush();
        req.setRequestCommand(FtpCommands.PASS);
        req.setArgument(pass);
        req.write();
        conn.flush();

        System.out.println("send: " + req.getLine());
        res.write();
        conn.close();
    }


    public static void answer(FtpRequestDatagram req,FtpResponseDatagram res,SocketConnection conn){
        System.out.println("receive: " + req.getLine());
        System.out.println("send: " + res.getLine());
    }
}
