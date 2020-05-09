package jd.net.connector.server.dispatcher;

import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;

public interface IProcessor {
    public void process(NetConnection netConnection, Datagram datagram);
}
