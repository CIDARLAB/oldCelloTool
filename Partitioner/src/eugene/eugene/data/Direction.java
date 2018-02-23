package eugene.data;

public enum Direction {
	UP("+"),
	DOWN("-");
	
	private final String direction;

	private Direction(String direction) {
		this.direction = direction;
	}
	
	public String toString() {
		return direction;
	}
}
