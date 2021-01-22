package jd.server.context.sevlet;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.HttpResponseBaseInfo;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.server.context.sevlet.datagram.HttpResponseHeadersHolder;
import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.util.Assert;
import jd.util.StrUt;
import jd.util.lang.concurrent.ThreadSafeVar;
import jd.util.lang.exceptions.WrapperException;
import lombok.Getter;

import static com.sun.deploy.net.HttpUtils.LOCATION_HEADER;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static jd.server.context.sevlet.NetServletConstants.DATE_FORMAT;
import static jd.server.context.sevlet.NetServletConstants.GMT;

public class DefaultHttpServletResponse extends CommonServletResponse implements HttpServletResponse {

	public static final String CHARSET_PREFIX = "charset=";

	protected volatile boolean responseCommitted = false;
	private final DefaultHttpServletRequest request ;
	private final HttpDatagram httpDatagram ;
	private final ThreadSafeVar<String> characterEncoding ;
	private final ThreadSafeVar<Integer> contentLength = new ThreadSafeVar<>(0) ;
	private final HttpResponseHeadersHolder headersHolder ;

	protected DefaultHttpServletResponse(DefaultHttpServletRequest request, ContextConfiguration servletSettings, HttpDatagram requestData) {
		super(request,new HttpResponseBaseInfo(),servletSettings,requestData);
		this.request = request;
		this.httpDatagram = new HttpDatagram();
		httpDatagram.attach(requestData.getNetConnection());
		HttpResponseBaseInfo responseBaseInfo = (HttpResponseBaseInfo) this.responseBaseInfo;
		responseBaseInfo.setVersion("HTTP/1.1");
		responseBaseInfo.setStatusCode(200);
		responseBaseInfo.setStatusText("OK");
		characterEncoding = new ThreadSafeVar<>(null);
		headersHolder = new HttpResponseHeadersHolder(httpDatagram.getHeaderAll());
		headersHolder.setHeader(HttpHeader.HeaderName.CONTENT_TYPE,"text/html; charset=" + characterEncoding.getValue());
	}

	private HttpResponseBaseInfo getResponseBaseInfo(){
		return (HttpResponseBaseInfo) responseBaseInfo;
	}


