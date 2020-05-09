package jd.server.context.sevlet.container;

import jd.util.lang.concurrent.ThreadSafeCounter;

public class SessionCore {

	protected String sessionId ;
	protected long creationTime = System.currentTimeMillis();
	protected long latestAccessTime ;
	protected boolean validate  ;
	private int maxInactiveInterval ;
	private boolean isNew = true ;
	private final ThreadSafeCounter cnt = new  ThreadSafeCounter();
	public SessionCore(String sessionId ) {
		this.sessionId = sessionId;
		this.validate = true ;
	}
	
	public void associate() {
		cnt.countUp();
	}

	public void separate() {
		cnt.countDown();
	}
	
	// TODO session mechanism
	protected void waitingForRequestComplish(Runnable run) {
		cnt.await(1000, run);
	}
	
	/**
	 * Invalidates this session 
	 */
	public void invalidate() {
		validate = false ;
	}
	
	public void updateAccessTime() {
		latestAccessTime = System.currentTimeMillis();
		isNew = false ;
	}
	
	public void changeSessionId(String sessionId ) {
		this.sessionId = sessionId;
	}
	
	/**
	 * Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
	 * @return
	 */
	public long getCreationTime() {
		return creationTime ;
	}
	
	/**
	 * Returns a string containing the unique identifier assigned to this session.
	 * @return
	 */
	public String getId() {
		return sessionId ;
	}
	
	/**
	 * Returns the last time the client sent a request associated with this session, 
	 * as the number of milliseconds since midnight January 1, 1970 GMT, and marked 
	 * by the time the container received the request.
	 * @return
	 */
	public long getLastAccessedTime() {
		return this.latestAccessTime ;
	}
	
	/**
	 * Specifies the time, in seconds, between client requests before the servlet container will invalidate this session.
	 * @param interval
	 */
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}
	
	
	/**
	 * Returns the maximum time interval, in seconds, that the servlet container 
	 * will keep this session open between client accesses.
	 * @return
	 */
	public int	getMaxInactiveInterval() {
		return maxInactiveInterval ;
	}
	
	/**
	 * Returns true if the client does not yet know about the session 
	 * or if the client chooses not to join the session.
	 * @return
	 */
	public boolean	isNew() {
		return isNew ;
	}
	
	
}
