package x7.core.bean;

/**
 * DataPermission, 数据权限, 支持数据库like查找,或搜索
 * Created by Sim on 2016/5/23.
 */
public interface DataPermission {
	
	String KEY = "dataPermissionSn";

	void setDataPermissionSn(String dataPermissionSn);
    String getDataPermissionSn();

}
