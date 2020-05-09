package jd.server.context.sevlet.container.attrctn;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class HttpSessionAttributeContainer extends ListenedAttributesContainer<HttpSessionAttributeListener> {

	private HttpSession sess ;
	private final List<HttpSessionBindingListener> bindingListeners ;
	
	public HttpSessionAttributeContainer(HttpSession sess,List<HttpSessionAttributeListener> attributeListeners,
			List<HttpSessionBindingListener> bindingListeners) {
		super(attributeListeners);
		this.sess = sess;
		this.bindingListeners = bindingListeners ;
	}
	
	@Override
	public void notifyAttributeAdded(String name, Object value) {
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(sess,name,  value);
		attributeListeners.forEach(l->l.attributeAdded(event));
		// TODO HttpSessionBindingListener
		bindingListeners.forEach(l->l.valueBound(event));
	}

	@Override
	public void notifyAttributeReplaced(String name, Object oldvalue) {
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(sess,name,  oldvalue);
		attributeListeners.forEach(l->l.attributeReplaced(event));
	}

	@Override
	public void notifyAttributeRemove(String name, Object value) {
		HttpSessionBindingEvent event = new HttpSessionBindingEvent(sess,name,  value);
		attributeListeners.forEach(l->l.attributeRemoved(event));
	}

	public void notifyUnBoundAllValues() {
		super.forEach((name,  value)->{
			HttpSessionBindingEvent event = new HttpSessionBindingEvent(sess,name,  value);
			bindingListeners.forEach(l->l.valueUnbound(event));
		});
		super.clear();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4512010666384359919L;
}
