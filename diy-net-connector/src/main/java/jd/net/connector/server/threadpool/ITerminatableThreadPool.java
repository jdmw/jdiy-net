package jd.net.connector.server.threadpool;

public interface ITerminatableThreadPool {
    public void submit(Runnable r);
    public void shutdown();
}
