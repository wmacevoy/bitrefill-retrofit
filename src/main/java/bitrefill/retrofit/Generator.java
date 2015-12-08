/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitrefill.retrofit;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 *
 * @author wmacevoy
 */
public class Generator {

    static String getAuth(Config config) {
        String key = config.getKey();
        String secret = config.getSecret();
        if (key == null || secret == null) {
            return null;
        }

        String encoded;
        try {
            encoded = Base64.getEncoder()
                    .withoutPadding()
                    .encodeToString((key + ":" + secret).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        return encoded;
    }

    static void configAuth(OkHttpClient client, Config config) {
        final String auth = getAuth(config);
        if (auth == null) {
            return;
        }
        client.interceptors().clear();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Basic " + auth)
                        .header("Accept", "applicaton/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

    }

    static void configLogger(OkHttpClient client, Config config) {
        HttpLoggingInterceptor.Logger logger = config.getLogger();
        if (logger == null) {
            return;
        }
        HttpLoggingInterceptor.Level loggerLevel = config.getLoggerLevel();
        if (loggerLevel == null) {
            loggerLevel = HttpLoggingInterceptor.Level.BODY;
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(logger);
        interceptor.setLevel(loggerLevel);
        client.interceptors().add(interceptor);
    }

    static OkHttpClient getClient(Config config) {
        OkHttpClient client = new OkHttpClient();
        configAuth(client, config);
        configLogger(client, config);
        return client;
    }

    static Retrofit.Builder getBuilder(Config config) {
        return new Retrofit.Builder()
                .baseUrl(config.getURL())
                .addConverterFactory(GsonConverterFactory.create());
    }

    /** create retrofit API from given configuration */
    public static APIv1 generate(Config config) {
        return getBuilder(config).client(getClient(config)).build().create(APIv1.class);
    }
}
