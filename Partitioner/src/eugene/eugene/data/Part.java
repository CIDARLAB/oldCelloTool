package eugene.data;

import common.CObject;

public class Part extends CObject{

	private PartType partType;
	private String sequence;
	private Direction direction;

	/**
	 * @return the partType
	 */
	public PartType getPartType() {
		return partType;
	}

	/**
	 * @param partType the partType to set
	 */
	public void setPartType(PartType partType) {
		this.partType = partType;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
