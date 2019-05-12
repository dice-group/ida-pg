import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

//    constants
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private static final String CHARSET = "UTF-8";
    private static final String resultFile = "result.ttl";
    private static String thisLine;

//    file paths
    private static final String dienststellungFile = "files/tbl_dienststellung.csv";
    private static final String ordenFile = "files/tbl_orden.csv";
    private static final String ssfuehrerFile = "files/tbl_ssfuehrer.csv";
    private static final String ssrangFile = "files/tbl_ssrang.csv";

//    prefixes
    private static String prefixes = "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
            "@prefix owl:  <http://www.w3.org/2002/07/owl#> .\n";

    public static void main(String[] args) throws Exception{
        addPrefixes();

//        String[][] dienstellungArray = converto2dArray(new FileInputStream(dienststellungFile));
//        String[][] ordenArray = converto2dArray(new FileInputStream(ordenFile));
//        String[][] ssfuehrerArray = converto2dArray(new FileInputStream(ssfuehrerFile));
//        String[][] ssrangArray = converto2dArray(new FileInputStream(ssrangFile));


//        FileInputStream fis = new FileInputStream(dienststellungFile);
//        DataInputStream myInput = new DataInputStream(fis);
//        List<String[]> lines = new ArrayList<String[]>();
//        while ((thisLine = myInput.readLine()) != null) {
//            lines.add(thisLine.split(","));
//        }
//        convert our list to a String array.
//        String[][] array = new String[lines.size()][0];
//        lines.toArray(array);


    }

    public static String[][] converto2dArray(FileInputStream fileInputStream){
        try{
            DataInputStream myInput = new DataInputStream(fileInputStream);
            List<String[]> lines = new ArrayList<String[]>();

            while ((thisLine = myInput.readLine()) != null) {
                lines.add(thisLine.split(","));
            }
//          convert our list to a String array.
            String[][] array = new String[lines.size()][0];
            lines.toArray();
            return array;
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
        return null;
    }

    public static void addPrefixes() throws Exception{
        File file = new File(resultFile);
        if (file.exists()){
            file.delete();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(prefixes);
        fileWriter.flush();
        fileWriter.close();
    }
}
