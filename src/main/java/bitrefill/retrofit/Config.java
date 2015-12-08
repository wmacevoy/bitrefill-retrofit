/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitrefill.retrofit;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.io.PrintStream;
import java.io.Reader;

/**
 *
 * @author wmacevoy
 */
public class Config {

    private static boolean isNullish(String value) {
        return (value == null || value.equals("") || value.equals("null"));
    }

    private HttpLoggingInterceptor.Logger logger = null;

    /** get the current logger, with a default of null. */
    HttpLoggingInterceptor.Logger getLogger() {
        return logger;
    }

    /** set the logger to use the given PrintStream, replacing lines
     *  starting with Authorization: with Authorization: (removed).
     * 
     * @param out 
     */
    void setLogger(final PrintStream out) {
        logger = new HttpLoggingInterceptor.Logger() {
            public void log(String message) {
                if (message.startsWith("Authorization:")) {
                    out.println("Authorization: (removed)");
                } else {
                    out.println(message);
                }
            }
        };
    }

    /** set the logger according to value:
     * <ul>
     * <li>null, "", or "null" corresponds to no logger.</li>
     * <li>"/dev/stdout" or "/dev/stderr" wraps System.out or System.err.</li>
     * </ul>
     */
    void setLogger(String value) {
        if (isNullish(value)) {
            logger = null;
            return;
        }
        if (value.equals("/dev/stdout") || value.equals("/dev/stderr")) {
            setLogger(value.equals("/dev/stdout") ? System.out : System.err);
            return;
        }
        throw new UnsupportedOperationException("setLogger("+value+")");
    }
    
    void setLogger(HttpLoggingInterceptor.Logger value) {
        logger = value;
    }
    
    
    private HttpLoggingInterceptor.Level loggerLevel = HttpLoggingInterceptor.Level.BODY;

    /** get current loggerLevel, with a default of 
     *  HttpLoggingInterceptor.Level.BODY */
    public HttpLoggingInterceptor.Level getLoggerLevel() {
        return loggerLevel;
    }
    
    /** "BASIC", "BODY" (default and for null, "" or "null"), "HEADERS", or "NONE" */
    public void setLoggerLevel(String value) {
        if (isNullish(value)) {
            loggerLevel=HttpLoggingInterceptor.Level.BODY;
            return;
        }
        switch(value) {
            case "BASIC": loggerLevel = HttpLoggingInterceptor.Level.BASIC; return;
            case "BODY": loggerLevel = HttpLoggingInterceptor.Level.BODY; return;
            case "HEADERS": loggerLevel = HttpLoggingInterceptor.Level.HEADERS; return;
            case "NONE": loggerLevel = HttpLoggingInterceptor.Level.NONE; return;
            default: throw new UnsupportedOperationException("Unsupported loggerLevel: " + value);
        }
    }
    
    public void setLoggerLevel(HttpLoggingInterceptor.Level value) {
        if (value == null) {
            loggerLevel=HttpLoggingInterceptor.Level.BODY;
            return;
        }
        loggerLevel = value;
    }

    private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String value) {
        key = isNullish(value) ? null : value;
    }
    
    private String secret;
    public String getSecret() {
        return secret;
    }
    public void setSecret(String value) {
        secret = isNullish(value) ? null : value;
    }

    private String URL = APIv1.URL;
    /** get base URL, width default of APIv1.URL */
    public String getURL() { return URL; }

    /** set base URL.  A value of
     *  null, "", "null", or "real" corresponds to APIv1.URL (the default),
     *  and "mock" corresponds to APIv1.MOCK_URL.
     */
    public void setURL(String value) {
        if (isNullish(value) || value.equals("real")) {
            URL = APIv1.URL;
            return;
        }
        if (value.equals("mock")) {
            URL=APIv1.MOCK_URL;
            return;
        }
        URL=value;
    }

    public Config() {}
    public Config(String key, String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    public Config(JsonObject json) {
        if (json == null) return;
        if (json.has("key")) setKey(json.get("key").getAsString());
        if (json.has("secret")) setSecret(json.get("secret").getAsString());
        if (json.has("url")) setURL(json.get("url").getAsString());
        if (json.has("logger")) setLogger(json.get("logger").getAsString());
        if (json.has("loggerLevel")) setLoggerLevel(json.get("loggerLevel").getAsString());
    }
    
    public static Config fromJson(Reader source) {
        return new Config(new GsonBuilder().create().fromJson(source, JsonObject.class));
    }

    public static Config fromJson(JsonObject source) {
        return new Config(source);
    }

    public static Config fromJson(String source) {
        return new Config(new GsonBuilder().create().fromJson(source, JsonObject.class));
    }
}
