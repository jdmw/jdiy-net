package jd.server.context.sevlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.http.*;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.server.context.sevlet.container.DefaultHttpSession;
import jd.server.context.sevlet.container.DefaultSessionContext;
import jd.util.StrUt;

public class DefaultHttpServletRequest extends CommonServletRequest implements HttpServletRequest {

	private final DefaultHttpServletResponse response ;
	private final DefaultSessionContext sessionContext ;
	protected DefaultHttpServletRequest(HttpDatagram httpDatagram , DefaultServletContext servletContext) {
		super(httpDatagram,servletContext);
		sessionContext = new DefaultSessionContext(servletContext,servletContext.getCfg());
		response = new DefaultHttpServletResponse(this,servletContext.getCfg(),httpDatagram);
	}

	protected DefaultHttpServletResponse getResponse() {
		return response ;
	}
	
	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return requestBaseInfo.getRequestURIPath();
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return getPathInfo();
	}

	@Override 
	public String getContextPath() {
		return super.getServletContext().getContextPath();
	}

	@Override
	public String getQueryString() {
		return requestBaseInfo.getRequestURIQuery();
	}

	@Override
	public String getServletPath() {
		return servletContext.getContextPath();
	}

	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	
	/***********************************************************************
	 *    Session
	 **********************************************************************/
	
	protected DefaultHttpSession currentSession ;
	
	private final String JSESSIONID = "JSESSIONID" ;
	private boolean sessionIdParsed = false ;
	private boolean sessionIdFromCookie = false ;
	private boolean sessionIdFromUrl = false ;
	private String requesedSessionId ;
	
	
	@Override
	public DefaultHttpSession getSession(boolean create) {
		if(response.responseCommitted) {
			throw new IllegalStateException("the response is committed");
		}
		if(currentSession == null) {
			// TODO ? If the container is using cookies to maintain session integrity and is asked to create a new session when the response is committed, an IllegalStateException is thrown.
			String requestedSessionId = getRequestedSessionId();
			currentSession = sessionContext.getValiditySession(requestedSessionId);
			if(create && currentSession == null) {
				if(isRequestedSessionIdFromCookie()) {
					throw new IllegalStateException("container is using cookies to maintain session integrity,creating a new session is forbidded");
				}
				currentSession = sessionContext.newSession();
			}
			if(currentSession != null) {
				currentSession.associate();
			}
		}/*else {
			if(!sessctx.checkValidation(currentSession)) {
				
			}
		}*/
		return currentSession;
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}


	/**
	 * Checks whether the requested session ID is still valid.
	 * If the client did not specify any session ID, this method returns false.
	 */
	@Override
	public boolean isRequestedSessionIdValid() {
		String id = getRequestedSessionId();
		return id != null ? sessionContext.checkValidation(id):false ;
	}

	@Override
	public String getRequestedSessionId() {
		if(!sessionIdParsed) {
			Cookie cookie = headersHolder.getCookie(JSESSIONID);
			if(cookie != null && StrUt.isNotBlank(requesedSessionId = cookie.getValue())) {
				sessionIdFromCookie = true ;
			}else {
				// TODO use sessionId as a parameter
				requesedSessionId = super.getParameter(JSESSIONID);
				if(StrUt.isNotBlank(requesedSessionId)) {
					sessionIdFromUrl = true ;
				}
			}
		}
		return requesedSessionId;
	}

	@Override
	public String getRequestURI() {
		return requestBaseInfo.getRequestURIPath();
	}

	@Override
	public StringBuffer getRequestURL() {
		StringBuffer sb = new StringBuffer(getScheme()).append("://");
		String host = getHeader(HttpHeader.HeaderName.HOST);
		if(StrUt.isBlank(host)){
			host = getLocalAddr();
		}
		sb.append(host).append(requestBaseInfo.getRequestURIPath());
		return sb;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		if(!sessionIdParsed) {
			getRequestedSessionId();
		}
		return sessionIdFromCookie;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		if(!sessionIdParsed) {
			getRequestedSessionId();
		}
		return sessionIdFromUrl;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	
	@Override
	public Cookie[] getCookies() {
		return headersHolder.getCookies();
	}

	@Override
	public long getDateHeader(String name) {
		return headersHolder.getDateHeader(name);
	}

	@Override
	public String getHeader(String name) {
		return headersHolder.getHeader(name);
	}

	@Override
	public Enumeration getHeaders(String name) {
		return headersHolder.getHeaders(name).elements();
	}

	@Override
	public Enumeration getHeaderNames() {
		return headersHolder.getHeaderNames().elements();
	}

	@Override
	public int getIntHeader(String name) {
		return headersHolder.getIntHeader(name);
	}

	@Override
	public String getMethod() {
		return requestBaseInfo.getRequestMethod();
	}

	/******************************************************************
	 * login mechanism
	 ******************************************************************/
	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	public void login(String username, String password) throws ServletException {

		getSession(true).setAttribute("login",true);
	}

	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	

	
}
