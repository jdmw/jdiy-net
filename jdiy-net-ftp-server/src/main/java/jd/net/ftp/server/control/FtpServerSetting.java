package jd.net.ftp.server.control;

import jd.net.connector.server.ServerConfig;
import lombok.Data;

import java.util.Map;

@Data
public class FtpServerSetting extends ServerConfig {

    private boolean enablePassiveMode = true ;
    private boolean enableAnonymity = true ;
    private Map<String,String> users ;

    public FtpServerSetting(int port) {
        super(port);
    }

    public FtpServerSetting(String serverName ,int port) {
        super(serverName,port);
    }

    private static final FtpServerSetting setting = new FtpServerSetting(21);

    public static FtpServerSetting getSetting(){
        return setting;
    }
}
