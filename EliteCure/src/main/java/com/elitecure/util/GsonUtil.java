package com.elitecure.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Utility class for creating Gson instance with Java 8 Date/Time support
 */
public class GsonUtil {
    
    private static final Gson GSON = createGson();
    
    /**
     * Get configured Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }
    
    /**
     * Create Gson with custom adapters for LocalDate, LocalTime, LocalDateTime
     */
    private static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, 
                (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
                    context.serialize(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class,
                (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                    context.serialize(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    context.serialize(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();
    }
}
