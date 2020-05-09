package jd.net.connector.server;

import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

@Data
public abstract class ServerConfig {

	private String serverName;
	private int serverPort ;
	private Locale defaultLocale = Locale.getDefault() ;
	
	public ServerConfig(int port) {
		this(null, port);
	}

	public ServerConfig(String serverName ,int port) {
		this.serverName = serverName ;
		this.serverPort = port ;
	}

	/**
	 * Returns the host name of the server to which the request was sent. It is the resolved server name,
	 * or the server IP address.
	 * @return
	 */
	public String getServerName() {
		if(serverName != null) {
			return serverName ;
		}else {
			try {
				return InetAddress.getLocalHost().getHostAddress() ;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return "" ;
			}
		}
	}
	
	
}
