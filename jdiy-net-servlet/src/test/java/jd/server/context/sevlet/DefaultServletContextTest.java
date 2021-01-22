package jd.server.context.sevlet;

import jd.net.connector.server.CommonServer;
import jd.net.connector.server.ServerConfig;
import jd.net.connector.server.ServerContextHandle;
import jd.net.connector.server.dispatcher.DefaultServerDispatcher;
import jd.net.connector.server.dispatcher.IDatagramMapper;
import jd.net.connector.server.dispatcher.IMappedProcessor;
import jd.net.connector.server.dispatcher.IProcessor;
import jd.net.connector.server.handler.io.ServerHandlerIOType;
import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.HttpRequestBaseInfo;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.net.protocol.app.http.datagram.cst.HttpMethod;
import jd.net.protocol.common.connection.LocalConnection;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.content.ByteArrayContent;
import jd.net.protocol.common.datagram.Datagram;
import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.server.context.sevlet.loader.DefaultServletConfig;
import jd.util.io.IOUt;
import jd.util.io.net.protocols.HttpProtocol;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DefaultServletContextTest {

    public static class DemoServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            System.out.println(req.getRequestURI());
            String body = IOUt.toString(req.getInputStream(),req.getCharacterEncoding());
            resp.getOutputStream().write("hello".getBytes());
            resp.getOutputStream().close();
        }
    }

    private static DefaultServletContext createServletContext()  {
        DefaultServletContext servletContext = new DefaultServletContext(new File(""), "/");
        ContextConfiguration cfg = new ContextConfiguration();
        DefaultServletConfig servletConfig = new DefaultServletConfig(servletContext);
        servletConfig.setUrlPattern(new String[]{"/hello"});
        servletConfig.setServletClass(DemoServlet.class);
        cfg.getServletConfigs().add(servletConfig);
        servletContext.setCfg(cfg);
        servletContext.init();
        return servletContext ;
    }


    @Test
    public void process() throws IOException {

        String echo = "hello" ;
        LocalConnection connection = new LocalConnection(echo.length());

        HttpRequestBaseInfo requestBaseInfo = new HttpRequestBaseInfo();
        requestBaseInfo.setRequestMethod(HttpMethod.GET.name());
        requestBaseInfo.setRequestVersion(HttpProtocol.HttpVersion.HTTP_1_1.getVersion());
        requestBaseInfo.setRequestURI("/hello?a=1");
        ByteArrayContent content = new ByteArrayContent(echo.length(),null,Integer.MAX_VALUE);
        content.setByteStream(new ByteArrayInputStream(echo.getBytes()));
        HttpDatagram datagram = new HttpDatagram()
                .firstLine(requestBaseInfo)
                .addHeader(HttpHeader.HeaderName.HOST,"127.0.0.1")
                .addHeader(HttpHeader.HeaderName.CONTENT_LENGTH,String.valueOf(echo.length()))
                .setContent(content)
                .attach(connection);
        //datagram.write();
        createServletContext().process(connection,datagram);

        HttpDatagram res = new HttpDatagram();
        res.attach(connection);
        res.read();
        String body = new String(IOUt.toByteArray(res.getBody().openStream()));
        assertEquals(echo ,body);
        /*assertEquals(datagram.getFirstLine(),res.getFirstLine());
        assertEquals(datagram.getHeaderAll(),res.getHeaderAll());*/
    }

    class TestHttpServerDispatcher extends DefaultServerDispatcher<String> implements ServerContextHandle {

        public TestHttpServerDispatcher() {
            super((d)->new String[]{"/"},null);
        }

        @Override
        public void handler(NetConnection connection) throws IOException {
            HttpDatagram datagram = new HttpDatagram();
            datagram.attach(connection);
            datagram.read();
            super.dispatch(connection,datagram);
        }
    }
    @Test
    public void startServer(){
        TestHttpServerDispatcher handle = new TestHttpServerDispatcher();
        handle.addProcess(createServletContext());
        new CommonServer()
                .config(new ServerConfig("JdServer/1.0",8001){})
                .handle(handle)
                .type(ServerHandlerIOType.BIO)
                .startup();
    }
}