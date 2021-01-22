package jd.net.protocol.common.datagram.text;

import jd.net.protocol.common.connection.NetConnection;

import java.io.IOException;
import java.util.Scanner;

public abstract class OneLineTextData<T> extends LineBasedTextData<T> {

    @Override
    public void read() throws IOException {
        super.setLowerDatagram(netConnection.getLowerLayout());
        Scanner ps = new Scanner(netConnection.getInputStream());
        ps.useDelimiter(NEWLINE);
        String line = ps.next();
        setLine(line);
    }

    public abstract void setLine(String line);
}
