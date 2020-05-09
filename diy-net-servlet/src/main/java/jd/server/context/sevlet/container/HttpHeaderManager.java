package jd.server.context.sevlet.container;

import jd.util.lang.collection.MultiValueMap;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpHeaderManager {

    private final MultiValueMap<String,Object> headers ;

}
