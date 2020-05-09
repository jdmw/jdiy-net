package jd.server.context.sevlet;

import jd.net.protocol.app.http.datagram.HttpRequestBaseInfo;
import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.server.context.sevlet.container.attrctn.ServletRequestAttributeContainer;
import jd.server.context.sevlet.datagram.HttpHeadersHolder;
import jd.server.context.sevlet.datagram.ServletSettings;
import jd.util.lang.collection.MultiValueMap;
import jd.util.lang.concurrent.ThreadSafeVar;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class CommonServletRequest implements ServletRequest {

	private final ServletRequestAttributeContainer attrCtx;
	private final ServletContext sc ;
	private final HttpDatagram httpDatagram ;
	private final HttpHeadersHolder headersHolder ;
	private final ServletSettings servletSettings ;
	private final HttpRequestBaseInfo requestBaseInfo ;
	
	private String characterEncoding ;
	private Vector<Locale> locales ;
	
	private final ThreadSafeVar<Boolean> supportAsync =  new ThreadSafeVar<>(false) ;
	private final ThreadSafeVar<Boolean> asyncStarted =  new ThreadSafeVar<>(false) ;
	private final ThreadSafeVar<Boolean> isSecure =  new ThreadSafeVar<>(false) ;

	protected CommonServletRequest(HttpDatagram httpDatagram ,ServletSettings servletSettings, ServletContext ctx,List<ServletRequestAttributeListener> lis) {
		attrCtx = new ServletRequestAttributeContainer(ctx,this,lis);
		this.sc = ctx ;
		this.httpDatagram = httpDatagram ;
		this.requestBaseInfo = HttpRequestBaseInfo.of(httpDatagram);
		this.servletSettings = servletSettings;
		this.headersHolder = new HttpHeadersHolder(Optional.ofNullable(httpDatagram.getHeaderAll()).orElseGet(()->new MultiValueMap<>()));
		this.characterEncoding = servletSettings.getDefaultRequestEncoding();

	}
	
	
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.characterEncoding = env ;
	}

	@Override
	public int getContentLength() {
		return Optional.ofNullable(httpDatagram.getBody()).map(d->d.getContentLength()).orElse(0);
	}

	@Override
	public String getContentType() {
		return httpDatagram.getHeader(HttpHeader.HeaderName.CONTENT_TYPE);
	}

	@Override
	public Locale getLocale() {
		//TODO
		return getLocales().nextElement();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		if(locales == null) {
			Enumeration<String> headers = httpDatagram.getHeaders(HttpHeader.HeaderName.ACCEPT_LANGUAGE);
			locales = HttpHeaderUtil.getAcceptLocales(headers);
		}
		return locales.isEmpty() ? new Vector<Locale>(Arrays.asList(servletSettings.getDefaultLocale())).elements(): locales.elements();
	}

	@Override
	public boolean isSecure() {
		return isSecure.getValue();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final InputStream is = super.getIs() ;
		return new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return is.read();
			}
		};
	}

	@Override
	public String getParameter(String s) {
		return null;
	}

	@Override
	public Enumeration getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String s) {
		return new String[0];
	}

	@Override
	public Map getParameterMap() {
		return null;
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return null;
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public String getRemoteHost() {
		return null;
	}


	/*******************************************************************************************
	 * 				Attributes
	 ********************************************************************************************/
	@Override
	public Object getAttribute(String name) {
		return attrCtx.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return attrCtx.getAttributeNames();
	}


	@Override
	public void setAttribute(String name, Object o) {
		attrCtx.setAttribute(name, o);
		
	}

	@Override
	public void removeAttribute(String name) {
		attrCtx.removeAttribute(name);
		
	}

	/*******************************************************************************************
	 * 				DispatcherType
	 ********************************************************************************************/
	
	private DispatcherType dispatcherType = DispatcherType.REQUEST ;

	public DispatcherType getDispatcherType() {
		return dispatcherType;
	}


	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}


	/*******************************************************************************************
	 * 				Async 
	 ********************************************************************************************/
	
	

	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		asyncStarted.setValue(true); ;
		return null;
	}

	public boolean isAsyncStarted() {
		return asyncStarted.getValue();
	}

	public boolean isAsyncSupported() {
		return supportAsync.getValue();
	}

	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletContext getServletContext() {
		return sc;
	}


}
