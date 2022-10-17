package dev.kejonamc.chewbotcca;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class RestClient {
    public static String getXBL(String url, String apiKey) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization", apiKey)
                .get()
                .build();

        LoggerFactory.getLogger(RestClient.class).debug("Making call to GET " + url);
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
            LoggerFactory.getLogger(RestClient.class).warn("Call to " + request.url() + " failed with IOException!");
            return "{error: 'IOException'}";
        }
    }

}
