package jd.server.context.sevlet;

import jd.net.protocol.app.http.datagram.HttpRequestBaseInfo;
import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.server.context.sevlet.container.attrctn.ServletRequestAttributeContainer;
import jd.server.context.sevlet.datagram.HttpRequestHeadersHolder;
import jd.server.context.sevlet.datagram.ServletInputStreamHolder;
import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.util.StrUt;
import jd.util.io.net.NetResourceLocationUtil;
import jd.util.lang.collection.MultiValueMap;
import jd.util.lang.collection.ObjArrayMap;
import jd.util.lang.concurrent.ThreadSafeVar;
import lombok.Data;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Data
public class CommonServletRequest implements ServletRequest {

	private String protocol = "HTTP" ;
	private String scheme = protocol ;

	private final ServletRequestAttributeContainer attrCtx;
	protected final ServletContext servletContext ;
	protected final HttpDatagram httpDatagram ;
	protected final ContextConfiguration servletSettings ;
	protected final HttpRequestBaseInfo requestBaseInfo ;
	protected final ServletInputStreamHolder servletInputStreamHolder ;
	protected final HttpRequestHeadersHolder headersHolder ;
	private  volatile ObjArrayMap<String,String> parameterMap ;

	private final ThreadSafeVar<Boolean> supportAsync =  new ThreadSafeVar<>(false) ;
	private final ThreadSafeVar<Boolean> asyncStarted =  new ThreadSafeVar<>(false) ;
	private final ThreadSafeVar<Boolean> isSecure =  new ThreadSafeVar<>(false) ;
	private final ThreadSafeVar<String> characterEncoding ;

	protected CommonServletRequest(HttpDatagram httpDatagram , DefaultServletContext ctx) {
		attrCtx = new ServletRequestAttributeContainer(ctx,this,ctx.getCfg().getRequestAttributeListener());
		this.servletContext = ctx ;
		this.httpDatagram = httpDatagram ;
		this.requestBaseInfo = HttpRequestBaseInfo.of(httpDatagram);
		this.servletSettings = ctx.getCfg();
		this.headersHolder = new HttpRequestHeadersHolder(Optional.ofNullable(httpDatagram.getHeaderAll()).orElseGet(()->new MultiValueMap<>()));
		this.characterEncoding = new ThreadSafeVar(servletSettings.getDefaultRequestEncoding());
		this.servletInputStreamHolder = new ServletInputStreamHolder(httpDatagram,characterEncoding);
	}
	
	
	@Override
	public String getCharacterEncoding() {
		return characterEncoding.getValue();
	}

	@Override
	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		this.characterEncoding.setValue(encoding); ;
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
		return headersHolder.getLocales(servletSettings.getDefaultLocale()).get(0);
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return headersHolder.getLocales(servletSettings.getDefaultLocale()).elements();
	}

	@Override
	public boolean isSecure() {
		return "HTTPS".equalsIgnoreCase(getScheme());
	}


	@Override
	public ServletInputStream getInputStream() throws IOException {
		return servletInputStreamHolder.getInputStream();
	}

	@Override
	public String getParameter(String s) {
		return getParameterMap().getParameter(s);
	}

	@Override
	public Enumeration getParameterNames() {
		return getParameterMap().getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return getParameterMap().get(name);
	}

	@Override
	public ObjArrayMap<String,String> getParameterMap() {
		if(parameterMap == null){
			ObjArrayMap<String,String> map = NetResourceLocationUtil.parseQueryString(requestBaseInfo.getRequestURIQuery(),characterEncoding.getValue());
			if(map == null){
				map = new ObjArrayMap<>();
			}
			synchronized (this){
				parameterMap = map ;
			}
		}
		return parameterMap;
	}



	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public String getServerName() {
		return servletSettings.getServerSettings().getServerName() ;
	}

	@Override
	public int getServerPort() {
		return httpDatagram.getLowerDatagram().getLocalPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return servletInputStreamHolder.getReader();
	}

	@Override
	public String getRemoteAddr() {
		return httpDatagram.getLowerDatagram().getRemoteAddress().getHostAddress();
	}

	@Override
	public String getRemoteHost() {
		String header = this.headersHolder.getHeader(HttpHeader.HeaderName.HOST);
		if(StrUt.isBlank(header)){
			header =  httpDatagram.getLowerDatagram().getRemoteAddress().getHostName();
		}
		return header;
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
		return httpDatagram.getLowerDatagram().getRemotePort();
	}

	@Override
	public String getLocalName() {
		return httpDatagram.getLowerDatagram().getLocalAddress().getHostName();
	}

	@Override
	public String getLocalAddr() {
		return NetResourceLocationUtil.getIpStringFromBytes(httpDatagram.getLowerDatagram().getLocalAddress().getAddress());
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


}
