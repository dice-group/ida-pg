/*package com.ida.ida_task;

import java.io.FileNotFoundException;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrudController {
	@RequestMapping("/create")
	public String getQuery3(@RequestBody recordclazz reco ) throws FileNotFoundException {
		
		System.out.println(reco.getUserName());
		System.out.println(reco.getPassword());
		//System.out.println(reco.getQry());
	    
        // some definitions
		String userId     =     reco.getId();
        String userName    = reco.getUserName();
        String password   = reco.getPassword();
       // String fullName     = userName + " " + familyName;
      //  String personURI    = "http://userdata/"+userName.replaceAll("\\s+","");
        
        ModelBuilder builder = new ModelBuilder();
        Model model = builder
                          .setNamespace("ex", "http://example.org/")
        		  .subject("ex:Picasso")
        		       .add(RDF.TYPE, "ex:Artist")
        		       .add(FOAF.FIRST_NAME, "Pablo")
        		  .build();
		return userName;
	}
}*/