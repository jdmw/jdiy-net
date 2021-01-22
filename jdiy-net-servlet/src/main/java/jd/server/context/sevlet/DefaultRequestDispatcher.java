package jd.server.context.sevlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jd.server.context.sevlet.loader.ContextConfiguration;

public class DefaultRequestDispatcher implements RequestDispatcher {

	private ContextConfiguration classedListeners;
	public DefaultRequestDispatcher(ContextConfiguration classedListeners) {
		this.classedListeners = classedListeners ;
	}

	private Servlet findServlet(String url,DispatcherType dispatcherTypp){
		// TODO Auto-generated method stub
		return null ;
	}
	
	private List<Filter> findFilters(String url,DispatcherType dispatcherTypp){
		// TODO Auto-generated method stub
		return null ;
	}
	
	// DispatcherType:ASYNC 
	public void asyncRequest(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	public void request(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	public void error(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	

}
