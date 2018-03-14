/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.technologymapping.data;

import org.cellocad.common.CObject;
import org.cellocad.common.CObjectCollection;
import org.cellocad.common.Utils;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 6, 2018
 *
 */
public class Gate extends CObject{

	private CObjectCollection<Part> parts;
	private ResponseFunction<?> responseFunction;
	private String group;

	public Gate() {
		super();
		init();
	}

	private void init() {
		parts = new CObjectCollection<Part>();
		responseFunction = null;
	}
	
	/**
	 * @return the parts
	 */
	public CObjectCollection<Part> getParts() {
		return parts;
	}

	/**
	 * @param parts the parts to set
	 */
	public void setParts(CObjectCollection<Part> parts) {
		this.parts = parts;
	}
	
	/**
	 * @return the responseFunction
	 */
	public ResponseFunction<?> getResponseFunction() {
		return responseFunction;
	}

	/**
	 * @param responseFunction the responseFunction to set
	 */
	public void setResponseFunction(ResponseFunction<?> responseFunction) {
		this.responseFunction = responseFunction;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parts == null) ? 0 : parts.hashCode());
		result = prime * result + ((responseFunction == null) ? 0 : responseFunction.hashCode());
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
		if (!(obj instanceof Gate)) {
			return false;
		}
		Gate other = (Gate) obj;
		if (parts == null) {
			if (other.parts != null)
				return false;
		} else if (!parts.equals(other.parts))
			return false;
		if (responseFunction == null) {
			if (other.responseFunction != null)
				return false;
		} else if (!responseFunction.equals(other.responseFunction))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// reponse function
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "responseFunction = ";
		indentStr = indentStr + Utils.getNewLine();
		indentStr = indentStr + this.getResponseFunction().toString();
		indentStr = indentStr + ",";
		indentStr = Utils.addIndent(2, indentStr);
		rtn = rtn + indentStr;
		rtn = rtn + Utils.getNewLine();
		// parts
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "parts = [";
		indentStr = "";
		if (this.getParts().size() > 0) {
			for (Part p : this.getParts()) {
				indentStr = indentStr + Utils.getNewLine();
				indentStr = indentStr + p.toString();
				indentStr = indentStr + ",";
			}
			indentStr = Utils.addIndent(3, indentStr);
			rtn = rtn + indentStr;
			rtn = rtn + Utils.getNewLine();
			rtn = rtn + Utils.getTabCharacter();
			rtn = rtn + Utils.getTabCharacter();
		}
		rtn = rtn + "]";
		rtn = rtn + Utils.getNewLine();
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		indentStr = super.toString();
		indentStr = Utils.addIndent(1, indentStr);
		rtn = rtn + indentStr;
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}

}
