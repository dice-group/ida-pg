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
    private static String prefixes = "@prefix : <https://www.uni-paderborn.de/ontology/> .\n" +
        "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
        "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
        "@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" +
        "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
        "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
        "@prefix dc: <http://purl.org/dc/terms/> .\n" +
        "@base <https://www.uni-paderborn.de/ontology/> .\n" +
        "\n" +
        "<https://www.uni-paderborn.de/ontology/> rdf:type owl:Ontology  ;\n" +
        "\t\t\t\t\t\t\tdc:title \"SS Vocabulary\"@en ; \n" +
        "                            dc:description \"Vocabulary describing Concepts and Relationships of Schutzstaffel\"@en .\n\n";

    public static void main(String[] args) throws Exception{
        addPrefixes();
        StringBuilder stringBuilder = new StringBuilder(" ");
//        String[][] dienstellungArray = new FileInputStream(dienststellungFile);
//        String[][] ordenArray = converto2dArray(new FileInputStream(ordenFile));
//        String[][] ssfuehrerArray = converto2dArray(new FileInputStream(ssfuehrerFile));
//        String[][] ssrangArray = converto2dArray(new FileInputStream(ssrangFile));


//        Regiment Class
        FileInputStream fis = new FileInputStream(dienststellungFile);
        DataInputStream myInput = new DataInputStream(fis);
        List<String[]> lines = new ArrayList<String[]>();
        while ((thisLine = myInput.readLine()) != null) {
            lines.add(thisLine.split(","));
        }
        String[][] dienstellungArray = new String[lines.size()][0];
        lines.toArray(dienstellungArray);

        try{
            for (int i = 0; i < dienstellungArray.length; i++){
                if(i > 0)
                    stringBuilder.append("<https://www.uni-paderborn.de/ontology/" + dienstellungArray[i][1].replaceAll("\\s+", "") + ">" + "\n rdf:type owl:NamedIndividual, : Regiment ;\n");
                for (int j = 0; j <= 2; j++){
                    if(i > 0){
                        switch (j){
                            case 0:
                                stringBuilder.append(" :" + dienstellungArray[0][j] + " " + dienstellungArray[i][j] + " ;\n");
                                break;
                            case 1:
                                stringBuilder.append(" :" + dienstellungArray[0][j] + " " + "\"" + dienstellungArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 2:
                                stringBuilder.append(" :" + dienstellungArray[0][j] + " " + "\"" + dienstellungArray[i][j] + "\" ^^xsd:string ;\n");
                                stringBuilder.append(" rdfs:label" + " " + "\"" + dienstellungArray[i][1] + "\" @en .\n\n");
                                break;
//                            case 3:
//                                stringBuilder.append(" :" + dienstellungArray[0][j] + " " + dienstellungArray[i][j] + " ;\n");
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e){}

//        Orden Class
        fis = new FileInputStream(ordenFile);
        myInput = new DataInputStream(fis);
        lines.clear();
        while ((thisLine = myInput.readLine()) != null) {
            lines.add(thisLine.split(","));
        }
        String[][] ordenArray = new String[lines.size()][0];
        lines.toArray(ordenArray);
        try {
            for (int i = 0; i < ordenArray.length; i++) {
                if (i > 0)
                    stringBuilder.append("<https://www.uni-paderborn.de/ontology/" + ordenArray[i][1].replaceAll("\\s+", "") + ">" + "\n rdf:type owl:NamedIndividual, : Medal ;\n");
                for (int j = 0; j < 4; j++) {
                    if (i > 0) {
                        switch (j) {
                            case 0:
                                stringBuilder.append(" :" + ordenArray[0][j] + " " + ordenArray[i][j] + " ;\n");
                                break;
                            case 1:
                                stringBuilder.append(" :" + ordenArray[0][j] + " " + "\"" + ordenArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 2:
                                if (ordenArray[i][j] != null)
//                                    System.out.println("values:" + ordenArray[i][j]);
                                stringBuilder.append(" :" + ordenArray[0][j] + " " + "\"" + ordenArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 3:
                                if (ordenArray[i][j] != null)
                                    stringBuilder.append(" :" + ordenArray[0][j] + " " + "\"" + ordenArray[i][j] + "\" ^^xsd:string ;\n");
                                else
                                    stringBuilder.append(" :" + ordenArray[0][j] + " " + "\"?\" ^^xsd:string ;\n");
                                stringBuilder.append(" rdfs:label" + " " + "\"" + ordenArray[i][1] + "\" @en .\n\n");
                            default:
                                break;
                        }
                    }
                }
            }
        }catch (Exception e){}

//        SSRank Class
        fis = new FileInputStream(ssrangFile);
        myInput = new DataInputStream(fis);
        lines.clear();
        while ((thisLine = myInput.readLine()) != null) {
            lines.add(thisLine.split(","));
        }
        String[][] rankArray = new String[lines.size()][0];
        lines.toArray(rankArray);

        try {
            for (int i = 0; i < rankArray.length; i++) {
                if (i > 0)
                    stringBuilder.append("<https://www.uni-paderborn.de/ontology/" + rankArray[i][1].replaceAll("\\s+", "") + ">" + "\n rdf:type owl:NamedIndividual, : Rank ;\n");
                for (int j = 0; j <= 3; j++) {
                    if (i > 0) {
                        switch (j) {
                            case 0:
                                stringBuilder.append(" :" + rankArray[0][j] + " " + rankArray[i][j] + " ;\n");
                                break;
                            case 1:
                                stringBuilder.append(" :" + rankArray[0][j] + " " + "\"" + rankArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 2:
                                stringBuilder.append(" :" + rankArray[0][j] + " " + "\"" + rankArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 3:
                                stringBuilder.append(" :" + rankArray[0][j] + " " + rankArray[i][j] + " ;\n");
                                stringBuilder.append(" rdfs:label" + " " + "\"" + rankArray[i][1] + "\" @en .\n\n");
                            default:
                                break;
                        }
                    }
                }
            }
        }catch (Exception e){}
//        System.out.println(stringBuilder);

//        SSFuhrer Class
        fis = new FileInputStream(ssfuehrerFile);
        myInput = new DataInputStream(fis);
        lines.clear();
        while ((thisLine = myInput.readLine()) != null) {
            lines.add(thisLine.split(","));
        }
        String[][] ssfuehrerArray = new String[lines.size()][0];
        lines.toArray(ssfuehrerArray);

        try {
            for (int i = 0; i < ssfuehrerArray.length; i++) {
                if (i > 0)
                    stringBuilder.append("<https://www.uni-paderborn.de/ontology/" + ssfuehrerArray[i][1].replaceAll("\\s+", "") + ">" + "\n rdf:type owl:NamedIndividual, : Soldier ;\n");
                for (int j = 0; j <= 15; j++) {
                    if (i > 0) {
                        switch (j) {
                            case 0:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + ssfuehrerArray[i][j] + " ;\n");
                                break;
                            case 1:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + ssfuehrerArray[i][j] + " ;\n");
                                break;
                            case 2:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 3:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + ssfuehrerArray[i][j] + " ;\n");
                                break;
                            case 4:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 5:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 6:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 7:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 8:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 9:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 11:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 12:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 13:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 14:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                break;
                            case 15:
                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
                                stringBuilder.append(" rdfs:label" + " " + ssfuehrerArray[i][1] + " .\n\n");
                                break;
//                            case 16:
//                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
//                                break;
//                            case 17:
//                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
//                                break;
//                            case 18:
//                                stringBuilder.append(" :" + ssfuehrerArray[0][j] + " " + "\"" + ssfuehrerArray[i][j] + "\" ^^xsd:string ;\n");
//                                stringBuilder.append(" rdfs:label" + " " + ssfuehrerArray[i][1] + " .\n\n");
//                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }catch (Exception e){}


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
