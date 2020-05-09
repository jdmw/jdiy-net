package jd.server.context.sevlet.container;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.util.lang.concurrent.CcUt;

@SuppressWarnings("deprecation")
public class DefaultSessionContext extends SessionContainer<DefaultHttpSession> implements HttpSessionContext{

	private ServletContext ctx ;
	private final ContextConfiguration lis ;
	public DefaultSessionContext(ServletContext ctx,ContextConfiguration cl) {
		this.ctx = ctx ;
		this.lis = cl ;
		checkValidation();
	}
	
	public DefaultHttpSession newSession() {
		return new DefaultHttpSession(this,lis,this.genNewSessionId());
	}
	
	public ServletContext getServletContext() {
		return ctx;
	}

	@Override
	public HttpSession getSession(String sessionId) {
		return super.getValiditySession(sessionId);
	}
	
	public void checkValidation() {
		CcUt.start(() -> super.sessMap.values().forEach(sess -> {
			if (!super.checkValidation(sess)) {
				((DefaultHttpSession) sess).invalidate();
			}
		}));
	}

}
