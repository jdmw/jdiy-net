package jd.server.context.sevlet.loader;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class DefaultServletConfig implements ServletConfig {

	private String displayName ;
	private String servletName;
	private String[] urlPattern;
	// Declares whether the servlet supports asynchronous operation mode.
	private boolean	asyncSupported ;
	// The load-on-startup order of the servlet
	private int	loadOnStartup ;
	// The icons of the servlet 
	private String largeIcon ;
	private String smallIcon ;
	private String	description;
	
	private final ServletContext sc ;
	
	private boolean isJsp ;
	private String classname;
	private String jsp ;
	private Class<? extends HttpServlet> servletClass ;
	private Servlet servlet ;
	
	private Hashtable<String,String> initParams = new Hashtable<>();
	
	private DefaultServletConfig(ServletContext sc) {
		this.sc = sc ;
	}

	public static DefaultServletConfig owner(ServletContext sc) {
		return new DefaultServletConfig(sc);
	}
	
	public void setClassname(String classname) {
		this.classname = classname;
		this.isJsp = false ;
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
		this.isJsp = true;
	}
	
	
	public Class<? extends HttpServlet> getServletClass() {
		return servletClass;
	}
	
	@Override
	public String getServletName() {
		return servletName;
	}

	@Override
	public ServletContext getServletContext() {
		return sc;
	}

	@Override
	public String getInitParameter(String name) {
		return initParams.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return initParams.keys();
	}

	public String getDisplayName() {
		return displayName;
	}

	public String[] getUrlPattern() {
		return urlPattern;
	}

	public boolean isAsyncSupported() {
		return asyncSupported;
	}

	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	public String getLargeIcon() {
		return largeIcon;
	}

	public String getSmallIcon() {
		return smallIcon;
	}

	public String getDescription() {
		return description;
	}


	public boolean isJsp() {
		return isJsp;
	}

	public String getClassname() {
		return classname;
	}

	public String getJsp() {
		return jsp;
	}

	public Servlet getServlet() {
		return servlet;
	}

	public Hashtable<String, String> getInitParams() {
		return initParams;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public void setUrlPattern(String[] urlPattern) {
		this.urlPattern = urlPattern;
	}

	public void setAsyncSupported(boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}

	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public void setLargeIcon(String largeIcon) {
		this.largeIcon = largeIcon;
	}

	public void setSmallIcon(String smallIcon) {
		this.smallIcon = smallIcon;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setServletClass(Class<? extends HttpServlet>servletClass) {
		this.servletClass = servletClass;
	}

	public void setServlet(Servlet servlet) {
		this.servlet = servlet;
	}

	public void putInitParam(String name,String value) {
		this.initParams.put(name, value);
	}
	
	

}
