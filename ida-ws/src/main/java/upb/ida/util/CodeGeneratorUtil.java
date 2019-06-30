package upb.ida.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import librarian.model.Scrape;
import librarian.generator.Generator;

@Component
@Scope("singleton")
public class CodeGeneratorUtil {
	@Autowired
	public Scrape scrape;
	
	public List<Long> performClustering(String algoName, List<List<Double>> data, HashMap<String, Object> params) {
		Generator generator = Generator.from(scrape);
		
		String initDesc = "" + 
				"[{:type :call-result\n" + 
				"  :position 1" + 
				"  :datatype [{:type :basetype\n" + 
				"              :name \"string\"}\n" + 
				"             {:type :semantic-type\n" + 
				"              :key \"name\"\n" + 
				"              :value \"n_clusters\"}]}\n" + 
				" {:type :call-result\n" + 
				"  :position 0" + 
				"  :datatype [{:type :role-type\n" + 
				"              :id :dataset}]}\n" + 
				" {:type :call\n" + 
				"  :callable {:type :function\n" + 
				"             :placeholder true\n" + 
				"             :namespace/_member {:type :namespace\n" + 
				"                                 :name \"sklearn.cluster\"}\n" + 
				"             :name \"k_means\"}\n" + 
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
			PersistentVector dataVec = PersistentVector.create(
					data.stream()
					.map(e -> PersistentVector.create(e))
					.collect(Collectors.toList()));
			
			IFn solver = generator.searchSolver(initDesc, 100);
			PersistentVector res = (PersistentVector) solver.invoke(dataVec, params.get("n_clusters"));
			@SuppressWarnings("unchecked")
			List<Long> listRes = new ArrayList<Long>(res);
			
			System.out.println(listRes);
			
			return listRes;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
