package x7.core.bean.condition;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.CriteriaCondition;

import java.util.Objects;


public class ReduceCondition {

    private Criteria.ReduceType type;
    private String reduceProperty;
    private CriteriaCondition condition;

    private transient Class clz;
    private transient CriteriaBuilder builder;

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

    public CriteriaCondition getCondition() {
        if (Objects.nonNull(this.builder)) {
            this.condition = builder.get();
        }
        return this.condition;
    }

    public void setCondition(CriteriaCondition condition) {
        this.condition = condition;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public CriteriaBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(CriteriaBuilder builder) {
        this.builder = builder;
    }

    public ReduceCondition(){
    }

    public ReduceCondition(Criteria.ReduceType type,String reduceProperty){
        this.type= type;
        this.reduceProperty = reduceProperty;
        CriteriaBuilder builder = CriteriaBuilder.buildCondition();
        this.builder = builder;
    }

    public CriteriaBuilder.ConditionBuilder  and(){
        return this.builder.and();
    }

    @Override
    public String toString() {
        return "ReduceCondition{" +
                "type=" + type +
                ", reduceProperty='" + reduceProperty + '\'' +
                ", condition=" + condition +
                '}';
    }
}