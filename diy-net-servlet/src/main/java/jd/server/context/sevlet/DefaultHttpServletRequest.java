package jd.server.context.sevlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import jd.server.protocol.RawResponseMessage;
import jd.server.context.sevlet.container.DefaultHttpSession;
import jd.server.context.sevlet.container.DefaultSessionContext;
import jd.util.StrUt;

public class DefaultHttpServletRequest extends CommonServletRequest implements HttpServletRequest {

	private final DefaultSessionContext sessctx; 
	private final DefaultHttpServletResponse response ;
	protected DefaultHttpServletRequest(DefaultSessionContext sessctx, ServletContext ctx,
			List<ServletRequestAttributeListener> lis,RawResponseMessage resp) {
		super(ctx, lis);
		this.sessctx = sessctx ;
		response = new DefaultHttpServletResponse(this,resp);
	}

	protected DefaultHttpServletResponse getResponse() {
		return response ;
	}
	
	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public String getContextPath() {
		// TODO Auto-generated method stub
		// TODO ? It is possible that a servlet container may match a context by more than one
		return super.getServletContext().getContextPath();
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
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
	public HttpSession getSession(boolean create) {
		if(response.responseCommitted) {
			throw new IllegalStateException("the response is committed");
		}
		if(currentSession == null) {
			// TODO ? If the container is using cookies to maintain session integrity and is asked to create a new session when the response is committed, an IllegalStateException is thrown.
			String requestedSessionId = getRequestedSessionId();
			currentSession = sessctx.getValiditySession(requestedSessionId);
			if(create && currentSession == null) {
				if(isRequestedSessionIdFromCookie()) {
					throw new IllegalStateException("container is using cookies to maintain session integrity,creating a new session is forbidded");
				}
				currentSession = sessctx.newSession();
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
		return id != null ? sessctx.checkValidation(id):false ;
	}

	@Override
	public String getRequestedSessionId() {
		if(!sessionIdParsed) {
			requesedSessionId = super.getCookieMap().get(JSESSIONID);
			if(StrUt.isNotBlank(requesedSessionId)) {
				sessionIdFromCookie = true ;
			}else {
				requesedSessionId = super.getParameter(JSESSIONID);
				if(StrUt.isNotBlank(requesedSessionId)) {
					sessionIdFromUrl = true ;
				}
			}
		}
		return requesedSessionId;
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
		// TODO Auto-generated method stub
		
	}

	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	

	
}
