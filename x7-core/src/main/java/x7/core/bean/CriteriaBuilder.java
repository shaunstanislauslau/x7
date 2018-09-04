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

import x7.core.bean.Criteria.Fetch;
import x7.core.bean.Criteria.X;
import x7.core.repository.Mapped;
import x7.core.util.*;
import x7.core.web.Fetched;
import x7.core.web.Paged;

import java.util.*;
import java.util.Map.Entry;

/**
 * Standard Query Builder
 *
 * @author Sim
 */
public class CriteriaBuilder {

    public final static String SPACE = " ";
    public final static String PLACE_HOLDER = "?";

    private Criteria criteria;

    private CriteriaBuilder instance;

    public P and() {

        X x = new X();
        x.setConjunction(Conjunction.AND);
        x.setValue(Conjunction.AND);

        X current = p.getX();
        if (current != null ) {
            X parent = current.getParent();
            if (parent != null) {
                List<X> subList = parent.getSubList();
                if (subList != null) {
                    subList.add(x);
                    x.setParent(parent);
                }
            }else{
                this.criteria.add(x);
            }
        } else {
            this.criteria.add(x);
        }

        p.under(x);

        return p;
    }

    public P or() {

        X x = new X();
        x.setConjunction(Conjunction.OR);
        x.setValue(Conjunction.OR);

        X current = p.getX();
        if (current != null ) {
            X parent = current.getParent();
            if (parent != null) {
                List<X> subList = parent.getSubList();
                if (subList != null) {
                    subList.add(x);
                    x.setParent(parent);
                }
            }else{
                this.criteria.add(x);
            }
        } else {
            this.criteria.add(x);
        }

        p.under(x);

        return p;
    }

    public CriteriaBuilder endSub() {

        X x = new X();
        x.setPredicate(Predicate.SUB_END);
        x.setValue(Predicate.SUB_END);

        X current = p.getX();
        X parent = current.getParent();
        if (parent != null) {
            List<X> subList = parent.getSubList();
            if (subList != null) {
                subList.add(x);
            }

            this.p.under(parent);
        }

        return instance;
    }


    private P p = new P() {

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
            if (isBaseType_0(property, value))
                return instance;
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
            x.setValue("%" + value + "%");

            return instance;
        }

        @Override
        public CriteriaBuilder likeRight(String property, String value) {

            if (StringUtil.isNullOrEmpty(value))
                return instance;

            x.setPredicate(Predicate.LIKE);
            x.setKey(property);
            x.setValue(value + "%");

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

            if (list.isEmpty())
                return instance;

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

            if (list.isEmpty())
                return instance;

            x.setPredicate(Predicate.NOT_IN);
            x.setKey(property);
            x.setValue(tempList);

            return instance;
        }

        @Override
        public CriteriaBuilder isNotNull(String property) {

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
        public P beginSub() {

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
            p.under(xx);

            return p;
        }

    };

    private CriteriaBuilder() {
        this.instance = this;
    }

