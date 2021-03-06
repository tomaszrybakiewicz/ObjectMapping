//
//   ObjectMappingProviderTest.java
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
import static org.junit.Assert.assertTrue;

import java.util.Map;

import me.rybakiewicz.objectmapping.models.TestUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectMappingProviderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testShouldSetMappingForKeyPath() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		ObjectMappingProvider provider = ObjectMappingProvider.newInstance();
		provider.setMappingForKeyPath("test_user", mapping);
		assertEquals(mapping, provider.getMappingForKeyPath("test_user"));
	}
	
	@Test
	public void testShouldReturnKeyPathToMappingMap() {
		ObjectMapping mapping = ObjectMapping.mappingForClass(TestUser.class);
		ObjectMappingProvider provider = ObjectMappingProvider.newInstance();
		provider.setMappingForKeyPath("test_user", mapping);
		
		Map<String, ObjectMapping> map = provider.keyPathToMappingsMap();
		assertTrue(map instanceof Map);
		assertEquals(map.get("test_user"), mapping);
	}
	
	@After
	public void tearDown() throws Exception {
	}


}
