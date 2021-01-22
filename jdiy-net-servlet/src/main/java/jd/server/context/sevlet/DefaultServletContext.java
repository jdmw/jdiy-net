package jd.server.context.sevlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import jd.net.connector.server.dispatcher.IMappedProcessor;
import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;
import jd.server.context.sevlet.container.DefaultSessionContext;
import jd.server.context.sevlet.container.attrctn.ServletContextAttributeContainer;
import jd.server.context.sevlet.lifecycle.ApplicationLifecycle;
import jd.server.context.sevlet.loader.ConfigurationLoader;
import jd.server.context.sevlet.loader.ContextClassLoader;
import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.server.context.sevlet.loader.DefaultServletConfig;
import jd.util.StrUt;
import jd.util.io.IOUt;
import jd.util.io.file.FileUt;
import jd.util.lang.exceptions.WrapperException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class DefaultServletContext  implements IMappedProcessor<String>, ServletContext{

	private final float version = 3.0f ;
	
	private final File ctxRoot ;
	private final String contextPath ;
	private ApplicationLifecycle lifecycle ;
	private ContextClassLoader classLoader ;
	private DefaultSessionContext sessionContext;
	private ServletContextAttributeContainer attributeContext ;
	private ServletRouter servletRouter = new ServletRouter();
	protected ContextConfiguration cfg ;
	private DefaultRequestDispatcher requestDispatcher;
	public DefaultServletContext(File ctxRoot,String contextPath) {
		this.ctxRoot = ctxRoot ;
		// in the default (root) context, this contextPath is ""
		this.contextPath = StrUt.isBlank(contextPath) ? contextPath : "/" ;
	}

	@Override
	public void init() {
		if(cfg == null){
			File webXmlFile = FileUt.file(ctxRoot, "WEB-INF","web.xml");
			cfg = ConfigurationLoader.loadConfig(webXmlFile, true);
		}
		classLoader = new ContextClassLoader(this.getClassLoader(),ctxRoot);
		lifecycle = new ApplicationLifecycle(this,cfg,classLoader);
		requestDispatcher = new DefaultRequestDispatcher(cfg);
		loadServlets(cfg,servletRouter);
		attributeContext = new ServletContextAttributeContainer(this,cfg.getServletContextAttrListeners());
		sessionContext = new DefaultSessionContext(this,cfg);
		// start
		lifecycle.onCreate();
	}

	private void loadServlets(ContextConfiguration cfg,ServletRouter servletRouter){
		cfg.getServletConfigs().sort((c1,c2)->c1.getLoadOnStartup()-c2.getLoadOnStartup());
		for (DefaultServletConfig servletConfig : cfg.getServletConfigs()) {
			if(servletConfig.getLoadOnStartup() >= 0 ){
				try {
					servletConfig.getServlet().init(servletConfig);
				} catch (ServletException e) {
					throw new WrapperException(e);
				}
			}
			for (String urlPattern : servletConfig.getUrlPattern()) {
				servletRouter.addServlet(urlPattern,servletConfig.getServlet());
			}
		}
	}

	@Override
	public void destroy() {
		lifecycle.onDestroy();
	}

	private static class ServletRouter {
		private final List<String> contentPaths = new ArrayList<>();
		private final Map<String, Servlet> servlets = new HashMap<>();
		private volatile Servlet defaultServlet ;

		protected synchronized void setDefaultServlet(Servlet defaultServlet) {
			this.defaultServlet = defaultServlet;
		}
		protected void addServlet(String contentPath,Servlet servlet){
			contentPath = formatContentPath(contentPath);
			if(contentPaths.contains(contentPath)){
				throw new IllegalArgumentException("contentPath exists");
			}
			synchronized (this){
				contentPaths.add(contentPath);
				servlets.put(contentPath,servlet);
				contentPaths.sort((p1,p2)->{
					int length = p2.length() - p1.length();
					if(length == 0){
						return p1.compareTo(p2);
					}
					return length;
				});
			}
		}
		protected static String formatContentPath(String contentPath){
			if(StrUt.isBlank(contentPath)){
				contentPath = "/" ;
			}
			if(!contentPath.startsWith("/")){
				contentPath = "/" + contentPath ;
			}
			if (contentPath.endsWith("/")){
				contentPath.substring(0,contentPath.length()-1);
			}
			return contentPath;
		}

		private Servlet getServlet(String url){
			url = formatContentPath(url);
			for (String contentPath : contentPaths) {
				if(url.startsWith(contentPath)){
					return servlets.get(contentPath);
				}
			}
			return defaultServlet ;
		}
	}


	@Override
	public String[][] mappings() {
		return new String[][]{new String[]{contextPath}};
	}

	@Override
	public void process(NetConnection netConnection, Datagram datagram) throws IOException {
		DefaultHttpServletRequest request = new DefaultHttpServletRequest((HttpDatagram) datagram, this) ;
		Servlet servlet = servletRouter.getServlet(request.requestBaseInfo.getRequestURIPath()) ;
		DefaultHttpServletResponse response = request.getResponse();
		if(servlet != null) {
			try {
				servlet.service(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}else{
			response.sendError(404,"NOT FOUND");
		}
		response.complete();
		response.getHttpDatagram().write();
		response.writeTo(netConnection.getOutputStream());
	}
	
	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public ServletContext getContext(String uripath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMajorVersion() {
		return (int)version;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}


	public int getEffectiveMajorVersion() {
		return (int)version;
	}

	public int getEffectiveMinorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return classLoader.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return classLoader.getResourceAsStream(path);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
		if(version < 3.0) {
			// TODO Auto-generated method stub
		}else {
			return null ;
		}
		return null;
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getServletNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(String msg) {
		log.info(msg);
	}

	@Override
	public void log(Exception exception, String msg) {
		log.error(msg,exception);
	}

	@Override
	public void log(String message, Throwable throwable) {
		log.error(message,throwable);
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerInfo() {
		// TODO Auto-generated method stub
		return cfg.getServerSettings().getServerName();
	}

	@Override
	public String getInitParameter(String name) {
		// TODO Auto-generated method stub
		return null ;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean setInitParameter(String name, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAttribute(String name) {
		return attributeContext.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return attributeContext.getAttributeNames();
	}

	@Override
	public void setAttribute(String name, Object value) {
		attributeContext.setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		attributeContext.remove(name);
		
	}

	@Override
	public String getServletContextName() {
		// TODO Auto-generated method stub
		return cfg.getServletContextName();
	}

	public Dynamic addServlet(String servletName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	public Dynamic addServlet(String servletName, Servlet servlet) {
		// TODO Auto-generated method stub
		return null;
	}

	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public ServletRegistration getServletRegistration(String servletName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public FilterRegistration getFilterRegistration(String filterName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	public SessionCookieConfig getSessionCookieConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
		// TODO Auto-generated method stub
		
	}

	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addListener(String className) {
		// TODO Auto-generated method stub
		
	}

	public <T extends EventListener> void addListener(T t) {
		// TODO Auto-generated method stub
		
	}

	public void addListener(Class<? extends EventListener> listenerClass) {
		// TODO Auto-generated method stub
		
	}

	public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public void declareRoles(String... roleNames) {
		// TODO Auto-generated method stub
		
	}



}
