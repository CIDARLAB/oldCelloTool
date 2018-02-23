package eugene.data;

import common.CObject;
import java.util.List;

public class Gate extends CObject{

	private List<Part> parts;

	/**
	 * @return the parts
	 */
	public List<Part> getParts() {
		return parts;
	}

	/**
	 * @param parts the parts to set
	 */
	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

}
