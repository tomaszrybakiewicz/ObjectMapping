//
//   ObjectMappingResult.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMappingResult {

	protected Map<String, Object> mKeyPathToMappedObjects;
	
	public static ObjectMappingResult newInstance() {
		return new ObjectMappingResult();
	}
	
	public static ObjectMappingResult newInstance(Map<String, Object> map) {
		return new ObjectMappingResult(map);
	}
	
	public ObjectMappingResult() {
		mKeyPathToMappedObjects = new HashMap<String, Object>();
	}
	
	public ObjectMappingResult(Map<String, Object> map) {
		mKeyPathToMappedObjects = map;
	}

	public void setMappedObjectForKeyPath(String keyPath, Object object) {
		mKeyPathToMappedObjects.put(keyPath, object);
	}
	
	public Object getMappedObjectForKeyPath(String keyPath) {
		return mKeyPathToMappedObjects.get(keyPath);
	}

	public List<Object> asCollection() {
		if (mKeyPathToMappedObjects.isEmpty()) {
			return null;
		}
		
		List<Object> collection = new ArrayList<Object>();
		for (Object object : mKeyPathToMappedObjects.values()) {
			if (object instanceof Collection) {
				collection.addAll((Collection<?>)object);
			} else {
				collection.add(object);
			}
		}
		return collection;
	}
	
	public Object asObject() {
		List<Object> collection = asCollection();
		return collection.isEmpty() ? null : collection.get(0);
	}
}
