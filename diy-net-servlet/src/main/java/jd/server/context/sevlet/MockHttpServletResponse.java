/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jd.server.context.sevlet;

import org.springframework.http.MediaType;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.mock.web.HeaderValueHolder;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Mock implementation of the {@link HttpServletResponse} interface.
 *
 * <p>As of Spring 4.0, this set of mocks is designed on a Servlet 3.0 baseline.
 * Beyond that, {@code MockHttpServletResponse} is also compatible with Servlet
 * 3.1's {@code setContentLengthLong()} method.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Brian Clozel
 * @since 1.0.2
 */
public class MockHttpServletResponse implements HttpServletResponse {

	private static final String CHARSET_PREFIX = "charset=";

	private static final String CONTENT_TYPE_HEADER = "Content-Type";

	private static final String CONTENT_LENGTH_HEADER = "Content-Length";

	private static final String LOCATION_HEADER = "Location";

	private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");


	//---------------------------------------------------------------------
	// ServletResponse properties
	//---------------------------------------------------------------------

	private boolean outputStreamAccessAllowed = true;

	private boolean writerAccessAllowed = true;

	private String characterEncoding = WebUtils.DEFAULT_CHARACTER_ENCODING;

	private boolean charset = false;

	private final ByteArrayOutputStream content = new ByteArrayOutputStream(1024);

	private final ServletOutputStream outputStream = new ResponseServletOutputStream(this.content);

	private PrintWriter writer;

	private long contentLength = 0;

	private String contentType;

	private int bufferSize = 4096;

	private boolean committed;

	private Locale locale = Locale.getDefault();


	//---------------------------------------------------------------------
	// HttpServletResponse properties
	//---------------------------------------------------------------------

	private final List<Cookie> cookies = new ArrayList<Cookie>();

	private final Map<String, HeaderValueHolder> headers = new LinkedCaseInsensitiveMap<HeaderValueHolder>();

	private int status = HttpServletResponse.SC_OK;

	private String errorMessage;

	private String forwardedUrl;

	private final List<String> includedUrls = new ArrayList<String>();


	//---------------------------------------------------------------------
	// ServletResponse interface
	//---------------------------------------------------------------------

	/**
	 * Set whether {@link #getOutputStream()} access is allowed.
	 * <p>Default is {@code true}.
	 */
	public void setOutputStreamAccessAllowed(boolean outputStreamAccessAllowed) {
		this.outputStreamAccessAllowed = outputStreamAccessAllowed;
	}

	/**
	 * Return whether {@link #getOutputStream()} access is allowed.
	 */
	public boolean isOutputStreamAccessAllowed() {
		return this.outputStreamAccessAllowed;
	}

	/**
	 * Set whether {@link #getWriter()} access is allowed.
	 * <p>Default is {@code true}.
	 */
	public void setWriterAccessAllowed(boolean writerAccessAllowed) {
		this.writerAccessAllowed = writerAccessAllowed;
	}

	/**
	 * Return whether {@link #getOutputStream()} access is allowed.
	 */
	public boolean isWriterAccessAllowed() {
		return this.writerAccessAllowed;
	}

	/**
	 * Return whether the character encoding has been set.
	 * <p>If {@code false}, {@link #getCharacterEncoding()} will return a default encoding value.
	 */
	public boolean isCharset() {
		return this.charset;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		this.charset = true;
		updateContentTypeHeader();
	}

