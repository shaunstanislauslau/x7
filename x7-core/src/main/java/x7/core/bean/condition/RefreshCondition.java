package x7.core.bean.condition;

import java.util.Map;

/**
 * Created by Sim on 2018/8/20.
 */
public class RefreshCondition<T> {
    private T obj;
    private Map<String, Object> conditionMap;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public Map<String, Object> getConditionMap() {
        return conditionMap;
    }

    public void setConditionMap(Map<String, Object> conditionMap) {
        for (String key : conditionMap.keySet()){
            Object value = conditionMap.get(key);
            if (value.getClass().isEnum()){
                conditionMap.put(key,value.toString());
            }
        }
        this.conditionMap = conditionMap;
    }

    @Override
    public String toString() {
        return "RefreshCondition{" +
                "obj=" + obj +
                ", conditionMap=" + conditionMap +
                '}';
    }
}
