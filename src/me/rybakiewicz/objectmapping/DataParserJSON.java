//
//   DataParserJSON.java
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

import java.io.File;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;

public class DataParserJSON implements DataParser {

	public DataParserResult parseFromString(String jsonString) {
		try {
			return new DataParserResult(new ObjectMapper().readValue(jsonString, Object.class));
		} catch (Exception e) {
		}
		return new DataParserResult();
	}
	
	public DataParserResult parseFromInputStream(InputStream inputStream) {
		try {
			return new DataParserResult(new ObjectMapper().readValue(inputStream, Object.class));
		} catch (Exception e) {
		}
		return new DataParserResult();
	}
	
	public DataParserResult parseFromFile(File file) {
		try {
			return new DataParserResult(new ObjectMapper().readValue(file, Object.class));
		} catch (Exception e) {
		}
		return new DataParserResult();
	}
	
}
