package x7.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sim on 2018/9/11.
 */
public class Distinct implements Serializable {

    private static final long serialVersionUID = 5436698915888081349L;

    private List<String> list = new ArrayList<>();

    public List<String> getList(){
        return list;
    }

    public void setList(List<String> list){
        this.list = list;
    }

    public void add(String resultKey){
        this.list.add(resultKey);
    }

    @Override
    public String toString() {
        return "Distinct{" +
                "list=" + list +
                '}';
    }
}
