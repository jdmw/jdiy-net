package jd.net.connector.server.handler.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jd.net.connector.server.ServerConfig;
import jd.net.connector.server.ServerContextHandle;
import jd.net.connector.server.handler.AbstractServerHandler;
import jd.net.connector.server.threadpool.ITerminatableThreadPool;
import jd.net.protocol.common.connection.SocketConnection;
import jd.util.pattern.concurrent.phase2termination.TwoPhaseTerminationCounter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BIOServerHandler extends AbstractServerHandler {

	private ServerContextHandle handle ;
	public BIOServerHandler(ServerConfig info, ServerContextHandle handle) {
		super(info);
		this.handle = handle;
	}

	public void startup() {
		Socket client = null ;
		ITerminatableThreadPool threadPool = getPool();
		try(ServerSocket ser = new ServerSocket(getConfig().getServerPort())) {
			System.out.println("server start up at port: " + super.getConfig().getServerPort());
			while(!super.isShutdownFlag() && (client=ser.accept())!= null) {
				Socket c = client;
				try {
					threadPool.submit(()-> {
						try {
							SocketConnection connection = new SocketConnection(c);
							handle.handler(connection);
							connection.flush();
						} catch (IOException e) {
							log.error(e.getMessage(),e);
						}
					});
				}catch(Throwable t) {
					t.printStackTrace();
				}
			}

		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}finally {
			threadPool.shutdown();
		}
	}



}
