package jd.server.context.sevlet.container.attrctn;

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ListenedAttributesContainer<L extends EventListener> extends AttributesContainer implements IListenedAttributesContainer<L> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5854786811345821525L;
	
	protected List<L> attributeListeners ; // = new CopyOnWriteArrayList<>();
	
	public ListenedAttributesContainer(List<L> attributeListeners) {
		this.attributeListeners = attributeListeners;
	}

	public void addListener(L listener) {
		if(attributeListeners == null) {
			attributeListeners = new CopyOnWriteArrayList<>();
		}else if(!attributeListeners.contains(listener)) {
			attributeListeners.add(listener);
		}
	}
	

	/**
	 * Removes the object bound with the specified name from this session.
	 * @param name
	 */
	public void removeAttribute(String name) {
		Object value = remove(name);
		if(value != null) {
			notifyAttributeRemove(name,value);
		}
	}
	
	/**
	 * Binds an object to this session, using the name specified.
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, Object value) {
		if(value == null) {
			removeAttribute(name);
		}else {
			Object oldvalue = super.set(name, value);
			if(oldvalue != null) {
				notifyAttributeAdded( name, value);
			}else {
				notifyAttributeReplaced( name, oldvalue);
			}
		}
	}
	
}
