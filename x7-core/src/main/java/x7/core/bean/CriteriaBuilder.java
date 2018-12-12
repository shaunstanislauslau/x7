/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package x7.core.bean;

import x7.core.bean.Criteria.ResultMapped;
import x7.core.bean.Criteria.X;
import x7.core.repository.Mapped;
import x7.core.util.*;
import x7.core.web.Fetched;
import x7.core.web.Paged;
import x7.core.web.ResultMappedKey;

import java.util.*;
import java.util.Map.Entry;

/**
 * Standard Query Builder
 *
 * @author Sim
 */
public class CriteriaBuilder {

    private Criteria criteria;
    private CriteriaBuilder instance;

    public ConditionBuilder and() {

        X x = new X();
        x.setConjunction(Conjunction.AND);
        x.setValue(Conjunction.AND);

        X current = conditionBuilder.getX();
        if (current != null) {
            X parent = current.getParent();
            if (parent != null) {
                List<X> subList = parent.getSubList();
                if (subList != null) {
                    subList.add(x);
                    x.setParent(parent);
                }
            } else {
                this.criteria.add(x);
            }
        } else {
            this.criteria.add(x);
        }

        conditionBuilder.under(x);

        return conditionBuilder;
    }

    public ConditionBuilder or() {

        X x = new X();
        x.setConjunction(Conjunction.OR);
        x.setValue(Conjunction.OR);

        X current = conditionBuilder.getX();
        if (current != null) {
            X parent = current.getParent();
            if (parent != null) {
                List<X> subList = parent.getSubList();
                if (subList != null) {
                    subList.add(x);
                    x.setParent(parent);
                }
            } else {
                this.criteria.add(x);
            }
        } else {
            this.criteria.add(x);
        }

        conditionBuilder.under(x);

        return conditionBuilder;
    }

    public CriteriaBuilder endSub() {

        X x = new X();
        x.setPredicate(Predicate.SUB_END);
        x.setValue(Predicate.SUB_END);

        X current = conditionBuilder.getX();
        X parent = current.getParent();
        if (parent != null) {
            List<X> subList = parent.getSubList();
            if (subList != null) {
                subList.add(x);
            }

            this.conditionBuilder.under(parent);
        }

        return instance;
    }


    private ConditionBuilder conditionBuilder = new ConditionBuilder() {

        private X x = null;

        @Override
        public X getX() {
            return x;
        }

        @Override
        public void under(X x) {
            this.x = x;
        }

        @Override
        public CriteriaBuilder eq(String property, Object value) {

            if (value == null)
                return instance;
            if (Objects.nonNull(criteria.getParsed())) {
                if (isBaseType_0(property, value))
                    return instance;
            }
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.EQ);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder lt(String property, Object value) {

            if (value == null)
                return instance;
            if (isBaseType_0(property, value))
                return instance;
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LT);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder lte(String property, Object value) {

            if (value == null)
                return instance;

            if (isBaseType_0(property, value))
                return instance;
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LTE);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder gt(String property, Object value) {

            if (value == null)
                return instance;
            if (isBaseType_0(property, value))
                return instance;
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.GT);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder gte(String property, Object value) {

            if (value == null)
                return instance;

            if (isBaseType_0(property, value))
                return instance;
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.GTE);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder ne(String property, Object value) {

            if (value == null)
                return instance;

            if (isBaseType_0(property, value))
                return instance;
            if (isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.NE);
            x.setKey(property);
            x.setValue(value);

            return instance;
        }

        @Override
        public CriteriaBuilder like(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LIKE);
            x.setKey(property);
            x.setValue(SqlScript.LIKE_HOLDER + value + SqlScript.LIKE_HOLDER);

            return instance;
        }

        @Override
        public CriteriaBuilder likeRight(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LIKE);
            x.setKey(property);
            x.setValue(value + SqlScript.LIKE_HOLDER);

