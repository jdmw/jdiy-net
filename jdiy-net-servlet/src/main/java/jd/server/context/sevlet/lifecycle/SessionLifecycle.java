package jd.server.context.sevlet.lifecycle;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jd.server.context.sevlet.container.SessionContainer;
import jd.server.context.sevlet.loader.ContextConfiguration;

public class SessionLifecycle implements LifeCycle {

	HttpSession sess;
	SessionContainer<?> sessCtx;

	private final List<HttpSessionListener> sessionListeners;
	// private final List<HttpSessionIdListener> sessionIdListener ;
	private final List<HttpSessionActivationListener> sessionActivationListener;

	public SessionLifecycle(HttpSession sess, ContextConfiguration liscase) {
		this.sess = sess;
		sessionListeners = liscase.getSessionListeners();
		sessionActivationListener = liscase.getSessionActivationListener();
	}

	@Override
	public void onCreate() {
		HttpSessionEvent event = new HttpSessionEvent(sess);
		sessionListeners.forEach(lis -> lis.sessionCreated(event));
	}

	//@Override
	public void onActive() {
		HttpSessionEvent event = new HttpSessionEvent(sess);
		sessionActivationListener.forEach(lis ->lis.sessionDidActivate(event));
	}

	//@Override
	public void onInactive() {
		HttpSessionEvent event = new HttpSessionEvent(sess);
		sessionActivationListener.forEach(lis ->lis.sessionWillPassivate(event));
	}

	@Override
	public void onDestroy() {
		// notify servletContextListeners
		HttpSessionEvent event = new HttpSessionEvent(sess);
		// notify that a session is about to be destroyed
		sessionListeners.forEach(lis -> lis.sessionDestroyed(event));
	}

	
}
