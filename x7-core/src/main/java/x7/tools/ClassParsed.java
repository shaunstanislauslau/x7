package x7.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassParsed {

	private Class<?> clz;
	private String simpleName;
	private String fullName;
	private String pkg;
	private String basePkg;
	
	private String mapping;
	private Set<String> importSet = new HashSet<>();
	private List<MethodParsed> methodList = new ArrayList<>();
	
	public ClassParsed(){}
	public ClassParsed(Class<?> clz2) {
		this.clz = clz2;
		this.fullName = clz.getName();
		this.pkg = clz.getPackage().getName();
		this.simpleName = this.fullName.replace(this.pkg+".", "");
	}
	public Class<?> getClz() {
		return clz;
	}
	public void setClz(Class<?> clz) {
		this.clz = clz;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getMapping() {
		return mapping;
	}
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public Set<String> getImportSet() {
		return importSet;
	}
	public void setImportSet(Set<String> importSet) {
		this.importSet = importSet;
	}
	public List<MethodParsed> getMethodList() {
		return methodList;
	}
	public void setMethodList(List<MethodParsed> methodList) {
		this.methodList = methodList;
	}
	public String getBasePkg() {
		return basePkg;
	}
	public void setBasePkg(String basePkg) {
		this.basePkg = basePkg;
	}
	@Override
	public String toString() {
		return "ClassParsed [clz=" + clz + ", simpleName=" + simpleName + ", fullName=" + fullName + ", pkg=" + pkg
				+ ", basePkg=" + basePkg + ", mapping=" + mapping + ", importSet=" + importSet + ", methodList="
				+ methodList + "]";
	}

}
