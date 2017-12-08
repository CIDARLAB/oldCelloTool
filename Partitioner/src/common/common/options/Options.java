/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package common.options;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import common.CObject;


/**
 * @author: Vincent Mirian
 * 
 * @date: Dec 8, 2017
 *
 */
public class Options extends CObject{

	private void init() {
		this.stageValues = new HashMap<String, String>();
		this.stageArgValues = new HashMap<String, Map <String, String>>();
	}
	
	public Options(String filename) {
		init();
		Reader in = null;
		try {
			in = new FileReader(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.DEFAULT.parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (CSVRecord record : records) {
			if (record.size() < 2){
				continue;				
			}
		    String arg = record.get(0);
		    String value = record.get(1);
		    if ((arg != null) &&
		    	(value != null))
		    {
		    	// stage param
		    	if (arg.contains(".")) {
		    		StringTokenizer st = new StringTokenizer(arg,".");
		    		String stageName = st.nextToken();
		    		String argumentName = st.nextToken();
		    		Map <String, String> params = this.getStageArgValue().get(stageName);
		    		if (params == null) {
		    			params = new HashMap<String, String>();
		    			this.getStageArgValue().put(stageName, params);
		    		}
		    		params.put(argumentName, value);
		    		
		    	}
		    	// stage name
		    	else {
		    		this.getStageValue().put(arg, value);
		    	}
		    }
		}
	}

	public String getStageName(String stage) {
		String rtn = null;
		rtn = this.getStageValue().get(stage);
		return rtn;
	}
	
	public String getStageArgValueName(String stage, String arg) {
		String rtn = null;
		Map <String, String> params = this.getStageArgValue().get(stage);
		if (params != null) {
			rtn = params.get(arg);
		}
		return rtn;
	}
	
	/*
	 * Getter and Setter
	 */
	private Map <String, String> getStageValue() {
		return this.stageValues;
	}
	
	private Map <String, Map <String, String>> getStageArgValue() {
		return this.stageArgValues;
	}
	
	private Map <String, String> stageValues;
	private Map <String, Map <String, String>> stageArgValues;
}
