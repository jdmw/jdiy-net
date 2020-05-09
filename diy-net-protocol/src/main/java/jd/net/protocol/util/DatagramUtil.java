package jd.net.protocol.util;

import jd.net.protocol.common.ProtocolNames;
import jd.net.protocol.common.datagram.LowerDatagram;

import java.net.DatagramSocket;
import java.net.Socket;

public class DatagramUtil {
    public static LowerDatagram toLowerDatagram(Socket socket){
        LowerDatagram datagram = new LowerDatagram();
        datagram.setProtocolName(ProtocolNames.TCP.name());
        datagram.setLocalPort(socket.getLocalPort());
        datagram.setLocalAddress(socket.getLocalAddress());
        datagram.setRemotePort(socket.getPort());
        datagram.setRemoteAddress(socket.getInetAddress());
        return datagram;
    }

    public static LowerDatagram toLowerDatagram(DatagramSocket socket){
        LowerDatagram datagram = new LowerDatagram();
        datagram.setProtocolName(ProtocolNames.UDP.name());
        datagram.setLocalPort(socket.getLocalPort());
        datagram.setLocalAddress(socket.getLocalAddress());
        datagram.setRemotePort(socket.getPort());
        datagram.setRemoteAddress(socket.getInetAddress());
        return datagram;
    }


}
