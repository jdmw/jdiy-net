package jd.net.protocol.common.connection;

import jd.net.protocol.util.DatagramUtil;
import jd.net.protocol.common.datagram.LowerDatagram;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Data
@Slf4j
public class SocketConnection extends NetConnection {

    private LowerDatagram lowerLayout;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public SocketConnection(Socket socket) throws IOException {
        this.socket = socket;
        inputStream =  socket.getInputStream();
        outputStream =  socket.getOutputStream();
    }

    public LowerDatagram getLowerDatagram(){
        if(lowerLayout == null){
            synchronized (this){
                if(lowerLayout == null){
                    lowerLayout = DatagramUtil.toLowerDatagram(socket);
                }
            }
        }
        return lowerLayout ;
    }
    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isSocketClose() {
        return socket.isClosed();
    }
}
