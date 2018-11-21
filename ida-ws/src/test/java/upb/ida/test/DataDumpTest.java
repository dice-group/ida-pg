package upb.ida.test;

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
	
	@Test
	public void strLenCompTest() {
		System.out.println(dataDumpUtil.getClusteringAlgoNames());
	}

}
