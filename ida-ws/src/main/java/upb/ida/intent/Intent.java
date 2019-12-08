package upb.ida.intent;

public enum Intent {

	GREETING("greeting"),
	LOAD_DATASET("load-dataset"),
	LOAD_ENTITY("load-entity"),
	BAR("bar"),
	FORCE_DIRECTED_GRAPH("force-directed"),
	GEO_SPATIAL("geo-spatial"),
	SUN_BURST("sun-burst"),
	VENN("venn"),
	CLUSTER("cluster"),
	HELP("help"),
	RESTART("restart"),
	UNKNOWN("unk"),
	EXIT("exit");

	private final String key;

	Intent(String key) {
		this.key = key;
	}

	public static Intent getForKey(String key) {
		for (Intent intent : Intent.values()) {
			if (intent.key.equalsIgnoreCase(key))
				return intent;
		}
		return UNKNOWN;
	}
}
