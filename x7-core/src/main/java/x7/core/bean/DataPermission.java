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

    public class Chain {

        public static void beforeHandle(DataPermission dataPermission, Object userDataPermissionValue) {
            DataPermission dp = (DataPermission) dataPermission;
            Object dataPermissionValue = dp.getDataPermissionValue();
            if (Objects.nonNull(dataPermissionValue)) {

                if (dataPermissionValue instanceof List) {
                    List<Object> dpList = (List<Object>) dataPermissionValue;
                    if (Objects.nonNull(userDataPermissionValue)) {
                        dpList.addAll((List<Object>) userDataPermissionValue);
                    }
                }
            } else {
                dp.setDataPermissionValue(userDataPermissionValue);
            }
        }

        protected static void onBuild(Criteria criteria, Object obj) {
            if (obj instanceof DataPermission) {
                if (Objects.isNull(obj))
                    return;
                DataPermission dp = (DataPermission) obj;
                criteria.setDataPermission(dp);
            }
        }

        protected static void befroeGetCriteria(CriteriaBuilder builder, Criteria criteria) {

            DataPermission dp = criteria.getDataPermission();
            if (Objects.isNull(dp))
                return;
            final String key = dp.getDataPermissionKey();
            final Object value = dp.getDataPermissionValue();

            if (Objects.isNull(value) || StringUtil.isNullOrEmpty(key))
                return;

            /*
             * DataPermission
             */
            String property = (criteria instanceof Criteria.ResultMapped) ? (criteria.getClz().getSimpleName() + "." + key) : key;
            if (value instanceof String) {
                String s = (String) value;

                String v = s.endsWith(SqlScript.LIKE_HOLDER) ? s : s + SqlScript.LIKE_HOLDER;
                builder.and().likeRight(property, v);

            } else if (value instanceof List) {

                List<Object> dpsList = (List<Object>) value;
                if (dpsList.isEmpty())
                    return;
                builder.and().in(property,dpsList);

            }
        }
    }

}
