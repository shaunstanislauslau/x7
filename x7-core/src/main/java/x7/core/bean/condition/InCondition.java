package x7.core.bean.condition;

import java.util.List;


public class InCondition {

    private String property;
    private List<? extends Object> inList;

    private transient Class clz;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public List<? extends Object> getInList() {
        return inList;
    }

    public void setInList(List<? extends Object> inList) {
        this.inList = inList;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public InCondition(){
    }

    public InCondition(String property,List<? extends Object> inList ){
        this.property = property;
        this.inList = inList;
    }

    @Override
    public String toString() {
        return "InCondition{" +
                "property='" + property + '\'' +
                ", inList=" + inList +
                '}';
    }
}