	private void updateContentTypeHeader() {
		if (this.contentType != null) {
			StringBuilder sb = new StringBuilder(this.contentType);
			if (!this.contentType.toLowerCase().contains(CHARSET_PREFIX) && this.charset) {
				sb.append(";").append(CHARSET_PREFIX).append(this.characterEncoding);
			}
			doAddHeaderValue(CONTENT_TYPE_HEADER, sb.toString(), true);
		}
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public ServletOutputStream getOutputStream() {
		if (!this.outputStreamAccessAllowed) {
			throw new IllegalStateException("OutputStream access not allowed");
		}
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws UnsupportedEncodingException {
		if (!this.writerAccessAllowed) {
			throw new IllegalStateException("Writer access not allowed");
		}
		if (this.writer == null) {
			Writer targetWriter = (this.characterEncoding != null ?
					new OutputStreamWriter(this.content, this.characterEncoding) : new OutputStreamWriter(this.content));
			this.writer = new ResponsePrintWriter(targetWriter);
		}
		return this.writer;
	}

	public byte[] getContentAsByteArray() {
		flushBuffer();
		return this.content.toByteArray();
	}

	public String getContentAsString() throws UnsupportedEncodingException {
		flushBuffer();
		return (this.characterEncoding != null ?
				this.content.toString(this.characterEncoding) : this.content.toString());
	}

	@Override
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
		doAddHeaderValue(CONTENT_LENGTH_HEADER, contentLength, true);
	}

	public int getContentLength() {
		return (int) this.contentLength;
	}

	public void setContentLengthLong(long contentLength) {
		this.contentLength = contentLength;
		doAddHeaderValue(CONTENT_LENGTH_HEADER, contentLength, true);
	}

	public long getContentLengthLong() {
		return this.contentLength;
	}

	@Override
	public void setContentType(String contentType) {
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
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	@Override
	public void flushBuffer() {
		setCommitted(true);
	}

	@Override
	public void resetBuffer() {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot reset buffer - response is already committed");
		}
		this.content.reset();
	}

	private void setCommittedIfBufferSizeExceeded() {
		int bufSize = getBufferSize();
		if (bufSize > 0 && this.content.size() > bufSize) {
			setCommitted(true);
		}
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}

	@Override
	public boolean isCommitted() {
		return this.committed;
	}

	@Override
	public void reset() {
		resetBuffer();
		this.characterEncoding = null;
		this.contentLength = 0;
		this.contentType = null;
		this.locale = null;
		this.cookies.clear();
		this.headers.clear();
		this.status = HttpServletResponse.SC_OK;
		this.errorMessage = null;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}


	//---------------------------------------------------------------------
	// HttpServletResponse interface
	//---------------------------------------------------------------------

	/**
	 * The default implementation returns the given URL String as-is.
	 * <p>Can be overridden in subclasses, appending a session id or the like.
	 */
	@Override
	public String encodeURL(String url) {
		return url;
	}

	/**
	 * The default implementation delegates to {@link #encodeURL},
	 * returning the given URL String as-is.
	 * <p>Can be overridden in subclasses, appending a session id or the like
	 * in a redirect-specific fashion. For general URL encoding rules,
	 * override the common {@link #encodeURL} method instead, applying
	 * to redirect URLs as well as to general URLs.
	 */
	@Override
	public String encodeRedirectURL(String url) {
		return encodeURL(url);
	}

	@Override
	@Deprecated
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	@Deprecated
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
	public void sendError(int status, String errorMessage) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot set error status - response is already committed");
		}
		this.status = status;
		this.errorMessage = errorMessage;
		setCommitted(true);
	}

	@Override
	public void sendError(int status) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot set error status - response is already committed");
		}
		this.status = status;
		setCommitted(true);
	}

	@Override
	public void sendRedirect(String url) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot send redirect - response is already committed");
		}
		Assert.notNull(url, "Redirect URL must not be null");
		setHeader(LOCATION_HEADER, url);
		setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		setCommitted(true);
	}

	public String getRedirectedUrl() {
		return getHeader(LOCATION_HEADER);
	}

	@Override
	public void setStatus(int status) {
		if (!this.isCommitted()) {
			this.status = status;
		}
	}

	@Override
	@Deprecated
	public void setStatus(int status, String errorMessage) {
		if (!this.isCommitted()) {
			this.status = status;
			this.errorMessage = errorMessage;
		}
	}

	@Override
	public int getStatus() {
		return this.status;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}


	//---------------------------------------------------------------------
	// Methods for MockRequestDispatcher
	//---------------------------------------------------------------------

	public void setForwardedUrl(String forwardedUrl) {
		this.forwardedUrl = forwardedUrl;
	}

	public String getForwardedUrl() {
		return this.forwardedUrl;
	}

	public void setIncludedUrl(String includedUrl) {
		this.includedUrls.clear();
		if (includedUrl != null) {
			this.includedUrls.add(includedUrl);
		}
	}

	public String getIncludedUrl() {
		int count = this.includedUrls.size();
		if (count > 1) {
			throw new IllegalStateException(
					"More than 1 URL included - check getIncludedUrls instead: " + this.includedUrls);
		}
		return (count == 1 ? this.includedUrls.get(0) : null);
	}

	public void addIncludedUrl(String includedUrl) {
		Assert.notNull(includedUrl, "Included URL must not be null");
		this.includedUrls.add(includedUrl);
	}

	public List<String> getIncludedUrls() {
		return this.includedUrls;
	}


	/**
	 * Inner class that adapts the ServletOutputStream to mark the
	 * response as committed once the buffer size is exceeded.
	 */
	private class ResponseServletOutputStream extends DelegatingServletOutputStream {

		public ResponseServletOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(int b) throws IOException {
			super.write(b);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void flush() throws IOException {
			super.flush();
			setCommitted(true);
		}
	}


	/**
	 * Inner class that adapts the PrintWriter to mark the
	 * response as committed once the buffer size is exceeded.
	 */
	private class ResponsePrintWriter extends PrintWriter {

		public ResponsePrintWriter(Writer out) {
			super(out, true);
		}

		@Override
		public void write(char[] buf, int off, int len) {
			super.write(buf, off, len);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void write(String s, int off, int len) {
			super.write(s, off, len);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void write(int c) {
			super.write(c);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void flush() {
			super.flush();
			setCommitted(true);
		}
	}

}
