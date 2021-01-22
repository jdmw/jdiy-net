package jd.net.protocol.app.http.datagram;

import jd.util.Assert;
import lombok.Data;

@Data
public class HttpRequestBaseInfo implements HttpBaseInfo {
    String requestMethod ;
    String requestURI ;
    String requestURIPath ;
    String requestURIQuery ;
    String requestVersion ;

    @Override
    public void parse(String line) {
        String[] args = line.split(" ");
        requestMethod = args[0];
        requestVersion = args[2];
        setRequestURI(args[1]);
    }

    public static HttpRequestBaseInfo of(HttpDatagram datagram){
        Assert.notNull(datagram);
        Assert.notBlank(datagram.getFirstLine());
        HttpRequestBaseInfo HttpRequestBaseInfo = new HttpRequestBaseInfo();
        HttpRequestBaseInfo.parse(datagram.getFirstLine());
        return HttpRequestBaseInfo;
    }

    public String toString() {
        Assert.notEmpty(requestMethod,requestURIPath,requestVersion);
        return (requestMethod + " " +  getRequestURI() + " " + requestVersion);
    }

    public void setRequestURI(String requestURI ){
        this.requestURI = requestURI;
        int index = requestURI.indexOf("?");
        if( index != -1 ){
            requestURIPath = requestURI.substring(0,index);
            requestURIQuery = requestURI.substring(index+1);
        }else {
            requestURIPath = requestURI;
            requestURIQuery = null ;
        }
    }
}
