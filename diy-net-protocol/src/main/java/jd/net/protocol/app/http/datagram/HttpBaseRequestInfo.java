package jd.net.protocol.app.http.datagram;

import jd.util.Assert;
import lombok.Data;

@Data
public class HttpBaseRequestInfo implements HttpBaseInfo {
    String requestMethod ;
    String requestURIPath ;
    String requestURIQuery ;
    String requestVersion ;

    @Override
    public void parse(String line) {
        String[] args = line.split(" ");
        requestMethod = args[0];
        String requestURI = args[1];
        requestVersion = args[2];
        setRequestURI(requestURI);
    }

    public String toString() {
        Assert.notEmpty(requestMethod,requestURIPath,requestVersion);
        return (requestMethod + " " +  getRequestURI() + " " + requestVersion);
    }

    private String getRequestURI(){
        if(requestURIQuery != null && requestURIQuery.length() > 0 ){
            return requestURIPath + "?" + requestURIQuery;
        }else {
            return requestURIPath;
        }
    }
    private void setRequestURI(String requestURI ){
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
