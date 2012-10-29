//
//   ObjectMappingTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectMappingTest {

	private MyObject myObject;
	
	@Before
	public void setUp() throws Exception {
		myObject = new MyObject();
		myObject.name = "This is some test name";
		myObject.details = "Some details goes here";
		myObject.identifier = 42213;
		myObject.created = new Date();
	}

	@Test
	public void testMappingForClass() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		assertNotNull(mapping);
		assertTrue(mapping.mappings.isEmpty());
		assertEquals(myObject.getClass(), mapping.objectClass);
		assertNotNull(mapping.mappings);
		assertTrue(mapping.ignoreUnknownKeyPaths);
	}
	
	@Test
	public void testDefaultDateFormatters() {
		ArrayList<SimpleDateFormat> formatters = ObjectMapping.defaultDateFormatters();
		assertFalse(formatters.isEmpty());
		assertTrue(formatters.get(0) instanceof SimpleDateFormat);
	}
	
	@Test
	public void testSetObjectClassName() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		try {
			mapping.setObjectClassName(String.class.getName());
			assertEquals(String.class, mapping.objectClass);
			
			mapping.setObjectClassName("SomeFakeClassName");
			fail("setObjectClassName should throw ClassNotFoundException");
		} catch (ClassNotFoundException e) {
		}
	}
	
	@Test 
	public void testGetObjectClassName() {
		assertEquals(MyObject.class.getName(), ObjectMapping.mappingForClass(myObject.getClass()).getObjectClassName());
		assertEquals(String.class.getName(), ObjectMapping.mappingForClass(String.class).getObjectClassName());
	}

	@Test
	public void testAddAttributeMapping() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		ObjectAttributeMapping am = ObjectAttributeMapping.mappingForKeyPath("first_name", "firstName");
		mapping.addAttributeMapping(am);
		assertEquals(1, mapping.mappings.size());
		assertEquals(am, mapping.mappings.get(0));
	}
	
	@Test
	public void testMapKeyPathToAttribute() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		String sourceKeyPath = "first_name";
		String destinationKeyPath = "firstName";
		mapping.mapKeyPathToAttribute(sourceKeyPath, destinationKeyPath);
		
		assertEquals(1, mapping.mappings.size());
		ObjectAttributeMapping am = (ObjectAttributeMapping) mapping.mappings.get(0);
		assertEquals(sourceKeyPath, am.sourceKeyPath);
		assertEquals(destinationKeyPath, am.destinationKeyPath);
	}
	
	@Test
	public void testMapAttributes() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		String[] attributes = new String[] {"first", "second", "third"};
		
		mapping.mapAttributes(attributes);
		assertEquals(attributes[0], ((ObjectAttributeMapping)mapping.mappings.get(0)).sourceKeyPath);
		assertEquals(attributes[0], ((ObjectAttributeMapping)mapping.mappings.get(0)).destinationKeyPath);
		assertEquals(attributes[2], ((ObjectAttributeMapping)mapping.mappings.get(2)).sourceKeyPath);
		assertEquals(attributes[2], ((ObjectAttributeMapping)mapping.mappings.get(2)).destinationKeyPath);
	}
	
	@Test
	public void testAttributesMappings() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapKeyPathToAttribute("first_name", "firstName");
		mapping.mapKeyPathToAttribute("last_name", "lastName");
		
		ArrayList<ObjectAttributeMapping> aMappings = mapping.attributesMappings();
		assertEquals(2, aMappings.size());
		assertEquals("last_name", aMappings.get(1).sourceKeyPath);
		assertEquals("lastName", aMappings.get(1).destinationKeyPath);
		assertEquals("first_name", aMappings.get(0).sourceKeyPath);
		assertEquals("firstName", aMappings.get(0).destinationKeyPath);
	}
	
	@Test
	public void testMappingForAttribute() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapKeyPathToAttribute("first_name", "firstName");
		mapping.mapKeyPathToAttribute("last_name", "lastName");
		
		ObjectAttributeMapping aMapping = mapping.mappingForAttribute("firstName");
		assertEquals("first_name", aMapping.sourceKeyPath);
		assertEquals("firstName", aMapping.destinationKeyPath);
		assertEquals(null, mapping.mappingForAttribute("dsasd"));
	}
	
	@Test
	public void testAddRelationshipMapping() {
		ObjectMapping objectMapping1 = ObjectMapping.mappingForClass(String.class);
		ObjectMapping objectMapping2 = ObjectMapping.mappingForClass(ArrayList.class);
		ObjectRelationshipMapping relMapping1 = ObjectRelationshipMapping.mappingFromKeyPath("some_string", "someString", objectMapping1);
		ObjectRelationshipMapping relMapping2 = ObjectRelationshipMapping.mappingFromKeyPath("some_array", "someArray", objectMapping2);
		
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.addRelationshipMapping(relMapping1);
		mapping.addRelationshipMapping(relMapping2);
		assertEquals(2, mapping.mappings.size());
		assertEquals(relMapping1, mapping.mappings.get(0));
		assertEquals(relMapping2, mapping.mappings.get(1));
	}
	
	@Test
	public void testMapKeyPathToRelationship() {
		ObjectMapping objectMapping1 = ObjectMapping.mappingForClass(String.class);
		ObjectMapping objectMapping2 = ObjectMapping.mappingForClass(ArrayList.class);
		
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapKeyPathToRelationship("some_string", "someString", objectMapping1);
		mapping.mapKeyPathToRelationship("some_array", "someArray", objectMapping2);
		assertEquals(2, mapping.mappings.size());
		
		ObjectRelationshipMapping relMapping1 = (ObjectRelationshipMapping)mapping.mappings.get(0);
		assertEquals("some_string", relMapping1.sourceKeyPath);
		assertEquals("someString", relMapping1.destinationKeyPath);
		assertEquals(objectMapping1, relMapping1.mapping);
		
		ObjectRelationshipMapping relMapping2 = (ObjectRelationshipMapping)mapping.mappings.get(1);
		assertEquals("some_array", relMapping2.sourceKeyPath);
		assertEquals("someArray", relMapping2.destinationKeyPath);
		assertEquals(objectMapping2, relMapping2.mapping);
	}
	
	@Test
	public void testMapRelationship() {
		ObjectMapping objectMapping = ObjectMapping.mappingForClass(String.class);
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapRelationship("string", objectMapping);
		assertEquals(1, mapping.mappings.size());
		
		ObjectRelationshipMapping relMapping = (ObjectRelationshipMapping)mapping.mappings.get(0);
		assertEquals("string", relMapping.sourceKeyPath);
		assertEquals("string", relMapping.destinationKeyPath);
		assertEquals(objectMapping, relMapping.mapping);
	}
	
	@Test
	public void testRelationshipMappings() {
		ObjectMapping objectMapping1 = ObjectMapping.mappingForClass(String.class);
		ObjectMapping objectMapping2 = ObjectMapping.mappingForClass(ArrayList.class);
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapRelationship("string", objectMapping1);
		mapping.mapRelationship("array", objectMapping2);
		
		ArrayList<ObjectRelationshipMapping> relMappings = mapping.relationshipMappings();
		assertEquals(2, relMappings.size());
		assertEquals("string", relMappings.get(0).sourceKeyPath);
		assertEquals(objectMapping1, relMappings.get(0).mapping);
		assertEquals(objectMapping2, relMappings.get(1).mapping);
	}
	
	@Test
	public void testMappingForRelationship() {
		ObjectMapping objectMapping1 = ObjectMapping.mappingForClass(String.class);
		ObjectMapping objectMapping2 = ObjectMapping.mappingForClass(ArrayList.class);
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapRelationship("string", objectMapping1);
		mapping.mapRelationship("array", objectMapping2);
		
		assertEquals(objectMapping1, mapping.mappingForRelationship("string").mapping);
		assertEquals("string", mapping.mappingForRelationship("string").destinationKeyPath);
		assertEquals(objectMapping2, mapping.mappingForRelationship("array").mapping);
	}
	
	@Test
	public void testRemoveAllMappings() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapAttributes(new String[] {"one", "two"});
		assertEquals(2, mapping.mappings.size());
		mapping.removeAllMappings();
		assertEquals(0, mapping.mappings.size());
	}
	
	@Test
	public void testRemoveMapping() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapAttributes(new String[] {"one", "two"});
		ObjectAttributeMapping atrMapping = ObjectAttributeMapping.mappingForKeyPath("first_name", "firstName");
		mapping.addAttributeMapping(atrMapping);
		
		assertEquals(3, mapping.mappings.size());
		assertEquals(atrMapping, mapping.mappingForAttribute("firstName"));
		mapping.removeMapping(atrMapping);
		assertEquals(2, mapping.mappings.size());
		assertEquals(null, mapping.mappingForAttribute("firstName"));
	}
	
	@Test
	public void testMappedKeyPaths() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		mapping.mapAttributes(new String[] {"one", "two"});
		ArrayList<String> keyPaths = mapping.mappedKeyPaths();
		assertEquals(2, keyPaths.size());
		assertEquals("two", keyPaths.get(1));
	}
	
	@Test
	public void testMappingForSourcePath() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		ObjectAttributeMapping am1 = ObjectAttributeMapping.mappingForKeyPath("first_name", "firstName");
		ObjectAttributeMapping am2 = ObjectAttributeMapping.mappingForKeyPath("last_name", "lastName");
		mapping.addAttributeMapping(am1);
		mapping.addAttributeMapping(am2);
		
		assertEquals(am1, mapping.mappingForSourcePath("first_name"));
		assertEquals(am2, mapping.mappingForSourcePath("last_name"));
	}
	
	@Test
	public void testMappingForDestinationKeyPath() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		ObjectAttributeMapping am1 = ObjectAttributeMapping.mappingForKeyPath("first_name", "firstName");
		ObjectAttributeMapping am2 = ObjectAttributeMapping.mappingForKeyPath("last_name", "lastName");
		mapping.addAttributeMapping(am1);
		mapping.addAttributeMapping(am2);
		
		assertEquals(am1, mapping.mappingForDestinationKeyPath("firstName"));
		assertEquals(am2, mapping.mappingForDestinationKeyPath("lastName"));
	}
	
	@Test
	public void testMappableObjectForData() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		
		Object obj = mapping.mappableObjectForData(null);
		assertTrue(obj instanceof MyObject);
	}
	
	@Test
	public void testClassForProperty() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(myObject.getClass());
		
		assertEquals(String.class, mapping.classForProperty("name"));
		assertEquals(String.class, mapping.classForProperty("details"));
		assertEquals(Date.class, mapping.classForProperty("created"));
	}
	
	@Test
	public void testMapsKeyPathToDestination() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(Object.class);
		mapping.mapKeyPathToAttribute("full_name", "name");
		mapping.mapKeyPathToAttribute("some_prop", "someProp");
		
		ObjectMapping relMapping = ObjectMapping.mappingForClass(String.class);
		mapping.mapKeyPathToRelationship("some_rel", "someRel", relMapping);
		assertTrue(mapping.mapsKeyPathToDest("full_name", "name"));
		assertTrue(mapping.mapsKeyPathToDest("some_prop", "someProp"));
		assertTrue(mapping.mapsKeyPathToDest("some_rel", "someRel"));
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	public static class MyObject {
		public String name;
		public String details;
		public int identifier;
		public Date created;
	}
}
