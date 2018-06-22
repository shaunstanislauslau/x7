package x7.repository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import x7.core.bean.SpringHelper;
import x7.core.util.TimeUtil;
import x7.core.web.ViewEntity;
import x7.repository.dao.Tx;

import java.lang.reflect.Method;

/**
 * Created by Sim on 2018/6/22.
 */
public class TxAop {

    public Object around(ProceedingJoinPoint proceedingJoinPoint) {

        {
			/*
			 * TX
			 */
            System.out.println("_______Transaction begin ....");
            long startTime = TimeUtil.now();
            Tx.begin();
            try {
                Object obj = null;

                org.aspectj.lang.Signature signature = proceedingJoinPoint.getSignature();
                Class returnType = ((MethodSignature) signature).getReturnType();
                Method method = ((MethodSignature) signature).getMethod();
                System.out.println("_______Method: "+method);
                System.out.println("_______Mapping: " + SpringHelper.getRequestMapping(method));
                if (returnType == void.class) {

                    proceedingJoinPoint.proceed();
                } else {
                    obj = proceedingJoinPoint.proceed();
                }

                Tx.commit();
                long endTime = TimeUtil.now();
                long handledTimeMillis = endTime - startTime;
                System.out.println("_______Transaction end, cost time: " + (handledTimeMillis) + "ms");
                if (obj instanceof ViewEntity){
                    ViewEntity ve = (ViewEntity)obj;
                    ve.setHandledTimeMillis(handledTimeMillis);
                }

                return obj;
            } catch (Throwable e) {
                Tx.rollback();
                System.out.println("_______Transaction rollback by exception: " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
