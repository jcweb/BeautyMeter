package cn.yaman.http;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * 自定义解析JSON结构
 * @author timpkins
 */
public class ResponseDeserializer implements cn.lamb.http.ResponseDeserializer<HttpResponse> {

    @Override
    public Class getTClass() {
        return HttpResponse.class;
    }

    @Override
    public HttpResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        HttpResponse response = new HttpResponse();
        response.setResultCode(object.get("resultCode").getAsInt());
        response.setResultMsg(object.get("resultMsg").getAsString());
        JsonElement element = object.get("data");
        if (element == null || element.isJsonNull()) {
            response.setData("");
        } else if (element.isJsonObject() || element.isJsonArray()) {
            response.setData(object.get("data").toString());
        } else {
            response.setData(element.getAsString());
        }
        return response;
    }
}
