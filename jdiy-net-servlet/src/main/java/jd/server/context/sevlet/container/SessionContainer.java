package jd.server.context.sevlet.container;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

public class SessionContainer<Session extends SessionCore> {

	protected Hashtable<String,Session> sessMap = new Hashtable<>(); 
	private long sessionTimeout = -1 ; // in milliseconds
	
	public void access(Session sess) {
		sessMap.put(sess.sessionId, sess);
		sess.updateAccessTime();
	}
	
	
	/**
	 * get validate 
	 * @param sessionId
	 * @return
	 */
	public Session getValiditySession(String sessionId) {
		Session sess =  sessMap.get(sessionId);
		if(sess != null && checkValidation(sess)) {
			return sess ;
		}
		return null ;
	}
	
	public void invalidate(String sessionId) {
		Session sess = sessMap.remove(sessionId);
		if(sess != null) {
			sess.validate = false ;
		}
	}
	
	public String genNewSessionId() {
		return UUID.randomUUID().toString().replaceAll("-", "") ;
	}
	
	public boolean changeSessionId(String oldId,String newId) {
		Session sess = sessMap.remove(oldId);
		if(sess != null && checkValidation(sess)) {
			sess.changeSessionId(newId);
			return true ;
		}
		return false ;
	}
	
	public boolean checkValidation(String sessionId) {
		Session sess = sessMap.get(sessionId);
		return sess != null ? checkValidation(sess) : false ;
	}
	
	public boolean checkValidation(Session sess) {
		if(sess != null && sess.validate) {
			if(sessionTimeout>0 && sess.latestAccessTime + sessionTimeout < System.currentTimeMillis()) {
				sess.validate = false ;
			}
			return sess.validate ;
		}
		return false ;
	}
	
	public Enumeration<String>	getIds(){
		return sessMap.keys();
	}
}
