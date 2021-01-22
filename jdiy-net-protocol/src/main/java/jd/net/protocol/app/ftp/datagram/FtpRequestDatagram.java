package jd.net.protocol.app.ftp.datagram;

import jd.net.protocol.common.ProtocolNames;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.text.OneLineTextData;
import jd.util.StrUt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Data
@Slf4j
public class FtpRequestDatagram extends OneLineTextData<FtpRequestDatagram> {

    private String requestCommand ;
    private String argument ;

    @Override
    public void read() throws IOException {
        super.setLowerDatagram(netConnection.getLowerLayout());
        byte[] bytes = netConnection.readByteArray();
        String line = StrUt.trim(new String(bytes));
        log.debug(line);
        setLine(line);
    }

    @Override
    public void setLine(String line) {
        if(StrUt.isNotBlank(line)){
            if(line.contains(" ")){
                this.requestCommand = line.substring(0,line.indexOf(" "));
                this.argument = StrUt.trim(line.substring(line.indexOf(" ")+1));
            }else{
                this.requestCommand = StrUt.trim(line);
            }
        }
    }

    public String toString(){
        return requestCommand + (StrUt.isNotBlank(argument) ? " " + argument : "") ;
    }

    @Override
    public List getLine() {
        return Arrays.asList(this);
    }

    @Override
    public String getProtocolName() {
        return ProtocolNames.FTP.name();
    }

}
