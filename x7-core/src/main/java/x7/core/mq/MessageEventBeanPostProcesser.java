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
package x7.core.mq;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import x7.core.bean.KV;
import x7.core.util.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MessageEventBeanPostProcesser implements BeanPostProcessor {

    private static Logger logger = Logger.getLogger(MessageEventBeanPostProcesser.class);

    private static List<KV> subscribeList = new ArrayList<KV>();

    public static  List<KV> getSubscribeList(){
        return  subscribeList;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                EventListener myListener = AnnotationUtils.findAnnotation(method,EventListener.class);
                if (myListener != null){
                    String condition = myListener.condition();
                    if (StringUtil.isNotNull(condition) && condition.contains("event.type")) {

                        String[] arr = condition.split("&&");
                        String topic = arr[0];
                        topic = topic.replace("#event.type=='","").replace("'","").trim();
                        String tag = arr[1];
                        tag = tag.replace("#event.tag=='","").replace("'","").trim();
                        KV kv = new KV(topic, tag);
                        subscribeList.add(kv);
                        logger.info("@EventListener condition: " + condition);
                    }
                }

            }
        }
        return bean;
    }
}