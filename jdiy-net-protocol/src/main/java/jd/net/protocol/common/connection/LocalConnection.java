package jd.net.protocol.common.connection;

import jd.net.protocol.common.datagram.LowerDatagram;

import java.io.*;
import java.net.InetAddress;

public class LocalConnection extends NetConnection {
    public static void main(String[] args) {
        System.out.println("");
    }

    private final ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private boolean closed ;
    public LocalConnection(int contentLength){
        outputStream = new ByteArrayOutputStream(contentLength);
    }
    @Override
    public LowerDatagram getLowerLayout() {
        LowerDatagram datagram = new LowerDatagram();
        datagram.setLocalPort(0);
        datagram.setLocalAddress(InetAddress.getLoopbackAddress());
        datagram.setRemotePort(0);
        datagram.setRemoteAddress(InetAddress.getLoopbackAddress());
        datagram.setProtocolName("Local");
        return datagram;
    }

    @Override
    public ByteArrayInputStream getInputStream() throws IOException {
        if( inputStream == null){
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        }
        return inputStream;
    }

    @Override
    public ByteArrayOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public void close() throws IOException {
        closed = true ;
        if(inputStream != null){
            inputStream.close();
        }
        outputStream.close(); ;
    }

    @Override
    public boolean isSocketClose() {
        return closed;
    }

    public void reset(){
        closed = false ;
        outputStream.reset();
        inputStream = null ;
    }
}
