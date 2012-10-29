//
//   ObjectMapper.java
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
import java.util.Set;

public class ObjectMapper {

	protected ObjectMappingProvider mMappingProvider;
	protected Object mSourceObject;
	protected Object mTargetObject;
	
	public static ObjectMapper mapperWithObjectAndMappingProvider(Object sourceObject, ObjectMappingProvider mappingProvider) {
		return new ObjectMapper(sourceObject, mappingProvider);
	}
	
	public ObjectMapper() {
	}
	
	public ObjectMapper(Object sourceObject, ObjectMappingProvider mappingProvider) {
		mSourceObject = sourceObject;
		mMappingProvider = mappingProvider;
	}

	public ObjectMappingResult performMapping() {
		Map<String, ObjectMapping> mappings = mMappingProvider.keyPathToMappingsMap();
		Map<String, Object> results = performKeyPathMappingUsingMappingMap(mappings);
		return ObjectMappingResult.newInstance(results);
	}

	public Object mapObjectAtKeyPathUsingMapping(Object object, String keyPath, ObjectMapping mapping) {
		Object destinationObject = null;
		
		if (null != mTargetObject) {
			destinationObject = mTargetObject;
			if (destinationObject.getClass() != mapping.objectClass) {
				// TODO: LOG invalid target object for mapping
				return null;
			}
		} else {
			destinationObject = mapping.mappableObjectForData(object);
		}
		
		if (null != destinationObject) {
			boolean success = mapFromObjectAtKeyPathUsingMapping(object, destinationObject, keyPath, mapping);
			if (success) {
				return destinationObject;
			}
		}
		return null;
	}
	
	public List<Object> mapCollectionAtKeyPathUsingMapping(Collection<?> sourceObject, String keyPath, ObjectMapping mapping) {
		ArrayList<Object> results = new ArrayList<Object>();
		for (Object data : sourceObject) {
			if (data instanceof Map) {
				Object targetObject = mapping.mappableObjectForData(data);
				if (null == targetObject) {
					continue;
				}
				boolean success = mapFromObjectAtKeyPathUsingMapping(data, targetObject, keyPath, mapping);
				if (success) {
					results.add(targetObject);
				}
			}
		}
		return results;
	}
	
	public boolean mapFromObjectAtKeyPathUsingMapping(Object sourceObject, Object targetObject, String keyPath, ObjectMapping mapping) {
		boolean success = false;
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(sourceObject, targetObject, mapping);
		success = operation.performMapping(null);
		
		return success;
	}
	
	public Object getSourceObject() {
		return mSourceObject;
	}
	
	public ObjectMappingProvider getObjectMappingProvider() {
		return mMappingProvider;
	}
	
	public void setTargetObject(Object object) {
		mTargetObject = object;
	}
	
	public Object getTargetObject() {
		return mTargetObject;
	}
	
	private Map<String, Object> performKeyPathMappingUsingMappingMap(Map<String, ObjectMapping> mappingsByKeyPath) {
		boolean foundMappable = false;
		Map<String, Object> results = new HashMap<String, Object>();
		Set<String> keyPaths = mappingsByKeyPath.keySet();
		KeyValueObjectWrapper sourceObjectWrapper = new KeyValueObjectWrapper(mSourceObject);
		for (String keyPath : keyPaths) {
			Object mappingResult = null;
			Object mappableValue = null;
			
			if (keyPath.isEmpty()) {
				mappableValue = sourceObjectWrapper.getObject();
			} else {
				mappableValue = sourceObjectWrapper.getValueForKeyPath(keyPath);
			}
			
			if (null == mappableValue) {
				continue;
			}
			
			foundMappable = true;
			ObjectMapping mapping = mappingsByKeyPath.get(keyPath);
			mappingResult = performMappingForObjectAtKeyPathUsingMapping(mappableValue, keyPath, mapping);
			if (null != mappingResult) {
				results.put(keyPath, mappingResult);
			}
		}

		return (true == foundMappable) ? results : null;
	}

	private Object performMappingForObjectAtKeyPathUsingMapping(Object mappableValue, String keyPath, ObjectMapping mapping) {
		Object result = null;
		if (mappableValue instanceof Collection) {
			result = mapCollectionAtKeyPathUsingMapping((Collection<?>) mappableValue, keyPath, mapping);
		} else {
			result = mapObjectAtKeyPathUsingMapping(mappableValue, keyPath, mapping);
		}
		return result;
	}
	
}
