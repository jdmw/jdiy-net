package jd.net.connector.server.threadpool;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TerminatableThreadPool implements ITerminatableThreadPool{
    //private final int maxLength ;
    private final int shutdownWaitingSeconds ;
    private final ExceptionHandler exceptionHandler;
    private final ExecutorService pool;
    //private Semaphore semaphore ;

    public TerminatableThreadPool(@NonNull ExecutorService pool, int shutdownWaitingSeconds, ExceptionHandler exceptionHandler) {
        this.shutdownWaitingSeconds = shutdownWaitingSeconds;
        //this.semaphore= new Semaphore(maxLength);
        this.pool =  pool;
        this.exceptionHandler = exceptionHandler;
    }

    public void submit(Runnable r){
        pool.submit(()->{
            try{
                //semaphore.acquire();
                r.run();
                //semaphore.release();
            }catch (Exception e){
                if(exceptionHandler !=null) {
                    exceptionHandler.handle(e);
                }else{
                    log.error(e.getMessage(),e);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        pool.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(shutdownWaitingSeconds, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(shutdownWaitingSeconds, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args){
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        TerminatableThreadPool pool = new TerminatableThreadPool(executorService,1,(e)->e.printStackTrace());
        //ExecutorService pool = Executors.newFixedThreadPool(3);
        for(int i=0;i<1000;i++){
            final int t = i ;
            pool.submit(()->{
                System.out.format("[%s] i=%d, isDaemon:%s\n",Thread.currentThread().getName(),t, Thread.currentThread().isDaemon());
            });
        }
        pool.shutdown();
        // assert: jvm should exit after all methods executed
    }

}
