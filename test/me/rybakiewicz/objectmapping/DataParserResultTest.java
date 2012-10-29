//
//   DataParserResultTest.java
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataParserResultTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testShouldReturnNull() {
		assertNull(new DataParserResult(null).asList());
		assertNull(new DataParserResult("string").asList());
		assertNull(new DataParserResult(null).asMap());
		assertNull(new DataParserResult("string").asMap());
	}
	
	@Test
	public void testShouldDetectEmpty() {
		assertTrue(new DataParserResult(null).isEmpty());
		
		Map<String,Object> map = new HashMap<String,Object>();
		assertTrue(new DataParserResult(map).isEmpty());
		map.put("name", "Tom");
		assertFalse(new DataParserResult(map).asMap().isEmpty());
		
		List<Object> list = new ArrayList<Object>();
		assertTrue(new DataParserResult(list).isEmpty());
		list.add("Tom");
		assertFalse(new DataParserResult(list).isEmpty());
	}
	
	@Test
	public void testShouldReturnMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "Tom");
		assertTrue(new DataParserResult(map).asMap() instanceof Map<?,?>);
		assertEquals("Tom", new DataParserResult(map).asMap().get("name"));
	}
	
	@Test
	public void testShouldReturnList() {
		List<Object> list = new ArrayList<Object>();
		list.add("Tom");
		assertTrue(new DataParserResult(list).asList() instanceof List<?>);
		assertEquals("Tom", new DataParserResult(list).asList().get(0));
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
