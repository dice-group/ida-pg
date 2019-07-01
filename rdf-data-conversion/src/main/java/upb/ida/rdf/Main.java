package upb.ida.rdf;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	private static final String resourceRoot = ""; // CHANGE THIS PATH BEFORE RUNNING THE PROGRAM, PATH ENDS WITH SLASH
	private static final String resultFile = "files/result.ttl";
	private static final String baseUrl = "https://www.upb.de/historisches-institut/neueste-geschichte/ssdal/";
	private static final String dataBaseUrl = baseUrl + "data/";

	//    file paths
	private static final String dienststellungFile = "files/tbl_dienststellung.csv";
	private static final String ordenFile = "files/tbl_orden.csv";
	private static final String ssfuehrerFile = "files/tbl_ssfuehrer.csv";
	private static final String ssrangFile = "files/tbl_ssrang.csv";
	private static final String soldierDecorationsFile = "files/tbl_ssfuehrer_orden.csv";
	private static final String soldierRegimentsFile = "files/tbl_ssfuehrer_dienststellung.csv";
	private static final String soldierRanksFile = "files/tbl_ssfuehrer_ssrang.csv";
	private static final String literatureFile = "files/tbl_literatur.csv";
	private static final String soldierLiteratureFile = "files/tbl_ssfuehrer_literatur.csv";

	//    prefixes
	private static String prefixes =
		"@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
			"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
			"@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" +
			"@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n" +
			"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
			"@prefix dbo: <http://dbpedia.org/ontology/> .\n" +
			"@prefix time: <http://www.w3.org/2006/time#> .\n" +
			"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
			"@prefix dc: <http://purl.org/dc/terms/> .\n" +
			"@prefix dbr: <http://dbpedia.org/resource/> .\n" +
			"@prefix regiment: <" + dataBaseUrl + "regiment/> .\n" +
			"@prefix rank: <" + dataBaseUrl + "rank/> .\n" +
			"@prefix decoration: <" + dataBaseUrl + "decoration/> .\n" +
			"@prefix literature: <" + dataBaseUrl + "literature/> .\n" +
			"@prefix soldier: <" + dataBaseUrl + "soldier/> .\n" +
			"@prefix soldier_decoration: <" + dataBaseUrl + "soldier_decoration/> .\n" +
			"@prefix soldier_regiment: <" + dataBaseUrl + "soldier_regiment/> .\n" +
			"@prefix soldier_rank: <" + dataBaseUrl + "soldier_rank/> .\n" +
			"@prefix soldier_literature: <" + dataBaseUrl + "soldier_literature/> .\n" +
			"@prefix : <" + dataBaseUrl + "> .\n" +
			"@base <" + dataBaseUrl + "> .\n\n\n";


	public static void main(String[] args) throws Exception {
		Main o = new Main();
		o.addPrefixes();
		StringBuilder stringBuilder = new StringBuilder("");

		o.processRegiments(stringBuilder);
		o.processDecorations(stringBuilder);
		o.processRanks(stringBuilder);
		o.processSoldiers(stringBuilder);
		o.processSoldierDecorations(stringBuilder);
		o.processSoldierRegiments(stringBuilder);
		o.processSoldierRanks(stringBuilder);
		o.processLiterature(stringBuilder);
		o.processSoldierLiterature(stringBuilder);

		BufferedWriter out = new BufferedWriter(new FileWriter(resourceRoot + resultFile, true));
		out.write(stringBuilder.toString());
		out.close();
	}


	private static boolean isValidString(String in) {
		return (in != null) && !in.isEmpty();
	}

	private static StringBuilder appendStringProperty(StringBuilder builder, String name, String value, boolean last) {
		return builder.append("\t").append(name).append(" \"").append(clean(value)).append("\"^^xsd:string ").append(last ? ".\n" : ";\n");
	}

	private static StringBuilder appendDatetimeProperty(StringBuilder builder, String name, String value, boolean last, boolean withTimezone) {
		return builder.append("\t").append(name)
			.append(" \"").append(value)
			.append(withTimezone ? "\"^^xsd:dateTimeStamp " : "\"^^xsd:dateTime ")
			.append(last ? ".\n" : ";\n");
	}

	private static StringBuilder appendDateProperty(StringBuilder builder, String domainPrefix, String domain, String propertyName, String date, boolean last, boolean leadingTab) {
		return builder.append(leadingTab ? "\t" : "")
			.append(domainPrefix).append(domain).append(" ")
			.append(propertyName)
			.append(" \"").append(date).append("\"")
			.append("^^xsd:date ")
			.append(last ? ".\n" : ";\n");
	}

	private static StringBuilder appendResourceProperty(StringBuilder builder, String domainPrefix, String domain, String propertyName, String rangePrefix, String range, boolean last, boolean leadingTab) {
		return builder.append(leadingTab ? "\t" : "")
			.append(domainPrefix).append(domain).append(" ")
			.append(propertyName).append(" ")
			.append(rangePrefix).append(range).append(" ")
			.append(last ? ".\n" : ";\n");
	}

	public void addPrefixes() throws Exception {
		File file = new File(resourceRoot + resultFile);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(prefixes);
		fileWriter.flush();
		fileWriter.close();
	}

	public InputStream getFileInputStream(String filename) {
		return Main.class.getClassLoader().getResourceAsStream(filename);
	}

	private static String clean(String input) {
		boolean leadingQuote = input.startsWith("\"");
		boolean trailingQuote = input.endsWith("\"");
		int begin = 0;
		int end = input.length();

		if (leadingQuote)
			begin = begin + 1;

		if (trailingQuote)
			end = end - 1;

		return input.substring(begin, end).replace("\"", "\\\"");
	}

	private void processRegiments(StringBuilder stringBuilder) {
		stringBuilder.append("######### REGIMENTS #########################################################\n\n");
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(dienststellungFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}

			String[][] dienstellungArray = new String[lines.size()][0];
			lines.toArray(dienstellungArray);

			for (int i = 1; i < dienstellungArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("regiment:").append(dienstellungArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :Regiment ;\n");

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
				stringBuilder.append("\t").append("rdfs:label \"").append(clean(dienstellungArray[i][1])).append("\" .\n");
				stringBuilder.append("##\n\n");
			}
		} catch (Exception e) {
			System.out.println("Regiments processing failed");
			e.printStackTrace();
		}
	}

	private void processLiterature(StringBuilder stringBuilder) {
		stringBuilder.append("######### LITERATURE #########################################################\n\n");
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(literatureFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}

			String[][] literatureArray = new String[lines.size()][0];
			lines.toArray(literatureArray);

			for (int i = 1; i < literatureArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("literature:").append(literatureArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :Literature ;\n");

				for (int j = 0; j < literatureArray[i].length; j++) {
					String currentVal = literatureArray[i][j];
					switch (j) {
						case 0:
							stringBuilder.append("\t").append(":id ").append(currentVal).append(" ;\n");
							break;
						case 1:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":name", currentVal, false);
							break;
						case 2:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":author", currentVal, false);
							break;
						case 3:
							stringBuilder.append("\t").append(":publicationYear ").append(currentVal).append(" ;\n");
							break;
						case 4:
							stringBuilder.append("\t").append(":citaviId ").append(currentVal).append(" ;\n");
							break;
						case 5:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":origin", currentVal, false);
							break;
						case 6:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":signature", currentVal, false);
							break;
						case 7:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":information", currentVal, false);
							break;
						case 8:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":status", currentVal, false);
							break;
						case 9:
							if (isValidString(currentVal))
								appendStringProperty(stringBuilder, ":internalInformation", currentVal, false);
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append(clean(literatureArray[i][1])).append("\" .\n");
				stringBuilder.append("##\n\n");
			}
		} catch (Exception e) {
			System.out.println("Literature processing failed");
			e.printStackTrace();
		}
	}

	private void processSoldierProperty(StringBuilder stringBuilder, String[][] ssfuehrerArray, int i, int j) {
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
					String[] dateParts = currentColumnVal.split("\\.");
					String entryDate = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
					appendDatetimeProperty(stringBuilder, ":enrolledOn", entryDate, false, false);
				}
				break;
			case 4:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":enrollmentReason", currentColumnVal, false);
				break;
			case 5:
				if (isValidString(currentColumnVal)) {
					String[] dateParts = currentColumnVal.split("\\.");
					String dischargeDate = String.format("%s-%s-%sT00:00:00", dateParts[2], dateParts[1], dateParts[0]);
					appendDatetimeProperty(stringBuilder, ":dischargedOn", dischargeDate, false, false);
				}
				break;
			case 6:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":dischargeReason", currentColumnVal, false);
				break;
			case 7:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, "dbo:firstName", currentColumnVal, false);
				break;
			case 8:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, "dbo:lastName", currentColumnVal, false);
				break;
			case 9:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":title", currentColumnVal, false);
				break;
			case 10:
				if (isValidString(currentColumnVal)) {
					String[] dateParts = currentColumnVal.split("\\.");
					String birthdate = String.format("%s-%s-%s", dateParts[2], dateParts[1], dateParts[0]);
					appendDateProperty(stringBuilder, "", "", "dbo:birthDate", birthdate, false, true);
				}
				break;
			case 11:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":birthPlaceStr", currentColumnVal, false);
				// appendStringProperty(stringBuilder, "dbo:birthPlace", "dbo:" + currentColumnVal, false);
				break;
			case 12:
				if (isValidString(currentColumnVal)) {
					String[] dateParts = currentColumnVal.split("\\.");
					String deathDate = String.format("%s-%s-%s", dateParts[2], dateParts[1], dateParts[0]);
					appendDateProperty(stringBuilder, "", "", "dbo:deathDate", deathDate, false, true);
				}
				break;
			case 13:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":deathPlaceStr", currentColumnVal, false);
				// appendStringProperty(stringBuilder, "dbo:deathPlace", "dbo:" + currentColumnVal, false);
				break;
			case 14:
				if (isValidString(currentColumnVal))
					stringBuilder.append("\t").append(":NSDAPNumber ").append(currentColumnVal).append(" ;\n");
				break;
			case 15:
				if (isValidString(currentColumnVal)) {
					boolean truthValue = Boolean.parseBoolean(currentColumnVal);
					stringBuilder.append("\t").append(":DALVerified ").append(truthValue).append(" ;\n");
				}
				break;
			case 16:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":divergentDAL", currentColumnVal, false);
				break;
			case 17:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":information", currentColumnVal, false);
				break;
			case 18:
				if (isValidString(currentColumnVal))
					appendStringProperty(stringBuilder, ":internalInformation", currentColumnVal, false);
				break;
			default:
				break;
		}
	}

	private void processSoldiers(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIERS ########################################################\n\n");

		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(ssfuehrerFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] ssfuehrerArray = new String[lines.size()][0];
			lines.toArray(ssfuehrerArray);

			for (int i = 1; i < ssfuehrerArray.length; i++) {
				// Using membership ID instead of PK for Soldier
				stringBuilder.append("##\n");
				stringBuilder.append("soldier:").append(ssfuehrerArray[i][1]).append("\n\trdf:type owl:NamedIndividual, :Soldier ;\n");

				for (int j = 0; j < ssfuehrerArray[i].length; j++) {
					processSoldierProperty(stringBuilder, ssfuehrerArray, i, j);
				}
				stringBuilder.append("\t").append("rdfs:label \"").append(ssfuehrerArray[i][7]).append(", ").append(ssfuehrerArray[i][8]).append("\" .\n");
				stringBuilder.append("##\n\n");
			}
		} catch (Exception e) {
			System.out.println("Soldiers processing failed");
			e.printStackTrace();
		}
	}


	private void processRanks(StringBuilder stringBuilder) {
		stringBuilder.append("########## RANKS ########################################################\n\n");

		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(ssrangFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] rankArray = new String[lines.size()][0];
			lines.toArray(rankArray);

			for (int i = 1; i < rankArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("rank:").append(rankArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :Rank ;\n");

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
				stringBuilder.append("\t").append("rdfs:label \"").append(clean(rankArray[i][1])).append("\" .\n");
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Ranks processing failed");
			e.printStackTrace();
		}
	}


	private void processDecorations(StringBuilder stringBuilder) {
		stringBuilder.append("########## DECORATIONS ########################################################\n\n");

		Map<String, String> instantTriples = new HashMap<>();
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(ordenFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] ordenArray = new String[lines.size()][0];
			lines.toArray(ordenArray);

			for (int i = 1; i < ordenArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("decoration:").append(ordenArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :Decoration ;\n");

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
									String durationStart = years[0] + "-01-01";
									String durationEnd = years[1] + "-12-31";

									String decorationStartInstantName = "decoration_start_" + ordenArray[i][0]; //id
									String decorationEndInstantName = "decoration_end_" + ordenArray[i][0]; //id
									instantTriples.put(decorationStartInstantName, durationStart);
									instantTriples.put(decorationEndInstantName, durationEnd);

									appendResourceProperty(stringBuilder, "", "", "time:hasBeginning", "decoration:", decorationStartInstantName, false, true);
									appendResourceProperty(stringBuilder, "", "", "time:hasEnd", "decoration:", decorationEndInstantName, false, true);

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

				stringBuilder.append("\t").append("rdfs:label \"").append(clean(ordenArray[i][1])).append("\" .\n");

				for (String instantName : instantTriples.keySet()) {
					String instantValue = instantTriples.get(instantName);

					stringBuilder.append("\n");
					stringBuilder.append("decoration:").append(instantName).append("\n\trdf:type owl:NamedIndividual, time:Instant ;\n");
					appendDateProperty(stringBuilder, "", "", "time:inXSDDate", instantValue, true, true);
				}
				instantTriples.clear();

				stringBuilder.append("\n");
				stringBuilder.append("##\n\n");
			}
		} catch (Exception e) {
			System.out.println("Decorations processing failed");
			e.printStackTrace();
		}
	}


	private void processSoldierRegiments(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER REGIMENTS ########################################################\n\n");

		Map<String, String> instantTriples = new HashMap<>();
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(soldierRegimentsFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] soldierRegimentsArray = new String[lines.size()][0];
			lines.toArray(soldierRegimentsArray);

			for (int i = 1; i < soldierRegimentsArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_regiment:").append(soldierRegimentsArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :SoldierRegimentInfo ;\n");

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
								String start = String.format("%s-%s-%s", dateParts[2], dateParts[1], dateParts[0]);

								String instantName = "soldier_regiment_from_" + soldierRegimentsArray[i][0]; //id
								instantTriples.put(instantName, start);

								appendResourceProperty(stringBuilder, "", "", ":applicableFrom", "soldier_regiment:", instantName, false, true);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Regiment: ").append(soldierRegimentsArray[i][0]).append("\" .\n");

				for (String instantName : instantTriples.keySet()) {
					String instantValue = instantTriples.get(instantName);

					stringBuilder.append("\n");
					stringBuilder.append("soldier_regiment:").append(instantName).append("\n\trdf:type owl:NamedIndividual, time:Instant ;\n");
					appendDateProperty(stringBuilder, "", "", "time:inXSDDate", instantValue, true, true);
				}
				instantTriples.clear();

				stringBuilder.append("\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierRegimentsArray[i][1], ":hasRegiment", "soldier_regiment:", soldierRegimentsArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Regiments processing failed");
			e.printStackTrace();
		}


	}

	private void processSoldierLiterature(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER LITERATURE ########################################################\n\n");

		Map<String, String> instantTriples = new HashMap<>();
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(soldierLiteratureFile)))) {
			List<String[]> lines = new ArrayList<String[]>();
			String thisLine;
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] soldierLiteratureArray = new String[lines.size()][0];
			lines.toArray(soldierLiteratureArray);

			for (int i = 1; i < soldierLiteratureArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_literature:").append(soldierLiteratureArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :SoldierLiteratureInfo ;\n");

				for (int j = 0; j < soldierLiteratureArray[i].length; j++) {
					String currentValue = soldierLiteratureArray[i][j];

					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(currentValue).append(" ;\n");
							break;
						case 2:
							appendResourceProperty(stringBuilder, "", "", ":hasLiterature", "literature:", currentValue, false, true);
							break;
						case 3:
							stringBuilder.append("\t").append(":originalDALId ").append(currentValue).append(" ;\n");
							break;
						case 4:
							stringBuilder.append("\t").append(":originalDALPage ").append(currentValue).append(" ;\n");
							break;
						case 5:
							if (isValidString(currentValue)) {
								String[] dateParts = currentValue.split("\\.");
								String start = String.format("%s-%s-%s", dateParts[2], dateParts[1], dateParts[0]);

								String instantName = "soldier_literature_from_" + soldierLiteratureArray[i][0]; //id
								instantTriples.put(instantName, start);

								appendResourceProperty(stringBuilder, "", "", ":applicableFrom", "soldier_literature:", instantName, false, true);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Literature: ").append(soldierLiteratureArray[i][0]).append("\" .\n");

				for (String instantName : instantTriples.keySet()) {
					String instantValue = instantTriples.get(instantName);

					stringBuilder.append("\n");
					stringBuilder.append("soldier_literature:").append(instantName).append("\n\trdf:type owl:NamedIndividual, time:Instant ;\n");
					appendDateProperty(stringBuilder, "", "", "time:inXSDDate", instantValue, true, true);
				}
				instantTriples.clear();

				stringBuilder.append("\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierLiteratureArray[i][1], ":literatureInfo", "soldier_literature:", soldierLiteratureArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Literature processing failed");
			e.printStackTrace();
		}


	}

	private void processSoldierDecorations(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER DECORATIONS ########################################################\n\n");

		Map<String, String> instantTriples = new HashMap<>();
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(soldierDecorationsFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] soldierDecorationArray = new String[lines.size()][0];
			lines.toArray(soldierDecorationArray);

			for (int i = 1; i < soldierDecorationArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_decoration:").append(soldierDecorationArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :DecorationEvent ;\n");

				for (int j = 0; j < soldierDecorationArray[i].length; j++) {
					String currentValue = soldierDecorationArray[i][j];

					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(currentValue).append(" ;\n");
							break;
						case 2:
							appendResourceProperty(stringBuilder, "", "", ":hasDecoration", "decoration:", currentValue, false, true);
							break;
						case 3:
							if (isValidString(currentValue)) {
								String[] dateParts = currentValue.split("\\.");
								String start = String.format("%s-%s-%s", dateParts[2], dateParts[1], dateParts[0]);

								String instantName = "soldier_decoration_from_" + soldierDecorationArray[i][0]; //id
								instantTriples.put(instantName, start);

								appendResourceProperty(stringBuilder, "", "", ":applicableFrom", "soldier_decoration:", instantName, false, true);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Decoration: ").append(soldierDecorationArray[i][0]).append("\" .\n");

				for (String instantName : instantTriples.keySet()) {
					String instantValue = instantTriples.get(instantName);

					stringBuilder.append("\n");
					stringBuilder.append("soldier_decoration:").append(instantName).append("\n\trdf:type owl:NamedIndividual, time:Instant ;\n");
					appendDateProperty(stringBuilder, "", "", "time:inXSDDate", instantValue, true, true);
				}
				instantTriples.clear();

				stringBuilder.append("\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierDecorationArray[i][1], ":decorationInfo", "soldier_decoration:", soldierDecorationArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Decorations processing failed");
			e.printStackTrace();
		}


	}

	private void processSoldierRanks(StringBuilder stringBuilder) {
		stringBuilder.append("########## SOLDIER RANKS ########################################################\n\n");

		Map<String, String> instantTriples = new HashMap<>();
		try (BufferedReader myInput = new BufferedReader(new InputStreamReader(getFileInputStream(soldierRanksFile)))) {
			String thisLine;
			List<String[]> lines = new ArrayList<String[]>();
			while ((thisLine = myInput.readLine()) != null) {
				lines.add(thisLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
			}
			String[][] soldierRanksArray = new String[lines.size()][0];
			lines.toArray(soldierRanksArray);

			for (int i = 1; i < soldierRanksArray.length; i++) {
				stringBuilder.append("##\n");
				stringBuilder.append("soldier_rank:").append(soldierRanksArray[i][0]).append("\n\trdf:type owl:NamedIndividual, :SoldierRankInfo ;\n");

				for (int j = 0; j < soldierRanksArray[i].length; j++) {
					String currentValue = soldierRanksArray[i][j];

					switch (j) {

						case 0:
							stringBuilder.append("\t").append(":id ").append(currentValue).append(" ;\n");
							break;
						case 2:
							appendResourceProperty(stringBuilder, "", "", ":hasRank", "rank:", currentValue, false, true);
							break;
						case 3:
							if (isValidString(currentValue)) {
								String[] dateParts = currentValue.split("\\.");
								String start = String.format("%s-%s-%s", dateParts[2], dateParts[1], dateParts[0]);

								String instantName = "soldier_rank_from_" + soldierRanksArray[i][0]; //id
								instantTriples.put(instantName, start);

								appendResourceProperty(stringBuilder, "", "", ":applicableFrom", "soldier_rank:", instantName, false, true);
							}
							break;
						default:
							break;
					}

				}
				stringBuilder.append("\t").append("rdfs:label \"").append("Soldier Rank: ").append(soldierRanksArray[i][0]).append("\" .\n");

				for (String instantName : instantTriples.keySet()) {
					String instantValue = instantTriples.get(instantName);

					stringBuilder.append("\n");
					stringBuilder.append("soldier_rank:").append(instantName).append("\n\trdf:type owl:NamedIndividual, time:Instant ;\n");
					appendDateProperty(stringBuilder, "", "", "time:inXSDDate", instantValue, true, true);
				}
				instantTriples.clear();

				stringBuilder.append("\n");
				appendResourceProperty(stringBuilder, "soldier:", soldierRanksArray[i][1], ":rankInfo", "soldier_rank:", soldierRanksArray[i][0], true, false);
				stringBuilder.append("##\n\n");
			}

		} catch (Exception e) {
			System.out.println("Soldier Ranks processing failed");
			e.printStackTrace();
		}
	}


}
