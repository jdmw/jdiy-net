package jd.server.context.sevlet.container.attrctn;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;


public class ServletRequestAttributeContainer extends ListenedAttributesContainer<ServletRequestAttributeListener > {


	private ServletContext ctx ;
	private ServletRequest request ;
	public ServletRequestAttributeContainer(ServletContext ctx,ServletRequest request ,List<ServletRequestAttributeListener> list ) {
		super(list);
		this.ctx = ctx;
		this.request = request ;
	}
	
	@Override
	public void notifyAttributeAdded(String name, Object value) {
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(ctx,request,name,  value);
		event.getSource();
		attributeListeners.forEach(l->l.attributeAdded(event));
	}

	@Override
	public void notifyAttributeReplaced(String name, Object value) {
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(ctx,request,name,  value);
		attributeListeners.forEach(l->l.attributeReplaced(event));
	}

	@Override
	public void notifyAttributeRemove(String name, Object value) {
		ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(ctx,request,name,  value);
		attributeListeners.forEach(l->l.attributeRemoved(event));
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7812544590808855783L;
}
