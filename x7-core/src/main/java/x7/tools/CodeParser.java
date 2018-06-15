package x7.tools;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import x7.core.util.BeanUtil;
import x7.core.util.ClassFileReader;

/**
 * 
 * 
 * @author Sim
 *
 */
public class CodeParser {

	public static void parse(String basePackages) {
		
		List<ClassParsed> list = new ArrayList<>();
		
		String[] arr = basePackages.split(",");

		for (String basePackage : arr) {
			System.out.println("______basePackage: " + basePackage);
			Set<Class<?>> setOne = ClassFileReader.getClasses(basePackage);

			for (Class clz : setOne){
				
				ClassParsed cp = ClassParser.parse(clz, basePackage);
				
				list.add(cp);
			}
		}

		
		//output
		for (String basePackage : arr) {
			String path = basePackage.replace(".", "/");
			System.out.println(path);
			path = CodeParser.class.getResource("/"+path).getPath();
			System.out.println(path);
			///E:/luer/server/ruhr/ruhr-api/target/classes/com/ruhr/api
			path = path.replace("target/classes", "src/main/java");
			System.out.println(path);
		}

	}
	
	
	public static class ClassParser {

		public static ClassParsed parse(Class<?> clz, String basePackage) {
			
			ClassParsed cp = new ClassParsed(clz);
			

			cp.setBasePkg(basePackage);
			
			String pkg = cp.getPkg();
			String clzMapping = cp.getSimpleName().replace("Service", "");
			clzMapping = BeanUtil.getByFirstLower(clzMapping);
			pkg = pkg.replace(basePackage, "");
			String mapping = pkg;
			if (! pkg.endsWith("."+clzMapping)){
				mapping = pkg + "." + clzMapping;
			}
			
			mapping = mapping.replace(".", "/");
			cp.setMapping(mapping);
			
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
			String calledMethodStr;

			Type genericReturnType = method.getGenericReturnType();
			String returnStr = genericReturnType.getTypeName();

			returnStr = stringifyGeneric(returnStr, genericReturnType,importSet);


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
					"short","Short","BigDecimal"
			};
			String baseStr = Arrays.toString(baseArr);
			for (String clzStr: paraClzStrList){

				if (i>0){
					methodStr += ", ";
				}
				methodStr = methodStr + clzStr;
				
				if (baseStr.contains(clzStr)){
					methodStr = methodStr + " value" + (i>0?i:"");
				}else if (clzStr.contains("Map")){
					methodStr = methodStr + " map" + (mapI>0?mapI:"");
					mapI++;
				}else if(clzStr.contains("List")){
					methodStr = methodStr + " list" + (listI>0?listI:"");
					listI++;
				}else if(clzStr.contains("Set")){
					methodStr = methodStr + " set" + (setI>0?setI:"");
					setI++;
				}else{
					methodStr = methodStr + " " + BeanUtil.getByFirstLower(clzStr) + (i>0?i:"");
				}
				i++;
			}
			methodStr += ")";
			
			calledMethodStr = methodStr;
			methodStr = "public " + returnStr +" " + calledMethodStr;
			calledMethodStr += ";";
			
			System.out.println(methodStr);
			System.out.println(calledMethodStr);
			
			mp.setCalledMethodStr(calledMethodStr);
			mp.setMethodStr(methodStr);
		
			return mp;
		}
		
		private static String stringifyGeneric(String str, Type type, Set<String> importSet) {

			String typeName = type.getTypeName(); 
			System.out.println(typeName);
			if (typeName.contains("<")) {
				typeName = typeName.substring(0, typeName.indexOf("<"));
				if (!typeName.startsWith("java.lang")){
					importSet.add(typeName);
				}
			}
			String typeNameSimpe = typeName.substring(typeName.lastIndexOf(".") + 1);
			str = str.replace(typeName, typeNameSimpe);

			if (type instanceof ParameterizedType) /* 如果是泛型类型 */ {
				Type[] types = ((ParameterizedType) type).getActualTypeArguments();// 泛型类型列表

				for (Type tt : types) {

					str = stringifyGeneric(str, tt, importSet);
					String ptName = tt.getTypeName();
					String ptNameSimple = ptName.substring(ptName.lastIndexOf(".") + 1);

					str = str.replace(ptName, ptNameSimple);
					if (!ptName.startsWith("java.lang")){
						importSet.add(ptName);
					}
				}
			}
			return str;
		}
	}
}
