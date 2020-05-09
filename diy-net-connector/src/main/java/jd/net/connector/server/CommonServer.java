package jd.net.connector.server;

import jd.net.connector.server.handler.AbstractServerHandler;
import jd.net.connector.server.handler.io.BIOServerHandler;
import jd.net.connector.server.handler.io.ServerHandlerIOType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CommonServer implements IServer {

    private ServerContextHandle handle ;
    private ServerConfig config;
    private ServerHandlerIOType type ;

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
