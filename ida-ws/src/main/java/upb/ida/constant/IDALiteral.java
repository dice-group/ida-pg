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
	public static final int UIA_NOACTION = 0;
	public static final int UIA_ONTOLOGY = 11;


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
	public static final StringBuilder PREFIXES = new StringBuilder("prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"prefix xml: <http://www.w3.org/XML/1998/namespace>\n" +
			"prefix foaf: <http://xmlns.com/foaf/0.1/>\n" +
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
			"prefix dbo: <http://dbpedia.org/ontology/>\n" +
			"prefix time: <http://www.w3.org/2006/time#>\n" +
			"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"prefix dbr: <http://dbpedia.org/resource/>\n" +
			"prefix dc: <http://purl.org/dc/elements/1.1/>\n");
	public static final String TEST_DATASET = "test-data";
	public static final String TEST_ONTOLOGYSET = "test-ontology";
	public static final String SS_DATASET = "ssfuehrer";
	public static final String ONTOLOGY_ERROR = "Please load a dataset before.";

}
