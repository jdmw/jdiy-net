package jd.server.context.sevlet.container.attrctn;

import java.util.Enumeration;
import java.util.Hashtable;

public class AttributesContainer extends Hashtable<String,Object>{

	//private Hashtable<String,Object> super = new Hashtable<>(); 

	/**
	 * 
	 */
	private static final long serialVersionUID = 6207577020035658677L;

	/*
	 * Returns the object bound with the specified name in this session,
	 *  or null if no object is bound under the name.
	 */
	public Object getAttribute(String name) {
		return super.get(name) ;
	}
	
	/**
	 * As of Version 2.2, this method is replaced by getAttributeNames()
	 * @return
	 */
	public String[] getValueNames() {
		return super.keySet().toArray(new String[super.size()]);
	}
	
	
	/**
	 * Returns an Enumeration of String objects containing the names of all the objects bound to this session.
	 * @return
	 */
	public Enumeration<String>	getAttributeNames(){
		return super.keys();
	}
	
	
	/**
	 * Removes the object bound with the specified name from this session.
	 * @param name
	 * @return 
	 */
	Object remove(String name) {
		return super.remove(name);
	}
	
	/**
	 * Binds an object to this session, using the name specified.
	 * @param name
	 * @param value
	 * @return 
	 */
	Object set(String name, Object value) {
		return super.put(name, value);
	}
	
}
