package jd.server.context.sevlet.lifecycle;

public interface LifeCycle {

	void onCreate();
	
/*    void onInit() ;

    void onActive();

    void onResume();

    void onPause();

    void onInactive();*/

    void onDestroy();
    
}