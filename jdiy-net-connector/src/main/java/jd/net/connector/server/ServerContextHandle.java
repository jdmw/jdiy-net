package jd.net.connector.server;

import jd.net.protocol.common.connection.NetConnection;

import java.io.IOException;

public interface ServerContextHandle{
    void handler(NetConnection connection) throws IOException;

}
