package bgu.spl.mics.application.helpers;

import com.google.gson.Gson;

public class JsonUtils {

    public static <T> T deserializeJsonToObj(String json, Class<T> tClass) {
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }
}
