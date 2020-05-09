package jd.net.protocol.app.http.datagram;

import jd.net.protocol.common.content.ByteArrayContent;
import jd.net.protocol.common.datagram.Datagram;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.net.protocol.common.datagram.LowerDatagram;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.ProtocolNames;
import jd.util.StrUt;
import jd.util.io.IOUt;
import jd.util.lang.collection.MultiValueMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static jd.net.protocol.common.datagram.text.LineBasedTextData.NEWLINE;

@Data
@Slf4j
public class HttpDatagram implements Datagram {

    private NetConnection netConnection;
    private String firstLine  ;
    private final MultiValueMap<String,String> headerAll = new MultiValueMap<>();
    private ByteArrayContent body ;
    private final StringBuilder rawTextBeforeBody = new StringBuilder();

    public HttpDatagram() {}

    @Override
    public LowerDatagram getLowerDatagram() {
        return netConnection.getLowerLayout();
    }

    @Override
    public String getProtocolName() {
        return ProtocolNames.HTTP.name();
    }

    public HttpDatagram firstLine(HttpRequestBaseInfo requestBaseInfo){
        return firstLine(requestBaseInfo.toString());
    }
    public HttpDatagram firstLine(String firstLine){
        this.firstLine = firstLine ;
        return this;
    }
    @Override
    public HttpDatagram attach(NetConnection netConnection) throws IOException {
        this.netConnection = netConnection ;
        return this;
    }
    @Override
    public void read() throws IOException {
        InputStream inputStream = netConnection.getInputStream();
        String firstLine = new String(IOUt.next(inputStream,NEWLINE));
        rawTextBeforeBody.append(firstLine);
        this.firstLine = firstLine.trim() ;

        // read header
        while(true){
            String line = new String(IOUt.next(inputStream,NEWLINE));
            rawTextBeforeBody.append(line);
            line = line.trim();
            if(StrUt.isEmpty(line)){
                break;
            }
            int index = line.indexOf(":");
            String name = line.substring(0,index);
            String value = line.substring(index+1).trim();
            addHeader(name,value);
        }

        // read body
        int contentLength = getIntHeader(HttpHeader.HeaderName.CONTENT_LENGTH,0);
        if(contentLength > 0){
            setContent(new ByteArrayContent(contentLength,null,0));
            body.read(netConnection);
        }

    }

    public HttpDatagram setContent(ByteArrayContent byteArrayContent){
        this.body =byteArrayContent;
        return this ;
    }
    public HttpDatagram addHeader(String name, String value){
        headerAll.add(name,value);
        return this ;
    }
    public String getHeader(String name){
        return headerAll.getFirstValue(name);
    }
    public String[] getHeaders(String name){
        List<String> list = headerAll.get(name);
        if(list == null || list.isEmpty()) return null ;
        return list.toArray(new String[list.size()]);
    }

    public int getIntHeader(String name,int defaultValue){
        String value =  getHeader(name);
        if(value == null) return defaultValue ;
        return new Integer(value);
    }

    @Override
    public void write() throws IOException {
        netConnection.write(firstLine.getBytes());
        netConnection.write(NEWLINE.getBytes());

        // write header
        if(headerAll != null){
            for (Map.Entry<String,List<String>> entry : headerAll.entrySet()){
                for(String value : entry.getValue()){
                    String line = entry.getKey() + ": " + value + NEWLINE;
                    netConnection.write(line.getBytes());
                }
            }
        }
        netConnection.write(NEWLINE.getBytes());

        // write body
        if(body != null && body.getContentLength() > 0){
            IOUt.copyLarge(body.openStream(),netConnection.getOutputStream());
        }
    }

}
