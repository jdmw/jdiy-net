package jd.net.connector.server.dispatcher;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;
import jd.net.protocol.common.datagram.LowerDatagram;
import jd.util.ArrUt;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class DefaultServerDispatcherTest {

    private final static Map<String,String> expectMap = new HashMap<>();
    private final static Map<String,String> actualMap = new HashMap<>();

    @Data
    @AllArgsConstructor
    public static class UrlDatagram implements Datagram {

        private String url ;

        @Override
        public LowerDatagram getLowerDatagram() {
            return null;
        }

        @Override
        public String getProtocolName() {
            return null;
        }

        @Override
        public void read() throws IOException {

        }

        @Override
        public UrlDatagram attach(NetConnection netConnection) throws IOException {
            return this;
        }

        @Override
        public void write() throws IOException {

        }
    }

    public static final class TestProcess implements IMappedProcessor<String>{
        private String name ;
        private String mapping ;
        public TestProcess(String name, String mapping) {
            this.name = name;
            this.mapping = mapping;
            expectMap.put(mapping,name);
        }

        @Override
        public String[][] mappings() {
            return new String[][]{mapping.split("/")};
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }

        @Override
        public void process(NetConnection netConnection, Datagram datagram) {
            String url = ((UrlDatagram) datagram).url;
            System.out.printf("process[%s] url= %s \n",name, url);
            actualMap.put(url,name);
            Assert.assertEquals(expectMap.get(url),name);
        }
    }
    @Test
    public void dispatch() throws IOException {
        IDatagramMapper mapper = (Datagram datagram)-> ((UrlDatagram)datagram).url.split("/") ;
        DefaultServerDispatcher dispatcher = new DefaultServerDispatcher(mapper,(netConnection, datagram) ->{
            System.err.println(((UrlDatagram)datagram).url);
            actualMap.put(((UrlDatagram)datagram).url,"default");
        });
        dispatcher.addProcess(new TestProcess("A","A/1"));
        dispatcher.addProcess(new TestProcess("B","B/2"));
        dispatcher.addProcess(new TestProcess("C","C/3"));
        dispatcher.dispatch(null,new UrlDatagram("A/1"));
        dispatcher.dispatch(null,new UrlDatagram("B/2"));
        dispatcher.dispatch(null,new UrlDatagram("C/3"));
        int i = 1000 ;
        List<String> urls = new ArrayList<>(i);
        while(i-->0){
            String uuid = UUID.randomUUID().toString();
            String url = uuid.replaceAll("-","/") + "/" + System.nanoTime() ;
            urls.add(url);
            dispatcher.addProcess(new TestProcess(uuid,url));
        }
        for (String url : urls) {
            dispatcher.dispatch(null,new UrlDatagram(url));
        }
        String noMappingUrl = "D/no";
        dispatcher.dispatch(null,new UrlDatagram(noMappingUrl));
        Assert.assertEquals("default",actualMap.get(noMappingUrl));
    }
}