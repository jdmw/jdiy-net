package jd.server.context.sevlet;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.app.http.datagram.HttpResponseBaseInfo;
import jd.net.protocol.app.http.datagram.cst.HttpHeader;
import jd.net.protocol.common.datagram.ResponseBaseInfo;
import jd.server.context.sevlet.datagram.HttpResponseHeadersHolder;
import jd.server.context.sevlet.loader.ContextConfiguration;
import jd.util.StrUt;
import jd.util.lang.concurrent.ThreadSafeVar;
import jd.util.lang.exceptions.WrapperException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static jd.server.context.sevlet.NetServletConstants.DATE_FORMAT;
import static jd.server.context.sevlet.NetServletConstants.GMT;


public class CommonServletResponse implements ServletResponse {

	public static final String CHARSET_PREFIX = "charset=";

	protected volatile boolean responseCommitted = false;
	private final DefaultHttpServletRequest request ;
	private final HttpDatagram httpDatagram ;
	private final ThreadSafeVar<String> characterEncoding ;
	private final ThreadSafeVar<Integer> contentLength = new ThreadSafeVar<>(0) ;
	protected final ResponseBaseInfo responseBaseInfo ;//  = new HttpResponseBaseInfo();
	private final HttpResponseHeadersHolder headersHolder ;
	protected final ContextConfiguration servletSettings ;
	private volatile int bufferSize ;

	public CommonServletResponse(DefaultHttpServletRequest request, ResponseBaseInfo responseBaseInfo,ContextConfiguration servletSettings, HttpDatagram requestData) {
		this.request = request;
		this.servletSettings = servletSettings;
		this.httpDatagram = new HttpDatagram();
		httpDatagram.attach(requestData.getNetConnection());
		this.responseBaseInfo = responseBaseInfo;
		//responseBaseInfo.parse("HTTP/1.1 200 OK"); // default
		characterEncoding = new ThreadSafeVar<>(null);
		headersHolder = new HttpResponseHeadersHolder(httpDatagram.getHeaderAll());
	}
	
	public void complete() throws IOException {
		if(!this.responseCommitted) {
			responseCommitted = true ;
			try{
				// remove the socciation with the request
				if(request.currentSession != null) {
					request.currentSession.separate();
				}
				httpDatagram.setFirstLine(responseBaseInfo.toString());
				if(headersHolder.getHeader(HttpHeader.HeaderName.CONTENT_TYPE) == null){
					String contentType = "text/html; charset=" + characterEncoding.getOrDefault(servletSettings.getDefaultResponseEncoding());
					headersHolder.setHeader(HttpHeader.HeaderName.CONTENT_TYPE,contentType);
				}
				if(baos != null && baos.size() > 0){
					setContentLength(baos.size());
				}
			}finally {
				//httpDatagram.write();
			}
		}
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding.getOrDefault(servletSettings.getDefaultResponseEncoding());
	}

	@Override
	public void setCharacterEncoding(String charset) {
		Charset.forName(charset);
		characterEncoding.setValue(charset);
	}

	@Override
	public String getContentType() {
		return httpDatagram.getHeader(HttpHeader.HeaderName.CONTENT_TYPE);
	}

	private volatile ByteArrayOutputStream baos ;
	private ServletOutputStream outputStream ;
	private PrintWriter writer ;
	private ByteArrayOutputStream createOutputStreamIfNotExist(){
		if(baos == null){
			baos = new ByteArrayOutputStream();
		}
		return baos ;
	}
	protected void writeTo(OutputStream os) throws IOException {
		if(baos != null && baos.size() >0){
			baos.writeTo(os);
		}
	}
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if(outputStream == null){
			synchronized (this){
				if(writer != null){
					throw new IllegalArgumentException("getWriter() has been called for this response");
				}
				OutputStream baos = createOutputStreamIfNotExist() ;
				outputStream = new ServletOutputStream() {
					@Override
					public void write(int b) throws IOException {
						baos.write(b);
					}
					@Override
					public void close() throws IOException {
						baos.close();
						complete();
					}
				};
			}
		}
		return outputStream ;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if(writer == null){
			synchronized (this){
				if(outputStream != null){
					throw new IllegalArgumentException("getWriter() has been called for this response");
				}
				writer = new PrintWriter(createOutputStreamIfNotExist()) {
					@Override
					public void close()  {
						try {
							complete();
						} catch (IOException e) {
							throw new WrapperException(e);
						}
					}
				};
			}
		}
		return writer ;
	}



	@Override
	public void setContentLength(int len) {
		if(len < 0 ){
			throw new IllegalArgumentException("content length can't be " + len );
		}
		this.contentLength.setValue(len);
		this.httpDatagram.addHeader(HttpHeader.HeaderName.CONTENT_LENGTH,Integer.toString(len));
	}

	@Override
	public void setContentType(String contentType) {
		int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
		if (charsetIndex != -1) {
			this.characterEncoding.setValue(contentType.substring(charsetIndex + CHARSET_PREFIX.length()));
		}
		headersHolder.setHeader(HttpHeader.HeaderName.CONTENT_TYPE,contentType);
		/*
		this.contentType = contentType;
		if (contentType != null) {
			try {
				MediaType mediaType = MediaType.parseMediaType(contentType);
				if (mediaType.getCharset() != null) {
					this.characterEncoding = mediaType.getCharset().name();
					this.charset = true;
				}
			}
			catch (Exception ex) {
				// Try to get charset value anyway
				int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
				if (charsetIndex != -1) {
					this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
					this.charset = true;
				}
			}
			updateContentTypeHeader();
		}
		 */
	}

	@Override
	public synchronized void setBufferSize(int size) {
		this.bufferSize = size ;
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public void flushBuffer() throws IOException {
		setCommitted();
	}

	protected synchronized void setCommitted(){
		this.responseCommitted = true;
	}

	@Override
	public void resetBuffer() {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot reset buffer - response is already committed");
		}
		this.createOutputStreamIfNotExist().reset();
	}

	@Override
	public boolean isCommitted() {
		return responseCommitted;
	}

	@Override
	public void reset() {
		resetBuffer();
		this.characterEncoding.setValue(null);
		this.contentLength.setValue(0);
		this.headersHolder.getHeaders().clear();
	}


	@Override
	public void setLocale(Locale locale) {
		headersHolder.setLocale(locale);
	}

	@Override
	public Locale getLocale() {
		return headersHolder.getLocale();
	}

	public HttpDatagram getHttpDatagram() {
		return httpDatagram;
	}

}
