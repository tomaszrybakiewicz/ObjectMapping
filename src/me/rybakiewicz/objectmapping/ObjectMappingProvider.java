//
//   ObjectMappingProvider.java
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ObjectMappingProvider {
	
	protected Map<String, ObjectMapping> mMappings;
	
	public static ObjectMappingProvider newInstance() {
		return new ObjectMappingProvider();
	}
	
	public ObjectMappingProvider() {
		mMappings = new HashMap<String, ObjectMapping>();
	}
	
	public void setMappingForKeyPath(String keyPath, ObjectMapping mapping) {
		mMappings.put(keyPath, mapping);
	}
	
	public ObjectMapping getMappingForKeyPath(String keyPath) {
		return mMappings.get(keyPath);
	}
	
	public Set<String> getRegisteredMappingKeyPaths() {
		return mMappings.keySet();
	}

	public Map<String, ObjectMapping> keyPathToMappingsMap() {
		return mMappings;
	}
}