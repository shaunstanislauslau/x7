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
package x7.repository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import x7.core.bean.SpringHelper;
import x7.core.util.TimeUtil;
import x7.core.web.ViewEntity;
import x7.repository.dao.Tx;

import java.lang.reflect.Method;


public class TxAop {

    public Object around(ProceedingJoinPoint proceedingJoinPoint) {

			/*
             * TX
			 */
        long startTime = TimeUtil.now();

        try {
            Object obj = null;

            org.aspectj.lang.Signature signature = proceedingJoinPoint.getSignature();
            Class returnType = ((MethodSignature) signature).getReturnType();
            Method method = ((MethodSignature) signature).getMethod();

            System.out.println("_______Tx begin ....Mapping="
                    + SpringHelper.getRequestMapping(method)
                    + "....Method=" + method);

            Tx.begin();

            if (returnType == void.class) {
                proceedingJoinPoint.proceed();
            } else {
                obj = proceedingJoinPoint.proceed();
            }

            Tx.commit();
            long endTime = TimeUtil.now();
            long handledTimeMillis = endTime - startTime;
            if (obj instanceof ViewEntity) {
                ViewEntity ve = (ViewEntity) obj;
                ve.setHandledTimeMillis(handledTimeMillis);
            }

            System.out.println("_______Tx end, cost time: " + (handledTimeMillis) + "ms");

            return obj;
        } catch (Throwable e) {
            Tx.rollback();
            System.out.println("_______Tx rollback by exception: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
