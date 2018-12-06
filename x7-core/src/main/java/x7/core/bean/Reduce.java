package x7.core.bean;


public class Reduce {
    private Criteria.ReduceType type;
    private String property;

    public Criteria.ReduceType getType() {
        return type;
    }
    public void setType(Criteria.ReduceType type) {
        this.type = type;
    }
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "Reduce{" +
                "type=" + type +
                ", property='" + property + '\'' +
                '}';
    }
}
