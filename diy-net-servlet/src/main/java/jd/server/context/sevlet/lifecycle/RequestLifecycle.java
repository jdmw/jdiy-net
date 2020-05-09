package jd.server.context.sevlet.lifecycle;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import jd.server.context.sevlet.loader.ContextConfiguration;

public class RequestLifecycle implements LifeCycle {

	private final ServletContext ctx;
	private final ServletRequest request;

	private final List<ServletRequestListener> 	requestListener ;

	public RequestLifecycle(ServletContext sc, ServletRequest request, ContextConfiguration liscase) {
		this.ctx = sc;
		this.request = request ;
		requestListener = liscase.getRequestListener();
	}

	@Override
	public void onCreate() {
		ServletRequestEvent event = new ServletRequestEvent(ctx,request);
		// notify that  a ServletRequest is about to come into scope of the web application.
		requestListener.forEach(lis -> lis.requestInitialized(event));
	}

	@Override
	public void onDestroy() {
		ServletRequestEvent event = new ServletRequestEvent(ctx,request);
		// notify that  a ServletRequest is about to leave scope of the web application.
		requestListener.forEach(lis -> lis.requestDestroyed(event));
	}

}
