package upb.ida.intent;

import upb.ida.intent.executor.BarChartExecutor;
import upb.ida.intent.executor.ForceDirectedGraphExecutor;
import upb.ida.intent.executor.IntentExecutor;
import upb.ida.intent.executor.UnknownExecutor;
import upb.ida.intent.model.Intent;

public class IntentExecutorFactory {

	private IntentExecutorFactory() {

	}

	public static IntentExecutor getExecutorFor(Intent intentClass) {
		if (intentClass.equals(Intent.UNKNOWN))
			return new UnknownExecutor();
		else if (intentClass.equals(Intent.BAR))
			return new BarChartExecutor();
		else if (intentClass.equals(Intent.FORCE_DIRECTED_GRAPH))
			return new ForceDirectedGraphExecutor();

		return null;
	}
}
