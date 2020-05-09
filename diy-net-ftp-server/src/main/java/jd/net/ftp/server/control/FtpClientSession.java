package jd.net.ftp.server.control;

import jd.net.protocol.common.datagram.Datagram;
import lombok.Data;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class FtpClientSession {

    private static final Map<String,FtpClientSession> sessions = new ConcurrentHashMap();

    private final int clientPort ;
    private final InetAddress clientAddress ;

    // auth
    private String user ;
    private final boolean enableAnonymity;
    private boolean authed ;
    private boolean authSuccess ;

    private FtpClientSession(Datagram datagram){
        this.enableAnonymity = FtpServerSetting.getSetting().isEnableAnonymity();
        this.clientPort = datagram.getRemotePort();
        this.clientAddress = datagram.getRemoteAddress();
    }

    public static FtpClientSession getSession(Datagram datagram){
        String key = datagram.getRemoteAddress().getHostAddress() + ":" + datagram.getRemotePort();
        FtpClientSession session = sessions.get(key);
        if(session == null){
            session = new FtpClientSession(datagram);
            FtpClientSession previous = sessions.putIfAbsent(key,session);
            if(previous != null){
                return previous;
            }
        }
        return session;
    }

    private static String getKey(Datagram datagram){
        String key = datagram.getRemoteAddress().getHostAddress() + ":" + datagram.getRemotePort();
        return key ;
    }

    public static void removeSession(Datagram datagram){
        sessions.remove(getKey(datagram));
    }
    public static void clearSession(Datagram datagram){
        sessions.clear();
    }


}
