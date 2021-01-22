package jd.server.context.sevlet.loader;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import jd.util.ArrUt;

public class AnntationConfigParser {

	protected static DefaultServletConfig parse(ServletContext sc,Class<? extends HttpServlet> servletClass) {
		DefaultServletConfig cfg = DefaultServletConfig.owner(sc);
		cfg.setServletClass(servletClass);
		cfg.setClassname(servletClass.getName());
		WebServlet webServlet = servletClass.getAnnotation(WebServlet.class);
		
		cfg.setServletName(webServlet.name());
		cfg.setDisplayName(webServlet.displayName());
		cfg.setDescription(webServlet.description());
		cfg.setLargeIcon(webServlet.largeIcon());
		cfg.setSmallIcon(webServlet.smallIcon());
		cfg.setLoadOnStartup(webServlet.loadOnStartup());
		cfg.setAsyncSupported(webServlet.asyncSupported());

		ArrUt.forEach(webServlet.initParams(),(param)->{
			cfg.putInitParam(param.name(), param.value());
		});
		
		Set<String> urls =  new LinkedHashSet<>();
		ArrUt.forEach(webServlet.urlPatterns(),url->urls.add(url));
		ArrUt.forEach(webServlet.value(),url->urls.add(url));
		cfg.setUrlPattern(urls.toArray(new String[urls.size()]));
		
		return cfg ;
	}
}
