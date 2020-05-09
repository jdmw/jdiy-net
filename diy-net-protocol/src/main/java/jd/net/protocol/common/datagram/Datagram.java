package jd.net.protocol.common.datagram;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.connection.NetConnection;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

public interface Datagram extends Serializable {

    LowerDatagram getLowerDatagram();
    String getProtocolName();

    void read() throws IOException;

    Datagram attach(NetConnection netConnection) throws IOException;

    void write() throws IOException;


    default int getLocalPort() {
        return getLowerDatagram().getLocalPort();
    }

    default InetAddress getLocalAddress() {
        return getLowerDatagram().getLocalAddress();
    }

    default int getRemotePort() {
        return getLowerDatagram().getRemotePort();
    }

    default InetAddress getRemoteAddress() {
        return getLowerDatagram().getRemoteAddress();
    }
}
