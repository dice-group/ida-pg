package upb.ida.logging;

/**
 * Logs Creation - Using Spring AOP to provide universal logging of all the
 * exceptions and method calls.
 * 
 * @author Ayaz
 *
 */

import java.io.File;
import org.apache.juli.FileHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


@Component
@Aspect
public class AspectLogger {

	static FileHandler fileHandler = new FileHandler();
	private final Logger log = Logger.getLogger(this.getClass());
	
    /**
     * Constructor of AspectLogger class
     */
	AspectLogger()
	{
		BasicConfigurator.configure();
		String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
		PropertyConfigurator.configure(log4jConfigFile);
	}
	
    /**
     * Method to log the Before or start of method
     * @param joinPoint
     */
    @Before("execution(* upb.ida..*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        // append args
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        logMessage.append("Before method");
        log.info(logMessage.toString());
    }

    /**
     * Method to log the After or end of method
     * @param joinPoint
     */
    @After("execution(* upb.ida..*.*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        // append args
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        logMessage.append("After method");
        log.info(logMessage.toString());
    }

    /**
     * Method to log the returning point of method
     * @param joinPoint
     * @param retVal
     */
    @AfterReturning(pointcut = "execution(* upb.ida..*.*(..))", returning = "retVal")
    public void logAfterReturningMethod(JoinPoint joinPoint, Object retVal) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        // append args
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        logMessage.append(" return Value :");
        logMessage.append(retVal);
        log.info(logMessage.toString());
    }

    /**
     * Method to log the exception returned by the method
     * @param joinPoint
     * @param exception
     */
    @AfterThrowing(pointcut = "execution(* upb.ida..*.*(..))", throwing = "exception")
    public void logAfterThrowingMethod(JoinPoint joinPoint, Exception exception) throws Throwable {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        // append args
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        logMessage.append("throwing exception :");
        logMessage.append(exception);
        log.info(logMessage.toString());
    }

    /**
     * Method to log the time take by the method
     * @param joinPoint
     */
    @Around("execution(* upb.ida..*.*(..))")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object retVal = joinPoint.proceed();

        stopWatch.stop();

        StringBuffer logMessage = new StringBuffer();
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        // append args
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        logMessage.append(" execution time: ");
        logMessage.append(stopWatch.getTotalTimeMillis());
        logMessage.append(" ms");
        log.info(logMessage.toString());
        return retVal;
    }
}