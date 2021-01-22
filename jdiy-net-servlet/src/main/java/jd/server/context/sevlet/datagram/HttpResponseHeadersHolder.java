package jd.server.context.sevlet.datagram;

import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.util.Assert;
import jd.util.lang.collection.MultiValueMap;
import lombok.AllArgsConstructor;
import lombok.Data;










import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static jd.server.context.sevlet.NetServletConstants.*;


@Data
public class HttpResponseHeadersHolder {

    private final MultiValueMap<String,String> headers ;
    private final Vector<Cookie> cookies = new Vector<>();
    private volatile Locale locale ;

    public HttpResponseHeadersHolder(MultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    //@Override
    public synchronized void addCookie(Cookie cookie) {
        Assert.notNull(cookie, "Cookie must not be null");
        this.cookies.add(cookie);
        doAddHeaderValue(HttpHeader.HeaderName.COOKIE,HttpSpecialHeaders.CookieHeader.toHeader(cookie),false);
    }
    
    public synchronized void setLocale(Locale loc) {
        Assert.notNull(loc);
        this.locale = locale ;
        doAddHeaderValue(HttpHeader.HeaderName.CONTENT_LANGUAGE,HttpSpecialHeaders.Language.toHeader(locale),true);
    }
    
    public Locale getLocale() {
        return locale;
    }


    public String getHeader(String name) {
        return headers.getFirstValue(name );
    }

    public void setHeader(String name, String value) {
        doAddHeaderValue(name, value, true);
    }

    public void addHeader(String name, String value) {
        doAddHeaderValue(name, value,false);
    }



    private void doAddHeaderValue(String name, String value, boolean replace) {
        Assert.notBlank(name,value);
        synchronized (headers){
            if(replace){
                headers.remove(name);
            }
            headers.add(name,value);
        }
    }







}
