package x7.core.bean;

import java.util.List;

/**
 * Created by Sim on 2018/11/16.
 */
public interface CriteriaCondition{
    List<Criteria.X> getListX();
    List<Object> getValueList();

//    default void addX(Criteria.X x){
//        getListX().add(x);
//    }
//
//
}
