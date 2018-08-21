package x7.core.bean.condition;

import java.util.List;

/**
 * Created by Sim on 2018/8/20.
 */
public class InCondition {
    private String property;
    private List<? extends Object> inList;

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

    @Override
    public String toString() {
        return "InCondition{" +
                "property='" + property + '\'' +
                ", inList=" + inList +
                '}';
    }
}