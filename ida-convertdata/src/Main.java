import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

//    variables
    private static int lengthDienststellung;

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
    private static String prefixes = "@prefix ab: <http://learningsparql.com/ns/addressbook#> .\n" +
            "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
            "@prefix owl:  <http://www.w3.org/2002/07/owl#> .\n\n";

    public static void main(String[] args) throws Exception{
        addPrefixes();
        StringBuilder stringBuilder = new StringBuilder(" ");
//        String[][] dienstellungArray = new FileInputStream(dienststellungFile);
//        String[][] ordenArray = converto2dArray(new FileInputStream(ordenFile));
//        String[][] ssfuehrerArray = converto2dArray(new FileInputStream(ssfuehrerFile));
//        String[][] ssrangArray = converto2dArray(new FileInputStream(ssrangFile));




//        for (int i = 0; i <= 1000; i++){
//            for (int j = 0; j < 2; j++){
//                if(i > 0){
//                    switch (j){
//                        case 0:
//                            stringBuilder.append("ab:" + dienstellungArray[i][j]);
//                            break;
//                        case 1:
//                            stringBuilder.append(" ab:" + dienstellungArray[0][j] + "    " + "\"" + dienstellungArray[i][j] + "\" ;\n");
//                            break;
//                        case 2:
//                            stringBuilder.append(" ab:" + dienstellungArray[0][j] + "   " + "\"" + dienstellungArray[i][j] + "\" ;\n");
//                            break;
//                        case 3:
//                            if (dienstellungArray[i][j] != null)
//                                stringBuilder.append(" ab:" + dienstellungArray[0][j] + "    " + "\"" + dienstellungArray[i][j] + "\" .\n\n");
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        }

//        System.out.println(dienstellungArray[0][0]);
        FileInputStream fis = new FileInputStream(dienststellungFile);
        DataInputStream myInput = new DataInputStream(fis);
        List<String[]> lines = new ArrayList<String[]>();
        while ((thisLine = myInput.readLine()) != null) {
            lines.add(thisLine.split(","));
        }
//        convert our list to a String array.
        String[][] array = new String[lines.size()][0];
        lines.toArray(array);

//        System.out.println(array[0][0]);


        for (int i = 0; i <= 1090; i++){
            if(i > 0)
                stringBuilder.append("ab:Regiment\n rdf:type rdfs:Class ;\n rdfs:label \"Regiment\"\n");
            for (int j = 0; j < 3; j++){
                if(i > 0){
                    switch (j){
                        case 0:
                            stringBuilder.append(" ab:" + array[0][j] + " " + "\"" + array[i][j] + "\" ;\n");
                            break;
                        case 1:
                            stringBuilder.append(" ab:" + array[0][j] + " " + "\"" + array[i][j] + "\" ;\n");
                            break;
                        case 2:
//                            if(array[i][j+1] == null)
//                                stringBuilder.append(" ab:" + array[0][j] + "   " + "\"" + array[i][j] + "\" .\n");
//                            else
                                stringBuilder.append(" ab:" + array[0][j] + " " + "\"" + array[i][j] + "\" .\n\n");
                            break;
                        case 3:
                            if (array[i][j] != null)
                                stringBuilder.append(" ab:" + array[0][j] + " " + "\"" + array[i][j] + "\" .\n\n");
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        System.out.println(stringBuilder);

        BufferedWriter out = new BufferedWriter(new FileWriter(resultFile, true));
        out.write(stringBuilder.toString());
        out.close();
    }

//    public static String[][] converto2dArray(FileInputStream fileInputStream, int number) throws Exception{
//            DataInputStream myInput = new DataInputStream(fileInputStream);
//            List<String[]> lines = new ArrayList<String[]>();
//
//            while ((thisLine = myInput.readLine()) != null) {
//                lines.add(thisLine.split(","));
//            }
////          convert our list to a String array.
//            String[][] array = new String[lines.size()][0];
//            lines.toArray();
//            return array;
//    }



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
