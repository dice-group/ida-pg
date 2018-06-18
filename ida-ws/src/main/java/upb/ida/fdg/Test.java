package upb.ida.fdg;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class Test {

	public static void main(String[] args) {
		FDG_Node node1 = new FDG_Node(1, "Paris", 1);
		FDG_Node node2 = new FDG_Node(2, "London", 2);
		FDG_Node node3 = new FDG_Node(3, "Berlin", 2);
		FDG_Node node4 = new FDG_Node(4, "Tokyo", 3);

		FDG_Triple triple1 = new FDG_Triple(1, node1, node2, 1D);
		FDG_Triple triple2 = new FDG_Triple(2, node1, node3, 2D);
		FDG_Triple triple3 = new FDG_Triple(3, node2, node4, 2D);
		FDG_Triple triple4 = new FDG_Triple(4, node3, node4, 5D);

		List<FDG_Triple> triples = new ArrayList<>();

		triples.add(triple1);
		triples.add(triple2);
		triples.add(triple3);
		triples.add(triple4);
		
		//JsonObject fdgData = FDG_Util.getFDGData(triples);
		
		//System.out.println(fdgData);

	}

}
