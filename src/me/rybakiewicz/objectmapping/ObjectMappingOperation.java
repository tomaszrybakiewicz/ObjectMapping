//
//   ObjectMappingOperation.java
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectMappingOperation {

	private KeyValueCoding sourceObject;
	private KeyValueCoding destinationObject;
	private ObjectMapping objectMapping;

	public static ObjectMappingOperation objectMappingOperation(Object sourceObject, Object destinationObject, ObjectMapping objectMapping) {
		return new ObjectMappingOperation(sourceObject, destinationObject, objectMapping);
	}
	
	private ObjectMappingOperation(Object sourceObject, Object destinationObject, ObjectMapping objectMapping) {
		this.setSourceObject(sourceObject);
		this.setDestinationObject(destinationObject);
		this.objectMapping = objectMapping;
	}
	
	private Date parseDateFromString(String value) {
		Date date = null;
		for (SimpleDateFormat dateFormatter : this.objectMapping.getDateFormatters()) {
			try {
				date = dateFormatter.parse(value);
				return date;
			} catch (ParseException e) {
				continue;
			}
		}
		return null;
	}
	
	private Object transformString(String value, Class<?> type) {
		if (Integer.class == type || int.class == type) {
			return Integer.parseInt(value);
		} else if(Short.class == type || short.class == type) {
			return Short.parseShort(value);
		} else if(Long.class == type || long.class == type) {
			return Long.parseLong(value);
		} else if(Double.class == type || double.class == type) {
			return Double.parseDouble(value);
		} else if(Float.class == type || float.class == type) {
			return Float.parseFloat(value);
		} else if(Boolean.class == type || boolean.class == type) {
			return Boolean.parseBoolean(value);
		} else if(Date.class == type) {
			return this.parseDateFromString(value);
		}
		return null;
	}
	
	private Object transformNumber(Number value,  Class<?> type) {
		if (Integer.class == type || int.class == type) {
			return (value).intValue();
		} else if(Short.class == type || short.class == type) {
			return (value).shortValue();
		} else if(Long.class == type || long.class == type) {
			return (value).longValue();
		} else if(Double.class == type || double.class == type) {
			return (value).doubleValue();
		} else if(Float.class == type || float.class == type) {
			return (value).floatValue();
		} else if (Boolean.class == type || boolean.class == type) {
			return value.equals(1);
		}
		return null;
	}
	
	private Object transformCollection(Collection<?> value, Class<?> type) {
		if (type.isAssignableFrom(Set.class)) {
			return new HashSet<Object>(value);
		} else if (type.isAssignableFrom(List.class)) {
			return new ArrayList<Object>(value);
		}
		return null;
	}
	
	private Object transformBoolean(boolean value, Class<?> type) {
		if (type.isAssignableFrom(Number.class)) {
			return transformNumber(value ? 1 : 0, type);
		} else if (String.class == type) {
			return value ? "true" : "false";
		} else if (Boolean.class == type || boolean.class == type) {
			return value;
		}
		return null;
	}
	
	private Object transformValueToType(Object value, Class<?> type) {
		if (value instanceof String) {
			return transformString((String) value, type);
		} else if (value instanceof Number){
			return transformNumber((Number) value, type);
		} else if (value instanceof Collection<?>) {
			return transformCollection((Collection<?>) value, type);
		} else if (value.getClass() == Boolean.class) {
			return transformBoolean((Boolean) value, type);
		} else {
			if (String.class == type) {
				return value.toString();
			}
		}
		return null;
	}
	
	private Boolean validateValueAtKeyPath(Object value, String keyPath) {
		return true;
	}
	
	private Boolean shouldSetValueAtKeyPath(Object value, String keyPath) {

		Object currentValue = this.destinationObject.getValueForKey(keyPath);
		if (null == currentValue && null == value) {
			return false;
		} else if (null == value || null == currentValue) {
			return validateValueAtKeyPath(value, keyPath);
		}
		if (!value.equals(currentValue)) {
			return validateValueAtKeyPath(value, keyPath);
		}
		
		return false;
	}
	
	private void applyAttributeMappingWithValue(ObjectAttributeMapping attributeMapping, Object value) {
		Class<?> type = this.objectMapping.classForProperty(attributeMapping.destinationKeyPath);
		if (null != type && value.getClass() != type) {
			value = transformValueToType(value, type);
		}
		if (shouldSetValueAtKeyPath(value, attributeMapping.destinationKeyPath)) {
			this.destinationObject.setValueForKey(attributeMapping.destinationKeyPath, value);
		}
	}
	
	private Boolean applyAttributeMappings() {
		Boolean appliedMappings = false;
		
		for (ObjectAttributeMapping attributeMapping : this.objectMapping.attributesMappings()) {
			Object value = null;
			if (attributeMapping.sourceKeyPath.equals("")) {
				value = this.sourceObject;
			} else {
				value = this.sourceObject.getValueForKeyPath(attributeMapping.sourceKeyPath);
			}
			
			if (null != value) {
				appliedMappings = true;
				this.applyAttributeMappingWithValue(attributeMapping, value);
			}
		}
		return appliedMappings;
	}
	
	@SuppressWarnings("unchecked")
	private Boolean mapNestedObjectToObjectWithRelationship(Object anObject, Object anotherObject, ObjectRelationshipMapping relationshipMapping) {
		ObjectMappingOperation supOperation = ObjectMappingOperation.objectMappingOperation((Map<String, Object>) anObject, anotherObject, (ObjectMapping)relationshipMapping.mapping);
		Error error = null;
		supOperation.performMapping(error);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void mapCollectionToRelationship(Collection<?> collection, Object destObject, ObjectRelationshipMapping relationshipMapping) {
		destObject = new ArrayList<Object>(collection.size());
		ObjectMapping objMapping = (ObjectMapping) relationshipMapping.mapping;
	
		for (Object nestedObject : collection) {
			Object mappedObject = objMapping.mappableObjectForData(nestedObject);
			if (mapNestedObjectToObjectWithRelationship(nestedObject, mappedObject, relationshipMapping)) {
				((List<Object>)destObject).add(mappedObject);
			}
		}
		
		Class<?> type = this.objectMapping.classForProperty(relationshipMapping.destinationKeyPath);
		if (null != type && false == destObject.getClass().isAssignableFrom(type)) {
			destObject = transformValueToType(destObject, type);
		}
		
		if (shouldSetValueAtKeyPath(destObject, relationshipMapping.destinationKeyPath)) {
			this.destinationObject.setValueForKey(relationshipMapping.destinationKeyPath, destObject);
		}
	}
	
	private Boolean applyRelationshipMappings() {
		Boolean appliedMappings = false;
		Object destObject = null;
		
		for (ObjectRelationshipMapping relationshipMapping : this.objectMapping.relationshipMappings()) {
			Object value = this.sourceObject.getValueForKeyPath(relationshipMapping.sourceKeyPath);
			if (null == value) {
				this.destinationObject.setValueForKey(relationshipMapping.destinationKeyPath, null);
				continue;
			}
			
			if (isValueACollection(value)) {
				// One to many relationship
				appliedMappings = true;
				mapCollectionToRelationship((Collection<?>) value, destObject, relationshipMapping);
			} else {
				// One to one relationship
				ObjectMapping objectMapping = (ObjectMapping) relationshipMapping.mapping;
				destObject = objectMapping.mappableObjectForData(value);
				if (mapNestedObjectToObjectWithRelationship(value, destObject, relationshipMapping)) {
					appliedMappings = true;
				}
				
				if (shouldSetValueAtKeyPath(destObject, relationshipMapping.destinationKeyPath)) {
					appliedMappings = true;
					this.destinationObject.setValueForKey(relationshipMapping.destinationKeyPath, destObject);
				}
			}
		}
		return appliedMappings;
	}
    
	public Boolean performMapping(Error error) {
		Boolean mappedAttributes = applyAttributeMappings();
		Boolean mappedRelationships = applyRelationshipMappings();
		if (mappedAttributes || mappedRelationships) {
			return true;
		}
		return false;
	}
	
    public void setSourceObject(Object sourceObject) {
    	if (sourceObject instanceof KeyValueCoding) {
    		this.sourceObject = (KeyValueCoding) sourceObject;
    	} else {
    		this.sourceObject = new KeyValueObjectWrapper(sourceObject);
    	}
	}
	
	public Object getSourceObject() {
		return this.sourceObject.getObject();
	}
	
	public void setDestinationObject(Object destinationObject) {
		if (destinationObject instanceof KeyValueCoding) {
			this.destinationObject = (KeyValueCoding) this.destinationObject;
		} else {
			this.destinationObject = new KeyValueObjectWrapper(destinationObject);
		}
	}
	
	public Object getDestinationObject() {
		return this.destinationObject.getObject();
	}
	
	public static boolean isValueACollection(Object value) {
		return null != value && (value instanceof List || value instanceof Set); 
	}
}
