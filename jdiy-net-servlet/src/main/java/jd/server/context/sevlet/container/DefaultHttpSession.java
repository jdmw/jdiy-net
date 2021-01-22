package jd.server.context.sevlet.container;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import jd.server.context.sevlet.container.attrctn.HttpSessionAttributeContainer;
import jd.server.context.sevlet.lifecycle.SessionLifecycle;
import jd.server.context.sevlet.loader.ContextConfiguration;

public class DefaultHttpSession extends SessionCore implements HttpSession{

	private final HttpSessionAttributeContainer attrCtx;
	private final DefaultSessionContext sessionContainer ;
	private final SessionLifecycle lifecycle ;
	
	protected DefaultHttpSession(DefaultSessionContext sessctx,ContextConfiguration lis ,String sessionId) {
		super(sessionId);
		this.sessionContainer = sessctx ;
		attrCtx = new HttpSessionAttributeContainer(this,lis.getSessionAttributeListener(),lis.getSessionBindingListener());
		lifecycle = new SessionLifecycle(this,lis);
		lifecycle.onCreate();
		lifecycle.onActive();
	}

	@Override
	public ServletContext getServletContext() {
		return sessionContainer.getServletContext();
	}

	@SuppressWarnings("deprecation")
	@Override
	public HttpSessionContext getSessionContext() {
		return sessionContainer;
	}

	@Override
	public Object getAttribute(String name) {
		return attrCtx.getAttribute(name);
	}

	@Override
	public Object getValue(String name) {
		return attrCtx.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return attrCtx.getAttributeNames();
	}

	@Override
	public String[] getValueNames() {
		return attrCtx.getValueNames();
	}

	@Override
	public void setAttribute(String name, Object value) {
		attrCtx.setAttribute(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		attrCtx.setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		attrCtx.removeAttribute(name);
	}

	@Override
	public void removeValue(String name) {
		attrCtx.removeAttribute(name);
	}

	/**
	 * Invalidates this session then unbinds any objects bound to it.
	 */
	public void invalidate() {
		super.invalidate();
		super.waitingForRequestComplish(()->{
			// unbinds any objects bound to it.
			attrCtx.notifyUnBoundAllValues(); // TODO 
			lifecycle.onInactive();
			lifecycle.onDestroy();
		});
	}

	

}
