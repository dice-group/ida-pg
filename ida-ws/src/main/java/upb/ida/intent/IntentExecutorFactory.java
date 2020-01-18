package upb.ida.intent;

import upb.ida.intent.exception.IntentException;
import upb.ida.intent.executor.*;
import upb.ida.intent.model.Intent;

public class IntentExecutorFactory {

	private IntentExecutorFactory() {

	}

	// IntentExecutors are initialized by this method. For making the executors work based on
	// external configuration, this method should be updated.
	public static IntentExecutor getExecutorFor(Intent intentClass) throws IntentException {

		if (intentClass.equals(Intent.GREETING))
			return new GreetingExecutor();
		else if (intentClass.equals(Intent.UNKNOWN))
			return new UnknownExecutor();
		else if (intentClass.equals(Intent.BAR))
			return new BarChartExecutor();
		else if (intentClass.equals(Intent.FORCE_DIRECTED_GRAPH))
			return new ForceDirectedGraphExecutor();
		else if (intentClass.equals(Intent.GEO_SPATIAL))
			return new GeoSpatialExecutor();
		else if (intentClass.equals(Intent.LOAD_DATASET))
			return new LoadDatasetExecutor();

		throw new IntentException(new Exception(String.format("No executor found for intent \"%s\"", intentClass.getKey())));
	}
}
