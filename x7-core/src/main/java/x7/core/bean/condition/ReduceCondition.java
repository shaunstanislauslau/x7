package x7.core.bean.condition;

import x7.core.bean.Criteria;

/**
 * Created by Sim on 2018/8/20.
 */
public class ReduceCondition {
    private Criteria.ReduceType type;
    private String reduceProperty;
    private Criteria criteria;

    public Criteria.ReduceType getType() {
        return type;
    }

    public void setType(Criteria.ReduceType type) {
        this.type = type;
    }

    public String getReduceProperty() {
        return reduceProperty;
    }

    public void setReduceProperty(String reduceProperty) {
        this.reduceProperty = reduceProperty;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "ReduceCondition{" +
                "type=" + type +
                ", reduceProperty='" + reduceProperty + '\'' +
                ", criteria=" + criteria +
                '}';
    }
}