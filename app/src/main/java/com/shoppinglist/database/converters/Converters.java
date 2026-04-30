package com.shoppinglist.database.converters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Converters {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromMap(Map<String, Boolean> map) {
        if (map == null) return null;
        return gson.toJson(map);
    }

    @TypeConverter
    public static Map<String, Boolean> toMap(String value) {
        if (value == null) return new HashMap<>();
        Type type = new TypeToken<HashMap<String, Boolean>>() {}.getType();
        return gson.fromJson(value, type);
    }
}