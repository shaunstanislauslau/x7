package x7.core.util;

import com.alibaba.fastjson.JSON;
import x7.core.web.ViewEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Sim on 2018/1/31.
 */
public class ViewEntityUtil {

    public static <T> T parseObject(String responseStr, Class<T> clz) {
        ViewEntity bean = JsonX.toObject(responseStr,ViewEntity.class);
        T t = JSON.toJavaObject((JSON)bean.getBody(),clz);
        return t;
    }

    public static <T> List<T> parseList(String responseStr, Class<T> clz) {
        ViewEntity bean = JsonX.toObject(responseStr,ViewEntity.class);
        JSON jsonObject = (JSON)bean.getBody();
        String text = jsonObject.toJSONString();
        List<T> list = JSON.parseArray(text, clz);
        return list;
    }

    public static Map<String,Object> parseMap(String responseStr) {
        ViewEntity bean = JsonX.toObject(responseStr,ViewEntity.class);
        JSON jsonObject = (JSON)bean.getBody();
        return (Map)jsonObject;
    }
}
