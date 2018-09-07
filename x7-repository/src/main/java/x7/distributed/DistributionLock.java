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
package x7.distributed;

import org.apache.log4j.Logger;
import x7.repository.redis.JedisConnector_Persistence;


/**
 * Created by Sim on 2018/9/6.
 */
public class DistributionLock {

    private static Logger logger = Logger.getLogger(DistributionLock.class);

    private static void lock(String key) {

        boolean locked = JedisConnector_Persistence.getInstance().lock(key);
        if (!locked) {
            logger.info("Get distributed lock failed, lockKey: " + key);
            throw new RuntimeException("service busy");
        }
        System.out.println("Get distribution lock");
    }

    private static void unLock( String key){
        JedisConnector_Persistence.getInstance().unLock(key);
        System.out.println("Release distribution lock");
    }

    public static Lock by(String key){
        Lock ml = new Lock();
        ml.setKey(key);
        return ml;
    }

    public static class Lock{
        private  String key;

        private void setKey(String key){
            this.key = key;
        }

        public <T> T lock(Task<T> obj){
            DistributionLock.lock(key);
            T o = null;
            try {
                o = obj.run(obj);
            }catch (Exception e) {
                DistributionLock.unLock(key);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }else {
                    throw new RuntimeException(e.getMessage());
                }
            }
            DistributionLock.unLock(key);
            return o;
        }
    }

    public interface Task<T> {
        T run(Object obj);
    }

}
