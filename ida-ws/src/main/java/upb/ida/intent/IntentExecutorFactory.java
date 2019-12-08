package upb.ida.intent;

public class IntentExecutorFactory {

	private IntentExecutorFactory() {

	}

	public static IntentExecutor getExecutorFor(Intent intentClass) {
		if (intentClass.equals(Intent.UNKNOWN))
			return new UnknownExecutor();
		else if (intentClass.equals(Intent.BAR))
			return new BarChartIntentExecutor();

		return null;
	}
}
