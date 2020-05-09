package jd.net.protocol.app.http.datagram.cst;

public class HttpHeader {
    public static class HeaderName {
        public static final String CONTENT_TYPE = "Content-Type" ;
        public static final String CONTENT_LENGTH = "Content-Length" ;
        public static final String CONTENT_ENCODING = "Content-Encoding" ;
        public static final String CONNECTION = "Connection" ;
        public static final String ACCEPT = "Accept" ;
        public static final String ACCEPT_ENCODING = "Accept-Encoding" ;
        public static final String ACCEPT_LANGUAGE = "Accept-Language" ;
        public static final String HOST = "Host" ;
        public static final String COOKIE = "Cookie" ;
        public static final String CACHE_CONTROL = "Cache-Control" ;
        public static final String USER_AGENT = "User-Agent" ;

    }

    public static class Connection {
        public static final String NAME = "Connection" ;
        public static final String CLOSE = "close" ;
        public static final String KEEP_ALIVE = "keep-alive" ;
    }
}
