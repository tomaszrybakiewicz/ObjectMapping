//
//   ObjectRelationshipMappingTest.java
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectRelationshipMappingTest {

	public static class Fixtures {
		public static final String sourceKeyPath = "first_name";
		public static final String destinationKeyPath = "firstName";
		public static final ObjectMappingDefinition mapping = new ObjectMappingDefinition("user");
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateObjectRelationshipMapping() {
		ObjectRelationshipMapping relMapping = new ObjectRelationshipMapping(Fixtures.sourceKeyPath, Fixtures.destinationKeyPath, Fixtures.mapping);
		assertEquals(Fixtures.sourceKeyPath, relMapping.sourceKeyPath);
		assertEquals(Fixtures.destinationKeyPath, relMapping.destinationKeyPath);
		assertEquals(Fixtures.mapping, relMapping.mapping);
	}
	
	@Test 
	public void testMappingFromKeyPath() {
		ObjectRelationshipMapping relMapping = ObjectRelationshipMapping.mappingFromKeyPath(
				Fixtures.sourceKeyPath, Fixtures.destinationKeyPath, Fixtures.mapping);
		assertEquals(Fixtures.sourceKeyPath, relMapping.sourceKeyPath);
		assertEquals(Fixtures.destinationKeyPath, relMapping.destinationKeyPath);
		assertEquals(Fixtures.mapping, relMapping.mapping);
	}
	
	@After
	public void tearDown() throws Exception {
	}
}
