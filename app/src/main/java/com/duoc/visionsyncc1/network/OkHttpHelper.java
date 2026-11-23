package com.duoc.visionsyncc1.network;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.File;

/**
 * Helper class to avoid Kotlin deprecation warnings on OkHttp 4.x APIs.
 * Java doesn't see the @Deprecated with ReplaceWith annotations.
 */
public class OkHttpHelper {
    public static MediaType parseMediaType(String mediaType) {
        return MediaType.parse(mediaType);
    }

    public static RequestBody createRequestBody(MediaType mediaType, File file) {
        return RequestBody.create(mediaType, file);
    }
}