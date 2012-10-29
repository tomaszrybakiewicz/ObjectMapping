//
//   ObjectMapperTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.rybakiewicz.objectmapping.models.TestCar;
import me.rybakiewicz.objectmapping.models.TestCarSpecs;
import me.rybakiewicz.objectmapping.models.TestImage;
import me.rybakiewicz.objectmapping.models.TestUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectMapperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void selftestShouldLoadFixture() {
		Map<String, Object> data = loadFixtureFromFile("user.json").asMap();
		assertNotNull(data);
	}
	
	@Test
	public void testShouldReturnConfiguredObjectMapper() {
		ObjectMappingProvider mappingProvider = new ObjectMappingProvider();
		Object sourceObject = new Object();
		
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(sourceObject, mappingProvider);
		assertEquals(sourceObject, mapper.getSourceObject());
		assertEquals(mappingProvider, mapper.getObjectMappingProvider());
	}
	
	@Test
	public void testShouldPerformBasicMapping() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		mapping.mapKeyPathToAttribute("id", "userID");
		mapping.mapKeyPathToAttribute("name", "name");
		
		TestUser user = new TestUser();
		Map<String, Object> data = loadFixtureFromFile("user.json").asMap();
		assertNotNull(data);
		
		ObjectMapper mapper = new ObjectMapper();
		boolean success = mapper.mapFromObjectAtKeyPathUsingMapping(data, user, "", mapping);
		assertTrue(success);
		assertEquals(44531, user.userID);
		assertEquals("Tomasz Rybakiewicz", user.name);
	}
	
	@Test
	public void testShouldMapListOfDictionaries() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		mapping.mapKeyPathToAttribute("id", "userID");
		mapping.mapKeyPathToAttribute("name", "name");
		
		List<Object> data = loadFixtureFromFile("users.json").asList();
		assertNotNull(data);
		
		ObjectMapper mapper = new ObjectMapper();
		List<?> users = mapper.mapCollectionAtKeyPathUsingMapping(data, "", mapping); 
		assertNotNull(users);
		assertEquals(2, users.size());
	}
	
	@Test
	public void testShouldMapToTargetObject() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		mapping.mapKeyPathToAttribute("id", "userID");
		mapping.mapKeyPathToAttribute("name", "name");
		
		Map<String, Object> data = loadFixtureFromFile("user.json").asMap();
		assertNotNull(data);
		
		ObjectMappingProvider mappingProvider = ObjectMappingProvider.newInstance();
		mappingProvider.setMappingForKeyPath("", mapping);
		
		TestUser user = new TestUser();
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(data, mappingProvider);
		mapper.setTargetObject(user);
		ObjectMappingResult result = mapper.performMapping();
		assertNotNull(result);
		assertEquals(44531, user.userID);
		assertEquals("Tomasz Rybakiewicz", user.name);
	}

	@Test
	public void testShouldMapWithoutTargetObject() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		mapping.mapKeyPathToAttribute("id", "userID");
		mapping.mapKeyPathToAttribute("name", "name");
		
		Map<String, Object> data = loadFixtureFromFile("user.json").asMap();
		assertNotNull(data);
		
		ObjectMappingProvider mappingProvider = ObjectMappingProvider.newInstance();
		mappingProvider.setMappingForKeyPath("", mapping);
		
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(data, mappingProvider);
		ObjectMappingResult result = mapper.performMapping();
		assertNotNull(result);
		TestUser user = (TestUser) result.asObject();
		assertEquals(44531, user.userID);
		assertEquals("Tomasz Rybakiewicz", user.name);
	}
	
	@Test
	public void testShouldMapCollectionOfObjects() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		mapping.mapKeyPathToAttribute("id", "userID");
		mapping.mapKeyPathToAttribute("name", "name");
		ObjectMappingProvider mappingProvider = ObjectMappingProvider.newInstance();
		mappingProvider.setMappingForKeyPath("", mapping);
		
		List<Object> data = loadFixtureFromFile("users.json").asList();
		assertNotNull(data);
		
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(data, mappingProvider);
		List<?> users =  mapper.performMapping().asCollection();
		assertNotNull(users);
		assertEquals(2, users.size());
		assertEquals("Tomasz Rybakiewicz", ((TestUser)users.get(0)).name);
		assertEquals(223, ((TestUser)users.get(1)).userID);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testShoudMapCollectionOfNestedComplexObjects() throws ParseException {
		
		ObjectMapping specMapping = ObjectMapping.mappingForClass(TestCarSpecs.class);
		specMapping.mapKeyPathToAttribute("weight", "weight");
		specMapping.mapKeyPathToAttribute("max_speed", "maxSpeed");
		
		ObjectMapping imageMapping = ObjectMapping.mappingForClass(TestImage.class);
		imageMapping.mapKeyPathToAttribute("url", "url");
		imageMapping.mapAttributes(new String[] { "name" });
		
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestCar.class);
		mapping.mapKeyPathToAttribute("name", "name");
		mapping.mapKeyPathToAttribute("paint_colors", "paintColors");
		mapping.mapKeyPathToAttribute("release_date", "releaseDate");
		mapping.mapKeyPathToAttribute("available", "available");
		mapping.mapKeyPathToRelationship("specs", "specs", specMapping);
		mapping.mapKeyPathToRelationship("thumbnails", "thumbnails", imageMapping);
		mapping.mapKeyPathToRelationship("images.image", "images", imageMapping);
		ArrayList<SimpleDateFormat> dfs = new ArrayList<SimpleDateFormat>();
		dfs.add(new SimpleDateFormat("yyyy/MM/dd"));
		mapping.setDateFormatters(dfs);
		
		ObjectMappingProvider provider = ObjectMappingProvider.newInstance();
		provider.setMappingForKeyPath("cars.car", mapping);
		
		Map<String, Object> data = loadFixtureFromFile("nested_cars.json").asMap();
		assertNotNull(data);
		
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(data, provider);
		List<?> resultList = mapper.performMapping().asCollection();
		List<TestCar> cars = (List<TestCar>) resultList;
		assertNotNull(cars);
		assertEquals(2, cars.size());
		assertEquals("Mustang", cars.get(0).name);
		assertEquals("white", cars.get(0).paintColors.get(1));
		assertEquals("http://images.askmen.com/top_10/cars/1301606037_top-10-best-car-names_1.jpg", cars.get(0).thumbnails.get(0).url);
		assertEquals(260, cars.get(0).specs.maxSpeed);
		assertEquals(true, cars.get(0).available);
		assertEquals(new SimpleDateFormat("yyyy/MM/dd").parse("1964/11/23"), cars.get(0).releaseDate);
		assertEquals(3, cars.get(0).images.size());
		assertEquals("Mustang Image #1", cars.get(0).images.get(0).getName());
		assertEquals("http://www.tuningnews.net/wallpaper/1024x768/ford-mustang-shelby-gt500kr-glass-roof-01.jpg", cars.get(0).images.get(2).url);
		
		
		assertEquals("Countach", cars.get(1).name);
		assertEquals("black", cars.get(1).paintColors.get(0));
		assertEquals(1, cars.get(1).thumbnails.size());
		assertEquals(1100, cars.get(1).specs.weight);
		assertEquals(false, cars.get(1).available);
		assertEquals(2, cars.get(1).images.size());
		assertEquals("Lambo Image #2", cars.get(1).images.get(1).getName());
		assertEquals("http://www.autoperceptions.com/autoperceptions/wp-content/uploads/2012/04/lamborghini-countach-09.jpg", cars.get(1).images.get(1).url);
	}
	
	@Test
	public void testShouldMapObjectToObject() {
		ObjectMapping brandToImageNameMapping = ObjectMapping.mappingForClass(TestImage.class);
		brandToImageNameMapping.mapKeyPathToAttribute("brand", "name");
		
		TestCar car = new TestCar();
		car.brand = "Ford";
		
		ObjectMappingProvider provider = ObjectMappingProvider.newInstance();
		provider.setMappingForKeyPath("", brandToImageNameMapping);
		
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(car, provider);
		Object resultObject = mapper.performMapping().asObject();
		TestImage image = (TestImage) resultObject;
		assertNotNull(image);
		assertEquals(car.brand, image.getName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testShouldMapObjectToMap() {
		TestCar car = new TestCar();
		car.name = "Mustang";
		car.brand = "Ford";
		car.available = true;
		
		ObjectMapping carToMapMapping = ObjectMapping.mappingForClass(HashMap.class);
		carToMapMapping.mapKeyPathToAttribute("name", "car_name");
		carToMapMapping.mapKeyPathToAttribute("brand", "car_brand");
		carToMapMapping.mapKeyPathToAttribute("available", "car_available");
		
		ObjectMappingProvider provider = ObjectMappingProvider.newInstance();
		provider.setMappingForKeyPath("", carToMapMapping);
		
		ObjectMapper mapper = ObjectMapper.mapperWithObjectAndMappingProvider(car, provider);
		Object resultObject = mapper.performMapping().asObject();
		Map<String, Object> map = (Map<String, Object>) resultObject;
		assertNotNull(map);
		assertEquals(car.name, map.get("car_name"));
		assertEquals(car.brand, map.get("car_brand"));
		assertEquals(car.available, map.get("car_available"));
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	public DataParserResult loadFixtureFromFile(String fileName) {
		URL path = ClassLoader.getSystemResource("fixtures/JSON/" + fileName);
		try {
			return new DataParserJSON().parseFromFile(new File(path.toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
		return new DataParserResult();
	}
}
