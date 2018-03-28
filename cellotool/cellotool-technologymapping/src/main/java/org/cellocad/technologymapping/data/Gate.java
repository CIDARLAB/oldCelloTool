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
// include regulable promoter member?
public class Gate extends CObject{

	private CObjectCollection<Part> parts;
	private ResponseFunction<?> responseFunction;
	private String group;
	private Part promoter;
	private Toxicity toxicity;
	private Cytometry cytometry;

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

	/**
	 * @return the promoter
	 */
	public Part getPromoter() {
		return promoter;
	}

	/**
	 * @param promoter the promoter to set
	 */
	public void setPromoter(Part promoter) {
		this.promoter = promoter;
	}

	/**
	 * @return the toxicity
	 */
	public Toxicity getToxicity() {
		return toxicity;
	}

	/**
	 * @param toxicity the toxicity to set
	 */
	public void setToxicity(Toxicity toxicity) {
		this.toxicity = toxicity;
	}

	/**
	 * @return the cytometry
	 */
	public Cytometry getCytometry() {
		return cytometry;
	}

	/**
	 * @param cytometry the cytometry to set
	 */
	public void setCytometry(Cytometry cytometry) {
		this.cytometry = cytometry;
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
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((promoter == null) ? 0 : promoter.hashCode());
		result = prime * result + ((toxicity == null) ? 0 : toxicity.hashCode());
		result = prime * result + ((cytometry == null) ? 0 : cytometry.hashCode());
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
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (promoter == null) {
			if (other.promoter != null)
				return false;
		} else if (!promoter.equals(other.promoter))
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
		rtn += "[ ";
		rtn += Utils.getNewLine();
		// group
		rtn += this.getEntryToString("group", this.getGroup().toString());
		// group
		rtn += this.getEntryToString("promoter", this.getPromoter().toString());
		// reponse function
		rtn += Utils.getTabCharacter();
		rtn += "responseFunction = ";
		indentStr += Utils.getNewLine();
		indentStr += this.getResponseFunction().toString();
		indentStr += ",";
		indentStr = Utils.addIndent(2, indentStr);
		rtn += indentStr;
		rtn += Utils.getNewLine();
		// toxicity
		if (toxicity != null) {
			rtn += Utils.getTabCharacter();
			rtn += "toxicity = ";
			indentStr = Utils.getNewLine();
			indentStr += this.getToxicity().toString();
			indentStr += ",";
			indentStr = Utils.addIndent(2, indentStr);
			rtn += indentStr;
			rtn += Utils.getNewLine();
		}
		// parts
		rtn += Utils.getTabCharacter();
		rtn += "parts = ";
		rtn += Utils.getNewLine();
		rtn += Utils.getTabCharacter();
		rtn += Utils.getTabCharacter();
		rtn += "[";
		indentStr = "";
		if (this.getParts().size() > 0) {
			for (Part p : this.getParts()) {
				indentStr += Utils.getNewLine();
				indentStr += p.toString();
				indentStr += ",";
			}
			indentStr = Utils.addIndent(3, indentStr);
			rtn += indentStr;
			rtn += Utils.getNewLine();
			rtn += Utils.getTabCharacter();
			rtn += Utils.getTabCharacter();
		}
		rtn += "]";
		rtn += Utils.getNewLine();
		// toString
		rtn += Utils.getTabCharacter();
		rtn += "toString() = ";
		rtn += Utils.getNewLine();
		indentStr = super.toString();
		indentStr = Utils.addIndent(1, indentStr);
		rtn += indentStr;
		rtn += ",";
		rtn += Utils.getNewLine();
		// end
		rtn += "]";
		return rtn;
	}

}
