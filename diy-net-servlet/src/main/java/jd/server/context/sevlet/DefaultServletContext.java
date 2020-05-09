package jd.server.context.sevlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

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

import jd.server.protocol.RawRequestMessage;
import jd.server.protocol.RawResponseMessage;
import jd.server.protocol.handler.http.HttpResponser;
import jd.server.context.PathPatternContext;
import jd.server.context.sevlet.container.DefaultSessionContext;
import jd.server.context.sevlet.container.attrctn.ServletContextAttributeContainer;
import jd.server.context.sevlet.lifecycle.ApplicationLifecycle;
import jd.server.context.sevlet.loader.ConfigurationLoader;
import jd.server.context.sevlet.loader.ContextClassLoader;
import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.util.StrUt;
import jd.util.io.file.FileUt;

public class DefaultServletContext extends PathPatternContext implements ServletContext{

	private final float version = 3.0f ;
	
	private final File ctxRoot ;
	private final String contextPath ;
	private ApplicationLifecycle lifecycle ;
	private final ContextClassLoader classLoader ;
	private final ContextConfiguration classedListeners = new ContextConfiguration();
	private final DefaultSessionContext sessionContext;
	private final ServletContextAttributeContainer attributeContext ;
	private ContextConfiguration cfg ;
	private PrintStream logPs ;
	public DefaultServletContext(File ctxRoot,String contextPath){
		super(contextPath,new HttpResponser());
		this.ctxRoot = ctxRoot ;
		try {
			logPs = new PrintStream(FileUt.file(ctxRoot.getParentFile().getParentFile(),"logs","default.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// in the default (root) context, this contextPath is ""
		this.contextPath = StrUt.isBlank(contextPath) || "/".equals(contextPath) ? contextPath : "" ;
		classLoader = new ContextClassLoader(this.getClassLoader(),ctxRoot);
		attributeContext = new ServletContextAttributeContainer(this,classedListeners.getServletContextAttrListeners());
		sessionContext = new DefaultSessionContext(this,classedListeners);
	}
	
	public void init() {
		File webxmlFile = FileUt.file(ctxRoot, "WEB-INF","web.xml");
		cfg = ConfigurationLoader.loadConfig(webxmlFile, true);
		lifecycle = new ApplicationLifecycle(this,cfg,classLoader);
	}
	
	private Servlet matchServlet(String path) {
		// init if neccessary
		//Servlet servlet ;
		//if(servlet.init(config);)
		return null ;
	}
	
	private DefaultRequestDispatcher requestDispatcher = new DefaultRequestDispatcher(classedListeners);
	
	@Override
	public boolean innerProcess(String path,RawRequestMessage reqMsg,RawResponseMessage resp) {
		DefaultHttpServletRequest request = new DefaultHttpServletRequest(sessionContext, this,
				cfg.getRequestAttributeListener(),resp) ;
		Servlet servlet = null ;
		request.merge(reqMsg);
		servlet = matchServlet(path);
		if(servlet != null) {
			try {
				servlet.service(request, request.getResponse());
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		return servlet != null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		logPs.println(msg);
		
	}

	@Override
	public void log(Exception exception, String msg) {
		// TODO Auto-generated method stub
		logPs.print(msg);
		exception.printStackTrace(logPs);
	}

	@Override
	public void log(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInitParameter(String name) {
		// TODO Auto-generated method stub
		return null;
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
		return null;
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
