package jd.server.context.sevlet;

import java.util.TimeZone;

/**
 * Created by huangxia on 2020/5/9.
 */
public class NetServletConstants {

    public static final String HTTP = "http";

    public static final String HTTPS = "https";

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    /**
     * Date formats as specified in the HTTP RFC
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    public static final String[] DATE_FORMATS = new String[] {
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd-MMM-yy HH:mm:ss zzz",
            "EEE MMM dd HH:mm:ss yyyy"
    };


    // ---------------------------------------------------------------------
    // Public constants
    // ---------------------------------------------------------------------

    /**
     * The default protocol: 'HTTP/1.1'.
     * @since 4.3.7
     */
    public static final String DEFAULT_PROTOCOL = "HTTP/1.1";

    /**
     * The default scheme: 'http'.
     * @since 4.3.7
     */
    public static final String DEFAULT_SCHEME = HTTP;

    /**
     * The default server address: '127.0.0.1'.
     */
    public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";

    /**
     * The default server name: 'localhost'.
     */
    public static final String DEFAULT_SERVER_NAME = "localhost";

    /**
     * The default server port: '80'.
     */
    public static final int DEFAULT_SERVER_PORT = 80;

    /**
     * The default remote address: '127.0.0.1'.
     */
    public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";

    /**
     * The default remote host: 'localhost'.
     */
    public static final String DEFAULT_REMOTE_HOST = "localhost";

}
