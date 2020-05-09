package jd.net.protocol.common.connection;

import jd.net.protocol.common.datagram.LowerDatagram;
import java.io.OutputStream;

import java.io.IOException;
import java.io.InputStream;

public interface INetConnection extends Cloneable{
    LowerDatagram getLowerLayout();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;

    void write(byte[] bytes) throws IOException;
    int read() throws IOException;
    byte[] readByteArray(int length) throws IOException;
    void close() throws IOException;
    boolean isSocketClose();
}
