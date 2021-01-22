package jd.server.context.sevlet.loader;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import jd.util.lang.reflect.ReflectUt;
import lombok.Data;

import java.io.IOException;
import java.io.WriteAbortedException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;

@Data
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
	
	private final ServletContext servletContext ;
	
	private boolean isJsp ;
	private String classname;
	private String jsp ;
	private Class<? extends Servlet> servletClass ;
	private Servlet servlet = new ServletProxy();

	private class ServletProxy implements Servlet {
		boolean init = false ;
		Servlet httpServlet ;
		@Override
		public void init(ServletConfig config) throws ServletException {
			if(!init){
				try {
					httpServlet = servletClass.newInstance();
					httpServlet.init(config);
				} catch (InstantiationException|IllegalAccessException e) {
					throw new WrappedRuntimeException(e);
				}
			}
			init = true ;
		}

		@Override
		public ServletConfig getServletConfig() {
			return DefaultServletConfig.this;
		}

		@Override
		public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
			if(!init){
				init(getServletConfig());
			}
			httpServlet.service(req,res);
		}

		@Override
		public String getServletInfo() {
			return httpServlet.getServletInfo();
		}

		@Override
		public void destroy() {
			httpServlet.destroy();
		}
	}
	private Hashtable<String,String> initParams = new Hashtable<>();
	
	public DefaultServletConfig(ServletContext sc) {
		this.servletContext = sc ;
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

	@Override
	public String getInitParameter(String name) {
		return initParams.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return initParams.keys();
	}

	public void setServletClass(Class<? extends HttpServlet>servletClass) {
		this.servletClass = servletClass;
	}

	public void putInitParam(String name,String value) {
		this.initParams.put(name, value);
	}
	
	

}
