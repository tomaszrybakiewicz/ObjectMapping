//
//   DataParserResult.java
//	 me.rybakiewicz.objectmapping
//	 
//   Created by Tomasz Rybakiewicz on Oct 29 2012.
//   Copyright (c) 2012 Tomasz Rybakiewicz. All rights reserved.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//
package me.rybakiewicz.objectmapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataParserResult {
	
	protected Object mData;
	
	public DataParserResult() {		
	}
	
	public DataParserResult(Object data) { 
		mData = data;
	}
	
	public boolean isEmpty() {
		if (mData instanceof Collection<?>) {
			return ((Collection<?>)mData).isEmpty();
		} else if (mData instanceof Map<?,?>) {
			return ((Map<?,?>)mData).isEmpty();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> asList() {
		if (mData instanceof List<?>) {
			return (List<Object>) mData;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> asMap() {
		if (mData instanceof Map<?, ?>) {
			return (Map<String, Object>) mData;
		}
		return null;
	}
}
