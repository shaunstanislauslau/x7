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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import x7.core.async.IAsyncTask;

/**
 * 
 * @author Sim
 *
 */
public class AsyncService extends Thread{
	private final BlockingQueue<IAsyncTask> tasks = new ArrayBlockingQueue<IAsyncTask>(
			1000);

	private boolean isWorking = false;
	
	public AsyncService(){
		isWorking = true;
		this.start();
	}
	
	public AsyncService(String name){
		isWorking = true;
		this.setName(name);
		this.start();
	}
	
	protected void accept(IAsyncTask task) {
		try {
			tasks.put(task);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}	


	@Override
	public void run() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (isWorking) {
			try {
				tasks.take().execute();
			} catch (Exception e){
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * abort
	 */
	public void abort(){
		this.accept(new IAsyncTask(){

			@Override
			public void execute() throws Exception {
				isWorking = false;
				
			}});
	}

	
}
