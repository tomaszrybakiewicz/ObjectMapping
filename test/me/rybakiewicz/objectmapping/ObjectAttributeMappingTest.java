//
//   ObjectAttributeMappingTest.java
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectAttributeMappingTest {
	
	public class Fixture {
		public static final String sourceKeyPath = "full_name";
		public static final String destinationKeyPath = "fullName";
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testDefaultConstructor() {
		ObjectAttributeMapping am = new ObjectAttributeMapping();
		assertNotNull(am);
	}
	
	@Test
	public void testCreateObjectAttrubuteMapping() {
		ObjectAttributeMapping am = new ObjectAttributeMapping(Fixture.sourceKeyPath, Fixture.destinationKeyPath);
		assertEquals(Fixture.sourceKeyPath, am.sourceKeyPath);
		assertEquals(Fixture.destinationKeyPath, am.destinationKeyPath);
	}
	
	@Test
	public void testMappingForKeyPath() {
		ObjectAttributeMapping am = 
				ObjectAttributeMapping.mappingForKeyPath(Fixture.sourceKeyPath, Fixture.destinationKeyPath);
		assertEquals(Fixture.sourceKeyPath, am.sourceKeyPath);
		assertEquals(Fixture.destinationKeyPath, am.destinationKeyPath);
	}
	
	@After
	public void tearDown() throws Exception {
	}
}
