package x7.tools;

import x7.core.util.BeanUtil;
import x7.core.util.ClassFileReader;

import java.lang.reflect.Method;
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

			cp.setBasePkg(packagePrefix);
			
			String pkg = cp.getPkg();
			String clzMapping = cp.getSimpleName().replace(clzSuffix, "");
			clzMapping = BeanUtil.getByFirstLower(clzMapping);
			if (clzMapping.contains("$")){
				clzMapping = clzMapping.substring(clzMapping.lastIndexOf("$"));
			}
			pkg = pkg.replace(packagePrefix, "");
			String mapping = pkg;
			if (! pkg.endsWith("."+clzMapping)){
				mapping = pkg + "." + clzMapping;
			}
			
			mapping = mapping.replace(".", "/");
			cp.setMapping(mapping);

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
			Type[] paramTypeList = method.getGenericParameterTypes();// 方法的参数列表

			for (Type paramType : paramTypeList) {
				String str = paramType.getTypeName();
				str = stringifyGeneric(str, paramType, importSet);
				paraClzStrList.add(str);
			}
			
			methodStr =  mp.getName() + "(";
			int i=0;
			int mapI = 0,listI = 0, setI = 0;
			
			String[] baseArr = {
					"int","Integer","long","Long",
					"byte","Byte","char","Char",
					"float","Float","double","Double",
					"short","Short","BigDecimal",
					"boolean","Boolean"
			};

			calledMethodStr += methodStr;

			String baseStr = Arrays.toString(baseArr);
			for (String clzStr: paraClzStrList){

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
				}else if (clzStr.contains("Map")){
					methodStr = methodStr + " map" + (mapI>0?mapI:"");
					calledMethodStr = calledMethodStr + "map" + (mapI>0?mapI:"");
					mapI++;
				}else if(clzStr.contains("List")){
					methodStr = methodStr + " list" + (listI>0?listI:"");
					calledMethodStr = calledMethodStr + "list" + (listI>0?listI:"");
					listI++;
				}else if(clzStr.contains("Set")){
					methodStr = methodStr + " set" + (setI>0?setI:"");
					calledMethodStr = calledMethodStr + "set" + (setI>0?setI:"");
					setI++;
				}else{
					String valueStr = clzStr;
					if (valueStr.contains(".")){
						valueStr = valueStr.substring(valueStr.lastIndexOf(".")+1);
					}
					methodStr = methodStr + " " + BeanUtil.getByFirstLower(valueStr) + (i>0?i:"");
					calledMethodStr = calledMethodStr  + BeanUtil.getByFirstLower(valueStr) + (i>0?i:"");
				}
				i++;
			}
			methodStr += ")";
			calledMethodStr += ")";

			methodStr = "public " + returnStr +" " + methodStr;

			String intfStr = methodStr.replace("public ","").replace("@RequestBody ","");
			
			mp.setCalledMethodStr(calledMethodStr);
			mp.setMethodStr(methodStr);
			mp.setIntfStr(intfStr);
			return mp;
		}
		
		private static String stringifyGeneric(String str, Type type, Set<String> importSet) {

			String typeName = type.getTypeName();

			if (typeName.contains("$")){
				typeName = typeName.substring(0,typeName.lastIndexOf("$"));
			}
			System.out.println(typeName);
			if (typeName.contains("<")) {
				typeName = typeName.substring(0, typeName.indexOf("<"));
			}
			if (!(typeName.startsWith("java.lang") || !typeName.contains("."))){
				importSet.add(typeName);
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
