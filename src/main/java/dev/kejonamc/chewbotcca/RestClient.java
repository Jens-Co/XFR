package dev.kejonamc.chewbotcca;

import dev.kejonamc.XFR;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;

public class RestClient {
    private static final Logger logger = new XFR().getLogger();

    public static String getXBL(String url, String apiKey) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization", apiKey)
                .get()
                .build();

        logger.debug("Making call to GET " + url);
        return performRequest(request);
    }

    @NotNull
    public static String performRequest(Request request) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                // Why http 1 sight..
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));

        OkHttpClient client =  builder.build();

        try (Response response = client.newCall(request).execute()) {
            String body;
            ResponseBody responseBody = response.body();
            if(responseBody == null) {
                body = "{}";
            } else {
                body = responseBody.string();
            }
            return body;
        } catch (IOException e) {
            logger.warn("Call to " + request.url() + " failed with IOException!");
            return "{error: 'IOException'}";
        }
    }

    public static String xblStatusCode(String url, String apiKey) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization", apiKey)
                .get()
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // Why http 1 sight..
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));

        OkHttpClient client =  builder.build();
        try {
            Response response = client.newCall(request).execute();
            String errorCode = String.valueOf(response.code());
            response.close();
            return errorCode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
