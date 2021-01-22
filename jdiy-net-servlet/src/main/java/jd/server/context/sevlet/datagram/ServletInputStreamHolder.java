package jd.server.context.sevlet.datagram;

import jd.net.protocol.app.http.datagram.HttpDatagram;
import jd.net.protocol.common.content.ByteArrayContent;
import jd.util.lang.concurrent.ThreadSafeVar;

import javax.servlet.ServletInputStream;
import java.io.*;
import java.util.Optional;

public class ServletInputStreamHolder {

    private final ByteArrayContent content ;
    private volatile InputStream rawInputStream  ;
    private volatile BufferedReader reader ;
    private volatile ServletInputStream inputStream;
    private final ThreadSafeVar<String> encoding ;
    public ServletInputStreamHolder(HttpDatagram httpDatagram,ThreadSafeVar<String> encoding) {
        this.content = httpDatagram.getBody();
        this.encoding =encoding ;
    }


    private synchronized InputStream getRawInputStream() throws IOException {
        if(rawInputStream == null){
            rawInputStream = content != null && content.getContentLength() > 0 ? content.openStream() : new ByteArrayInputStream(new byte[]{});
        }
        return rawInputStream ;
    }

    public ServletInputStream getInputStream() throws IOException {
        if(inputStream == null){
            if(reader != null){
                synchronized (this){
                    if(reader != null){
                        throw new IllegalStateException("getReader() method  has already been called for this request");
                    }
                }
            }
            final InputStream is = new BufferedInputStream(getRawInputStream());
            inputStream = new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return is.read();
                }
            };
        }
        return inputStream;
    }


    /**
     * Retrieves the body of the request as character data using
     * a <code>BufferedReader</code>.  The reader translates the character
     * data according to the character encoding used on the body.
     * Either this method or {@link #getInputStream} may be called to read the
     * body, not both.
     *
     *
     * @return					a <code>BufferedReader</code>
     *						containing the body of the request
     *
     * @exception UnsupportedEncodingException 	if the character set encoding
     * 						used is not supported and the
     *						text cannot be decoded
     *
     * @exception IllegalStateException   	if {@link #getInputStream} method
     * 						has been called on this request
     *
     * @exception IOException  			if an input or output exception occurred
     *
     * @see 					#getInputStream
     *
     */

    public BufferedReader getReader() throws IOException{
        if(reader == null){
            if(inputStream != null){
                synchronized (this){
                    if(inputStream != null){
                        throw new IllegalStateException("getInputStream() method  has already been called for this request");
                    }
                }
            }
            this.reader = new BufferedReader(new InputStreamReader(getRawInputStream(),encoding.getValue()));
        }
        return this.reader;
    }

}
