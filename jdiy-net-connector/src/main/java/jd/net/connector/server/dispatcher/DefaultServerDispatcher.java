package jd.net.connector.server.dispatcher;

import jd.net.protocol.common.connection.NetConnection;
import jd.net.protocol.common.datagram.Datagram;
import jd.util.ArrUt;
import jd.util.lang.collection.MultiLevelMap;
import jd.util.lang.collection.MultiValueMap;

import java.io.IOException;

public class DefaultServerDispatcher<K> implements IServerDispatcher {

    final MultiLevelMap<K,IProcessor> processors = new MultiLevelMap<>();
    final private IDatagramMapper<K> datagramMapper ;
    private IProcessor matchAnyProcess = null ;
    public DefaultServerDispatcher(IDatagramMapper datagramMapper, IProcessor matchAnyProcess) {
        this.datagramMapper = datagramMapper;
        this.matchAnyProcess = matchAnyProcess;
    }
    public synchronized void addProcess(IMappedProcessor<K> processor){
        for (K[] mapping : processor.mappings()) {
            if(mapping != null && mapping.length > 0){
                processors.put(mapping,processor);
            }else{
                if( matchAnyProcess == null){
                    matchAnyProcess = processor ;
                }else {
                    throw new IllegalArgumentException("mapping is empty");
                }
            }
        }
    }

    @Override
    public void dispatch(NetConnection netConnection, Datagram datagram) throws IOException {
        K[] mapping = datagramMapper.mapping(datagram);
        IProcessor processor = matchAnyProcess ;
        if(mapping != null || mapping.length >0){
            processor = processors.getValue(mapping);
        }
        if( processor == null){
            processor = matchAnyProcess ;
        }
        if(processor == null){
            throw new RuntimeException("not process found");
        }
        processor.process(netConnection,datagram);
    }
}
