package upb.ida.intent;

import upb.ida.intent.executor.*;
import upb.ida.intent.model.Intent;

public class IntentExecutorFactoryHelper {

	private IntentExecutorFactoryHelper() {

	}

	// IntentExecutors are initialized by this method. For making the executors work based on
	// external configuration, this method should be updated.
	public static IntentExecutor getExecutorFor(Intent intentClass) {

		IntentExecutor executor;

		switch (intentClass){
			case GREETING:
				executor = new GreetingExecutor();
				break;
			case BAR:
				executor = new BarChartExecutor();
				break;
			case FORCE_DIRECTED_GRAPH:
				executor = new ForceDirectedGraphExecutor();
				break;
			case GEO_SPATIAL:
				executor = new GeoSpatialExecutor();
				break;
			case LOAD_DATASET:
				executor = new LoadDatasetExecutor();
				break;
			case ONTOLOGY_EXPLORER:
				executor = new OntologyExplorerExecutor();
				break;
			case SOLDIER_CAREER_TIMELINE:
				executor = new SoldierCareerTimelineExecutor();
				break;
			default:
				executor = new UnknownExecutor();
				break;
		}

		return executor;
	}
}
