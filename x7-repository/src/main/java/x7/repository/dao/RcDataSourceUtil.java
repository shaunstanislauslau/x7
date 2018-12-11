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
package x7.repository.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * X7-repository defect design readable datasource <br>
 * only read committed <br>
 * X7-repository doesn't suggest for system dependent on database tx <br>
 */
public class RcDataSourceUtil {

    /**
     * <Key, Connection>
     */
    private final static Map<String, Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

    /**
     * <ThreadId, Key>
     */
    private final static Map<String, String> keyMap = new ConcurrentHashMap<>();



    public static Connection getConnection() {

        try{
            return getConnection0();
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Connection getConnection0() throws SQLException {

        String key = getKey();

        DataSource ds = getDataSourceReadable();

        if (key == null) {

            Connection conn = ds.getConnection();
            if (conn == null) {
                try {
                    TimeUnit.MICROSECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ds.getConnection();
            }

            if (conn == null)
                throw new RuntimeException("NO CONNECTION");

            return conn;

        } else {
            Connection conn = connectionMap.get(key);
            if (conn == null) {

                conn = ds.getConnection();
                if (conn == null) {
                    try {
                        TimeUnit.MICROSECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    conn = ds.getConnection();
                }

                if (conn == null)
                    throw new RuntimeException("NO CONNECTION");

                connectionMap.put(key, conn);

            }

            return conn;
        }

    }


    protected static void releaseConnection(Connection conn){

        String key = getKey();

        if (key == null) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }

    }

    protected static void closeConnection(){

        String key = getKey();

        if (key == null)
            return;

        Connection conn = connectionMap.remove(key);

        if (conn != null){
            try {
                conn.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        String threadId = String.valueOf(Thread.currentThread().getId());
        keyMap.remove(threadId);
    }

    private static String getKey() {
        String threadId = String.valueOf(Thread.currentThread().getId());
        return keyMap.get(threadId);

    }

    private static DataSource getDataSourceReadable(){
        DataSource ds = DataSourceRouter.getDataSourceReadable();
        if (ds == null) {
            ds = DataSourceRouter.getDataSource();
        }
        return ds;
    }

    protected static void key(){
        String threadId = String.valueOf(Thread.currentThread().getId());
        String key = "rcTx."+threadId;
        keyMap.put(threadId,key);
    }


}
