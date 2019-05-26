package src;

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
	private static final String resultFile = "/home/hardik/UPB/PG/ida-convertdata/result.ttl";
	private static String thisLine;

	//    file paths
	private static final String dienststellungFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_dienststellung.csv";
	private static final String ordenFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_orden.csv";
	private static final String ssfuehrerFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_ssfuehrer.csv";
	private static final String ssrangFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_ssrang.csv";
	private static final String soldierDecorationsFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_ssfuehrer_orden.csv";
	private static final String soldierRegimentsFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_ssfuehrer_dienststellung.csv";
	private static final String soldierRanksFile = "/home/hardik/UPB/PG/ida-convertdata/files/tbl_ssfuehrer_ssrang.csv";

	//    prefixes
	private static String prefixes =
		"@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
			"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
			"@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" +
			"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
			"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
			"@prefix dc: <http://purl.org/dc/terms/> .\n" +
			"@prefix regiment: <https://www.uni-paderborn.de/ontology/regiment/> .\n" +
			"@prefix ssrank: <https://www.uni-paderborn.de/ontology/ssrank/> .\n" +
			"@prefix decoration: <https://www.uni-paderborn.de/ontology/decoration/> .\n" +
			"@prefix soldier: <https://www.uni-paderborn.de/ontology/soldier/> .\n" +
			"@prefix soldier_decoration: <https://www.uni-paderborn.de/ontology/soldier_decoration/> .\n" +
			"@prefix soldier_regiment: <https://www.uni-paderborn.de/ontology/soldier_regiment/> .\n" +
			"@prefix soldier_ssrank: <https://www.uni-paderborn.de/ontology/soldier_ssrank/> .\n" +
			"@prefix : <https://www.uni-paderborn.de/ontology/> .\n" +
			"@base <https://www.uni-paderborn.de/ontology/> .\n" +
			"\n" +
			"<https://www.uni-paderborn.de/ontology/> rdf:type owl:Ontology  ;\n" +
			"\t\t\t\t\t\t\tdc:title \"SS Vocabulary\"@en ; \n" +
			"                            dc:description \"Vocabulary describing Concepts and Relationships of Schutzstaffel\"@en .\n\n";


	public static void main(String[] args) throws Exception {
		addPrefixes();
		StringBuilder stringBuilder = new StringBuilder("");

		processRegiments(stringBuilder);
		processDecorations(stringBuilder);
		processSSRanks(stringBuilder);
		processSoldiers(stringBuilder);
		processSoldierDecorations(stringBuilder);
		processSoldierRegiments(stringBuilder);
		processSoldierRanks(stringBuilder);

//        String[][] dienstellungArray = new FileInputStream(dienststellungFile);
//        String[][] ordenArray = converto2dArray(new FileInputStream(ordenFile));
//        String[][] ssfuehrerArray = converto2dArray(new FileInputStream(ssfuehrerFile));
//        String[][] ssrangArray = converto2dArray(new FileInputStream(ssrangFile));


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


	public static void addPrefixes() throws Exception {
		File file = new File(resultFile);
		if (file.exists()) {
			file.delete();
		}
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(prefixes);
		fileWriter.flush();
		fileWriter.close();
	}

	private static boolean isValidString(String in) {
		return (in != null) && !in.isEmpty();
	}

	private static StringBuilder appendStringProperty(StringBuilder builder, String name, String value, boolean last) {
		return builder.append("\t").append(name).append(" \"").append(value).append("\"^^xsd:string ").append(last ? ".\n" : ";\n");
	}

	private static StringBuilder appendDatetimeProperty(StringBuilder builder, String name, String value, boolean last) {
		return builder.append("\t").append(name).append(" \"").append(value).append("\"^^xsd:dateTime ").append(last ? ".\n" : ";\n");
	}

	private static StringBuilder appendResourceProperty(StringBuilder builder, String domainPrefix, String domain, String name, String rangePrefix, String range, boolean last, boolean leadingTab) {
		return builder.append(leadingTab ? "\t" : "")
			.append(domainPrefix).append(domain).append(" ")
			.append(name).append(" ")
			.append(rangePrefix).append(range).append(" ")
			.append(last ? ".\n" : ";\n");
	}

	private static void processRegiments(StringBuilder stringBuilder) {
		stringBuilder.append("######### REGIMENTS #########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(dienststellungFile); DataInputStream myInput = new DataInputStream(fis)) {

			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}

			String[][] dienstellungArray = new String[lines.size()][0];
			lines.toArray(dienstellungArray);

			for (int i = 1; i < dienstellungArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("regiment:").append(dienstellungArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :Regiment ;\n").
					append("\t").append("rdfs:label \"").append(dienstellungArray[i][1]).append("\" .\n");
				stringBuilder.append("\n");
				stringBuilder.append("regiment:").append(dienstellungArray[i][1].replaceAll("\\s+", "")).append("\n\trdf:type owl:NamedIndividual, :Regiment ;\n");

				for (int j = 0; j < dienstellungArray[i].length; j++) {
					switch (j) {
						case 0:
							stringBuilder.append("\t").append(":id ").append(dienstellungArray[i][j]).append(" ;\n");
							break;
						case 1:
							appendStringProperty(stringBuilder, ":name", dienstellungArray[i][j], false);
							break;
						case 2:
							if (isValidString(dienstellungArray[i][j])) {
								appendStringProperty(stringBuilder, ":abbreviation", dienstellungArray[i][j], false);
							}
							break;
						case 3:
							if (isValidString(dienstellungArray[i][j])) {
								appendStringProperty(stringBuilder, ":information", dienstellungArray[i][j], false);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append(dienstellungArray[i][1]).append("\" ;\n");
				stringBuilder.append("\t").append("owl:sameAs ").append("regiment:").append(dienstellungArray[i][0]).append(" .\n");
				stringBuilder.append("##\n\n");
			}
		} catch (Exception e) {
			System.out.println("Regiments processing failed");
			e.printStackTrace();
		}
	}


	private static void processSoldiers(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIERS ########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(ssfuehrerFile); DataInputStream myInput = new DataInputStream(fis)) {
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}
			String[][] ssfuehrerArray = new String[lines.size()][0];
			lines.toArray(ssfuehrerArray);

			for (int i = 1; i < ssfuehrerArray.length; i++) {
				// Using membership ID instead of PK for Soldier
				stringBuilder.append("##\n");
				stringBuilder.append("soldier:").append(ssfuehrerArray[i][1]).append("\n\trdf:type owl:NamedIndividual, :Soldier ;\n").
					append("\t").append("rdfs:label \"").append(ssfuehrerArray[i][7]).append(", ").append(ssfuehrerArray[i][8]).append("\" .\n");
				stringBuilder.append("\n");
				stringBuilder.append("soldier:")
					.append(ssfuehrerArray[i][7].replaceAll("\\s+", "")).append("_").append(ssfuehrerArray[i][8].replaceAll("\\s+", ""))
					.append("\n\trdf:type owl:NamedIndividual, :Soldier ;\n");


				for (int j = 0; j < ssfuehrerArray[i].length; j++) {
					String currentColumnVal = ssfuehrerArray[i][j];

					switch (j) {
						case 0:
							stringBuilder.append("\t").append(":id ").append(currentColumnVal).append(" ;\n");
							break;
						case 1:
							stringBuilder.append("\t").append(":membershipNumber ").append(currentColumnVal).append(" ;\n");
							break;
						case 2:
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":membershipType", currentColumnVal, false);
							break;
						case 3:
							if (isValidString(currentColumnVal)) {
//								System.out.println("Enroll date: " + currentColumnVal);
								String[] dateParts = currentColumnVal.split("\\.");
								String entryDate = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":enrolledOn", entryDate, false);
							}
							break;
						case 4:
//							enrollmentReason
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":enrollmentReason", currentColumnVal, false);
							break;
						case 5:
//							dischargedOn
							if (isValidString(currentColumnVal)) {
//								System.out.println("Discharge date: " + currentColumnVal);
								String[] dateParts = currentColumnVal.split("\\.");
								String dischargeDate = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":dischargedOn", dischargeDate, false);
							}
							break;
						case 6:
//							dischargeReason
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":dischargeReason", currentColumnVal, false);
							break;
						case 7:
//							firstName
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":firstName", currentColumnVal, false);
							break;
						case 8:
//							lastName
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":lastName", currentColumnVal, false);
							break;
						case 9:
