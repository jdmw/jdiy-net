package jd.net.connector.server;

public interface IServer {

	public void startup();
	public void shutdown();

	public default void startupInNewThread(){
		new Thread(()->startup()).start();
	}

}
