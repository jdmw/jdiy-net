package jd.net.protocol.common.datagram.text;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;
import jd.net.protocol.common.datagram.LowerDatagram;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
public abstract class LineBasedTextData<T> implements Datagram {

    protected LowerDatagram lowerDatagram;
    protected NetConnection netConnection ;
    public static final String NEWLINE = "\r\n" ;

    public HttpDatagram attach(NetConnection netConnection){
        this.netConnection = netConnection ;
        this.lowerDatagram = netConnection.getLowerLayout();
        return null;
    }
    public abstract List<T> getLine();

    public void write() throws IOException {
        List<T> list = getLine();
        if(list != null && list.size() > 0){
            for ( T line : list){
                netConnection.write((line.toString()+NEWLINE).getBytes());
            }
        }
    }

}