    private CriteriaBuilder(Criteria criteria) {
        this.criteria = criteria;
        this.instance = this;
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

    public static Fetchable buildFetchable(Class<?> clz, Fetched ro) {
        CriteriaBuilder b = new CriteriaBuilder();
        Fetchable builder = b.new Fetchable(clz, ro);

        if (ro != null) {

            if (ro instanceof Paged) {
                builder.paged((Paged) ro);
            }

        }

        return builder;
    }

    public void paged(Paged paged) {
        criteria.paged(paged);
        DataPermission.Chain.onBuild(criteria,paged);
    }

    public Class<?> getClz() {
        return this.criteria.getClz();
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
        boolean hasSourceScript = criteria.sourceScript(sb);

		/*
		 * StringList
		 */
        X groupBy = x(sb, criteria);

        String sql = sb.toString();


		/*
		 * sort
		 */
        sort(sb, criteria);


        String column = criteria.resultAllScript();

        String[] sqlArr = new String[3];
        String str = sql.replace(Mapped.TAG, column);
        sqlArr[1] = str;
        if (groupBy != null) {
            str = str.replaceAll(" +", " ");
            str = str.replace(") count", ") _count").replace(")count", ") _count");
            str = str.replace("count (", "count(");
            str = str.replace(" count ", " _count ");
            sqlArr[0] = "select count(tc." + groupBy.getKey() + ") count from (" + str + ") tc";
        } else {
            sqlArr[0] = sql.replace(Mapped.TAG, "COUNT(*) count");
        }
        sqlArr[2] = sql;

        if (hasSourceScript) {
            // sqlArr[1]: core sql
            Map<String, List<String>> map = new HashMap<>();
            {
                String[] arr = sqlArr[1].split(SPACE);
                for (String ele : arr) {
                    if (ele.contains(".")) {
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
            FetchMapper fetchMapper = new FetchMapper();
            criteria.setFetchMapper(fetchMapper);
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
                        fetchMapper.put(key + "." + property, tableName + "." + mapper);
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
                String temp = sqlArr[i];
                for (String property : fetchMapper.getPropertyMapperMap().keySet()) {
                    temp = temp.replace(property, fetchMapper.mapper(property));
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
                sqlArr[i] = BeanUtilX.mapper(sqlArr[i], parsed);
            }
        }

        System.out.println(sqlArr[1]);

        return sqlArr;
    }

    private static void select(StringBuilder sb, Criteria criteria) {
        sb.append("SELECT").append(SPACE).append(Mapped.TAG);
    }

    private static void sort(StringBuilder sb, Criteria criteria) {

        if (StringUtil.isNotNull(criteria.getOrderBy())) {
            sb.append(Conjunction.ORDER_BY.sql()).append(criteria.getOrderBy()).append(SPACE)
                    .append(criteria.getDirection());
        }

    }


    private static void x(StringBuilder sb, List<X> xList, Criteria criteria, boolean isWhere) {
        for (X x : xList) {

            Object v = x.getValue();
            if (Objects.isNull(v))
                continue;


            if (Objects.nonNull(x.getConjunction())) {

                List<X> subList = x.getSubList();
                if (x.getSubList() != null) {
                    StringBuilder xSb = new StringBuilder();

                    x(xSb, subList, criteria, false);//sub concat

                    String script = xSb.toString();
                    if (StringUtil.isNotNull(script)){
                        final String and = Conjunction.AND.sql();
                        final String or = Conjunction.OR.sql();
                        if (script.startsWith(and)){
                            script = script.replaceFirst(and,"");
                        }else if (script.startsWith(or)){
                            script = script.replaceFirst(or,"");
                        }
                        x.setScript(Predicate.SUB_BEGIN.sql()+script+Predicate.SUB_END.sql());
                    }
                }

            }

            if (Predicate.SUB_BEGIN == x.getPredicate()) {
                continue;
            } else if (Predicate.SUB_END == x.getPredicate()) {
                continue;
            }

//			if (x.getConjunction() == Conjunction.GROUP_BY) {
//
//				xx = x;
//				return xx;
//			}

            if (StringUtil.isNotNull(x.getKey())) {
                if (x.getKey().equals(Predicate.SUB.sql())) {
                    if (Objects.nonNull(x.getScript())) {

                        appendConjunction(sb,x,criteria,isWhere);
                        sb.append(x.getScript());
                    }
                    continue;
                }
            }
            x(x, criteria,isWhere);

            if (Objects.nonNull(x.getScript())) {
                sb.append(x.getScript());
            }
        }

    }

    private static X x(StringBuilder sb, Criteria criteria) {

        X xx = null;
        List<X> xList = criteria.getListX();

        x(sb, xList, criteria,true);

        return xx;
    }


    private static void appendConjunction(StringBuilder sb, X x, Criteria criteria,boolean isWhere){
        if (Objects.isNull(x.getConjunction()))
            return;
        if (isWhere && criteria.isWhere){
            criteria.isWhere = false;
            sb.append(Conjunction.WHERE.sql());
        }else {
            sb.append(x.getConjunction().sql());
        }
    }

    private static void x(X x, Criteria criteria,boolean isWhere) {

        StringBuilder sb = new StringBuilder();
        Predicate p = x.getPredicate();
        Object v = x.getValue();

        if (p == Predicate.IN || p == Predicate.NOT_IN) {

            appendConjunction(sb,x,criteria,isWhere);

            sb.append(x.getKey()).append(p.sql());
            List<Object> inList = (List<Object>) v;
            in(sb, inList);
        } else if (p == Predicate.BETWEEN) {

            appendConjunction(sb,x,criteria,isWhere);

            sb.append(x.getKey()).append(p.sql());
            between(sb);

            MinMax minMax = (MinMax) v;
            List<Object> valueList = criteria.getValueList();
            valueList.add(minMax.getMin());
            valueList.add(minMax.getMax());

        } else if (p == Predicate.IS_NOT_NULL || p == Predicate.IS_NULL) {

            appendConjunction(sb,x,criteria,isWhere);

            sb.append(v).append(p.sql());

        } else {
            if (StringUtil.isNullOrEmpty(x.getKey()))
                return;

            appendConjunction(sb,x,criteria,isWhere);

            Class clz = v.getClass();
            sb.append(x.getKey()).append(x.getPredicate().sql());
            if (clz == String.class) {
                String str = v.toString();
                if (str.startsWith("#") && str.endsWith("#")) {
                    str = str.replace("#", "");
                    sb.append(str);
                    return;
                } else {
                    sb.append(PLACE_HOLDER);
                }
            } else {
                sb.append(PLACE_HOLDER);
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

        sb.append(PLACE_HOLDER).append(Conjunction.AND.sql()).append(PLACE_HOLDER);

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

    protected static void fetchSql(StringBuilder sb, Criteria criteria) {

    }

    private BeanElement getBeanElement(String property) {

        String str = null;
        if (property.contains(" ")) {
            String[] arr = property.split(" ");
            str = arr[0];
        } else {
            str = property;
        }
        if (str.contains(".")) {
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

                if (s.contains(".")) {
                    return Double.valueOf(s) == 0;
                }
                return Long.valueOf(s) == 0;
            }
            return false;
        }

        Class<?> vType = be.clz;

        String s = v.toString();

        if (vType == int.class) {
            if (s.contains(".")) {
                s = s.substring(0, s.indexOf("."));
            }
            return Integer.valueOf(s) == 0;
        }
        if (vType == long.class) {
            if (s.contains(".")) {
                s = s.substring(0, s.indexOf("."));
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
            if (s.contains(".")) {
                s = s.substring(0, s.indexOf("."));
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

    public interface P {

        CriteriaBuilder eq(String property, Object value);
        CriteriaBuilder lt(String property, Object value);
        CriteriaBuilder lte(String property, Object value);
        CriteriaBuilder gt(String property, Object value);
        CriteriaBuilder gte(String property, Object value);
        CriteriaBuilder ne(String property, Object value);
        CriteriaBuilder like(String property, String value);
        CriteriaBuilder likeRight(String property, String value);
        CriteriaBuilder between(String property, Object min, Object max);
        CriteriaBuilder in(String property, List<Object> list);
        CriteriaBuilder nin(String property, List<Object> list);
        CriteriaBuilder isNotNull(String property);
        CriteriaBuilder isNull(String property);

        void under(X x);

        X getX();

        P beginSub();

    }

    public Criteria get() {
        DataPermission.Chain.befroeGetCriteria(this,this.criteria);
        Iterator<X> ite = this.criteria.getListX().iterator();
        while (ite.hasNext()) {
            X x = ite.next();
            if (Objects.isNull(x.getConjunction()) && Objects.isNull(x.getPredicate()) && Objects.isNull(x.getKey()))
                ite.remove();
        }
        return this.criteria;
    }


    public class Fetchable extends CriteriaBuilder {

        @Override
        public Fetch get() {
            return (Fetch) super.get();
        }

        private void init() {
            super.instance = this;
            Criteria c = new Criteria();
            Criteria.Fetch join = c.new Fetch();
            super.criteria = join;
        }

        private void init(Class<?> clz) {
            Criteria.Fetch f = (Criteria.Fetch) super.criteria;
            f.setClz(clz);
            Parsed parsed = Parser.get(clz);
            f.setParsed(parsed);
        }

        public Fetchable(Class<?> clz) {
            init();
            init(clz);
        }

        public Fetchable(Class<?> clz, Fetched fetchResult) {

            init();
            init(clz);

            xAddResultKey(fetchResult);

        }

        private Criteria.Fetch getCriteriaFetch() {
            return (Criteria.Fetch) super.criteria;
        }

        private void xAddResultKey(List<String> xExpressionList) {
            for (String xExpression : xExpressionList) {
                getCriteriaFetch().getResultList().add(xExpression);
            }
        }

        private void xAddResultKey(Fetched fetchResutl) {
            if (fetchResutl == null)
                return;
            Map<String, Object> resultObjMap = fetchResutl.getResultKeyMap();
            if (resultObjMap == null || resultObjMap.isEmpty())
                return;
            List<String> xExpressionList = BeanMapUtil.toStringKeyList(resultObjMap);
            xAddResultKey(xExpressionList);
        }

    }

    ///////////////////////////////////////////////////////////////////// <BR>
    /////////////////////////// REPOSITORY DEV WEB IO//////////////////// <BR>
    ///////////////////////////////////////////////////////////////////// <BR>
    ///////////////////////////////////////////////////////////////////// <BR>

    public static class FetchMapper {
        private Map<String, String> propertyMapperMap = new HashMap<String, String>();
        private Map<String, String> mapperPropertyMap = new HashMap<String, String>();

        public Map<String, String> getPropertyMapperMap() {
            return propertyMapperMap;
        }

        public Map<String, String> getMapperPropertyMap() {
            return mapperPropertyMap;
        }

        public void put(String property, String mapper) {
            this.propertyMapperMap.put(property, mapper);
            this.mapperPropertyMap.put(mapper, property);
        }

        public String mapper(String property) {
            return this.propertyMapperMap.get(property);
        }

        public String property(String mapper) {
            return this.mapperPropertyMap.get(mapper);
        }

        @Override
        public String toString() {
            return "FetchMapper [propertyMapperMap=" + propertyMapperMap + ", mapperPropertyMap=" + mapperPropertyMap
                    + "]";
        }
    }

}