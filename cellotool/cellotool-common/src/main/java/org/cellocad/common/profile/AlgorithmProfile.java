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
package org.cellocad.common.profile;

import java.util.HashMap;
import java.util.Map;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * @author: Vincent Mirian
 *
 * @date: Oct 27, 2017
 *
 */
public class AlgorithmProfile extends ProfileObject {

	public AlgorithmProfile(final JSONObject JObj){
		super(JObj);
		if (types == null){
			AlgorithmProfile.types = new CObjectCollection<CObject>();
		}
		booleanParameters = new HashMap<String, Boolean>();
		byteParameters = new HashMap<String, Byte>();
		charParameters = new HashMap<String, Character>();
		shortParameters = new HashMap<String, Short>();
		intParameters = new HashMap<String, Integer>();
		longParameters = new HashMap<String, Long>();
		floatParameters = new HashMap<String, Float>();
		doubleParameters = new HashMap<String, Double>();
		stringParameters = new HashMap<String, String>();
		this.parse(JObj);
	}

	public String getTypeName(){
		String rtn = "";
		int type = this.getType();
		CObject tCObj = AlgorithmProfile.types.get(type);
		if (tCObj != null){
			rtn = tCObj.getName();
		}
		return rtn;
	}

	public Pair<Boolean, Boolean> getBooleanParameter(final String name){
		Boolean value = booleanParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Boolean> rtn = new Pair<Boolean, Boolean>(new Boolean(first), new Boolean(value));
		return rtn;
	}

	public Pair<Boolean, Byte> getByteParameter(final String name){
		Byte value = byteParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Byte> rtn = new Pair<Boolean, Byte>(new Boolean(first), new Byte(value));
		return rtn;
	}

	public Pair<Boolean, Character> getCharParameter(final String name){
		Character value = charParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Character> rtn = new Pair<Boolean, Character>(new Boolean(first), new Character(value));
		return rtn;
	}

	public Pair<Boolean, Short> getShortParameter(final String name){
		Short value = shortParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Short> rtn = new Pair<Boolean, Short>(new Boolean(first), new Short(value));
		return rtn;
	}

	public Pair<Boolean, Integer> getIntParameter(final String name){
		Integer value = intParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Integer> rtn = new Pair<Boolean, Integer>(new Boolean(first), new Integer(value));
		return rtn;
	}

	public Pair<Boolean, Long> getLongParameter(final String name){
		Long value = longParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Long> rtn = new Pair<Boolean, Long>(new Boolean(first), new Long(value));
		return rtn;
	}

	public Pair<Boolean, Float> getFloatParameter(final String name){
		Float value = floatParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Float> rtn = new Pair<Boolean, Float>(new Boolean(first), new Float(value));
		return rtn;
	}

	public Pair<Boolean, Double> getDoubleParameter(final String name){
		Double value = doubleParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Double> rtn = new Pair<Boolean, Double>(new Boolean(first), new Double(value));
		return rtn;
	}

	public Pair<Boolean, String> getStringParameter(final String name){
		String value = stringParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, String> rtn = new Pair<Boolean, String>(new Boolean(first), new String(value));
		return rtn;
	}

	/*
	 * Parse
	 */
	private void parseType(final JSONObject JObj){
		String type = (String) ProfileUtils.getString(JObj, "type");
		if (type == null){
			throw new RuntimeException("Type not specified for AlgorithmProfile " + this.getName() +".");
		}
		/*
		 * types contains Object with the name being a reference to the Algorithm
		 */
		CObject cObj = types.findCObjectByName(type);
		if (cObj == null){
			cObj = new CObject();
			cObj.setName(type);
			cObj.setIdx(types.size());
			types.add(cObj);
		}
		this.setType(cObj.getIdx());
	}

