package jd.server.context.sevlet.container.attrctn;

import java.util.EventListener;

public interface IListenedAttributesContainer<L extends EventListener> {

	public void notifyAttributeAdded(String name,Object value);
	public void notifyAttributeReplaced(String name,Object oldvalue);
	public void notifyAttributeRemove(String name,Object oldvalue);
	
}
