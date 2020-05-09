package jd.server.context.sevlet.datagram;

import lombok.Data;

import java.util.Locale;

@Data
public class ServletSettings {

    private String defaultRequestEncoding = "uft8";
    private String defaultResponseEncoding = "uft8";
    private Locale defaultLocale = Locale.getDefault() ;

}
