package jd.net.connector.server.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
public class CachedTerminatableThreadPool extends TerminatableThreadPool{

    public static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            // set all worker thread as daemon threads,when the thread pool is shut down,jvm exit
            thread.setDaemon(true);
            return thread;
        }
    };

    public CachedTerminatableThreadPool(int shutdownWaitingSeconds, ExceptionHandler exceptionHandler) {
        super(Executors.newCachedThreadPool(new DaemonThreadFactory()), shutdownWaitingSeconds, exceptionHandler);
    }


}
