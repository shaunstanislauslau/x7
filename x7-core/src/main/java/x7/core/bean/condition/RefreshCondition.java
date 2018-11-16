package x7.core.bean.condition;

import x7.core.bean.CriteriaCondition;

/**
 * Created by Sim on 2018/8/20.
 */
public class RefreshCondition<T> {
    private T obj;
    private CriteriaCondition condition;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public CriteriaCondition getCondition() {
        return condition;
    }

    public void setCondition(CriteriaCondition condition) {
        this.condition = condition;
    }
}
