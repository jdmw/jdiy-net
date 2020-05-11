package jd.net.protocol.app.http.datagram;

import jd.net.protocol.common.datagram.ResponseBaseInfo;
import jd.util.Assert;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Setter
@Slf4j
public class HttpResponseBaseInfo extends ResponseBaseInfo implements HttpBaseInfo {

    String version ;
    protected Integer statusCode ;
    // the descriptive message
    protected String statusText ;

    @Override
    public void parse(String firstLine) {
        int idxBlankInFirstLine = firstLine.indexOf(" ");
        this.version = firstLine.substring(0,idxBlankInFirstLine);
        int idx2BlankInFirstLine = firstLine.indexOf(" ",idxBlankInFirstLine+1);
        this.statusCode = Integer.valueOf(firstLine.substring(idxBlankInFirstLine+1,idx2BlankInFirstLine));
        this.statusText = firstLine.substring(idx2BlankInFirstLine+1);
    }

    @Override
    public String toString() {
        Assert.notEmpty(version,statusText);
        Objects.requireNonNull(statusCode);
        return version + " " +  statusCode + " "+ statusText;
    }


}
