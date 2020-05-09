package jd.net.protocol.app.ftp.datagram;

import jd.net.protocol.common.ProtocolNames;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.text.LineBasedTextData;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Data
public class FtpLinesDataDatagram extends LineBasedTextData<String> {

    List<String> data ;

    public FtpLinesDataDatagram(List<String> data){
        this.data = data ;
    }

    @Override
    public List<String> getLine() {
        return data;
    }

    @Override
    public String getProtocolName() {
        return ProtocolNames.TCP.name();
    }

    @Override
    public void read() throws IOException {
        setLowerDatagram(netConnection.getLowerLayout());
        data = new ArrayList<>();
        Scanner scanner = new Scanner(netConnection.getInputStream());
        scanner.useDelimiter(NEWLINE);
        while (scanner.hasNext()){
            data.add(scanner.next());
        }
    }
}
