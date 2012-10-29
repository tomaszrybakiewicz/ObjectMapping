//
//   KeyValueObjectWrapperTest.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KeyValueObjectWrapperTest {
	
	public MyObject myObject;
	public MyCompositeObject myCompositeObject;
	public Map<String, Object> myMap;
	public List<Object> myList;
	
	@Before
	public void setUp() throws Exception {
		myObject = new MyObject();
		myObject.name = "This is some test name";
		myObject.details = "Some details goes here";
		myObject.identifier = 42213;
		myObject.created = new Date();
		
		myList = new ArrayList<Object>();
		myList.add("Fist string");
		myList.add(321);
		
		myCompositeObject = new MyCompositeObject();
		myCompositeObject.myObject = myObject;
		myCompositeObject.title = "My Composite Title";
		myCompositeObject.map = new HashMap<String, Object>();
		myCompositeObject.map.put("mapName", "My Map Name");
		myCompositeObject.list = myList;
		
		myMap = new HashMap<String, Object>();
		myMap.put("foo", "my foo value");
		myMap.put("bar", new Date());
		myMap.put("object", myObject);
		myMap.put("composite", myCompositeObject);
		
		HashMap<String,Object> nestedMap = new HashMap<String, Object>();
		nestedMap.put("age", 32);
		myMap.put("nestedMap", nestedMap);
	}

	@Test
	public void testCreateObjectWrapper() {
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(myObject);
		assertNotNull(wrapper);
		assertEquals(myObject, wrapper.getObject());
	}
	
	@Test
	public void testGetValueForKey_fromObject() {
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(myObject);
		assertEquals(myObject.name, wrapper.getValueForKey("name"));
		assertEquals(myObject.details, wrapper.getValueForKey("details"));
		assertEquals(myObject.identifier, wrapper.getValueForKey("identifier"));
		assertEquals(myObject.created, wrapper.getValueForKey("created"));
	}
	
	@Test
	public void testGetValueForKey_fromMap() {
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(myMap);
		assertEquals(myMap.get("foo"), wrapper.getValueForKey("foo"));
		assertEquals(myMap.get("bar"), wrapper.getValueForKey("bar"));
		assertEquals(myMap.get("object"), wrapper.getValueForKey("object"));
		assertEquals(myMap.get("composite"), wrapper.getValueForKey("composite"));
		assertEquals(myMap.get("nestedMap"), wrapper.getValueForKey("nestedMap"));
	}
	
	@Test
	public void testGetValueForKey_fromList() {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "Tom");
		list.add(m);
		m = new HashMap<String, Object>();
		m.put("name", "Marta");
		list.add(m);
		
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(list);
		List<?> values = (List<?>) wrapper.getValueForKey("name");
		assertEquals(2, values.size());
		assertEquals("Tom", values.get(0));
		assertEquals("Marta", values.get(1));
	}
	
	@Test
	public void testGetValueForKeyPath_fromObject() {
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(myCompositeObject);
		assertEquals(null, wrapper.getValueForKeyPath("title."));
		assertEquals(myCompositeObject.title, wrapper.getValueForKeyPath("title"));
		assertEquals(myCompositeObject.myObject.name, wrapper.getValueForKeyPath("myObject.name"));
		assertEquals(myCompositeObject.myObject.created, wrapper.getValueForKeyPath("myObject.created"));
		assertEquals(myCompositeObject.map.get("mapName"), wrapper.getValueForKeyPath("map.mapName"));
		assertEquals(null, wrapper.getValueForKeyPath("list.-1"));
		assertEquals(null, wrapper.getValueForKeyPath("list.321"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueForKeyPath_fromMap() {
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(myMap);
		assertEquals(myMap.get("foo"), wrapper.getValueForKey("foo"));
		assertEquals(myMap.get("bar"), wrapper.getValueForKey("bar"));
		assertEquals(((Map<String,Object>) myMap.get("nestedMap")).get("age"), wrapper.getValueForKeyPath("nestedMap.age"));
		assertEquals(myMap.get("composite"), wrapper.getValueForKey("composite"));
		assertEquals(myCompositeObject.myObject.name, wrapper.getValueForKeyPath("composite.myObject.name"));
		assertEquals(myObject.name, wrapper.getValueForKeyPath("object.name"));
	}
	
	@Test
	public void testShouldCallGetterFirst() {
		MyObject object = new MyObject();
		object.title = "my title";
		object.name = "some name";
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(object);
		assertEquals(object.title, wrapper.getValueForKey("title"));
		assertEquals(object.name, wrapper.getValueForKey("name"));
	}
	
	@Test
	public void testShouldCallSetterFirst() {
		MyObject object = new MyObject();
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(object);
		wrapper.setValueForKey("title", "some title");
		assertEquals("some title", object.title);
		wrapper.setValueForKey("name", "some name");
		assertEquals("some name", object.name);
	}
	
	@Test
	public void testShouldReturnListFromNestedObjects() {
		Map<String,Object> map = new DataParserJSON().parseFromString("{\"users\":[{\"user\":{\"id\":1234,\"name\":\"Tomasz Rybakiewicz\"}},{\"user\":{\"id\":321,\"name\":\"Marta Statucka\"}}]}").asMap();
		KeyValueObjectWrapper wrapper = new KeyValueObjectWrapper(map);
		List<?> obj = (List<?>) wrapper.getValueForKeyPath("users.user.id");
		assertEquals(2, obj.size());
		assertEquals(1234, obj.get(0));
		assertEquals(321, obj.get(1));
		
		obj = (List<?>) wrapper.getValueForKeyPath("users.user.name");
		assertEquals(2, obj.size());
		assertEquals("Tomasz Rybakiewicz", obj.get(0));
		
		obj = (List<?>) wrapper.getValueForKeyPath("users.user");
		assertEquals(2, obj.size());
		assertEquals("Marta Statucka", ((Map<?,?>)obj.get(1)).get("name"));
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	public class MyObject {
		public String name;
		public String details;
		public int identifier;
		public Date created;
		private String title;
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public class MyCompositeObject {
		public MyObject myObject;
		public String title;
		public Map<String, Object> map;
		public List<Object> list;
	}
}
