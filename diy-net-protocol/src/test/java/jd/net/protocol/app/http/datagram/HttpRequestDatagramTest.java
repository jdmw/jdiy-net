package jd.net.protocol.app.http.datagram;


import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.net.protocol.app.http.datagram.cst.HttpMethod;
import jd.net.protocol.app.http.datagram.cst.HttpVersion;
import jd.net.protocol.common.connection.LocalConnection;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.connection.SocketConnection;
import jd.net.protocol.common.content.ByteArrayContent;
import jd.util.io.CompressUt;
import jd.util.io.IOUt;
import jd.util.io.net.protocols.HttpProtocol;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

public class HttpRequestDatagramTest {

    @Test
    public void unitTest() throws IOException {
        String queryString = "a=1&b=2" ;
        LocalConnection connection = new LocalConnection(queryString.length());

        HttpBaseRequestInfo requestBaseInfo = new HttpBaseRequestInfo();
        requestBaseInfo.setRequestMethod(HttpMethod.POST.name());
        requestBaseInfo.setRequestVersion(HttpProtocol.HttpVersion.HTTP_1_1.getVersion());
        requestBaseInfo.setRequestURIPath("/");
        ByteArrayContent content = new ByteArrayContent(queryString.length(),null,Integer.MAX_VALUE);
        content.setByteStream(new ByteArrayInputStream(queryString.getBytes()));
        HttpDatagram datagram = new HttpDatagram()
                .firstLine(requestBaseInfo)
                .addHeader(HttpHeader.HeaderName.HOST,"127.0.0.1")
                .addHeader(HttpHeader.HeaderName.CONTENT_LENGTH,String.valueOf(queryString.length()))
                .setContent(content)
                .attach(connection);
        datagram.write();

        HttpDatagram res = new HttpDatagram();
        res.attach(connection);
        res.read();
        String body = new String(IOUt.toByteArray(res.getBody().openStream()));
        assertEquals(queryString,body);
        assertEquals(datagram.getFirstLine(),res.getFirstLine());
        assertEquals(datagram.getHeaderAll(),res.getHeaderAll());
    }

    @Test
    public void send() throws IOException {
        String host = "www.97ic.com" ;
        Socket socket = new Socket(host,80);
        OutputStream os = socket.getOutputStream() ;

        HttpBaseRequestInfo requestBaseInfo = new HttpBaseRequestInfo();
        requestBaseInfo.setRequestMethod(HttpMethod.GET.name());
        requestBaseInfo.setRequestVersion(HttpVersion.HTTP_1_1.getVersion());
        requestBaseInfo.setRequestURIPath("/");
        HttpDatagram datagram = new HttpDatagram()
                .firstLine(requestBaseInfo)
                .addHeader(HttpHeader.HeaderName.HOST,host)
                .addHeader(HttpHeader.Connection.NAME,HttpHeader.Connection.KEEP_ALIVE)
                .addHeader(HttpHeader.HeaderName.CACHE_CONTROL,"max-age=0")
                .addHeader(HttpHeader.HeaderName.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader(HttpHeader.HeaderName.ACCEPT_ENCODING,"gzip,deflate")
                .addHeader(HttpHeader.HeaderName.ACCEPT_LANGUAGE,"zh-CN,zh;q=0.8");

        NetConnection connection = new SocketConnection(socket);
        datagram.attach(connection);
        datagram.write();
        os.flush();

        HttpDatagram response = new HttpDatagram();
        response.attach(connection);
        response.read();
        System.out.println(response.getRawTextBeforeBody());
        String body = new String(CompressUt.gzipUncompress(IOUt.toByteArray(response.getBody().openStream())));
        System.out.println(body);
    }
}