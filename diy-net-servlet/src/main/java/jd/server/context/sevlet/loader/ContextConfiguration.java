package jd.server.context.sevlet.loader;

import java.util.EventListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

public class ContextConfiguration {

	protected final Set<Class<? extends Filter>> filterClasses = new CopyOnWriteArraySet<>();
	protected final Set<Class<? extends Servlet>> ServletClasses = new CopyOnWriteArraySet<>();
	protected final Set<Class<? extends EventListener>> ListenerClasses = new CopyOnWriteArraySet<>();
	
	//protected final List<EventListener> listeners = new CopyOnWriteArrayList<>();
	protected final List<Filter> Filters = new CopyOnWriteArrayList<>();
	protected final List<Servlet> Servlets = new CopyOnWriteArrayList<>();
	protected final List<EventListener> listeners = new CopyOnWriteArrayList<>();

	protected final List<DefaultServletConfig> servletConfigs = new CopyOnWriteArrayList<>();
	
	protected final List<ServletContextListener> 			servletContextListeners = new CopyOnWriteArrayList<>();
	protected final List<ServletContextAttributeListener> 	servletContextAttrListeners = new CopyOnWriteArrayList<>();
	
	protected final List<HttpSessionListener> 				sessionListeners = new CopyOnWriteArrayList<>();
	protected final List<HttpSessionAttributeListener> 		sessionAttributeListener = new CopyOnWriteArrayList<>();
	protected final List<HttpSessionBindingListener> 		sessionBindingListener = new CopyOnWriteArrayList<>();
	protected final List<HttpSessionIdListener> 			sessionIdListener = new CopyOnWriteArrayList<>();
	protected final List<HttpSessionActivationListener> 	sessionActivationListener = new CopyOnWriteArrayList<>();
	
	protected final List<ServletRequestListener> 			requestListener = new CopyOnWriteArrayList<>();
	protected final List<ServletRequestAttributeListener> requestAttributeListener = new CopyOnWriteArrayList<>();
	
	public void addListener(EventListener e) {
		listeners.add(e);
		if(e instanceof ServletContextListener) {
			servletContextListeners.add((ServletContextListener)e);
		}else if(e instanceof ServletContextAttributeListener) {
			servletContextAttrListeners.add((ServletContextAttributeListener)e);
		}else if(e instanceof HttpSessionListener) {
			sessionListeners.add((HttpSessionListener)e);
		}else if(e instanceof HttpSessionAttributeListener) {
			sessionAttributeListener.add((HttpSessionAttributeListener)e);
		}else if(e instanceof HttpSessionBindingListener) {
			sessionBindingListener.add((HttpSessionBindingListener)e);
		}else if(e instanceof HttpSessionIdListener) {
			sessionIdListener.add((HttpSessionIdListener)e);
		}else if(e instanceof HttpSessionActivationListener) {
			sessionActivationListener.add((HttpSessionActivationListener)e);
		}else if(e instanceof ServletRequestListener) {
			requestListener.add((ServletRequestListener)e);
		}else if(e instanceof ServletRequestAttributeListener) {
			requestAttributeListener.add((ServletRequestAttributeListener)e);
		}
	}

	public List<ServletContextListener> getServletContextListeners() {
		return servletContextListeners;
	}

	public List<ServletContextAttributeListener> getServletContextAttrListeners() {
		return servletContextAttrListeners;
	}

	public List<HttpSessionListener> getSessionListeners() {
		return sessionListeners;
	}

	public List<HttpSessionAttributeListener> getSessionAttributeListener() {
		return sessionAttributeListener;
	}

	public List<HttpSessionBindingListener> getSessionBindingListener() {
		return sessionBindingListener;
	}

	public List<HttpSessionIdListener> getSessionIdListener() {
		return sessionIdListener;
	}

	public List<HttpSessionActivationListener> getSessionActivationListener() {
		return sessionActivationListener;
	}

	public List<ServletRequestListener> getRequestListener() {
		return requestListener;
	}

	public List<ServletRequestAttributeListener> getRequestAttributeListener() {
		return requestAttributeListener;
	}

	public Set<Class<? extends Filter>> getFilterClasses() {
		return filterClasses;
	}

	public Set<Class<? extends EventListener>> getListenerClasses() {
		return ListenerClasses;
	}
}
