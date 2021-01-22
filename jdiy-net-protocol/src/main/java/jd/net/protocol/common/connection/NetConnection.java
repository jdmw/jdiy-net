package jd.net.protocol.common.connection;

import jd.util.io.IOUt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Data
@Slf4j
public abstract class NetConnection implements INetConnection {

    private boolean isNew  = true;
    private Runnable closeAction ;
    private boolean closed ;

    @Override
    public void write(byte[] bytes) throws IOException {
        getOutputStream().write(bytes);
    }

    @Override
    public int read() throws IOException {
        return getInputStream().read();
    }

    public byte[] readByteArray() throws IOException {
        try{
            return IOUt.toByteArray(getInputStream());
        }catch (IOException e){
            checkIsClose(e);
            throw e ;
        }
    }

    @Override
    public byte[] readByteArray(int length) throws IOException {
        try{
            return IOUt.readToByteArray(getInputStream(),length);
        }catch (IOException e){
            checkIsClose(e);
            throw e ;
        }
    }

    public void checkIsClose(IOException e) throws IOException {
        if( e instanceof java.net.SocketException){
            close();
        }else{
            checkIsClosed();
        }
    }

    public boolean checkIsClosed(){
        if(!closed && isSocketClose()){
            try {
                close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return closed ;
    }

    @Override
    public synchronized void close() throws IOException {
        closed = false;
        if(!isSocketClose()) IOUt.close(getOutputStream(),getInputStream());
        if(closeAction != null) {
            try{
                closeAction.run();
            }catch (Exception e){
                log.error(e.getMessage(),e);
            } finally {
                closeAction = null ;
            }
        }
    }


    public void flush() throws IOException {
        getOutputStream().flush();
    }
}
