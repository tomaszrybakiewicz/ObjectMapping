//
//   ObjectMappingResultTest.java
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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectMappingResultTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testReturnesConfiguredMappingResult() {
		assertTrue(ObjectMappingResult.newInstance() instanceof ObjectMappingResult);
	}
	
	@Test
	public void testShouldSetMappedObjectForKeyPath() {
		ObjectMappingResult result = ObjectMappingResult.newInstance();
		String object = "some string object";
		result.setMappedObjectForKeyPath("my.key.path", object);
		Object o = result.getMappedObjectForKeyPath("my.key.path");
		assertNotNull(o);
		assertEquals(object, o);
	}
	
	@Test
	public void testShouldReturnMappingResultFromMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		String o1 = "my string";
		int o2 = 3214;
		map.put("my.key.path", o1);
		map.put("other_key_path", o2);
		ObjectMappingResult result = ObjectMappingResult.newInstance(map);
		assertTrue(result instanceof ObjectMappingResult);
		assertEquals(o1, result.getMappedObjectForKeyPath("my.key.path"));
		assertEquals(o2, result.getMappedObjectForKeyPath("other_key_path"));
	}
	
	@After
	public void tearDown() throws Exception {
	}
}
