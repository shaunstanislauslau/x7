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
package x7.core.tx;

/**
 * x7框架的异步事务管理<br>
 * 支持异步事务的dto必须实现这个接口<br>
 * txKeyed需要持久化<br>
 * Producer创建, Consumer通过key保存事务<br>
 * @author Sim
 *
 */
public interface TxTraceable {

	String ROOT = "/tx";
	String CONFIRM = "/confirm/";
	String CANCEL ="/cancel/";
	String OK = "/OK/";
	String FAIL = "/FAIL/";
	
	TxKeyed getTxKeyed();
	void setTxKeyed(TxKeyed txKeyed);
	
	public class TxKeyed {
		private String mapping;// restful
		private String domain;//ip:port
		private String callback;//ip:port
		private long total;//总步骤
		private long step;//当期步骤
		private long trys;//补偿次数
		private long time;//System.nanoTime();
		private long random;//随机数
		public String getMapping() {
			return mapping;
		}
		public void setMapping(String mapping) {
			this.mapping = mapping;
		}
		public String getDomain() {
			return domain;
		}
		public void setDomain(String domain) {
			this.domain = domain;
		}
		public String getCallback() {
			return callback;
		}
		public void setCallback(String callback) {
			this.callback = callback;
		}
		public long getTotal() {
			return total;
		}
		public void setTotal(long total) {
			this.total = total;
		}
		public long getStep() {
			return step;
		}
		public void setStep(long step) {
			this.step = step;
		}
		public long getTrys() {
			return trys;
		}
		public void setTrys(long trys) {
			this.trys = trys;
		}
		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		public long getRandom() {
			return random;
		}
		public void setRandom(long random) {
			this.random = random;
		}
		public String key(){
			return mapping + "." + time + "." + random;
		}
		@Override
		public int hashCode() {
			String key = key();
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TxKeyed other = (TxKeyed) obj;
			String key = key();
			if (key == null) {
				if (other.key() != null)
					return false;
			} else if (!key.equals(other.key()))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "TxKeyed [mapping=" + mapping + ", domain=" + domain + ", callback=" + callback + ", total=" + total
					+ ", step=" + step + ", trys=" + trys + ", time=" + time + ", random=" + random + "]";
		}
	}
}
