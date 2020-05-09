package jd.net.protocol.common.datagram;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.connection.NetConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;

/**
 * ip and tcp/udp protocol
 */
@Data
@Slf4j
public class LowerDatagram implements Datagram {

    private String protocolName ;
    private int localPort ;
    private InetAddress localAddress ;
    private int remotePort ;
    private InetAddress remoteAddress ;

    @Override
    public LowerDatagram getLowerDatagram() {
        return null;
    }

    @Override
    public void read() throws IOException {

    }

    @Override
    public HttpDatagram attach(NetConnection netConnection) throws IOException {

        return null;
    }

    @Override
    public void write() throws IOException {

    }
}
