package jd.server.context.sevlet;

import lombok.Data;

@Data
public class ServerSettings {
    private String serverName = "JdServer/1.0" ;
    private String serverInfo;
/*    private String protocol = "HTTP" ;
    private String scheme = "HTTP" ;*/
}
