package jd.server.context.sevlet.datagram;

import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.util.Assert;
import jd.util.StrUt;
import jd.util.lang.collection.MultiValueMap;
import jd.util.lang.time.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static jd.server.context.sevlet.NetServletConstants.*;


@Data
public class HttpRequestHeadersHolder {

    private final MultiValueMap<String,String> headers ;
    private final Vector<Cookie>  cookies ;
    private volatile Vector<Locale> locales ;
    private volatile boolean specialHeaderModified ;

    public HttpRequestHeadersHolder() {
        this.headers = new MultiValueMap<String,String>();
        cookies = new Vector<>(); // thread-safe
        locales = new Vector<>();
    }

    public HttpRequestHeadersHolder(MultiValueMap<String, String> headers) {
        this.headers = headers;
        String cookieHeader = headers.getFirstValue(HttpHeader.HeaderName.COOKIE);
        this.cookies = StrUt.isNotBlank(cookieHeader) ? new Vector<>(Arrays.asList(HttpSpecialHeaders.CookieHeader.parse(cookieHeader))) :new Vector<>();
    }

    public Cookie[] getCookies() {
        return this.cookies.toArray(new Cookie[cookies.size()]);
    }

    public Cookie getCookie(String name) {
        Assert.notNull(name, "Cookie name must not be null");
        for (Cookie cookie : this.cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public Vector<Locale> getLocales(Locale defaultLocale){
        if(locales == null){
            synchronized (this) {
                String header = headers.getFirstValue(HttpHeader.HeaderName.ACCEPT_LANGUAGE);
                if (StrUt.isNotBlank(header)) {
                    locales = HttpSpecialHeaders.Language.parse(header);
                } else {
                    locales = new Vector<>();
                }
                if(locales.isEmpty()){
                    locales.add(defaultLocale);
                }
            }
        }
        return locales ;
    }


    //// @Override
    public boolean containsHeader(String name) {
        return headers.getFirstValue(name) != null ;
    }

    //// @Override
    public Vector<String> getHeaderNames() {
        return new Vector<>(this.headers.keySet());
    }

    //// @Override
    public String getHeader(String name) {
        return this.headers.getFirstValue(name);
    }


    /**
     * Return the long timestamp for the date header with the given {@code name}.
     * <p>If the internal value representation is a String, this method will try
     * to parse it as a date using the supported date formats:
     * <ul>
     * <li>"EEE, dd MMM yyyy HH:mm:ss zzz"</li>
     * <li>"EEE, dd-MMM-yy HH:mm:ss zzz"</li>
     * <li>"EEE MMM dd HH:mm:ss yyyy"</li>
     * </ul>
     * @param name the header name
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    // @Override
    public long getDateHeader(String name) {
        String dateStr = headers.getFirstValue(name);
        if(StrUt.isNotBlank(dateStr)){
            String errorMsg = "Value for header '" + name + "' is not a valid Date: " + dateStr ;
            Date date = DateUtil.getDate(dateStr,Locale.US,GMT,errorMsg, DATE_FORMATS);
            if(date != null){
                return date.getTime();
            }
        }
        return -1 ;
    }

    private String formatDate(long date) {
        return newDateFormat().format(new Date(date));
    }

    private DateFormat newDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(GMT);
        return dateFormat;
    }


    private long parseDateHeader(String name, String value) {
        for (String dateFormat : DATE_FORMATS) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            simpleDateFormat.setTimeZone(GMT);
            try {
                return simpleDateFormat.parse(value).getTime();
            }
            catch (ParseException ex) {
                // ignore
            }
        }
        throw new IllegalArgumentException("Cannot parse date value '" + value + "' for '" + name + "' header");
    }

    // @Override
    public int getIntHeader(String name) {
        String header = getHeader(name);
        if(StrUt.isNotBlank(header)){
            return Integer.parseInt(header);
        }
        return -1 ;
    }

    //// @Override
    public Vector<String> getHeaders(String name) {
        List<String> header = this.headers.get(name);
        if (header != null) {
            return new Vector<>(header) ;
        }
        else {
            return new Vector<>();
        }
    }

    public MultiValueMap<String, String> getHeaders(Locale defaultLocale) {
        if(specialHeaderModified){
            String contentLanguage = HttpSpecialHeaders.Language.toHeader(locales) ;
            //if(contentLanguage)
            Vector<Locale> locales = this.getLocales(defaultLocale);
            if(locales != null && !locales.isEmpty()){
                contentLanguage = HttpSpecialHeaders.Language.toHeader(locales);
            }

            synchronized (this){
                if(StrUt.isNotBlank(contentLanguage)){
                    headers.add(HttpHeader.HeaderName.CONTENT_LANGUAGE,contentLanguage);
                }
                specialHeaderModified = false ;
            }
        }
        return headers;
    }
}
