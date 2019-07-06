package upb.ida.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.PersistentVector;
import librarian.model.Scrape;
import librarian.generator.Generator;

@Component
@Scope("singleton")
public class CodeGeneratorUtil {
	@Autowired
	public Scrape scrape;
	
	private String paramDescriptor(int pos, String name) {
		return "" +
				" {:type :call-result\n" + 
				"  :position " + pos + "\n" +
				"  :datatype [{:type :basetype\n" + 
				"              :name \"string\"}\n" + 
				"             {:type :semantic-type\n" + 
				"              :key \"name\"\n" + 
				"              :value \"" + name + "\"}]}\n";
	}
	@SuppressWarnings("unchecked")
	public List<Long> performClustering(String algoName, List<List<Double>> data, HashMap<String, Object> params) {
		Generator generator = Generator.from(scrape);
		
		PersistentVector dataVec = PersistentVector.create(
				data.stream()
				.map(e -> PersistentVector.create(e))
				.collect(Collectors.toList()));
		
		int i = 1;
		String paramString = "";
		ArrayList<Object> paramVals = new ArrayList<Object>();
		
		paramVals.add(dataVec);
		
		for(Entry<String, Object> e: params.entrySet()) {
			paramString += paramDescriptor(i++, e.getKey());
			paramVals.add(e.getValue());
		}
		
		String initDesc = "" + 
				"[{:type :call-result\n" + 
				"  :position 0\n" + 
				"  :datatype [{:type :role-type\n" + 
				"              :id :dataset}]}\n" + 
				paramString +
				" {:type :call\n" + 
				"  :callable {:type :function\n" + 
				"             :placeholder true\n" + 
				"             :namespace/_member {:type :namespace\n" + 
				"                                 :name \"sklearn.cluster\"}\n" + 
				"             :name \"" + algoName + "\"}\n" + 
				"  :parameter {:type :call-parameter\n" + 
				"              :datatype {:type :role-type\n" + 
				"                         :id :dataset}\n" + 
				"              :parameter {:type :parameter\n" + 
				"                          :placeholder true\n" + 
				"                          :name \"X\"}}\n" + 
				"  :result {:type :call-result\n" + 
				"           :datatype {:type :role-type\n" + 
				"                      :id :labels}\n" + 
				"           :result {:type :result\n" + 
				"                    :placeholder true\n" + 
				"                    :name \"label\"}}}\n" + 
				" {:type :call-parameter\n" + 
				"  :datatype [{:type :role-type\n" + 
				"              :id :labels}]}]";
		
		try {
			IFn solver = generator.searchSolver(initDesc, 500);
			ISeq paramValsSeq = RT.seq(paramVals);
			Object res = solver.applyTo(paramValsSeq);
			
			return (List<Long>) res;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
