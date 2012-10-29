//
//   DataParserJSONTest.java
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

import static org.junit.Assert.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataParserJSONTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testShouldSuccessfullyParseJSONFromString() {
		String jsonString = "{\"name\":\"Tomasz Rybakiewicz\",\"height\":190 }";
		Map<?, ?> result = new DataParserJSON().parseFromString(jsonString).asMap();
		
		assertNotNull(result);
		assertEquals("Tomasz Rybakiewicz", result.get("name"));
		assertEquals(190, result.get("height"));
	}

	@Test
	public void testShouldSuccessfullyParseJSONFromInputStream() throws URISyntaxException, IOException {
		URL path = ClassLoader.getSystemResource("fixtures/JSON/user.json");
		InputStream fis = new FileInputStream(new File(path.toURI()));
		Map<?, ?> result = new DataParserJSON().parseFromInputStream(fis).asMap();
		fis.close();
		
		assertNotNull(result);
		assertEquals("Tomasz Rybakiewicz", result.get("name"));
		assertEquals("1985/04/12", result.get("birthdate"));
	}
	
	@Test
	public void testShouldSuccessfullyParseJSONFromFile() throws URISyntaxException {
		URL path = ClassLoader.getSystemResource("fixtures/JSON/user.json");
		Map<String,Object> result = new DataParserJSON().parseFromFile(new File(path.toURI())).asMap();
		
		assertNotNull(result);
		assertEquals("Tomasz Rybakiewicz", result.get("name"));
		assertEquals("1985/04/12", result.get("birthdate"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testShouldSuccessfullyParseCollectionFromJSON() {
		String jsonString = "[{\"id\":1234,\"name\":\"Tomasz Rybakiewicz\"},{\"id\":223,\"name\":\"Marta Statucka\"}]";
		List<Object> result = new DataParserJSON().parseFromString(jsonString).asList();
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1234, ((Map<String,Object>) result.get(0)).get("id"));
		assertEquals("Marta Statucka", ((Map<String,Object>) result.get(1)).get("name"));
	}
	
	@Test
	public void testShouldReturnDataParserResult() {
		DataParser parser = new DataParserJSON();
		assertTrue(parser.parseFromString("{\"name\":\"Tomasz Rybakiewicz\"}") instanceof DataParserResult);
		assertTrue(parser.parseFromString("") instanceof DataParserResult);
		assertTrue(parser.parseFromString(null) instanceof DataParserResult);
	}
	
	@After
	public void tearDown() throws Exception {
	}
}
