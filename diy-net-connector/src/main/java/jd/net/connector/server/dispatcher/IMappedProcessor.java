package jd.net.connector.server.dispatcher;

public interface IMappedProcessor<K> extends IProcessor {
    public K[][] mappings() ;

}
