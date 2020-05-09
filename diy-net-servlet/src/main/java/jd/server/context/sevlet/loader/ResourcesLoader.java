package jd.server.context.sevlet.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import jd.util.lang.collection.One2ManyMap;
import jd.util.io.IOUt;


public class ResourcesLoader {

	
	public static One2ManyMap<ClassLoader, URL> loadResources(ClassLoader ... loaders) throws IOException {
		One2ManyMap<ClassLoader,URL> map = new One2ManyMap<>(true);
		Enumeration<URL> res = null ;
		for(ClassLoader loader: loaders) {
			res	= loader.getResources("META-INF/services/javax.servlet.ServletContainerInitializer");
			while(res.hasMoreElements()) {
				map.add(loader,res.nextElement());
			}
		}
		return map ;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Class<? extends ServletContainerInitializer>> loadServletContainerInitializers(ClassLoader ... loaders) throws IOException {
		Set<Class<? extends ServletContainerInitializer>> inits = new HashSet<>();
		One2ManyMap<ClassLoader, URL> map = loadResources(loaders);
		for(ClassLoader loader: map.keySet()) {
			Set<String> classes = new HashSet<>();
			for(URL url : map.get(loader)) {
				classes.addAll(IOUt.readLines(url.openStream(),true));
			}
			classes.forEach((item)->{
				Class<?> clazz = null ;
				try {
					clazz = loader.loadClass(item);
					inits.add((Class<? extends ServletContainerInitializer>)clazz);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("load servlet container initializer's class '" + item +"' error",e);
				}
			});
		}
		return inits ;
	}
}
