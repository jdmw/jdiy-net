package jd.server.context.sevlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletResponse;

import jd.server.protocol.http.HttpHeaderNames;
import jd.server.protocol.http.HttpHeaderUtil;
import jd.server.protocol.handler.http.HttpRequestMessage;
import jd.server.context.sevlet.container.attrctn.ServletRequestAttributeContainer;
import jd.util.lang.concurrent.ThreadSafeVar;

public class CommonServletRequest extends HttpRequestMessage implements ServletRequest {

	private final ServletRequestAttributeContainer attrCtx;
	private final ServletContext sc ;
	
	
	private String characterEncoding ;
	private Vector<Locale> locales ;
	
	private final ThreadSafeVar<Boolean> supportAsync =  new ThreadSafeVar<>(false) ;
	private final ThreadSafeVar<Boolean> asyncStarted =  new ThreadSafeVar<>(false) ;
	
	
	protected CommonServletRequest(ServletContext ctx,List<ServletRequestAttributeListener> lis) {
		attrCtx = new ServletRequestAttributeContainer(ctx,this,lis);
		this.sc = ctx ;
	}
	
	
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.characterEncoding = env ; // TODO
	}
	
	@Override
	public Locale getLocale() {
		return getLocales().nextElement();
	}
	
	
	@Override
	public Enumeration<Locale> getLocales() {
		if(locales == null) {
			Enumeration<String> headers = super.getHeaders(HttpHeaderNames.ACCEPT_LANGUAGE);
			locales = HttpHeaderUtil.getAcceptLocales(headers);
		}
		return locales.isEmpty() ? new Vector<Locale>(Arrays.asList(getDefaultLocale())).elements(): locales.elements();
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
