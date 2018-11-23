package x7.core.bean.condition;

import x7.core.bean.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RefreshCondition<T> {

    private T obj;
    private CriteriaCondition condition;
    private List<Criteria.X> refreshList = new ArrayList<>();
    private String sourceStript;//FIXME fetch

    private transient Class clz;
    private transient  CriteriaBuilder builder;

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public List<Criteria.X> getRefreshList() {
        return refreshList;
    }

    public void setRefreshList(List<Criteria.X> refreshList) {
        this.refreshList = refreshList;
    }

    public String getSourceStript() {
        return sourceStript;
    }

    public void setSourceStript(String sourceStript) {
        this.sourceStript = sourceStript;
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

    public RefreshCondition(){
        CriteriaBuilder builder = CriteriaBuilder.buildCondition();
        this.builder = builder;
    }

    public RefreshCondition(T t){

        this.obj = t;
        CriteriaBuilder builder = CriteriaBuilder.buildCondition();
        this.builder = builder;

    }


    public CriteriaBuilder.ConditionBuilder  and(){

        return this.builder.and();
    }

    /**
     *
     * String sqlX = "propertyA = propertyA + propertyB + 1"
     * @return RefreshCondition
     */
    public RefreshCondition refresh(String sqlX){

        if (Objects.isNull(sqlX))
            return this;

        Criteria.X x = new Criteria.X();
        x.setPredicate(Predicate.X);
        x.setValue(sqlX);
        this.refreshList.add(x);

        return this;
    }

    public RefreshCondition refresh(String property, Object value){

        if (Objects.isNull(value))
            return this;

        Criteria.X x = new Criteria.X();
        x.setPredicate(Predicate.EQ);
        x.setKey(property);
        x.setValue(value);
        this.refreshList.add(x);

        return this;
    }

}
