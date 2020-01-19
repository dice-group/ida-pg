package upb.ida.intent.model;

public enum Intent {

	GREETING("greeting"),
	LOAD_DATASET("load-dataset"),
	BAR("bar"),
	FORCE_DIRECTED_GRAPH("force-directed"),
	SOLDIER_CAREER_TIMELINE("soldier-timeline"),
	ONTOLOGY_EXPLORER("onto-explorer"),
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

	public String getKey() {
		return key;
	}
}
