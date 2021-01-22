package jd.net.ftp.server.control.auth;

import jd.net.ftp.server.FtpServerHandler;
import jd.net.ftp.server.control.FtpClientSession;
import jd.net.ftp.server.control.FtpServerSetting;
import jd.net.protocol.app.ftp.datagram.FtpRequestDatagram;
import jd.net.protocol.app.ftp.datagram.FtpResponseDatagram;
import jd.net.protocol.app.ftp.datagram.cst.FTPReturnCode;
import jd.net.protocol.app.ftp.datagram.cst.FtpCommands;
import jd.net.protocol.common.connection.NetConnection;
import lombok.extern.slf4j.Slf4j;

import javax.print.DocFlavor;
import java.io.IOException;

@Slf4j
public class FtpAuthStateMachine {

    private static enum State {
        READY,
        USER_INPUTED,
        PASS_INPUTED,
        AUTH_SUCCESS,
        AUTH_FAILD
    }
    public static void handleUntilAuth(FtpRequestDatagram datagram, NetConnection con,
                                       FtpServerSetting setting, FtpClientSession session) throws IOException {
        log.debug("receive {} : {}" ,datagram.getRequestCommand(),datagram.getArgument());
        FtpResponseDatagram response = null;
        State state = State.READY ;
        datagram.attach(con);
        while (!con.checkIsClosed()){
            if(con.isNew()){
                // ask for username and password
                response = new FtpResponseDatagram(FTPReturnCode.READY_FOR_NEW_USER,"("+ FtpServerHandler.APP_NAME+")");
                con.setNew(false);
            }else{
                datagram.read();
                String reqCmd = datagram.getRequestCommand();

                // quit
                if(FtpCommands.QUIT.equals(reqCmd)){
                    con.close();
                    return  ;
                }
                switch (state){
                    case READY:{
                        if(FtpCommands.USER.equals(reqCmd)){
                            if(setting.getUsers().containsKey(datagram.getArgument())){
                                response = new FtpResponseDatagram(FTPReturnCode.NAME_OK,"Please specify the password");
                            }else{
                                response = new FtpResponseDatagram(FTPReturnCode.NEG_INVALIED_USER_OR_PASS,"Please specify the password");
                            }
                        }else {
                            //response = new FtpResponseDatagram(FTPReturnCode.READY_FOR_NEW_USER,"("+ FtpServerHandler.APP_NAME+")");
                            // ignore
                            con.flush();
                        }
                    }
                }
            }
            response.attach(con);
            response.write();
        }
    }


}
