package x7.tools;

import x7.core.async.ActualType;
import x7.core.bean.KV;
import x7.core.util.BeanUtil;
import x7.core.util.ClassFileReader;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 
 * 
 * @author Sim
 *
 */
public class CodeParser {

	public static List<ClassParsed> parse(List<String> packagePrefixs,String clzSuffix) {
		
		List<ClassParsed> list = new ArrayList<>();

		for (String packagePrefix : packagePrefixs) {
			System.out.println("______basePackage: " + packagePrefix);
			Set<Class<?>> setOne = ClassFileReader.getClasses(packagePrefix);

			for (Class clz : setOne){
				
				ClassParsed cp = ClassParser.parse(clz, packagePrefix,clzSuffix);
				
				list.add(cp);
			}
		}

		return list;

	}
	
	
	public static class ClassParser {

		public static ClassParsed parse(Class<?> clz, String packagePrefix, String clzSuffix) {
			
			ClassParsed cp = new ClassParsed(clz);

			Type[] typeArr = clz.getGenericInterfaces();
			if (typeArr != null && typeArr.length > 0) {
				Type actualType = typeArr[0];
				if (actualType.getTypeName().startsWith(ActualType.class.getName())) {
					Type[] tt = ((ParameterizedType) actualType).getActualTypeArguments();
					Class actualClz = (Class)tt[0];
					cp.setActualType(actualClz);
					cp.getImportSet().add(actualClz.getName());
				}
			}

			cp.setBasePkg(packagePrefix);
			
			String pkg = cp.getPkg();
			cp.createMapping(pkg,clzSuffix,cp.getSimpleName());

			String importService = cp.getFullName();
			if (importService.contains("$")) {
				importService = importService.substring(0, importService.lastIndexOf("$"));
			}
			cp.getImportSet().add(importService);
			
			//MethodParsed
			for (Method method : clz.getMethods()){
				MethodParsed mp = MethodParser.parse(method, cp.getImportSet());
				cp.getMethodList().add(mp);
			}
			
			System.out.println("______ClassParsed: " + cp);
			
			return cp;
		}
		
	}
	
	public static class MethodParser {
		
