package jd.net.ftp.server;

import jd.net.connector.server.CommonServer;
import jd.net.connector.server.ServerContextHandle;
import jd.net.connector.server.handler.io.ServerHandlerIOType;
import jd.net.ftp.server.control.FtpClientSession;
import jd.net.ftp.server.control.FtpServerSetting;
import jd.net.ftp.server.control.auth.FtpAuthStateMachine;
import jd.net.protocol.app.ftp.datagram.FtpRequestDatagram;
import jd.net.protocol.common.connection.NetConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FtpServerHandler implements ServerContextHandle {


    public final static String APP_NAME = "JD-FTP-0.0.1" ;
    @Override
    public void handler(NetConnection connection) throws IOException {
        FtpClientSession session = FtpClientSession.getSession(connection.getLowerLayout());
        FtpRequestDatagram datagram = new FtpRequestDatagram();
        datagram.attach(connection);
        if(connection.isNew()){
            log.info("ftp connection from " +datagram.getRemoteAddress());
            /*connection.setCloseAction(()->{
                log.info("ftp quit at {}, from client{} " ,datagram.getLocalPort(),datagram.getRemoteAddress());
                FtpClientSession.removeSession(datagram);
            });*/
        }
        try {
            if(!session.isEnableAnonymity()) {
                FtpAuthStateMachine.handleUntilAuth(datagram, connection,FtpServerSetting.getSetting(),session);
            }
            while (!connection.checkIsClosed()){
                datagram.read();
                handleAfterAuth(datagram,connection);
            }
        } catch (SocketException se){
            log.info("ftp quit at {}, from client{} " ,datagram.getLocalPort(),datagram.getRemoteAddress());
            FtpClientSession.removeSession(datagram);
        } catch (IOException e) {
            //connection.checkIsClose(e);
            log.error(e.getMessage(),e);
            //throw e ;
        }
    }

    public void handleAfterAuth(FtpRequestDatagram datagram,NetConnection connection) {
        log.debug("receive {} : {}" ,datagram.getRequestCommand(),datagram.getArgument());
    }

    public static void main(String[] args) {
        FtpServerSetting setting = FtpServerSetting.getSetting();
        setting.setEnableAnonymity(false);
        Map<String,String> users = new HashMap<>();
        users.put("ftp","@jdmw1234");
        users.put("jdmw","@hubery1234");
        setting.setUsers(users);
        new CommonServer()
                .config(setting)
                .handle(new FtpServerHandler())
                .type(ServerHandlerIOType.BIO)
                .startupInNewThread();

    }
}
