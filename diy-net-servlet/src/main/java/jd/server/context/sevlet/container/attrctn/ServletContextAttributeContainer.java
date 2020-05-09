package jd.server.context.sevlet.container.attrctn;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

public class ServletContextAttributeContainer extends ListenedAttributesContainer<ServletContextAttributeListener > {


	private ServletContext ctx ;
	
	public ServletContextAttributeContainer(ServletContext ctx,List<ServletContextAttributeListener> list ) {
		super(list);
		this.ctx = ctx;
	}
	
	@Override
	public void notifyAttributeAdded(String name, Object value) {
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(ctx,name,  value);
		event.getSource();
		attributeListeners.forEach(l->l.attributeAdded(event));
	}

	@Override
	public void notifyAttributeReplaced(String name, Object value) {
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(ctx,name,  value);
		attributeListeners.forEach(l->l.attributeReplaced(event));
	}

	@Override
	public void notifyAttributeRemove(String name, Object value) {
		ServletContextAttributeEvent event = new ServletContextAttributeEvent(ctx,name,  value);
		attributeListeners.forEach(l->l.attributeRemoved(event));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5452388850939776609L;
}
