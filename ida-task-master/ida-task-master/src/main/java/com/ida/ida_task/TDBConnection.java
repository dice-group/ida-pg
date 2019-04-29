package com.ida.ida_task;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

/**
 * Class for CRUD operations on TDB-backed Models
 */
public class TDBConnection 
{
	private Dataset ds;
	
	public TDBConnection( String path )
	{
		ds = TDBFactory.createDataset( path );
	}

	/**
	 * Load ontology models into the TDB back-end
	 * @param modelName - graph identifier
	 * @param path - path to the model
	 */
	public void loadModel( String modelName, String path )
	{
		Model model = null;
		
		ds.begin( ReadWrite.WRITE );
		try
		{
			model = ds.getNamedModel( modelName );
			FileManager.get().readModel( model, path );
			ds.commit();
		}
		finally
		{
			ds.end();
		}
	}
}
	
//	/**
//	 * Request triples from the TDB back-end
//	 * @param modelName - graph identifier
//	 * @return statements that fit the s-p-o description
//	 */
//	public List<Statement> getStatements( String modelName, String subject, String property, String object )
//	{
//		List<Statement> results = new ArrayList<Statement>();
//		
//		Model model = null;
//		
//		ds.begin( ReadWrite.READ );
//		try
//		{
//			model = ds.getNamedModel( modelName );
//			
//			Selector selector = new SimpleSelector
//								(
//									( subject != null ) ? model.createResource( subject ) : (Resource) null, 
//									( property != null ) ? model.createProperty( property ) : (Property) null,
//									( object != null ) ? model.createResource( object ) : (RDFNode) null
//								);
//			
//			StmtIterator it = model.listStatements( selector );
//			{
//				while( it.hasNext() )
//				{
//					Statement stmt = it.next(); 
//					results.add( stmt );
//				}
//			}
//			
//			ds.commit();
//		}
//		finally
//		{
//			if( model != null ) model.close();
//			ds.end();
//		}
//		
//		return results;
//	}
//	
//	/**
//	 * Add a statement to the TDB back-end for the given UserSession Model
//	 * @param modelName - graph identifier
//	 */
//	public void addStatement( String modelName, String subject, String property, String object )
//	{
//		Model model = null;
//		
//		ds.begin( ReadWrite.WRITE );
//		try
//		{
//			model = ds.getNamedModel( modelName );
//			
//			Statement stmt = model.createStatement
//							 ( 	
//								model.createResource( subject ), 
//								model.createProperty( property ), 
//								model.createResource( object ) 
//							 );
//			
//			model.add( stmt );
//			ds.commit();
//		}
//		finally
//		{
//			if( model != null ) model.close();
//			ds.end();
//		}
//	}
//	
//	/**
//	 * Remove a statement to the TDB Backend for the given UserSession Model
//	 * @param modelName - graph identifier
//	 */
//	public void removeStatement( String modelName, String subject, String property, String object )
//	{
//		Model model = null;
//		
//		ds.begin( ReadWrite.WRITE );
//		try
//		{
//			model = ds.getNamedModel( modelName );
//			
//			Statement stmt = model.createStatement
//							 ( 	
//								model.createResource( subject ), 
//								model.createProperty( property ), 
//								model.createResource( object ) 
//							 );
//					
//			model.remove( stmt );
//			ds.commit();
//		}
//		finally
//		{
//			if( model != null ) model.close();
//			ds.end();
//		}
//	}
//	
//	/**
//	 * Close the dataset
//	 */
//	public void close()
//	{
//		ds.close();
//	}
//	
//}
