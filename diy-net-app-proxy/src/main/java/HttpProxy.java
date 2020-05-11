import jd.net.connector.server.CommonServer;
import jd.net.connector.server.ServerConfig;
import jd.net.connector.server.ServerContextHandle;
import jd.net.connector.server.handler.io.ServerHandlerIOType;
import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.HttpRequestBaseInfo;
import jd.net.protocol.common.connection.NetConnection;

import java.io.IOException;

public class HttpProxy {

    public static void main(String[] args) {
        new CommonServer().config(new ServerConfig("",81) { })
                .type(ServerHandlerIOType.BIO)
                .handle(new ServerContextHandle() {
                    @Override
                    public void handler(NetConnection connection) throws IOException {
                        HttpDatagram inputData = HttpDatagram.read(connection);
                        HttpRequestBaseInfo requestBaseInfo = HttpRequestBaseInfo.of(inputData);
                        String urlPath = requestBaseInfo.getRequestURIPath();

                    }
                }).startup();

    }
}
