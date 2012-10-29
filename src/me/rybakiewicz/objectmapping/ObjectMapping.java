//
//   ObjectMapping.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ObjectMapping extends ObjectMappingDefinition {
	
	protected static ArrayList<SimpleDateFormat> defaultDateFormatters = null;
	
	protected Class<?> objectClass;
	protected ArrayList<Object> mappings;
	protected ArrayList<SimpleDateFormat> dateFormatters;
	
	public Boolean ignoreUnknownKeyPaths;  // Default: true
	
	
	public static ObjectMapping mappingForClass(Class<?> objectClass) {
		return new ObjectMapping(objectClass);
	}
	
	public static ArrayList<SimpleDateFormat> defaultDateFormatters() {
		if (null == defaultDateFormatters) {
			defaultDateFormatters = new ArrayList<SimpleDateFormat>();
			defaultDateFormatters.add(new SimpleDateFormat("MM/dd/yyyy", Locale.US));
			defaultDateFormatters.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US));
		}
		return defaultDateFormatters;
	}
	
	public void setDateFormatters(ArrayList<SimpleDateFormat> dateFormatters) { 
		this.dateFormatters = dateFormatters; 
	}
	
	public ArrayList<SimpleDateFormat> getDateFormatters() {
		return (null != this.dateFormatters) ? this.dateFormatters : defaultDateFormatters();
	}
	
	public void setObjectClassName(String objectClassName) throws ClassNotFoundException {
		Class<?> cls = Class.forName(objectClassName);
		this.objectClass = cls;
	}
	
	public String getObjectClassName() {
		return this.objectClass.getName();
	}
	
	/* Attribute Mapping */
	
	public void addAttributeMapping(ObjectAttributeMapping mapping) {
		this.mappings.add(mapping);
	}
	
	public void mapKeyPathToAttribute(String sourceKeyPath, String destinationKeyPath) {
		ObjectAttributeMapping mapping = ObjectAttributeMapping.mappingForKeyPath(sourceKeyPath, destinationKeyPath);
		this.addAttributeMapping(mapping);
	}
	
	public void mapAttributes(String[] attributes) {
		for (int i = 0; i < attributes.length; ++i) {
			mapKeyPathToAttribute(attributes[i], attributes[i]);
		}
	}
	
	public ArrayList<ObjectAttributeMapping> attributesMappings() {
		ArrayList<ObjectAttributeMapping> mappings = new ArrayList<ObjectAttributeMapping>();
		for (int i=0; i < this.mappings.size(); ++i) {
			if (this.mappings.get(i) instanceof ObjectAttributeMapping) {
				mappings.add((ObjectAttributeMapping) this.mappings.get(i));
			}
		}
		return mappings;
	}
	
	public ObjectAttributeMapping mappingForAttribute(String attributeKey) {
		for (ObjectAttributeMapping mapping : this.attributesMappings()) {
			if (0 == mapping.destinationKeyPath.compareTo(attributeKey)) {
				return mapping;
			}
		}
		return null;
	}
	
	/* Relationship Mapping */
	
	public void addRelationshipMapping(ObjectRelationshipMapping mapping) {
		this.mappings.add(mapping);
	}
	
	public void mapKeyPathToRelationship(String relationshipKeyPath, String keyPath, ObjectMappingDefinition objectMapping) {
		ObjectRelationshipMapping mapping = ObjectRelationshipMapping.mappingFromKeyPath(relationshipKeyPath, keyPath, objectMapping);
		this.addRelationshipMapping(mapping);
	}
	
	public void mapRelationship(String relationshipKeyPath, ObjectMappingDefinition objectMapping) {
		this.mapKeyPathToRelationship(relationshipKeyPath, relationshipKeyPath, objectMapping);
	}
	
	public ArrayList<ObjectRelationshipMapping> relationshipMappings() {
		ArrayList<ObjectRelationshipMapping> mappings = new ArrayList<ObjectRelationshipMapping>();
		for (int i=0; i < this.mappings.size(); ++i) {
			if (this.mappings.get(i) instanceof ObjectRelationshipMapping) {
				mappings.add((ObjectRelationshipMapping) this.mappings.get(i));
			}
		}
		return mappings;
	}
	
	public ObjectRelationshipMapping mappingForRelationship(String relationshipKey) {
		for (ObjectRelationshipMapping mapping : this.relationshipMappings()) {
			if (0 == mapping.destinationKeyPath.compareTo(relationshipKey)) {
				return mapping;
			}
		}
		return null;
	}
	
	/* Remove methods */
	
	public void removeAllMappings() {
		this.mappings.clear();
	}
	
	public void removeMapping(ObjectAttributeMapping attributeOrRelationshipMapping) {
		this.mappings.remove(attributeOrRelationshipMapping);
	}
	
	/* Mapping get helper methods */
	
	public ArrayList<String> mappedKeyPaths() {
		ArrayList<String> keyPaths = new ArrayList<String>();
		for (Object mapping : this.mappings) {
			 keyPaths.add(((ObjectAttributeMapping)mapping).destinationKeyPath);
		}
		return keyPaths;
	}
	
	public Object mappingForSourcePath(String sourceKeyPath) {
		for (Object obj : this.mappings) {
			if (0 == ((ObjectAttributeMapping)obj).sourceKeyPath.compareTo(sourceKeyPath)) {
				return obj;
			}
		}
		return null;
	}
	
	public Object mappingForDestinationKeyPath(String destinationKeyPath) {
		for (Object obj : this.mappings) {
			if (0 == ((ObjectAttributeMapping)obj).destinationKeyPath.compareTo(destinationKeyPath)) {
				return obj;
			}
		}
		return null;
	}
		
	public Object mappableObjectForData(Object mappableData) {
		Object obj = null;
		try {
			Constructor<?> cons = this.objectClass.getConstructor(new Class[] {});
			obj = cons.newInstance(new Object[] {});
		} catch (Exception e) {
		} 
		return obj;
	}
	
	public Class<?> classForProperty(String propertyName) {
		Class<?> cls = null;
		try {
			Field field = this.objectClass.getDeclaredField(propertyName);
			cls = field.getType();
		} catch (Exception e) {
		}
		return cls;
	}
	
	public boolean mapsKeyPathToDest(String sourceKeyPath, String destKeyPath) {
		return sourceKeyPath.equals(((ObjectAttributeMapping)this.mappingForDestinationKeyPath(destKeyPath)).sourceKeyPath);
	}
	
	public Object defaultValueForMissingAttribute(String attributeName) {
		return null;
	}
	
	public String toString() {
		String str = "";
		for (Object mapping : this.mappings) {
			str += "\n" + mapping.toString();
		}
		return str;
	}
	
	private ObjectMapping(Class<?> objectClass) {
		super();
		this.objectClass = objectClass;
		this.mappings = new ArrayList<Object>();
		this.ignoreUnknownKeyPaths = true;
	}
}