	@Override
	public void addCookie(Cookie cookie) {
		headersHolder.addCookie(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return headersHolder.getHeader(name) != null;
	}

	/**
	 * Encodes the specified URL by including the session ID in it,
	 * or, if encoding is not needed, returns the URL unchanged.
	 * The implementation of this method includes the logic to
	 * determine whether the session ID needs to be encoded in the URL.
	 * For example, if the browser supports cookies, or session
	 * tracking is turned off, URL encoding is unnecessary.
	 *
	 * <p>For robust session tracking, all URLs emitted by a servlet
	 * should be run through this
	 * method.  Otherwise, URL rewriting cannot be used with browsers
	 * which do not support cookies.
	 *
	 * @param	url	the url to be encoded.
	 * @return		the encoded URL if encoding is needed;
	 * 			the unchanged URL otherwise.
	 */
	@Override
	public String encodeURL(String url) {
		return request.requestBaseInfo.getRequestURI();
	}

	/**
	 * Encodes the specified URL for use in the
	 * <code>sendRedirect</code> method or, if encoding is not needed,
	 * returns the URL unchanged.  The implementation of this method
	 * includes the logic to determine whether the session ID
	 * needs to be encoded in the URL.  Because the rules for making
	 * this determination can differ from those used to decide whether to
	 * encode a normal link, this method is separated from the
	 * <code>encodeURL</code> method.
	 *
	 * <p>All URLs sent to the <code>HttpServletResponse.sendRedirect</code>
	 * method should be run through this method.  Otherwise, URL
	 * rewriting cannot be used with browsers which do not support
	 * cookies.
	 *
	 * @param	url	the url to be encoded.
	 * @return		the encoded URL if encoding is needed;
	 * 			the unchanged URL otherwise.
	 *
	 * @see #sendRedirect
	 * @see #encodeUrl
	 */

	public String encodeRedirectURL(String url){
		return encodeURL(url);
	}

	/**
	 * @deprecated	As of version 2.1, use encodeURL(String url) instead
	 *
	 * @param	url	the url to be encoded.
	 * @return		the encoded URL if encoding is needed;
	 * 			the unchanged URL otherwise.
	 */

	public String encodeUrl(String url){
		Assert.notEmpty(url);
		try {
			return URLEncoder.encode(url,getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new WrapperException(e);
		}
	}

	/**
	 * @deprecated	As of version 2.1, use
	 *			encodeRedirectURL(String url) instead
	 *
	 * @param	url	the url to be encoded.
	 * @return		the encoded URL if encoding is needed;
	 * 			the unchanged URL otherwise.
	 */

	public String encodeRedirectUrl(String url){
		return encodeURL(url);
	}

	/**
	 * Sends an error response to the client using the specified
	 * status.  The server defaults to creating the
	 * response to look like an HTML-formatted server error page
	 * containing the specified message, setting the content type
	 * to "text/html", leaving cookies and other headers unmodified.
	 *
	 * If an error-page declaration has been made for the web application
	 * corresponding to the status code passed in, it will be served back in
	 * preference to the suggested msg parameter.
	 *
	 * <p>If the response has already been committed, this method throws
	 * an IllegalStateException.
	 * After using this method, the response should be considered
	 * to be committed and should not be written to.
	 *
	 * @param	statusCode	the error status code
	 * @param	msg	the descriptive message
	 * @exception	IOException	If an input or output exception occurs
	 * @exception	IllegalStateException	If the response was committed
	 */
	@Override
	public void sendError(int statusCode, String msg){
		if (isCommitted()) {
			throw new IllegalStateException("Cannot set error status - response is already committed");
		}
		if(statusCode <0 ){
			throw new IllegalArgumentException("status code " + statusCode +"is illegal");
		}
		setStatus(statusCode,msg);
		super.setCommitted();
	}

	/**
	 * Sends an error response to the client using the specified status
	 * code and clearing the buffer.
	 * <p>If the response has already been committed, this method throws
	 * an IllegalStateException.
	 * After using this method, the response should be considered
	 * to be committed and should not be written to.
	 *
	 * @param	sc	the error status code
	 * @exception	IOException	If an input or output exception occurs
	 * @exception	IllegalStateException	If the response was committed
	 *						before this method call
	 */

	public void sendError(int sc) throws IOException{
		setStatus(sc,null);
		setCommitted();
	}

	/**
	 * Sends a temporary redirect response to the client using the
	 * specified redirect location URL.  This method can accept relative URLs;
	 * the servlet container must convert the relative URL to an absolute URL
	 * before sending the response to the client. If the location is relative
	 * without a leading '/' the container interprets it as relative to
	 * the current request URI. If the location is relative with a leading
	 * '/' the container interprets it as relative to the servlet container root.
	 *
	 * <p>If the response has already been committed, this method throws
	 * an IllegalStateException.
	 * After using this method, the response should be considered
	 * to be committed and should not be written to.
	 *
	 * @param		location	the redirect location URL
	 * @exception	IOException	If an input or output exception occurs
	 * @exception	IllegalStateException	If the response was committed or
	if a partial URL is given and cannot be converted into a valid URL
	 */

	public void sendRedirect(String location) throws IOException{
		if (isCommitted()) {
			throw new IllegalStateException("Cannot send redirect - response is already committed");
		}
		Assert.notNull(location, "Redirect URL must not be null");
		setHeader(HttpHeader.HeaderName.LOCATION, location);
		setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		setCommitted();
	}

	public String getRedirectedUrl() {
		return headersHolder.getHeader(LOCATION_HEADER);
	}

	@Override
	public void setDateHeader(String name, long date) {
		headersHolder.setHeader(name,formatDate(date));
	}

	private String formatDate(long date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		dateFormat.setTimeZone(GMT);
		return dateFormat.format(new Date(date));
	}


	@Override
	public void addDateHeader(String name, long date) {
		headersHolder.addHeader(name,formatDate(date));
	}

	@Override
	public void setHeader(String name, String value) {
		if(setSpecialHeader(name,value)){
			return;
		}
		headersHolder.setHeader(name,value);
	}

	@Override
	public void addHeader(String name, String value) {
		if(setSpecialHeader(name,value)){
			return;
		}
		headersHolder.addHeader(name,value);
	}

	private boolean setSpecialHeader(String name, String value) {
		if (HttpHeader.HeaderName.CONTENT_TYPE.equalsIgnoreCase(name)) {
			setContentType(value.toString());
			return true;
		}
		else if (HttpHeader.HeaderName.CONTENT_LENGTH.equalsIgnoreCase(name)) {
			setContentLength(Integer.parseInt(value.toString()));
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void setIntHeader(String name, int value) {
		headersHolder.setHeader(name,Integer.toString(value));
	}

	@Override
	public void addIntHeader(String name, int value) {
		headersHolder.addHeader(name,Integer.toString(value));
	}

	@Override
	public void setStatus(int sc) {
		setStatus(sc,null);
	}

	@Override
	public void setStatus(int sc, String sm) {
		if (!this.isCommitted()) {
			synchronized (responseBaseInfo){
				if(sc > 0){
					getResponseBaseInfo().setStatusCode(sc);
				}
				if(StrUt.isNotBlank(sm)){
					getResponseBaseInfo().setStatusText(sm);
				}
			}
		}
	}

	public int getStatus() {
		return getResponseBaseInfo().getStatusCode();
	}



	@Override
	public void reset() {
		super.reset();
		this.getResponseBaseInfo().setStatusCode(SC_OK);
		this.getResponseBaseInfo().setStatusText("OK");
	}



}
