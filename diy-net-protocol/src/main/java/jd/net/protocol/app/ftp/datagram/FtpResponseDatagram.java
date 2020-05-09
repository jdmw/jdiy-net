package jd.net.protocol.app.ftp.datagram;

import jd.net.protocol.common.ProtocolNames;
import jd.net.protocol.common.datagram.text.OneLineTextData;
import jd.util.StrUt;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class FtpResponseDatagram extends OneLineTextData<FtpResponseDatagram> {

    private String responsCode ;
    private String argument ;

    public FtpResponseDatagram(){}
    public FtpResponseDatagram(String responsCode, String argument) {
        this.responsCode = responsCode;
        this.argument = argument;
    }

    @Override
    public void setLine(String line) {
        if(StrUt.isNotBlank(line)){
            if(line.contains(" ")){
                this.responsCode = line.substring(0,line.indexOf(" "));
                this.argument = StrUt.trim(line.substring(line.indexOf(" ")+1));
            }else{
                this.responsCode = StrUt.trim(line);
            }
        }
    }

    public String toString(){
        return responsCode + (StrUt.isNotBlank(argument) ? " " + argument : "") ;
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
