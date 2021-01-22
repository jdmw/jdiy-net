package jd.net.connector.server;

import jd.net.connector.server.handler.AbstractServerHandler;
import jd.net.connector.server.handler.io.BIOServerHandler;
import jd.net.connector.server.handler.io.ServerHandlerIOType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * common server
 *
 * base on TCP protocol
 * support BIO,NIO,AIO
 */
@Data
@Slf4j
public class CommonServer implements IServer {

    private ServerContextHandle handle ;
    private ServerConfig config;
    private ServerHandlerIOType type = ServerHandlerIOType.BIO;

    private AbstractServerHandler serverHandler;

    public CommonServer handle(ServerContextHandle handle) {
        this.handle = handle;
        return this;
    }

    public CommonServer config(ServerConfig config) {
        this.config = config;
        return this;
    }

    public CommonServer type(ServerHandlerIOType type) {
        this.type = type;
        return this;
    }

    public void startup(){
        // set default http port
        if(config.getPort() == 0){
            config.setPort(80) ;
        }
        switch (type){
            case BIO: serverHandler=  new BIOServerHandler(config,handle);break;
        }
        serverHandler.startup();
    }

    @Override
    public void shutdown() {
        serverHandler.setShutdownFlag(true);
    }

    public static void main(String[] args) {
        System.out.println("");
    }



}
