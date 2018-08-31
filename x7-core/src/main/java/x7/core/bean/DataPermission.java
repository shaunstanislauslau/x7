package x7.core.bean;

import x7.core.util.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * DataPermission, 数据权限, 支持数据库like查找, 或IN; 或搜索,
 * Created by Sim on 2016/5/23.
 */
public interface DataPermission {

    String getDataPermissionKey();

    void setDataPermissionValue(Object dataPermissionValue);

    Object getDataPermissionValue();

    public class Filter {

        public static void beforeHandle(Object dataPermission, Object userDataPermissionValue) {
            if (dataPermission instanceof DataPermission){
                DataPermission dp = (DataPermission) dataPermission;
                Object dataPermissionValue = dp.getDataPermissionValue();
                if (Objects.nonNull(dataPermissionValue)){

                    if (dataPermissionValue instanceof List){
                        List<Object> dpList = (List<Object>)dataPermissionValue;
                        if (Objects.nonNull(userDataPermissionValue)){
                            dpList.addAll((List<Object>)userDataPermissionValue);
                        }
                    }
                }else{
                    dp.setDataPermissionValue(userDataPermissionValue);
                }
            }
        }

        public static void onBuild(Criteria criteria, Object obj) {
            if (obj instanceof DataPermission) {
                DataPermission dp = (DataPermission) obj;
                criteria.setDataPermissionKey(dp.getDataPermissionKey());
                criteria.setDataPermissionValue(dp.getDataPermissionValue());
            }
        }

        public static void x(Criteria criteria) {

            final String key = criteria.getDataPermissionKey();
            final Object value = criteria.getDataPermissionValue();

            if (Objects.isNull(value) || StringUtil.isNullOrEmpty(key))
                return;

            for (Criteria.X x : criteria.getListX()) {
                if (x.getKey().endsWith(key)) {//if added, return
                    return;
                }
            }

            if (value instanceof String) {
                criteria.add(new Criteria.X() {

                    @Override
                    public String getKey() {
                        return (criteria instanceof Criteria.Fetch) ? (criteria.getClz().getSimpleName() + "." + key) : key;
                    }

                    @Override
                    public Predicate getPredicate() {
                        return Predicate.LIKE;
                    }

                    @Override
                    public Object getValue() {
                        return ((String) value).endsWith("%") ? value : value + "%";
                    }

                });
            } else if (value instanceof List) {

                List<Object> dpsList = (List<Object>) value;
                if (dpsList.isEmpty())
                    return;

                criteria.add(new Criteria.X() {

                    @Override
                    public String getKey() {
                        return (criteria instanceof Criteria.Fetch) ? (criteria.getClz().getSimpleName() + "." + key) : key;
                    }

                    @Override
                    public Predicate getPredicate() {
                        return Predicate.IN;
                    }

                    @Override
                    public Object getValue() {
                        return dpsList;
                    }

                });

            }
        }
    }


}