	private void parseParameter(final JSONObject JObj){
		//name
		String name = (String) ProfileUtils.getString(JObj, "name");
		if (name == null){
			throw new RuntimeException("Name not specified for parameter in AlgorithmProfile " + this.getName() + ".");
		}
		//type
		String type = (String) ProfileUtils.getString(JObj, "type");
		if (type == null){
			throw new RuntimeException("Type not specified for parameter " + name + ".");
		}
		// value
		Object value = ProfileUtils.getObject(JObj, "value");
		if (value == null){
			throw new RuntimeException("Value not specified for parameter " + name + ".");
		}
		switch (type) {
		case BOOLEAN:{
			Boolean data = ProfileUtils.getBoolean(JObj, "value");
			booleanParameters.put(name, data);
			break;
		}
		case BYTE:{
			Byte data = ProfileUtils.getByte(JObj, "value");
			byteParameters.put(name, data);
			break;
		}
		case CHAR:{
			Character c = ProfileUtils.getCharacter(JObj, "value");
			charParameters.put(name, c);
			break;
		}
		case SHORT:{
			Short data = ProfileUtils.getShort(JObj, "value");
			shortParameters.put(name, data);
			break;
		}
		case INT:{
			Integer data = ProfileUtils.getInteger(JObj, "value");
			intParameters.put(name, data);
			break;
		}
		case LONG:{
			Long data = ProfileUtils.getLong(JObj, "value");
			longParameters.put(name, data);
			break;
		}
		case FLOAT:{
			Float data = ProfileUtils.getFloat(JObj, "value");
			floatParameters.put(name, data);
			break;
		}
		case DOUBLE:{
			Double data = ProfileUtils.getDouble(JObj, "value");
			doubleParameters.put(name, data);
			break;
		}
		case STRING:{
			String data = ProfileUtils.getString(JObj, "value");
			stringParameters.put(name, data);
			break;
		}
		default:{
			throw new RuntimeException("Invalid type for parameter " + name + ".");
		}
		}
	}

	private void parseParameters(final JSONObject JObj){
		JSONArray jsonArr = (JSONArray) JObj.get("parameters");
		for (int i = 0; i < jsonArr.size(); i++){
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
			parseParameter(jsonObj);
		}
	}

	private void parse(final JSONObject JObj){
		// name
		// parseName(JObj);
		// type
		parseType(JObj);
		// parameters
		parseParameters(JObj);
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((booleanParameters == null) ? 0 : booleanParameters.hashCode());
		result = prime * result + ((byteParameters == null) ? 0 : byteParameters.hashCode());
		result = prime * result + ((charParameters == null) ? 0 : charParameters.hashCode());
		result = prime * result + ((doubleParameters == null) ? 0 : doubleParameters.hashCode());
		result = prime * result + ((floatParameters == null) ? 0 : floatParameters.hashCode());
		result = prime * result + ((intParameters == null) ? 0 : intParameters.hashCode());
		result = prime * result + ((longParameters == null) ? 0 : longParameters.hashCode());
		result = prime * result + ((shortParameters == null) ? 0 : shortParameters.hashCode());
		result = prime * result + ((stringParameters == null) ? 0 : stringParameters.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgorithmProfile other = (AlgorithmProfile) obj;
		if (booleanParameters == null) {
			if (other.booleanParameters != null)
				return false;
		} else if (!booleanParameters.equals(other.booleanParameters))
			return false;
		if (byteParameters == null) {
			if (other.byteParameters != null)
				return false;
		} else if (!byteParameters.equals(other.byteParameters))
			return false;
		if (charParameters == null) {
			if (other.charParameters != null)
				return false;
		} else if (!charParameters.equals(other.charParameters))
			return false;
		if (doubleParameters == null) {
			if (other.doubleParameters != null)
				return false;
		} else if (!doubleParameters.equals(other.doubleParameters))
			return false;
		if (floatParameters == null) {
			if (other.floatParameters != null)
				return false;
		} else if (!floatParameters.equals(other.floatParameters))
			return false;
		if (intParameters == null) {
			if (other.intParameters != null)
				return false;
		} else if (!intParameters.equals(other.intParameters))
			return false;
		if (longParameters == null) {
			if (other.longParameters != null)
				return false;
		} else if (!longParameters.equals(other.longParameters))
			return false;
		if (shortParameters == null) {
			if (other.shortParameters != null)
				return false;
		} else if (!shortParameters.equals(other.shortParameters))
			return false;
		if (stringParameters == null) {
			if (other.stringParameters != null)
				return false;
		} else if (!stringParameters.equals(other.stringParameters))
			return false;
		return true;
	}

	private static CObjectCollection<CObject> types = null;
	public static final String BOOLEAN = "boolean";
	public static final String BYTE = "byte";
	public static final String CHAR = "char";
	public static final String SHORT = "short";
	public static final String INT = "int";
	public static final String LONG = "long";
	public static final String FLOAT = "float";
	public static final String DOUBLE = "double";
	public static final String STRING = "string";
	//private static final String[] parameterNames = {BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING};
	private Map<String, Boolean> booleanParameters;
	private Map<String, Byte> byteParameters;
	private Map<String, Character> charParameters;
	private Map<String, Short> shortParameters;
	private Map<String, Integer> intParameters;
	private Map<String, Long> longParameters;
	private Map<String, Float> floatParameters;
	private Map<String, Double> doubleParameters;
	private Map<String, String> stringParameters;
}
