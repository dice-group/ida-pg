package upb.ida.intent;

import upb.ida.intent.executor.*;
import upb.ida.intent.model.Intent;

public class IntentExecutorFactory {

	private IntentExecutorFactory() {

	}

	// IntentExecutors are initialized by this method. For making the executors work based on
	// external configuration, this method should be updated.
	public static IntentExecutor getExecutorFor(Intent intentClass) {

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

		return null;
	}
}