		public static MethodParsed parse(Method method, Set<String> importSet) {

			MethodParsed mp = new MethodParsed(method);


			String methodStr;
			String calledMethodStr = "";

			Type genericReturnType = method.getGenericReturnType();
			String returnStr = genericReturnType.getTypeName();

			returnStr = stringifyGeneric(returnStr, genericReturnType,importSet);

			String fallbackStr = "";
			String[] numberArr = {
					"int","long",
					"byte","char",
					"float","double",
					"short"
			};

			String[] numberObjectArr = {
					"Integer","Long",
					"Byte","Char",
					"Float","Double",
					"Short","BigDecimal","Boolean"
			};

			String numberStr = Arrays.toString(numberArr);
			String numberObjectStr = Arrays.toString(numberObjectArr);
			if (numberStr.contains(returnStr)){
				fallbackStr = "return -1;";
			}else if (numberObjectStr.contains(returnStr)){
				fallbackStr = "return null;";
			}else if (returnStr.equals("boolean") ){
				fallbackStr = "return false;";
			}else if (returnStr.startsWith("List")){
				fallbackStr = "return new Array"+returnStr+"();";
				importSet.add("java.util.ArrayList");
			}else if (returnStr.startsWith("Map")){
				fallbackStr = "return new Hash"+returnStr+"();";
				importSet.add("java.util.HashMap");
			}else if (returnStr.startsWith("Set")){
				fallbackStr = "return new Hash"+returnStr+"();";
				importSet.add("java.util.HashSet");
			}else {
				fallbackStr = "return new " + returnStr + "();";
			}

			mp.setFallbackStr(fallbackStr);

			List<String> paraClzStrList = new ArrayList<>();
			List<KV> kvList = new ArrayList<KV>();
			Type[] paramTypeList = method.getGenericParameterTypes();// 方法的参数列表

			for (Type paramType : paramTypeList) {

				String str = paramType.getTypeName();
				str = stringifyGeneric(str, paramType, importSet);
				paraClzStrList.add(str);

				String ptName = paramType.getTypeName();
//				String fullName = ptName;
//				if (ptName.contains("."+str)) {
//					fullName = ptName.substring(0, ptName.indexOf(str)) + str;
//				}

				KV kv = new KV(str,ptName);
				kvList.add(kv);
			}
			
			methodStr =  mp.getName() + "(";
			int i=0;
			int mapI = 0,listI = 0, setI = 0;
			
			String[] baseArr = {
					"int","Integer","long","Long",
					"byte","Byte","char","Char",
					"float","Float","double","Double",
					"short","Short","BigDecimal",
					"boolean","Boolean","String"
			};

			calledMethodStr += methodStr;

			String baseStr = Arrays.toString(baseArr);
			int size = kvList.size();
			for (KV kv: kvList){

				String clzStr = kv.getK();
				String paraName = kv.getV().toString();
				paraName = paraName.replace("$",".");
				if (clzStr.contains("$")){
					clzStr = clzStr.replace("$",".");
				}
				if (i>0){
					methodStr += ", ";
				}
				methodStr = methodStr +"@RequestBody " + clzStr;
				
				if (baseStr.contains(clzStr)){
					methodStr = methodStr + " value" + (i>0?i:"");
					calledMethodStr = calledMethodStr + "value" + (i>0?i:"");

					String temp = "java.lang." + BeanUtil.getByFirstUpper(clzStr);
					mp.setParaName(temp);
					mp.setParaSimpleName(clzStr);
					mp.setParaSimpleNameLower("value");

				}else if (clzStr.contains("Map")){
					methodStr = methodStr + " map" + (mapI>0?mapI:"");
					calledMethodStr = calledMethodStr + "map" + (mapI>0?mapI:"");
					mapI++;

					mp.setParaName(paraName);
					mp.setParaSimpleName(clzStr);
					mp.setParaSimpleNameLower("map");

				}else if(clzStr.contains("List")){
					methodStr = methodStr + " list" + (listI>0?listI:"");
					calledMethodStr = calledMethodStr + "list" + (listI>0?listI:"");
					listI++;

					mp.setParaName(paraName);
					mp.setParaSimpleName(clzStr);
					mp.setParaSimpleNameLower("list");

				}else if(clzStr.contains("Set")){
					methodStr = methodStr + " set" + (setI>0?setI:"");
					calledMethodStr = calledMethodStr + "set" + (setI>0?setI:"");
					setI++;

					mp.setParaName(paraName);
					mp.setParaSimpleName(clzStr);
					mp.setParaSimpleNameLower("set");

				}else{
					String valueStr = clzStr;
					if (valueStr.contains("<")){
						valueStr = valueStr.substring(0,valueStr.indexOf("<"));
					}
					if (valueStr.contains(".")){
						valueStr = valueStr.substring(valueStr.lastIndexOf(".")+1);
					}
					String paraStr = BeanUtil.getByFirstLower(valueStr) + (i>0?i:"");
					methodStr = methodStr + " " + paraStr;
					calledMethodStr = calledMethodStr  + BeanUtil.getByFirstLower(valueStr) + (i>0?i:"");

					mp.setParaName(paraName);
					mp.setParaSimpleName(clzStr);
					mp.setParaSimpleNameLower(paraStr);
				}
				if (i<size-1){
					calledMethodStr +=", ";
				}
				i++;
			}
			methodStr += ")";
			calledMethodStr += ")";

			methodStr = "public " + returnStr +" " + methodStr;

			String intfStr = methodStr.replace("public ","").replace("@RequestBody ","");

			String returnType = method.getReturnType().getTypeName();
			System.out.println("----------> return type: " + returnType);
			returnStr = "null";
			String numStr = "long,int,short,double,float,byte";
			String bStr = "boolean";
			if (numStr.contains(returnType)){
				returnStr = "1";
			}else if (bStr.contains(returnType)){
				returnStr = "true";
			}

			
			mp.setCalledMethodStr(calledMethodStr);
			mp.setMethodStr(methodStr);
			mp.setIntfStr(intfStr);
			mp.setReturnType(returnType);
			mp.setReturnStr(returnStr);
			return mp;
		}
		
		private static String stringifyGeneric(String str, Type type, Set<String> importSet) {

			String typeName = type.getTypeName();
			System.out.println(typeName);
			if (typeName.contains("$")){
				typeName = typeName.substring(0,typeName.lastIndexOf("$"));
			}
			System.out.println(typeName);
			if (typeName.contains("<")) {
				typeName = typeName.substring(0, typeName.indexOf("<"));
			}
			if (!(typeName.startsWith("java.lang") || !typeName.contains("."))){
				importSet.add(typeName);
				System.out.println("______Method import1: " + typeName);
			}
			String typeNameSimpe = typeName.substring(typeName.lastIndexOf(".") + 1);
			str = str.replace(typeName, typeNameSimpe);

			if (type instanceof ParameterizedType) /* 如果是泛型类型 */ {
				Type[] types = ((ParameterizedType) type).getActualTypeArguments();// 泛型类型列表

				for (Type tt : types) {

					str = stringifyGeneric(str, tt, importSet);
					typeName = tt.getTypeName();

					if (typeName.contains("$")){
						typeName = typeName.substring(0,typeName.lastIndexOf("$"));
						str = str.replace("$",".");
					}
					String ptNameSimple = typeName.substring(typeName.lastIndexOf(".") + 1);
					str = str.replace(typeName, ptNameSimple);
					if (typeName.contains("<")) {
						typeName = typeName.substring(0, typeName.indexOf("<"));
					}
					if (!(typeName.startsWith("java.lang") || !typeName.contains("."))){
						importSet.add(typeName);
					}
				}
			}
			return str;
		}
	}


}