//							hasTitle
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":hasTitle", currentColumnVal, false);
							break;
						case 10:
//							birthDate
							if (isValidString(currentColumnVal)) {
//								System.out.println("Birth date: " + currentColumnVal);
								String[] dateParts = currentColumnVal.split("\\.");
								String birthdate = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":birthDate", birthdate, false);
							}
							break;
						case 11:
//							birthPlace
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":birthPlace", currentColumnVal, false);
							break;
						case 12:
//							deathDate
							if (isValidString(currentColumnVal)) {
//								System.out.println("Death date: " + currentColumnVal);
								String[] dateParts = currentColumnVal.split("\\.");
								String deathDate = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":deathDate", deathDate, false);
							}
							break;
						case 13:
//							deathPlace
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":deathPlace", currentColumnVal, false);
							break;
						case 14:
//							NSDAPNumber
							stringBuilder.append("\t").append(":NSDAPNumber ").append(currentColumnVal).append(" ;\n");
							break;
						case 17:
//							information
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":information", currentColumnVal, false);
							break;
						case 18:
//							internalInformation
							if (isValidString(currentColumnVal))
								appendStringProperty(stringBuilder, ":internalInformation", currentColumnVal, false);
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append(ssfuehrerArray[i][7]).append(", ").append(ssfuehrerArray[i][8]).append("\" ;\n");
				stringBuilder.append("\t").append("owl:sameAs ").append("soldier:").append(ssfuehrerArray[i][1]).append(" .\n");
				stringBuilder.append("##\n\n");
			}


		} catch (Exception e) {
			System.out.println("Soldiers processing failed");
			e.printStackTrace();
		}
	}


	private static void processSSRanks(StringBuilder stringBuilder) {
		stringBuilder.append("########## SSRANKS ########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(ssrangFile); DataInputStream myInput = new DataInputStream(fis)) {
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}
			String[][] rankArray = new String[lines.size()][0];
			lines.toArray(rankArray);

			for (int i = 1; i < rankArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("ssrank:").append(rankArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :SSRank ;\n").
					append("\t").append("rdfs:label \"").append(rankArray[i][1]).append("\" .\n");
				stringBuilder.append("\n");
				stringBuilder.append("ssrank:").append(rankArray[i][1].replaceAll("\\s+", "")).append("\n\trdf:type owl:NamedIndividual, :SSRank ;\n");

				for (int j = 0; j < rankArray[i].length; j++) {
					switch (j) {
						case 0:
							stringBuilder.append("\t").append(":id ").append(rankArray[i][j]).append(" ;\n");
							break;
						case 1:
							appendStringProperty(stringBuilder, ":name", rankArray[i][j], false);
							break;
						case 2:
							if (isValidString(rankArray[i][j])) {
								appendStringProperty(stringBuilder, ":information", rankArray[i][j], false);
							}
							break;
						case 3:
							stringBuilder.append("\t").append(":sortation ").append(rankArray[i][j]).append(" ;\n");
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append(rankArray[i][1]).append("\" ;\n");
				stringBuilder.append("\t").append("owl:sameAs ").append("ssrank:").append(rankArray[i][0]).append(" .\n");
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("SSRanks processing failed");
			e.printStackTrace();
		}
	}


	private static void processDecorations(StringBuilder stringBuilder) {
		stringBuilder.append("########## DECORATIONS ########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(ordenFile); DataInputStream myInput = new DataInputStream(fis)) {

			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}
			String[][] ordenArray = new String[lines.size()][0];
			lines.toArray(ordenArray);

			for (int i = 1; i < ordenArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("decoration:").append(ordenArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :Decoration ;\n").
					append("\t").append("rdfs:label \"").append(ordenArray[i][1]).append("\" .\n");
				stringBuilder.append("\n");
				stringBuilder.append("decoration:").append(ordenArray[i][1].replaceAll("\\s+", "")).append("\n\trdf:type owl:NamedIndividual, :Decoration ;\n");

				for (int j = 0; j < ordenArray[i].length; j++) {
					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(ordenArray[i][j]).append(" ;\n");
							break;
						case 1:
							appendStringProperty(stringBuilder, ":name", ordenArray[i][j], false);
							break;
						case 2:
							if (isValidString(ordenArray[i][j])) {
								appendStringProperty(stringBuilder, ":abbreviation", ordenArray[i][j], false);
							}
							break;
						case 3:
							if (ordenArray[i][j] != null) {
								String[] years = ordenArray[i][j].replaceAll("[^/\\d{4}-]", "").
									replaceAll("[/-]", " ").split(" ");
								if (years.length == 2) {
									String durationStart = years[0] + "-01-01T00:00:00";
									String durationEnd = years[1] + "-01-01T00:00:00";

									appendDatetimeProperty(stringBuilder, ":durationStart", durationStart, false);
									appendDatetimeProperty(stringBuilder, ":durationEnd", durationEnd, false);

								} else {
									System.out.println("Problem parsing decoration duration for id: " + ordenArray[i][0]);
								}
							}
							break;
						case 4:
							appendStringProperty(stringBuilder, ":information", ordenArray[i][j], false);
							break;
						case 5:
							appendStringProperty(stringBuilder, ":internalInformation", ordenArray[i][j], false);
							break;

						default:
							break;
					}

				}

				stringBuilder.append("\t").append("rdfs:label \"").append(ordenArray[i][1]).append("\" ;\n");
				stringBuilder.append("\t").append("owl:sameAs ").append("decoration:").append(ordenArray[i][0]).append(" .\n");
				stringBuilder.append("##\n\n");
			}
		} catch (Exception e) {
			System.out.println("Decorations processing failed");
			e.printStackTrace();
		}
	}


	private static void processSoldierRegiments(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER REGIMENTS ########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(soldierRegimentsFile); DataInputStream myInput = new DataInputStream(fis)) {
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}
			String[][] soldierRegimentsArray = new String[lines.size()][0];
			lines.toArray(soldierRegimentsArray);

			for (int i = 1; i < soldierRegimentsArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_regiment:").append(soldierRegimentsArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :_SoldierRegimentInfo ;\n");

				for (int j = 0; j < soldierRegimentsArray[i].length; j++) {
					String currentValue = soldierRegimentsArray[i][j];

					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(currentValue).append(" ;\n");
							break;
						case 2:
							appendResourceProperty(stringBuilder, "", "", ":regimentInfo", "regiment:", currentValue, false, true);
							break;
						case 3:
							if (isValidString(currentValue)) {
								String[] dateParts = currentValue.split("\\.");
								String start = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":applicableFrom", start, false);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Regiment: ").append(soldierRegimentsArray[i][0]).append("\" .\n\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierRegimentsArray[i][1], ":hasRegiment", "soldier_regiment:", soldierRegimentsArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Regiments processing failed");
			e.printStackTrace();
		}


	}

	private static void processSoldierDecorations(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER DECORATIONS ########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(soldierDecorationsFile); DataInputStream myInput = new DataInputStream(fis)) {
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}
			String[][] soldierDecorationArray = new String[lines.size()][0];
			lines.toArray(soldierDecorationArray);

			for (int i = 1; i < soldierDecorationArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_decoration:").append(soldierDecorationArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :_SoldierDecorationInfo ;\n");

				for (int j = 0; j < soldierDecorationArray[i].length; j++) {
					String currentValue = soldierDecorationArray[i][j];

					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(currentValue).append(" ;\n");
							break;
						case 2:
							appendResourceProperty(stringBuilder, "", "", ":decorationInfo", "decoration:", currentValue, false, true);
							break;
						case 3:
							if (isValidString(currentValue)) {
								String[] dateParts = currentValue.split("\\.");
								String start = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":applicableFrom", start, false);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Decoration: ").append(soldierDecorationArray[i][0]).append("\" .\n\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierDecorationArray[i][1], ":hasDecoration", "soldier_decoration:", soldierDecorationArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Decorations processing failed");
			e.printStackTrace();
		}


	}

	private static void processSoldierRanks(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER RANKS ########################################################\n\n");

		try (FileInputStream fis = new FileInputStream(soldierRanksFile); DataInputStream myInput = new DataInputStream(fis)) {
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(","));
			}
			String[][] soldierRanksArray = new String[lines.size()][0];
			lines.toArray(soldierRanksArray);

			for (int i = 1; i < soldierRanksArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_ssrank:").append(soldierRanksArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :_SoldierSSRankInfo ;\n");

				for (int j = 0; j < soldierRanksArray[i].length; j++) {
					String currentValue = soldierRanksArray[i][j];

					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(currentValue).append(" ;\n");
							break;
						case 2:
							appendResourceProperty(stringBuilder, "", "", ":SSRankInfo", "ssrank:", currentValue, false, true);
							break;
						case 3:
							if (isValidString(currentValue)) {
								String[] dateParts = currentValue.split("\\.");
								String start = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
								appendDatetimeProperty(stringBuilder, ":applicableFrom", start, false);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Rank: ").append(soldierRanksArray[i][0]).append("\" .\n\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierRanksArray[i][1], ":hasRegiment", "soldier_ssrank:", soldierRanksArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Ranks processing failed");
			e.printStackTrace();
		}


	}


}
