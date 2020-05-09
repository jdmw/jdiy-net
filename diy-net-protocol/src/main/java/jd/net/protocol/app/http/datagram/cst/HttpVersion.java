package jd.net.protocol.app.http.datagram.cst;


public enum  HttpVersion {

    HTTP_0_9(0.9f),HTTP_1_0(1.0f),HTTP_1_1(1.1f);

    private String  version ;
    private HttpVersion(float version) {
        this.version = "HTTP/" + version;
    }
    public String getVersion() {
        return version;
    }


}

