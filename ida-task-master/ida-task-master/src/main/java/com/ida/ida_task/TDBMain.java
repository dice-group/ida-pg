package com.ida.ida_task;

import java.util.List;

import org.apache.jena.rdf.model.Statement;

public class TDBMain 
{
	public static void main(String[] args) 
	{
		TDBConnection tdb = null;
		
		String URI = "http://tutorial-academy.com/2015/tdb#";
		
		String namedModel1 = "Model_German_Cars";
		String namedModel2 = "Model_US_Cars";
		
		String john = URI + "John";
		String mike = URI + "Mike";
		String bill = URI + "Bill";
		String owns = URI + "owns";
		
		
		tdb = new TDBConnection("tdb");
		// named Model 1
		tdb.addStatement( namedModel1, john, owns, URI + "Porsche" );
		tdb.addStatement( namedModel1, john, owns, URI + "BMW" );
		tdb.addStatement( namedModel1, mike, owns, URI + "BMW" );
		tdb.addStatement( namedModel1, bill, owns, URI + "Audi" );
		tdb.addStatement( namedModel1, bill, owns, URI + "BMW" );
		
		// named Model 2
		tdb.addStatement( namedModel2, john, owns, URI + "Chrysler" );
		tdb.addStatement( namedModel2, john, owns, URI + "Ford" );
		tdb.addStatement( namedModel2, bill, owns, URI + "Chevrolet" );
		
		// null = wildcard search. Matches everything with BMW as object!
		List<Statement> result = tdb.getStatements( namedModel1, null, null, URI + "BMW");
		System.out.println( namedModel1 + " size: " + result.size() + "\n\t" + result );
		
		// null = wildcard search. Matches everything with john as subject!
		result = tdb.getStatements( namedModel2, john, null, null);
		System.out.println( namedModel2 + " size: " + result.size() + "\n\t" + result );
		
		// remove all statements from namedModel1
		tdb.removeStatement( namedModel1, john, owns, URI + "Porsche" );
		tdb.removeStatement( namedModel1, john, owns, URI + "BMW" );
		tdb.removeStatement( namedModel1, mike, owns, URI + "BMW" );
		tdb.removeStatement( namedModel1, bill, owns, URI + "Audi" );
		tdb.removeStatement( namedModel1, bill, owns, URI + "BMW" );
		
		result = tdb.getStatements( namedModel1, john, null, null);
		System.out.println( namedModel1 + " size: " + result.size() + "\n\t" + result );
		tdb.close();

	}

}