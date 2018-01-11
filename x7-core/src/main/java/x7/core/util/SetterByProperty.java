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
package x7.core.util;

public class SetterByProperty {
	
	public static final String SETTER_PREFIX = "set";
	
	public static String convertPropToSetter(String property) throws Exception{

		String setter = SETTER_PREFIX;
		
		if (property.equals(""))
			return setter;
		
		String c = property.substring(0, 1);
		int length = property.length();
		String remains = "";
		if (length>1)
		{
			remains = property.substring(1);
			return setter+c.toUpperCase()+remains;
		}
		
		return setter+c.toUpperCase();
	}
	

}
