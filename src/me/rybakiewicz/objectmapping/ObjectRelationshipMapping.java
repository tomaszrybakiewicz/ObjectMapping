//
//   ObjectRelationshipMapping.java
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

public class ObjectRelationshipMapping extends ObjectAttributeMapping {
	public ObjectMappingDefinition mapping;
	
	public static ObjectRelationshipMapping mappingFromKeyPath(String sourceKeyPath, String destinationKeyPath, ObjectMappingDefinition objectMapping) {
		return new ObjectRelationshipMapping(sourceKeyPath, destinationKeyPath, objectMapping);
	}
	
	public ObjectRelationshipMapping(String sourceKeyPath, String destinationKeyPath, ObjectMappingDefinition objectMapping) {
		super(sourceKeyPath, destinationKeyPath);
		this.mapping = objectMapping;
	}
}
