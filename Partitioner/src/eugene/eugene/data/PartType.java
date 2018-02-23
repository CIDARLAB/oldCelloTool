package eugene.data;

public enum PartType {
	PROMOTER("promoter"),
	CDS("cds"),
	TERMINATOR("terminator"),
	RBS("rbs"),
	SCAR("scar");
	
	private final String partType;

	private PartType(String partType) {
		this.partType = partType;
	}

	public String toString() {
		return partType;
	}
}
