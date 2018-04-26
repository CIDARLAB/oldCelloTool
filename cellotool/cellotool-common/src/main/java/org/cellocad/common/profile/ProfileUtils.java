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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author: Vincent Mirian
 *
 * @date: Nov 21, 2017
 *
 */
public class ProfileUtils {

	public static Boolean getBoolean(final JsonObject JObj, final String member) {
		Boolean rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsBoolean();
		}
		return rtn;
	}

	public static Byte getByte(final JsonObject JObj, final String member) {
		Byte rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsByte();
		}
		return rtn;
	}

	public static Character getCharacter(final JsonObject JObj, final String member) {
		Character rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsCharacter();
		}
		return rtn;
	}

	public static Short getShort(final JsonObject JObj, final String member) {
		Short rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsShort();
		}
		return rtn;
	}

	public static Integer getInteger(final JsonObject JObj, final String member) {
		Integer rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsInt();
		}
		return rtn;
	}

	public static Long getLong(final JsonObject JObj, final String member) {
		Long rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsLong();
		}
		return rtn;
	}

	public static Float getFloat(final JsonObject JObj, final String member) {
		Float rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsFloat();
		}
		return rtn;
	}

	public static Double getDouble(final JsonObject JObj, final String member) {
		Double rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsDouble();
		}
		return rtn;
	}

	public static String getString(final JsonObject JObj, final String member) {
		String rtn = null;
		JsonElement value = ProfileUtils.getJsonElement(JObj, member);
		if (value != null) {
			rtn = value.getAsString();
		}
		return rtn;
	}

	public static JsonElement getJsonElement(final JsonObject JObj, final String member) {
		JsonElement rtn = null;
		rtn = JObj.get(member);
		return rtn;
	}
}
