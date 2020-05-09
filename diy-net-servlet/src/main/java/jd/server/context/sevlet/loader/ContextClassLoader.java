package jd.server.context.sevlet.loader;

import java.io.File;

public class ContextClassLoader extends ClassLoader {

	private File path ;
	public ContextClassLoader(ClassLoader parentLoader,File path){
		this.path = path ;
	}
}
