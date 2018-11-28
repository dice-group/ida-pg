package upb.ida.logging;

/**
 * Logs Creation - Using Spring AOP to provide universal logging of all the
 * exceptions and method calls.
 * 
 * @author Ayaz
 *
 */

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.bean.ResponseBean;
import upb.ida.constant.IDALiteral;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


@Component
@Aspect
public class AspectLogger {

	@Autowired(required=true)
	private HttpServletRequest request;
	@Autowired
	private ResponseBean response;
	private Logger requestReponseLogger;
	private Logger exceptionLogger;
	private Logger datasetResponseLogger;
	
    /**
     * Constructor of AspectLogger class
     */
	AspectLogger()
	{
		BasicConfigurator.configure();
		String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
		PropertyConfigurator.configure(log4jConfigFile);
		requestReponseLogger = Logger.getLogger("RequestResponseLogger");
		exceptionLogger = Logger.getLogger("ExceptionLogger");
		datasetResponseLogger = Logger.getLogger("DatasetResponseLogger");
	}
	
	@Pointcut("execution(* upb.ida.rest.MessageRestController*.*(..))")
	public void controller() {
	}

    /**
     * Method to log the Request to Message Rest Controller
     * @param joinPoint
     */
    @Before("controller()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append("Request - ");
        logMessage.append(request.getRemoteAddr());
        logMessage.append(" ");
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }

        logMessage.append(")");
        requestReponseLogger.info(logMessage.toString());
    }
    

    /** Method to log the response by Message Rest Controller
    * @param joinPoint
    * @param retVal
    */
   @AfterReturning(pointcut = "controller()", returning = "retVal")
   public void logAfterReturningMethod(JoinPoint  joinPoint, Object retVal) {
       StringBuffer logMessage = new StringBuffer();
       logMessage.append("Response - ");
       logMessage.append(request.getRemoteAddr());
       logMessage.append(" ");
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
       ResponseBean returnBean = (ResponseBean) retVal;
       if(returnBean.getActnCode() == IDALiteral.UIA_DTTABLE)
       {
    	   logMessage.append("Dataset");
           requestReponseLogger.info(logMessage.toString());
           datasetResponseLogger.trace(returnBean.toString());
       }
       else
       {
    	   logMessage.append(retVal.toString());
    	   requestReponseLogger.info(logMessage.toString());
       }
   }

    /**
     * Method to log the exception returned by the method
     * @param joinPoint
     * @param exception
     */
    @AfterThrowing(pointcut = "execution(* upb.ida..*.*(..))", throwing = "exception")
    public void logAfterThrowingMethod(JoinPoint joinPoint, Exception exception) throws Throwable {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append("Exception - ");
        logMessage.append(joinPoint.getTarget().getClass().getName());
        logMessage.append(".");
        logMessage.append(joinPoint.getSignature().getName());
        logMessage.append("(");
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            logMessage.append(args[i]).append(",");
        }
        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }
        logMessage.append(")");
        response.setErrCode(1);
        exceptionLogger.error(logMessage.toString(), exception);
    }
}