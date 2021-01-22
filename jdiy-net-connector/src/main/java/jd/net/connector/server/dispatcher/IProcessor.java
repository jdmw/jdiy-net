package jd.net.connector.server.dispatcher;

import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;

import java.io.IOException;

public interface IProcessor {
    public void process(NetConnection netConnection, Datagram datagram) throws IOException;
}
