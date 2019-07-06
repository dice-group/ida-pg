package upb.ida.constant;
/**
 * Container for all the string literals across application
 * @author Nikit
 *
 */
public interface IDALiteral {
	public static final String EXAMPLE = "this is an example constant string";
	
	//Action literals
	public static final int UIA_LOADDS = 1;
	public static final int UIA_FDG = 2;
	public static final int UIA_BG = 3;
	public static final int UIA_CLUSTER = 4;
	public static final int UIA_DTTABLE = 5;
	public static final int UIA_VENNDIAGRAM = 7;
	public static final int UIA_GSDIAGRAM = 8;
	public static final int UIA_SSBDIAGRAM = 9;
	public static final int UIA_PRMFRQDIAGRAM = 10;

	
	//Error Literals
	public static final int FAILURE_USERLIST = 15;
	public static final int FAILURE_UPDATEUSER = 16;
	public static final int FAILURE_NEWUSER = 17;
	public static final int FAILURE_EMAILSENT = 18;
	public static final int FAILURE_USEREXISTS = 19;
	public static final int FAILURE_CREDENTIALSINCORRECT = 20;
	public static final int LOGIN_REQUIRED = 21;
	public static final int FAILURE_USERNOTEXISTS = 22;
	public static final int ALREADY_LOGGEDIN = 23;
	
	
	//Rivescript literals
	public static final String RS_INSTANCE = "RSbot";
	public static final String RS_USER = "user";
	public static final String RS_DIRPATH = "./rivescript";
	public static final String RS_LOADDATA_ROUTINE = "loadData";
	
	//Response literals
	public static final String RESP_PASS_ROUTINE = "pass";
	public static final String RESP_FAIL_ROUTINE = "fail";
	
	//Property File Paths
	public static final String IDA_PROP_FILEPATH = "ida.properties";
	public static final String DSMAP_PROP_FILEPATH = "datasetmap.properties";
	public static final String ASPECTLOGGER_PROP_FILEPATH = "log4j.properties";
	
	//CSV File name Pattern
	public static final String CSV_FILE_PATTERN = ".*[cC][sS][vV]$";
	//Metadata File name Pattern
	public static final String DSMD_FILE_PATTERN = ".*_dsmd\\.[jJ][sS][oO][nN]$";

}
