package jd.server.context.sevlet.datagram;

import jd.util.Assert;
import jd.util.StrUt;

import javax.servlet.http.Cookie;
import java.util.*;

public class HttpSpecialHeaders {

    public final static class CookieHeader {
        public static Cookie[]  parse(String cookieHeader){
            if(StrUt.isNotBlank(cookieHeader)){
                List<Cookie> cookieList = new ArrayList<>();
                for (String cookieStr : cookieHeader.split(";")) {
                    String[] nv = cookieStr.split("=");
                    if(nv.length==2){
                        cookieList.add(new Cookie(nv[0],nv[1]));
                    }
                }
                return cookieList.toArray(new Cookie[cookieList.size()]);
            }
            return new Cookie[]{};
        }

        public static String toHeader(Cookie cookie){
            Assert.notNull(cookie);
            StringBuilder sb = new StringBuilder(cookie.getName()).append("=").append(cookie.getValue());
            if(StrUt.isNotBlank(cookie.getDomain())){
                sb.append("; Domain=").append(cookie.getDomain());
            }
            if(StrUt.isNotBlank(cookie.getPath())){
                sb.append("; Path=").append(cookie.getPath());
            }
            if(cookie.getMaxAge() > 0){
                sb.append("; Max-Age=").append(cookie.getMaxAge());
            }
            if(cookie.getSecure()){
                sb.append("; Secure");
            }
            return sb.toString();
        }
    }

    public static final class Language {
        public static Vector<Locale> getAcceptLocales(Enumeration<String> acceptLanguages){
            Map<Float,Locale> map = new HashMap<>();
            if(acceptLanguages != null && acceptLanguages.hasMoreElements()) {
                String acceptLanguage;
                while(acceptLanguages.hasMoreElements()) {
                    acceptLanguage = acceptLanguages.nextElement();
                    map.putAll(getAcceptLocales(acceptLanguage));
                }
            }
            return new Vector<>(map.values()) ;
        }

        public static Map<Float, Locale> getAcceptLocales(String acceptLanguage){
            Map<Float,Locale> map = new HashMap<>();
            String lang ;
            float q = 1 ;
            String[] langQ ;
            Locale locale ;
            for(String item  : acceptLanguage.split(",")) {
                if(StrUt.isNotBlank(item)) {
                    langQ = item.trim().split(";q=");
                    lang = langQ[0];
                    q = langQ.length > 1 ? Float.valueOf(langQ[1]) : 1 ;

                    String[] langCountry = lang.split("-");
                    locale = langCountry.length > 1
                            ? new Locale(langCountry[0],langCountry[1])
                            : new Locale(lang) ;
                    map.put(q, locale);
                }
            }
            return map ;
        }

        public static Vector<Locale> parse(String header){
            Map<Float, Locale> localeMap = getAcceptLocales(header);
            return new Vector<>(localeMap.values());
        }

        public static String toHeader(Vector<Locale> locales){
            Assert.notEmpty(locales);
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<locales.size();i++) {
                if(i>0){
                    sb.append(", ");
                }
                Locale locale = locales.get(i);
                sb.append(toHeader(locale));
            }
            return sb.toString();
        }
        public static String toHeader(Locale locale){
            return locale.getLanguage().toLowerCase() + "-" +locale.getCountry().toUpperCase() ;
        }
    }


    public static void main(String[] args) {
        System.out.println("");
    }
}
