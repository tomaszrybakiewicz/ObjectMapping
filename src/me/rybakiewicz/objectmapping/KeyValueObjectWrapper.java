//
//   KeyValueObjectWrapper.java
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyValueObjectWrapper implements KeyValueCoding {
	
	protected Object object;
	
	public KeyValueObjectWrapper(Object object) {
		this.object = object;
	}
	
	public Object getObject() {
		return this.object;
	}

	public Object getValueForKeyPath(String keyPath) {
		return valueAtKeyPath(keyPath, object);
	}
    
	public Object getValueForKey(String key) {
		return valueForKey(key, this.object);
	}

	@SuppressWarnings("unchecked")
	public void setValueForKey(String key, Object value) {
		if (this.object instanceof Map<?,?>) {
			((Map<Object,Object>)this.object).put(key, value);
			return;
		}
		try {
			Method setter = object.getClass().getDeclaredMethod("set" + key.substring(0, 1).toUpperCase() + key.substring(1) , new Class[]{value.getClass()});
			setter.invoke(object, new Object[]{value});
			return;
		} catch (Exception e) {
		}
		try {
			Field field = this.object.getClass().getDeclaredField(key);
			field.set(this.object, value);
			return;
		} catch (Exception e) {
		}
	}
	
	protected static Object valueForKey(String key, Object object) {
		if (object instanceof Map<?,?>) {
			return ((Map<?,?>)object).get(key);
		}
		if (object instanceof List<?>) {
			List<Object> values = new ArrayList<Object>();
			List<?> list = (List<?>) object;
			for (Object obj : list) {
				Object v = new KeyValueObjectWrapper(obj).getValueForKey(key);
				if (null != v) {
					values.add(v);
				}
			}
			return values.isEmpty() ? null : values;
		}
		try {
			Method getter = object.getClass().getDeclaredMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1) , new Class[]{});
			return getter.invoke(object, new Object[]{});
		} catch (Exception e) {
		}
		try {
			Field field = object.getClass().getDeclaredField(key);
			return field.get(object);
		} catch (Exception e) {
		}
		return null;
	}
	
	protected static Object valueAtKeyPath(String keyPath, Object data) {
    	String[] keys = keyPath.split("\\.");
    	ArrayList<String> path = new ArrayList<String>();
    	for (int i=0; i < keys.length; ++i) {
    		path.add(keys[i]);
    	}
    	return path.size() <= 1 ? valueForKey(keyPath, data) : valueAtKeyPath(path, data);
    }
    
    @SuppressWarnings("unchecked")
    protected static Object valueAtKeyPath(List<String> keyPath, Object data) {
    	String key = keyPath.get(0);
    	int keyPathSize = keyPath.size();
    	if (data instanceof Map<?,?>) {
    		Map<String,Object> map = (Map<String,Object>)data;
    		if (1 == keyPathSize) {
    			return map.get(key);
    		} 
    		return valueAtKeyPath(keyPath.subList(1, keyPathSize), map.get(key));
    	} 
    	if (data instanceof List<?>) {
    		Object object = valueForKey(key, data);
    		if (1 < keyPathSize) {
    			return valueAtKeyPath(keyPath.subList(1, keyPathSize), object);
    		} else {
    			return object;
    		}
    	} 
    	Object object = valueForKey(key, data);
    	if (object != null && 1 < keyPathSize) {
    		return valueAtKeyPath(keyPath.subList(1, keyPathSize), object);
    	}
    	return object;
    }
}
