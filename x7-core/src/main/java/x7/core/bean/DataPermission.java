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

    static void filter(Criteria criteria) {

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

            List<String> dpsList = (List<String>) value;
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