            return instance;
        }

        @Override
        public CriteriaBuilder notLike(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.NOT_LIKE);
            x.setKey(property);
            x.setValue(SqlScript.LIKE_HOLDER + value + SqlScript.LIKE_HOLDER);

            return instance;
        }

        @Override
        public CriteriaBuilder between(String property, Object min, Object max) {

            if (min == null || max == null)
                return instance;

            if (isBaseType_0(property, max))
                return instance;
            if (isNullOrEmpty(max))
                return instance;
            if (isNullOrEmpty(min))
                return instance;

            MinMax minMax = new MinMax();
            minMax.setMin(min);
            minMax.setMax(max);

            x.setPredicate(Predicate.BETWEEN);
            x.setKey(property);
            x.setValue(minMax);

            return instance;
        }

        @Override
        public CriteriaBuilder in(String property, List<Object> list) {

            if (list == null || list.isEmpty())
                return instance;

            List<Object> tempList = new ArrayList<Object>();
            for (Object obj : list) {
                if (Objects.isNull(obj))
                    continue;
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                }
            }

            if (tempList.isEmpty())
                return instance;

            if (tempList.size() == 1){
                return eq(property,tempList.get(0));
            }

            x.setPredicate(Predicate.IN);
            x.setKey(property);
            x.setValue(tempList);

            return instance;
        }

        @Override
        public CriteriaBuilder nin(String property, List<Object> list) {

            if (list == null || list.isEmpty())
                return instance;

            List<Object> tempList = new ArrayList<Object>();
            for (Object obj : list) {
                if (Objects.isNull(obj))
                    continue;
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                }
            }

            if (tempList.isEmpty())
                return instance;

            if (tempList.size() == 1){
                return ne(property,tempList.get(0));
            }

            x.setPredicate(Predicate.NOT_IN);
            x.setKey(property);
            x.setValue(tempList);

            return instance;
        }

        @Override
        public CriteriaBuilder nonNull(String property) {

            if (StringUtil.isNullOrEmpty(property))
                return instance;

            x.setPredicate(Predicate.IS_NOT_NULL);
            x.setValue(property);

            return instance;
        }

        @Override
        public CriteriaBuilder isNull(String property) {

            if (StringUtil.isNullOrEmpty(property))
                return instance;

            x.setPredicate(Predicate.IS_NULL);
            x.setValue(property);

            return instance;
        }

        @Override
        public CriteriaBuilder x(String sql) {

            if (StringUtil.isNullOrEmpty(sql))
                return instance;

            sql = BeanUtilX.normalizeSql(sql);

            x.setPredicate(Predicate.X);
            x.setValue(sql);

            return instance;
        }

        @Override
        public ConditionBuilder beginSub() {

            x.setKey(Predicate.SUB.sql());// special treat FIXME
            x.setValue(Predicate.SUB);

            List<X> subList = new ArrayList<>();
            x.setSubList(subList);

            X from = new X();
            from.setPredicate(Predicate.SUB_BEGIN);
            from.setValue(Predicate.SUB_BEGIN);

            subList.add(from);

            X xx = new X();//?
            subList.add(xx);//?
            xx.setParent(x);
            conditionBuilder.under(xx);

            return conditionBuilder;
        }

    };

    private CriteriaBuilder() {
        this.instance = this;
    }

    private CriteriaBuilder(Criteria criteria) {
        this.criteria = criteria;
        this.instance = this;
    }

    public static CriteriaBuilder buildCondition(){
        Criteria criteria = new Criteria();
        CriteriaBuilder builder = new CriteriaBuilder(criteria);
        return builder;
    }

    public static CriteriaBuilder build(Class<?> clz) {
        Criteria criteria = new Criteria();
        criteria.setClz(clz);
        CriteriaBuilder builder = new CriteriaBuilder(criteria);

        if (criteria.getParsed() == null) {
            Parsed parsed = Parser.get(clz);
            criteria.setParsed(parsed);
        }

        return builder;
    }

    public static CriteriaBuilder build(Class<?> clz, Paged paged) {
        Criteria criteria = new Criteria();
        criteria.setClz(clz);
        CriteriaBuilder builder = new CriteriaBuilder(criteria);

        if (criteria.getParsed() == null) {
            Parsed parsed = Parser.get(clz);
            criteria.setParsed(parsed);
        }

        if (paged != null) {
            builder.paged(paged);
        }

        return builder;
    }

    public static ResultMappedBuilder buildResultMapped(Class<?> clz, Fetched ro) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder(clz);

        if (ro != null) {

            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }

        }

        return builder;
    }

    public static ResultMappedBuilder buildResultMapped(Class<?> clz) {
        CriteriaBuilder b = new CriteriaBuilder();
        ResultMappedBuilder builder = b.new ResultMappedBuilder(clz);

        return builder;
    }

    public void paged(Paged paged) {
        criteria.paged(paged);
        DataPermission.Chain.onBuild(criteria, paged);
    }

    public Class<?> getClz() {
        return this.criteria.getClz();
    }


    public static String parseCondition(CriteriaCondition criteriaCondition){
        StringBuilder sb = new StringBuilder();
        List<X> xList = criteriaCondition.getListX();
        x(sb, xList, criteriaCondition, true);
        return sb.toString();
    }

    public static String[] parse(Criteria criteria) {

        StringBuilder sb = new StringBuilder();

		/*
         * select column
		 */
        select(sb, criteria);

        /*
         * from table
		 */
        criteria.sourceScript(sb);
        /*
         * StringList
		 */
        x(sb, criteria);

        /*
         * group by
         */
        groupBy(sb, criteria);
        /*
		 * sort
		 */
        sort(sb, criteria);

        String sql = sb.toString();

        String[] sqlArr = new String[3];
        if (!criteria.isScroll()) {
            sqlArr[0] = sql.replace(Mapped.TAG, criteria.getCountDistinct());
        }

        sqlArr[1] = sql.replace(Mapped.TAG, criteria.resultAllScript());
        sqlArr[2] = sql;

        boolean isResultMap = (criteria instanceof ResultMapped);
        if (isResultMap) {
            // sqlArr[1]: core sql

            String tabledSql = sqlArr[1];
            ResultMapped resultMapped = (ResultMapped) criteria;
            Map<String, List<String>> map = new HashMap<>();
            {
                String[] arr = tabledSql.split(SqlScript.SPACE);
                for (String ele : arr) {
                    if (ele.contains(SqlScript.POINT)) {
                        ele = ele.replace(",", "");
                        ele = ele.trim();
                        String[] tc = ele.split("\\.");
                        List<String> list = map.get(tc[0]);
                        if (list == null) {
                            list = new ArrayList<>();
                            map.put(tc[0], list);
                        }
                        list.add(tc[1]);
                    }
                }
            }

            Criteria.MapMapper mapMapper = resultMapped.getMapMapper();//
            if (Objects.isNull(mapMapper)) {
                mapMapper = new Criteria.MapMapper();
                resultMapped.setMapMapper(mapMapper);
            }
            Map<String, String> clzTableMapper = new HashMap<String, String>();
            {
                Set<Entry<String, List<String>>> set = map.entrySet();
                for (Entry<String, List<String>> entry : set) {
                    String key = entry.getKey();
                    List<String> list = entry.getValue();
                    Parsed parsed = Parser.get(key);
                    if (Objects.isNull(parsed))
                        throw new RuntimeException("Entity Bean Not Exist: " + BeanUtil.getByFirstUpper(key));
                    String tableName = parsed.getTableName();
                    clzTableMapper.put(key, tableName);// clzName, tableName
                    for (String property : list) {
                        String mapper = parsed.getMapper(property);
                        if (StringUtil.isNullOrEmpty(mapper)) {
                            mapper = property;// dynamic
                        }
                        mapMapper.put(key + SqlScript.POINT + property, tableName + SqlScript.POINT + mapper);
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
                String temp = sqlArr[i];
                if (StringUtil.isNullOrEmpty(temp))
                    continue;
                for (String property : mapMapper.getPropertyMapperMap().keySet()) {
                    String key = SqlScript.SPACE + property + SqlScript.SPACE;
                    String value = SqlScript.SPACE + mapMapper.mapper(property) + SqlScript.SPACE;
                    temp = temp.replace(key,value);
                }
                for (String clzName : clzTableMapper.keySet()) {
                    String tableName = clzTableMapper.get(clzName);
                    temp = BeanUtilX.mapperName(temp, clzName, tableName);
                }
                sqlArr[i] = temp;
            }

        } else {
            Parsed parsed = Parser.get(criteria.getClz());
            for (int i = 0; i < 3; i++) {
                if (StringUtil.isNullOrEmpty(sqlArr[i]))
                    continue;
                sqlArr[i] = BeanUtilX.mapper(sqlArr[i], parsed);
            }
        }

        System.out.println(sqlArr[1]);

        return sqlArr;
    }

    private static void select(StringBuilder sb, Criteria criteria) {

        sb.append(SqlScript.SELECT).append(SqlScript.SPACE).append(Mapped.TAG);

        if (!(criteria instanceof Criteria.ResultMapped))
            return;

        boolean flag = false;

        ResultMapped resultMapped = (Criteria.ResultMapped) criteria;
        StringBuilder column = new StringBuilder();

        if (Objects.nonNull(resultMapped.getDistinct())) {

            if (!flag) resultMapped.getResultList().clear();//去掉构造方法里设置的返回key

            column.append(SqlScript.DISTINCT);
            List<String> list = resultMapped.getDistinct().getList();
            int size = list.size();
            int i = 0;
            for (String resultKey : list) {
                column.append(resultKey).append(SqlScript.SPACE);
                resultMapped.getResultList().add(resultKey);
                i++;
                if (i < size) {
                    column.append(", ");
                }
            }
            criteria.setCountDistinct("COUNT(" + column.toString() + ") count");
            flag = true;
        }

        List<Reduce> reduceList = resultMapped.getReduceList();

        if (!reduceList.isEmpty()) {

            if (!flag) resultMapped.getResultList().clear();//去掉构造方法里设置的返回key

            Criteria.MapMapper mapMapper = resultMapped.getMapMapper();
            if (Objects.isNull(mapMapper)) {
                mapMapper = new Criteria.MapMapper();
                resultMapped.setMapMapper(mapMapper);
            }

            for (Reduce reduce : reduceList) {
                if (flag) {
                    column.append(", ");
                }
                String alianName = reduce.getProperty() + "_" + reduce.getType().toString().toLowerCase();//property_count
                alianName = alianName.replace(SqlScript.POINT, "_");
                column.append(reduce.getType()).append("( ").append(reduce.getProperty()).append(" ) ")
                        .append(alianName);

                String alainProperty = reduce.getProperty() + BeanUtil.getByFirstUpper(reduce.getType().toString().toLowerCase());
                mapMapper.put(alainProperty, alianName);//REDUCE ALIAN NAME
                resultMapped.getResultList().add(alainProperty);
                flag = true;
            }
        }


        String cs = column.toString();
        if (StringUtil.isNullOrEmpty(cs)) {
            criteria.setCustomedResultKey(null);
        } else {
            criteria.setCustomedResultKey(column.toString());
        }

    }

    private static void groupBy(StringBuilder sb, Criteria criteria) {
        if (criteria instanceof ResultMapped) {
            ResultMapped rm = (ResultMapped) criteria;

            String groupByS = rm.getGroupBy();
            if (StringUtil.isNullOrEmpty(groupByS))
                return;

            sb.append(Conjunction.GROUP_BY.sql());

            String[] arr = groupByS.split(SqlScript.COMMA);

            int i = 0, l = arr.length;
            for (String groupBy : arr) {
                groupBy = groupBy.trim();
                if (StringUtil.isNotNull(groupBy)) {
                    sb.append(groupBy);
                    i++;
                    if (i<l){
                        sb.append(SqlScript.COMMA);
                    }
                }
            }
        }
    }

    private static void sort(StringBuilder sb, Criteria criteria) {

        if (StringUtil.isNotNull(criteria.getOrderBy())) {
            sb.append(Conjunction.ORDER_BY.sql()).append(criteria.getOrderBy()).append(SqlScript.SPACE)
                    .append(criteria.getDirection());
        }

    }


    private static void x(StringBuilder sb, List<X> xList, CriteriaCondition criteria, boolean isWhere) {
        for (X x : xList) {

            Object v = x.getValue();
            if (Objects.isNull(v))
                continue;

            if(x.getPredicate() == Predicate.X){
                appendConjunction(sb, x, criteria, isWhere);
                sb.append(x.getValue());
                continue;
            }

            if (Objects.nonNull(x.getConjunction())) {

                List<X> subList = x.getSubList();
                if (x.getSubList() != null) {
                    StringBuilder xSb = new StringBuilder();

                    x(xSb, subList, criteria, false);//sub concat

                    String script = xSb.toString();
                    if (StringUtil.isNotNull(script)) {
                        final String and = Conjunction.AND.sql();
                        final String or = Conjunction.OR.sql();
                        if (script.startsWith(and)) {
                            script = script.replaceFirst(and, "");
                        } else if (script.startsWith(or)) {
                            script = script.replaceFirst(or, "");
                        }
                        x.setScript(Predicate.SUB_BEGIN.sql() + script + Predicate.SUB_END.sql());
                    }
                }

            }

            if (Predicate.SUB_BEGIN == x.getPredicate()) {
                continue;
            } else if (Predicate.SUB_END == x.getPredicate()) {
                continue;
            }

            if (StringUtil.isNotNull(x.getKey())) {
                if (x.getKey().equals(Predicate.SUB.sql())) {
                    if (Objects.nonNull(x.getScript())) {

                        appendConjunction(sb, x, criteria, isWhere);
                        sb.append(x.getScript());
                    }
                    continue;
                }
            }
            x(x, criteria, isWhere);

            if (Objects.nonNull(x.getScript())) {
                sb.append(x.getScript());
            }
        }

    }

    private static void x(StringBuilder sb, Criteria criteria) {

        List<X> xList = criteria.getListX();
        x(sb, xList, criteria, true);

    }


    private static void appendConjunction(StringBuilder sb, X x, CriteriaCondition criteriaBuilder, boolean isWhere) {
        if (Objects.isNull(x.getConjunction()))
            return;
        if (criteriaBuilder instanceof Criteria) {
            Criteria criteria = (Criteria) criteriaBuilder;
            if (isWhere && criteria.isWhere) {
                criteria.isWhere = false;
                sb.append(Conjunction.WHERE.sql());
            } else {
                sb.append(x.getConjunction().sql());
            }
        }else{
            sb.append(x.getConjunction().sql());
        }
    }

    private static void x(X x, CriteriaCondition criteria, boolean isWhere) {

        StringBuilder sb = new StringBuilder();
        Predicate p = x.getPredicate();
        Object v = x.getValue();

        if (p == Predicate.IN || p == Predicate.NOT_IN) {

            appendConjunction(sb, x, criteria, isWhere);

            sb.append(x.getKey()).append(p.sql());
            List<Object> inList = (List<Object>) v;
            in(sb, inList);
        } else if (p == Predicate.BETWEEN) {

            appendConjunction(sb, x, criteria, isWhere);

            sb.append(x.getKey()).append(p.sql());
            between(sb);

            MinMax minMax = (MinMax) v;
            List<Object> valueList = criteria.getValueList();
            valueList.add(minMax.getMin());
            valueList.add(minMax.getMax());

        } else if (p == Predicate.IS_NOT_NULL || p == Predicate.IS_NULL) {

            appendConjunction(sb, x, criteria, isWhere);

            sb.append(v).append(p.sql());

        } else {
            if (StringUtil.isNullOrEmpty(x.getKey()))
                return;

            appendConjunction(sb, x, criteria, isWhere);

            Class clz = v.getClass();
            sb.append(x.getKey()).append(x.getPredicate().sql());
            if (clz == String.class) {
                String str = v.toString();
                if (str.startsWith("#") && str.endsWith("#")) {
                    str = str.replace("#", "");
                    sb.append(str);
                    return;
                } else {
                    sb.append(SqlScript.PLACE_HOLDER);
                }
            } else {
                sb.append(SqlScript.PLACE_HOLDER);
            }

            if (clz.getSuperclass().isEnum() || clz.isEnum()) {
                criteria.getValueList().add(v.toString());
            } else {
                criteria.getValueList().add(v);
            }
        }
        x.setScript(sb.toString());
    }

    private static void between(StringBuilder sb) {

        sb.append(SqlScript.PLACE_HOLDER).append(Conjunction.AND.sql()).append(SqlScript.PLACE_HOLDER);

    }

    private static void in(StringBuilder sb, List<Object> inList) {

        if (inList == null || inList.isEmpty())
            return;

        Object v = inList.get(0);

        Class<?> vType = v.getClass();

        boolean isNumber = (vType == long.class || vType == int.class || vType == Long.class || vType == Integer.class);

        sb.append("( ");

        int length = inList.size();
        if (isNumber) {
            for (int j = 0; j < length; j++) {
                Object id = inList.get(j);
                if (id == null)
                    continue;
                sb.append(id);
                if (j < length - 1) {
                    sb.append(",");
                }
            }
        } else {
            for (int j = 0; j < length; j++) {
                Object id = inList.get(j);
                if (id == null || StringUtil.isNullOrEmpty(id.toString()))
                    continue;
                sb.append("'").append(id).append("'");
                if (j < length - 1) {
                    sb.append(",");
                }
            }
        }

        sb.append(" )");

    }


    private BeanElement getBeanElement(String property) {

        String str = null;
        if (property.contains(SqlScript.SPACE)) {
            String[] arr = property.split(SqlScript.SPACE);
            str = arr[0];
        } else {
            str = property;
        }
        if (str.contains(SqlScript.POINT)) {
            String[] xxx = str.split("\\.");
            if (xxx.length == 1)
                property = xxx[0];
            else
                property = xxx[1];
        } else {
            property = str;
        }

        BeanElement be = criteria.getParsed().getElement(property);

        return be;

    }

    private boolean isBaseType_0(String property, Object v) {

        if (v instanceof String)
            return false;

        BeanElement be = getBeanElement(property);

        if (be == null) {

            String s = v.toString();
            boolean isNumeric = NumberUtil.isNumeric(s);
            if (isNumeric) {

                if (s.contains(SqlScript.POINT)) {
                    return Double.valueOf(s) == 0;
                }
                return Long.valueOf(s) == 0;
            }
            return false;
        }

        Class<?> vType = be.clz;

        String s = v.toString();

        if (vType == int.class) {
            if (s.contains(SqlScript.POINT)) {
                s = s.substring(0, s.indexOf(SqlScript.POINT));
            }
            return Integer.valueOf(s) == 0;
        }
        if (vType == long.class) {
            if (s.contains(SqlScript.POINT)) {
                s = s.substring(0, s.indexOf(SqlScript.POINT));
            }
            return Long.valueOf(s) == 0;
        }
        if (vType == float.class) {
            return Float.valueOf(s) == 0;
        }
        if (vType == double.class) {
            return Double.valueOf(s) == 0;
        }
        if (vType == short.class) {
            return Short.valueOf(s) == 0;
        }
        if (vType == byte.class) {
            return Byte.valueOf(s) == 0;
        }
        if (vType == boolean.class) {
            if (s.contains(SqlScript.POINT)) {
                s = s.substring(0, s.indexOf(SqlScript.POINT));
            }
            return Integer.valueOf(s) == 0;
        }

        return false;
    }

    private boolean isNullOrEmpty(Object v) {

        Class<?> vType = v.getClass();

        if (vType == String.class) {
            return StringUtil.isNullOrEmpty(v.toString());
        }

        return false;
    }

    public interface ConditionBuilder {

        CriteriaBuilder eq(String property, Object value);

        CriteriaBuilder lt(String property, Object value);

        CriteriaBuilder lte(String property, Object value);

        CriteriaBuilder gt(String property, Object value);

        CriteriaBuilder gte(String property, Object value);

        CriteriaBuilder ne(String property, Object value);

        CriteriaBuilder like(String property, String value);

        CriteriaBuilder likeRight(String property, String value);

        CriteriaBuilder notLike(String property, String value);

        CriteriaBuilder between(String property, Object min, Object max);

        CriteriaBuilder in(String property, List<Object> list);

        CriteriaBuilder nin(String property, List<Object> list);

        CriteriaBuilder nonNull(String property);

        CriteriaBuilder isNull(String property);

        CriteriaBuilder x(String sql);

        void under(X x);

        X getX();

        ConditionBuilder beginSub();

    }

    public Criteria get() {
        DataPermission.Chain.befroeGetCriteria(this, this.criteria);
        Iterator<X> ite = this.criteria.getListX().iterator();
        while (ite.hasNext()) {
            X x = ite.next();
            if (Objects.isNull(x.getConjunction()) && Objects.isNull(x.getPredicate()) && Objects.isNull(x.getKey()))
                ite.remove();
        }
        return this.criteria;
    }


    public class ResultMappedBuilder extends CriteriaBuilder {

        @Override
        public Criteria.ResultMapped get() {
            return (ResultMapped) super.get();
        }

        private void init() {
            super.instance = this;
            Criteria c = new Criteria();
            Criteria.ResultMapped resultMapped = c.new ResultMapped();
            super.criteria = resultMapped;
        }

        private void init(Class<?> clz) {
            ResultMapped f = (Criteria.ResultMapped) super.criteria;
            f.setClz(clz);
            Parsed parsed = Parser.get(clz);
            f.setParsed(parsed);
        }

        public ResultMappedBuilder(Class<?> clz) {
            init();
            init(clz);
        }


        private void xAddResultKey(List<String> xExpressionList) {
            for (String xExpression : xExpressionList) {
                get().getResultList().add(xExpression);
            }
        }

        private void xAddResultKey(Fetched fetchResult) {
            if (fetchResult == null)
                return;
            Map<String, Object> resultObjMap = fetchResult.getResultKeyMap();
            if (resultObjMap == null || resultObjMap.isEmpty())
                return;
            List<String> xExpressionList = BeanMapUtil.toStringKeyList(resultObjMap);
            xAddResultKey(xExpressionList);
        }

        private void xAddResultKey(ResultMappedKey mappedKey) {
            if (mappedKey == null)
                return;
            List<String> list = mappedKey.getResultKeyList();
            xAddResultKey(list);
        }

        @Override
        public void paged(Paged paged) {
            super.criteria.paged(paged);
            if (paged instanceof Fetched) {
                xAddResultKey((Fetched) paged);
            }else if (paged instanceof ResultMappedKey){
                xAddResultKey((ResultMappedKey) paged);
            }
            DataPermission.Chain.onBuild(super.criteria, paged);
        }

        public ResultMappedBuilder distinct(Object... objs) {
            if (objs == null)
                throw new RuntimeException("distinct non resultKey");
            ResultMapped resultMapped = get();
            Distinct distinct = resultMapped.getDistinct();
            if (Objects.isNull(distinct)) {
                distinct = new Distinct();
                resultMapped.setDistinct(distinct);
            }
            for (Object obj : objs) {
                if (obj instanceof String) {
                    distinct.add(obj.toString());
                } else if (obj instanceof Map) {
                    Map map = (Map) obj;
                    Set<Entry> set = map.entrySet();
                    for (Entry entry : set) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        if (value instanceof Map) {
                            Map vMap = (Map) value;
                            for (Object k : vMap.keySet()) {
                                distinct.add(key.toString() + SqlScript.POINT + k.toString());
                            }
                        }
                    }

                } else {
                    throw new RuntimeException("distinct param suggests String, or Map");
                }
            }
            return this;
        }

        public ResultMappedBuilder groupBy(String property) {
            get().setGroupBy(property);
            return this;
        }


        public ResultMappedBuilder reduce(Criteria.ReduceType type, String property) {
            Reduce reduce = new Reduce();
            reduce.setType(type);
            reduce.setProperty(property);
            get().getReduceList().add(reduce);
            return this;
        }


    }


}