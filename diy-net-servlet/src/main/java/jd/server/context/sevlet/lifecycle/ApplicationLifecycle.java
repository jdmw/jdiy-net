package jd.server.context.sevlet.lifecycle;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.server.context.sevlet.loader.ResourcesLoader;

public class ApplicationLifecycle implements LifeCycle {

	ServletContext ctx ;
	private ClassLoader[] ctxClassLoaders = null;
	private final ContextConfiguration cfg ;
	
	public ApplicationLifecycle(ServletContext ctx,ContextConfiguration listernerCase,ClassLoader  ctxClassLoaders) {
		this.ctx = ctx ;
		this.ctxClassLoaders = new ClassLoader[]{ctxClassLoaders};
		this.cfg = listernerCase;
	}
	
	/**
	 * load classes of filter,servlet,listener
	 * @return
	 */
	//abstract void loadFSLClasses();
/*	abstract Set<Class<? extends Filter>> loadFilterClasses();
	abstract Set<Class<? extends Servlet>> loadServletClasses();
	abstract Set<Class<? extends EventListener>> loadListenerClasses();*/
	
/*	abstract List<EventListener> createListeners();
	abstract List<Filter> createFilters();
	abstract List<Servlet> createServlets();
	
	
	abstract Servlet getServlet(String path);
	abstract List<Filter> getFilters(String path);*/
	
/*	abstract boolean isServletInit(Servlet servlet);
	abstract boolean initServlet(Servlet servlet);*/
	
	@Override
	public void onCreate() {
		// loadFSLClasses();
		
		Set<Class<?>> sfSet = new HashSet<>();
		sfSet.addAll(cfg.getFilterClasses());
		sfSet.addAll(cfg.getListenerClasses());
		
		try {
			// load ServletContainerInitializer
			for(Class<? extends ServletContainerInitializer> cl : ResourcesLoader.loadServletContainerInitializers(ctxClassLoaders)) {
					cl.newInstance().onStartup(sfSet, ctx);
			}
			
			// instantiate listeners
			for(Class<? extends EventListener> cl : cfg.getListenerClasses()) {
				EventListener l = cl.newInstance() ;
				cfg.addListener(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// notify servletContextListeners
		ServletContextEvent event = new ServletContextEvent(ctx);
		cfg.getServletContextListeners().forEach(lis->lis.contextInitialized(event));
	}
	
/*	public void doServletService(Servlet servlet,ServletRequest req,ServletResponse res) throws ServletException, IOException {
		//Servlet servlet = getServlet( path);
		if(servlet != null) {
			if(!isServletInit(servlet)) {
				initServlet(servlet);
			}
			servlet.service(req, res);
		}
		List<Filter> filters = getFilters( path);
		if(servlet != null) {
			if(filters != null && !filters.isEmpty()) {
				for(Filter filter : filters) {
					filter.
				}
			}
		}
		
		// servlet
	}
	*/
	
	@Override
	public void onDestroy() {
		// notify servletContextListeners
		ServletContextEvent event = new ServletContextEvent(ctx);
		cfg.getServletContextListeners().forEach(lis->lis.contextDestroyed(event));
	}

}
