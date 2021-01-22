package jd.net.protocol.common.content;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;
import jd.net.protocol.common.datagram.LowerDatagram;
import jd.util.io.IOUt;
import lombok.Data;

import java.io.*;
import java.util.UUID;

@Data
public class ByteArrayContent {

    private final int contentLength ;
    private ByteArrayInputStream byteStream ;
    private final File file ;
    private final int memoryLimit ;

    public ByteArrayContent(int contentLength, File file, int memoryLimit) {
        this.contentLength = contentLength;
        if(file == null){
            file = new File(System.getProperty("java.io.tmpdir") + "jd-net" + File.separator  + File.separator + UUID.randomUUID() ) ;
        }
        this.file = file;
        this.memoryLimit = memoryLimit == 0 ? 1024_000 : memoryLimit ;
    }

    public InputStream openStream() throws IOException {
        if(byteStream != null  ){
            return byteStream ;
        }else if(file != null && file.exists() && file.length() > 0){
            return new FileInputStream(file);
        }else{
            throw new IOException("no content");
        }
    }

    public void read(NetConnection netConnection) throws IOException {
        if(contentLength <= memoryLimit){
            byteStream = new ByteArrayInputStream(netConnection.readByteArray(contentLength));
        }else{
            IOUt.copyLarge(netConnection.getInputStream(),new FileOutputStream(file));
        }
    }

    public int getContentLength() {
        return contentLength;
    }


}
