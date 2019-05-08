package upb.ida.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import upb.ida.Application;
import upb.ida.util.DataDumpUtil;
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {Application.class})
public class DataDumpTest {
	@Autowired
	DataDumpUtil dataDumpUtil;
	
	
	@Ignore
	public void  getClusteringAlgoNamesPosTest() {
		List<String> algonames = dataDumpUtil.getClusteringAlgoNames();
		List<String> resList = new ArrayList<>();
		resList.add("KMeans");
		resList.add("AffinityPropagation");
		resList.add("AffinityPropagation_TEST");
		resList.add("KMeans_TEST");
		System.out.println("algonames ======== "+algonames);
		assertEquals(algonames, resList);
		assertNotEquals(algonames.size(),4);
		
		//System.out.println(dataDumpUtil.getClusteringAlgoNames());
	}

	@Test
	public void  getClusteringAlgoNamesNegTest() {
		List<String> algonames = dataDumpUtil.getClusteringAlgoNames();
		List<String> resList = new ArrayList<>();
		
		resList.add("AffinityPropagation");
		resList.add("AffinityPropagation_TEST");
		resList.add("KMeans_TEST");
		assertNotEquals(algonames, resList);
		assertNotEquals(algonames.size(),3);
		
		//System.out.println(dataDumpUtil.getClusteringAlgoNames());
	}

	@Test
	public void  getClusteringAlgoNamesExtTest() {
		List<String> algonames = dataDumpUtil.getClusteringAlgoNames();
		List<String> resList = new ArrayList<>();
		resList.add("Keans");
		resList.add("AffinityPropagation");
		resList.add("AffinityPropagation_TEST");
		resList.add("KMeans_TEST");
		assertNotEquals(algonames, resList);
		assertNotEquals(algonames.size(),4);
		
		//System.out.println(dataDumpUtil.getClusteringAlgoNames());
	}
	

	@Test
	public void Âlgoparams() {
		//System.out.println(dataDumpUtil.getClusterAlgoParams("KMeans_TEST"));
	}
	
	
}
