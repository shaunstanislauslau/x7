package x7.repository.dao;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by Sim on 2018/11/27.
 */
public class DataSourceUtil {

    public static Connection getConnection(DataSource dataSource) {

        return DataSourceUtils.getConnection(dataSource);
    }

    public static void releaseConnection(Connection conn, DataSource dataSource){
        DataSourceUtils.releaseConnection(conn,dataSource);
    }
}
