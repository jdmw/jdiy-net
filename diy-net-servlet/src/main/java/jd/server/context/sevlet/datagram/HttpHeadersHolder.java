package jd.server.context.sevlet.datagram;

import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.util.Assert;
import jd.util.lang.collection.MultiValueMap;
import lombok.AllArgsConstructor;
import lombok.Data;










import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static jd.server.context.sevlet.NetServletConstants.*;


@Data
@AllArgsConstructor
public class HttpHeadersHolder {

    private MultiValueMap<String,String> headers ;
    private final List<Cookie> cookies = new ArrayList<>();


    //@Override
    public void addCookie(Cookie cookie) {
        HttpServletResponse response ;
        Assert.notNull(cookie, "Cookie must not be null");
        this.cookies.add(cookie);
    }

    public Cookie[] getCookies() {
        return this.cookies.toArray(new Cookie[this.cookies.size()]);
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

    //@Override
    public boolean containsHeader(String name) {
        return headers.getFirstValue(name) != null ;
    }

    //@Override
    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }


    //@Override
    public String getHeader(String name) {
        return this.headers.getFirstValue(name);
    }


    /**
     * Add a header entry for the given name.
     * <p>While this method can take any {@code Object} as a parameter, it
     * is recommended to use the following types:
     * <ul>
     * <li>String or any Object to be converted using {@code toString()}; see {@link #getHeader}.</li>
     * <li>String, Number, or Date for date headers; see {@link #getDateHeader}.</li>
     * <li>String or Number for integer headers; see {@link #getIntHeader}.</li>
     * <li>{@code String[]} or {@code Collection<String>} for multiple values; see {@link #getHeaders}.</li>
     * </ul>
     * @see #getHeaderNames
     * @see #getHeaders
     * @see #getHeader
     * @see #getDateHeader
     */
    public void addHeader(String name, Object value) {
        if (CONTENT_TYPE_HEADER.equalsIgnoreCase(name) && !this.headers.containsKey(CONTENT_TYPE_HEADER)) {
            setContentType(value.toString());
        }
        else {
            doAddHeaderValue(name, value, false);
        }
    }

    private void doAddHeaderValue(String name, Object value, boolean replace) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Assert.notNull(value, "Header value must not be null");
        if (header == null || replace) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (value instanceof Collection) {
            header.addValues((Collection<?>) value);
        }
        else if (value.getClass().isArray()) {
            header.addValueArray(value);
        }
        else {
            header.addValue(value);
        }
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
    @Override
    public long getDateHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object value = (header != null ? header.getValue() : null);
        if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        else if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        else if (value instanceof String) {
            return parseDateHeader(name, (String) value);
        }
        else if (value != null) {
            throw new IllegalArgumentException(
                    "Value for header '" + name + "' is not a Date, Number, or String: " + value);
        }
        else {
            return -1L;
        }
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

    @Override
    public String getHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return (header != null ? header.getStringValue() : null);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return Collections.enumeration(header != null ? header.getStringValues() : new LinkedList<String>());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object value = (header != null ? header.getValue() : null);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        else if (value != null) {
            throw new NumberFormatException("Value for header '" + name + "' is not a Number: " + value);
        }
        else {
            return -1;
        }
    }

    //@Override
    public List<String> getHeaders(String name) {
        List<String> header = this.headers.get(name);
        if (header != null) {
            return header ;
        }
        else {
            return Collections.emptyList();
        }
    }

    
    //@Override
    public void setDateHeader(String name, long value) {
        setHeaderValue(name, formatDate(value));
    }

    //@Override
    public void addDateHeader(String name, long value) {
        addHeaderValue(name, formatDate(value));
    }

    public long getDateHeader(String name) {
        String headerValue = getHeader(name);
        if (headerValue == null) {
            return -1;
        }
        try {
            return newDateFormat().parse(getHeader(name)).getTime();
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(
                    "Value for header '" + name + "' is not a valid Date: " + headerValue);
        }
    }

    private String formatDate(long date) {
        return newDateFormat().format(new Date(date));
    }

    private DateFormat newDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(GMT);
        return dateFormat;
    }

    //@Override
    public void setHeader(String name, String value) {
        setHeaderValue(name, value);
    }

    //@Override
    public void addHeader(String name, String value) {
        addHeaderValue(name, value);
    }

    //@Override
    public void setIntHeader(String name, int value) {
        setHeaderValue(name, value);
    }

    //@Override
    public void addIntHeader(String name, int value) {
        addHeaderValue(name, value);
    }

    private void setHeaderValue(String name, Object value) {
        if (setSpecialHeader(name, value)) {
            return;
        }
        doAddHeaderValue(name, value, true);
    }

    private void addHeaderValue(String name, Object value) {
        if (setSpecialHeader(name, value)) {
            return;
        }
        doAddHeaderValue(name, value, false);
    }

    private boolean setSpecialHeader(String name, Object value) {
        if (HttpHeader.HeaderName.CONTENT_TYPE.equalsIgnoreCase(name)) {
            setContentType(value.toString());
            return true;
        }
        else if (CONTENT_LENGTH_HEADER.equalsIgnoreCase(name)) {
            setContentLength(value instanceof Number ? ((Number) value).intValue() :
                    Integer.parseInt(value.toString()));
            return true;
        }
        else {
            return false;
        }
    }

    private void doAddHeaderValue(String name, Object value, boolean replace) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Assert.notNull(value, "Header value must not be null");
        if (header == null) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (replace) {
            header.setValue(value);
        }
        else {
            header.addValue(value);
        }
    }





}
