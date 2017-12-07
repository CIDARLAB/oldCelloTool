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
package common;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 26, 2017
 *
 */
public class CObject {

	public CObject(){
		this.setName("");
	}
	
	public CObject(final String name, int type, int idx) {
		this.setName(name);
        this.setType(type);
        this.setIdx(idx);
    }
	
	public CObject(final CObject other){
		this(other.getName(), other.getType(), other.getIdx());
	}
	
	@Override
	public CObject clone(){
		CObject rtn;
		rtn = new CObject(this);
		return rtn;
	}
	
	public void setName(final String name){
		this.name = name;
	}
		
	public String getName(){
		return this.name;
	}

	public void setType(int type){
		this.type = type;
	}
		
	public int getType(){
		return this.type;
	}

	public void setIdx(int idx){
		this.idx = idx;
	}
		
	public int getIdx(){
		return this.idx;
	}

	/*
	 * is valid?
	 */
	public boolean isValid(){
		return true;
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idx;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + type;
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CObject other = (CObject) obj;
		if (idx != other.idx)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	/*
	 * toString
	 */
	protected String getEntryToString(String name, String value) {
		String rtn = "";
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + name;
		rtn = rtn + " = ";
		rtn = rtn + value;
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		return rtn;
	}
	
	protected String getEntryToString(String name, int value) {
		String rtn = "";
		rtn = rtn + this.getEntryToString(name, Integer.toString(value));
		return rtn;
	}
	
	protected String getEntryToString(String name, boolean value) {
		String rtn = "";
		rtn = rtn + this.getEntryToString(name, Boolean.toString(value));
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", name);
		// type
		rtn = rtn + this.getEntryToString("type", type);
		// idx
		rtn = rtn + this.getEntryToString("idx", idx);
		// isValid
		rtn = rtn + this.getEntryToString("isValid()", isValid());
		// className
		rtn = rtn + this.getEntryToString("getClass()", getClass().getName());
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + super.toString();
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}

	/*
	 * Members of class
	 */
	private String name;
	private int type;
	private int idx;
	
}
