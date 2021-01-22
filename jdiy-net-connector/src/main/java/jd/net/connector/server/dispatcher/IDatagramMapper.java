package jd.net.connector.server.dispatcher;

import jd.net.protocol.common.datagram.Datagram;

public interface IDatagramMapper<K> {
    public K[] mapping(Datagram datagram) ;
}
