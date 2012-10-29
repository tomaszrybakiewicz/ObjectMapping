//
//   ObjectMappingOperationTest.java
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.rybakiewicz.objectmapping.models.TestCar;
import me.rybakiewicz.objectmapping.models.TestCarSpecs;
import me.rybakiewicz.objectmapping.models.TestImage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectMappingOperationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsValueACollection() {
		List<Object> list = new ArrayList<Object>();
		String str = new String();
		Set<Object> set = new HashSet<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		assertTrue(ObjectMappingOperation.isValueACollection(list));
		assertTrue(ObjectMappingOperation.isValueACollection(set));
		assertFalse(ObjectMappingOperation.isValueACollection(str));
		assertFalse(ObjectMappingOperation.isValueACollection(map));
	}
	
	// -- collections
	
	@Test
	public void testShouldSuccessfullyMapArrays() {
		Map<String,Object> data = new DataParserJSON().parseFromString("{\"paint_colors\":[\"black\",\"blue\",\"red\"]}").asMap();
		 
		TestCar object = new TestCar();
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToAttribute("paint_colors", "paintColors");
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertNotNull(object.paintColors);
		assertEquals("black", object.paintColors.get(0));
		assertEquals("blue", object.paintColors.get(1));
		assertEquals("red", object.paintColors.get(2));
	}
	
	@Test
	public void testShouldSuccessfullyMapOneToManyRelationship() {
		Map<String, Object> data = new DataParserJSON().parseFromString("{\"images\":[{\"url\":\"url1\"},{\"url\":\"url2\"}]}").asMap();
		
		ObjectMapping imageMapping = ObjectMapping.mappingForClass(TestImage.class);
		imageMapping.mapAttributes(new String[]{"url"});
		TestCar object = new TestCar();
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToRelationship("images", "thumbnails", imageMapping);
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertNotNull(object.thumbnails);
		assertEquals("url1", object.thumbnails.get(0).url);
		assertEquals("url2", object.thumbnails.get(1).url);
	}
	
	@Test
	public void testShouldSuccessfullyMapOneToOneRelationship() {
		Map<String, Object> data = new DataParserJSON().parseFromString("{\"car_specs\":{\"weight\":321,\"max_speed\":290}}").asMap();
		
		ObjectMapping specsMapping = ObjectMapping.mappingForClass(TestCarSpecs.class);
		specsMapping.mapKeyPathToAttribute("weight", "weight");
		specsMapping.mapKeyPathToAttribute("max_speed", "maxSpeed");
		TestCar object = new TestCar();
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToRelationship("car_specs", "specs", specsMapping);
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertNotNull(object.specs);
		assertEquals(321, object.specs.weight);
		assertEquals(290, object.specs.maxSpeed);
	}
	
	// -- booleans
	
	@Test
	public void testShouldSuccessfullyMapBooleans() {
		TestCar object = new TestCar();
		object.available = false;
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_available", true);
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToAttribute("is_available", "available");
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertEquals(true, object.available);
	}
	
	@Test
	public void testShouldSuccessfullyMapNumberToBooleans() {
		TestCar object = new TestCar();
		object.available = false;
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_available", 1);
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToAttribute("is_available", "available");
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertEquals(true, object.available);
	}
	
	@Test
	public void testShouldSuccessfullyMapStringToBooleans() {
		TestCar object = new TestCar();
		object.available = false;
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_available", "true");
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToAttribute("is_available", "available");
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertEquals(true, object.available);
	}
	
	// -- dates
	
	@Test
	public void testShouldSuccessfullyMapDates() throws ParseException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("release_date", "2010-02-12");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.getDateFormatters().add(df);
		mapping.mapKeyPathToAttribute("release_date", "releaseDate");
		TestCar object = new TestCar();
		
		ObjectMappingOperation operation = ObjectMappingOperation.objectMappingOperation(data, object, mapping);
		boolean success = operation.performMapping(null);
		assertTrue(success);
		assertEquals(df.parse("2010-02-12"), object.releaseDate);
	}
	
	// TODO: TEST value validation before mapping 
	
	@After
	public void tearDown() throws Exception {
	}
}
