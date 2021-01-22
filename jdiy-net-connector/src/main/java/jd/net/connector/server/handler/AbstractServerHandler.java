package jd.net.connector.server.handler;

import jd.net.connector.server.ServerConfig;
import jd.net.connector.server.threadpool.CachedTerminatableThreadPool;
import jd.net.connector.server.threadpool.ITerminatableThreadPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class AbstractServerHandler {
    private ITerminatableThreadPool pool ;
    private ServerConfig config ;
    private boolean shutdownFlag;


    public AbstractServerHandler(ServerConfig config) {
        this.config = config;
        this.pool = new CachedTerminatableThreadPool(60,(exception)->{
            log.error(exception.getMessage(),exception);
            throw new RuntimeException(exception);
        });
    }

    public AbstractServerHandler(ServerConfig config, ITerminatableThreadPool pool) {
        this.config = config;
        this.pool = pool;
    }

    public abstract void startup();

}
