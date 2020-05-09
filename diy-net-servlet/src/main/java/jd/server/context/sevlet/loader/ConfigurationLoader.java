package jd.server.context.sevlet.loader;

import java.io.File;

public class ConfigurationLoader {

	
	public static ContextConfiguration loadConfig(File webxmlFile,boolean useAn) {
		ContextConfiguration cfg = new ContextConfiguration();
		if(webxmlFile.exists()) {
			loadXmlConfig(cfg,webxmlFile);
		}
		if(useAn) {
			loadAnnotaionConfig(cfg);
		}
		return cfg ;
	}
	
	private static void loadXmlConfig(ContextConfiguration cfg,File webxmlFile) {
		
	}
	
	private static void loadAnnotaionConfig(ContextConfiguration cfg) {
		
	}
}
