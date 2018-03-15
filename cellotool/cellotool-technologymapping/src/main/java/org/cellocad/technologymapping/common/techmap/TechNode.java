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
package org.cellocad.technologymapping.common.techmap;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.common.CObject;
import org.cellocad.common.Utils;
import org.cellocad.technologymapping.data.Gate;

/**
 * @author: Timothy Jones
 * 
 * @date: Mar 6, 2018
 *
 */
public class TechNode extends CObject{

	private Gate gate;
	private List<Boolean> logic;
	private List<Double> activity;

	public TechNode() {
		super();
		init();
	}
	
	public TechNode(final TechNode other){
		super(other);
		this.setActivity(new ArrayList<>(other.getActivity()));
		this.setLogic(new ArrayList<>(other.getLogic()));
		if (other.getGate() != null) {
			this.setGate(other.getGate());
		}
	}

	private void init() {
		logic = new ArrayList<Boolean>();
		activity = new ArrayList<Double>();
	}

	public enum TechNodeType {
	    NONE, SOURCE, SINK
	}

	public void setType(final TechNodeType nodeType) {
		this.setType(nodeType.ordinal());
	}
	
	public TechNodeType getTechNodeType() {
		return TechNodeType.values()[this.getType()];
	}
	
	/**
	 * @return the gate
	 */
	public Gate getGate() {
		return gate;
	}

	/**
	 * @param gate the gate to set
	 */
	public void setGate(Gate gate) {
		this.gate = gate;
	}

	/**
	 * @return the logic
	 */
	public List<Boolean> getLogic() {
		return logic;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(List<Boolean> logic) {
		this.logic = logic;
	}

	/**
	 * @return the activity
	 */
	public List<Double> getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(List<Double> activity) {
		this.activity = activity;
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((gate == null) ? 0 : gate.hashCode());
		result = prime * result + ((logic == null) ? 0 : logic.hashCode());
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
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
		TechNode other = (TechNode) obj;
		if (gate == null) {
			if (other.gate != null)
				return false;
		} else if (!gate.equals(other.gate))
			return false;
		if (logic == null) {
			if (other.logic != null)
				return false;
		} else if (!logic.equals(other.logic))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// logic
		rtn = rtn + this.getEntryToString("logic", this.getLogic().toString());
		// activity
		rtn = rtn + this.getEntryToString("activity", this.getActivity().toString());
		// gate
		if (this.getGate() != null) {
			indentStr = this.getGate().toString();
			indentStr = Utils.addIndent(1, indentStr);
			rtn = rtn + this.getEntryToString("gate", indentStr);
		} else {
			rtn = rtn + Utils.getTabCharacter();
			rtn = rtn + "gate = null,";
			rtn = rtn + Utils.getNewLine();
		}
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
