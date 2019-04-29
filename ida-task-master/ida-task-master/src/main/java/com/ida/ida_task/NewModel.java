//package com.ida.ida_task;
//
//import org.eclipse.rdf4j.model.Model;
//import org.eclipse.rdf4j.model.Statement;
//import org.eclipse.rdf4j.model.util.ModelBuilder;
//import org.eclipse.rdf4j.model.vocabulary.FOAF;
//import org.eclipse.rdf4j.model.vocabulary.RDF;
//
//public class NewModel {
//	public static void main(String[] args) {
//
//		// Create a new RDF model containing two statements by using a ModelBuilder
//		ModelBuilder builder = new ModelBuilder();
//		Model model = builder
//				.setNamespace("ex", "http://example.org/")
//				.subject("ex:Picasso")
//					.add(RDF.TYPE, "ex:Artist")		// Picasso is an Artist
//					.add(FOAF.FIRST_NAME, "Pablo") 	// his first name is "Pablo"
//				.build();
//
//		// To see what's in our model, let's just print it to the screen
//		for(Statement st: model) {
//			System.out.println(st);
//		}
//	}
//}









