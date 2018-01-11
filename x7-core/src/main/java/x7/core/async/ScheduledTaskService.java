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
package x7.core.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ScheduledTaskService {
	public final static long ONE_DAY = 86400000L;
	private final ScheduledExecutorService service = Executors
			.newScheduledThreadPool(2);
	private final TimeUnit UNIT = TimeUnit.MILLISECONDS;

	private static ScheduledTaskService instance;
	public static ScheduledTaskService getInstance(){
		if (instance == null){
			instance = new ScheduledTaskService();
		}
		return instance;
	}
	
	/**
	 * ONE SHOT ACTION
	 * 
	 * @param command
	 * @param delay
	 *            call TimeUtil.ONE_DAY, or TimeUtil.ONE_HOUR, ....
	 * 
	 * <BR>
	 *            SAMPLE:<BR>
	 *            service.schedule(new Runnable() {<BR>
	 * <BR>
	 * @Override public void run() {<BR>
	 *           System.out.println(">>>>>>>>>>>>>>>>");<BR>
	 *           }<BR>
	 *           }, TimeUtil.ONE_MINUTE);<BR>
	 */
	public void schedule(Runnable command, long delay) {
		try {
			service.schedule(command, delay, UNIT);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * REPEATED ACTION
	 * 
	 * @param command
	 * @param scheduledAt
	 *            call TimeUtil.scheduledAt(int HOUR) or
	 *            TimeUtil.scheduledAt(int HOUR, int minute)
	 * @param delay
	 *            call TimeUtil.ONE_DAY, or TimeUtil.ONE_HOUR, .... <BR>
	 *            SAMPLE:<BR>
	 *            service.schedule(new Runnable() {<BR>
	 * <BR>
	 * @Override public void run() {<BR>
	 *           System.out.println(">>>>>>>>>>>>>>>>");<BR>
	 *           }<BR>
	 *           }, TimeUtil.scheduledAt(10, 33), TimeUtil.ONE_MINUTE);<BR>
	 */
	public void schedule(Runnable command, long scheduledAt, long delay) {
		long now = System.currentTimeMillis();
		try {
			service.scheduleWithFixedDelay(command, scheduledAt - now, delay,
					UNIT);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
