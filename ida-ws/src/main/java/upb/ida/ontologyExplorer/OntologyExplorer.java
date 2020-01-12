package upb.ida.ontologyExplorer;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import upb.ida.constant.IDALiteral;
import upb.ida.util.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.*;
import java.io.*;
import upb.ida.util.FileUtil;

@Component
public class OntologyExplorer {

	@Autowired
	private FileUtil fileUtil;
	public String fetchSysFilePath(String path) {
		System.out.println("path sdfg=>"+path);
		System.out.println("getClass() sdfg=>"+getClass());
		System.out.println("getClass().getClassLoader() sdfg=>"+getClass().getClassLoader());
		System.out.println("getClass().getClassLoader().getResource(path) sdfg=>"+getClass().getClassLoader().getResource(path));
		System.out.println("ggetClass().getClassLoader().getResource(path).getFile() sdfg=>"+getClass().getClassLoader().getResource(path).getFile());


		return getClass().getClassLoader().getResource(path).getFile();
	}

	public String fetchData(String filename) throws IOException {

		System.out.println("before read =>");

		//Model model = ModelFactory.createDefaultModel();
		//String path = getClass().getClassLoader().getResource("test.ttl").getFile();
		Path currentRelativePath = Paths.get("");

		System.out.println("before readfghj 45 =>"+currentRelativePath);
		//model.read("ss-ontology.ttl", "TURTLE") ;



		//String path = fetchSysFilePath("ss-ontology.ttl");
		String path = fileUtil.fetchSysFilePath("ss-ontology.ttl");
		//System.out.println("path =>"+path);

		/*
		File file = new File(path);

		System.out.println("file =>"+file);

		model.read(String.valueOf(new File(path)));
		*/
		String inputFile="ss-ontology.ttl";
		Model model = ModelFactory.createDefaultModel();

		try
		{
			InputStream in =new  FileInputStream("/src/main/resources/ontologies/ss-ontology.ttl");
			if (in == null) {
				System.out.println("File not found");
			}
			else
			{
				System.out.println("File found"+in);
			}
			model.read(in," ");
			//model.write(System.out);
		}
		catch(Exception e)
		{
			System.out.println("exception "+e);
		}


			System.out.println("before read 56789 =>");
			System.out.println("after read =>"+model);

return "none-empty";
	}


}
