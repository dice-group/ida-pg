package upb.ida.constant;
/**
 * Container for all the string literals across application
 * @author Nikit
 *
 */
public interface IDALiteral {
	public static final String EXAMPLE = "this is an example constant string";
	
	//Action literals (User Interface Action)
	public static final int UIA_LOADDS = 1;
	public static final int UIA_FDG = 2;
	public static final int UIA_BG = 3;
	public static final int UIA_CLUSTER = 4;
	public static final int UIA_DTTABLE = 5;
	public static final int UIA_UPLOAD = 6;
	public static final int UIA_VENNDIAGRAM = 7;
	public static final int UIA_GSDIAGRAM = 8;

	
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

	public static final String DS_PATH = System.getProperty("user.dir") + "/uploads/";

}
