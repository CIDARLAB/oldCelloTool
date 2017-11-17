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
package common.profile;

import org.json.simple.JSONObject;

import common.CObject;
import common.Utils;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 27, 2017
 *
 */
public class ProfileObject extends CObject{

	public ProfileObject(){
		
	}
	
	protected ProfileObject(final JSONObject JObj){
		this.parseName(JObj);
	}
	
	/*
	 * Parse
	 */
	protected void parseName(final JSONObject JObj){
		// name
		String name = (String) this.getString(JObj, "name");
		if (name == null) {
	    	throw new RuntimeException("Name not specified!");
		}
		this.setName(name);
	}

	protected Boolean getBoolean(final JSONObject JObj, final String member) {
		Boolean rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isBoolean(value)
				) {
			rtn = (Boolean) value;
		}
		return rtn;
	}

	protected Byte getByte(final JSONObject JObj, final String member) {
		Byte rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
			Long temp = (Long) value;
	    	rtn = (Byte) temp.byteValue();
		}
		return rtn;
	}
	
	protected Character getCharacter(final JSONObject JObj, final String member) {
		Character rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isString(value)
				) {
	    	String data = (String) value;
	    	if (data.length() > 0)
	    		rtn = data.charAt(0);
		}
		return rtn;
	}

	protected Short getShort(final JSONObject JObj, final String member) {
		Short rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
        	Long temp = (Long) value;
        	rtn = (Short) temp.shortValue();
		}
		return rtn;
	}

	protected Integer getInteger(final JSONObject JObj, final String member) {
		Integer rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
        	Long temp = (Long) value;
        	rtn = (Integer) temp.intValue();
		}
		return rtn;
	}

	protected Long getLong(final JSONObject JObj, final String member) {
		Long rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
			rtn = (Long) value;
		}
		return rtn;
	}

	protected Float getFloat(final JSONObject JObj, final String member) {
		Float rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isDouble(value)
				) {
        	Double temp = (Double) value;
        	rtn = (Float) temp.floatValue();
		}
		return rtn;
	}

	protected Double getDouble(final JSONObject JObj, final String member) {
		Double rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isDouble(value)
				) {
        	rtn = (Double) value;
		}
		return rtn;
	}
	
	protected String getString(final JSONObject JObj, final String member) {
		String rtn = null;
		Object value = this.getObject(JObj, member);
		if (
				(value != null)
				&& Utils.isString(value)
				) {
        	rtn = (String) value;
		}
		return rtn;
	}

	protected Object getObject(final JSONObject JObj, final String member) {
		Object rtn = null;
		rtn = (Object) JObj.get(member);
		return rtn;
	}
}
