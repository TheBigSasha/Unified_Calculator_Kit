package Uckit;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
/**
 * Directed Graph Test - Dijkstra Shortest Path
 * 
 * @author /u/Philboyd_Studge
 */
public class DiGraphTest
{
	public static void main(String[] args)
	{

		// create graph
		DiGraph graph = new DiGraph();

		// add a bunch of edges
		graph.add("SACRAMENTO", "PHOENIX", 150);
		graph.add("PHOENIX", "SACRAMENTO", 135);
		graph.add("PHOENIX", "SLC", 120);
		graph.add("SLC", "SACRAMENTO", 175);
		graph.add("SACRAMENTO", "PHOENIX", 160); // edge already exists!!!
		graph.add("SACRAMENTO", "PORTLAND", 90);
		graph.add("PORTLAND", "SLC", 185);
		graph.add("OAKLAND", "SACRAMENTO", 45);
		graph.add("PORTLAND", "OAKLAND", 100);
		graph.add("SLC", "OAKLAND", 150);
		graph.add("LAX","OAKLAND", 75);
		graph.add("SLC", "LAS VEGAS", 100);
		graph.add("LAS VEGAS", "CHICAGO", 250);

		System.out.println("Graph is connected: " + graph.DepthFirstSearch());
		System.out.println("Connected from LAX:" + graph.BreadthFirstSearch("LAX"));
		System.out.println();

		System.out.println(graph);
		System.out.println(graph.edgesToString());

		System.out.println("LAX to PORTLAND");
		List<String> path = graph.getPath("LAX", "PORTLAND");
		for (String each : path) 
			System.out.println(each);

		System.out.println("\nSLC to PHOENIX");
		path = graph.getPath("SLC", "PHOENIX");
		for (String each : path) 
			System.out.println(each);

		System.out.println("Adding Edge Las Vegas to Phoenix at cost $120");
		graph.add("LAS VEGAS", "PHOENIX", 120);

		System.out.println("\nSLC to PHOENIX");
		path = graph.getPath("SLC", "PHOENIX");
		for (String each : path) 
			System.out.println(each);

		System.out.println("\nSACRAMENTO to LAX");
		path = graph.getPath("SACRAMENTO", "LAX");
		for (String each : path) 
			System.out.println(each);

		System.out.println("\nSACRAMENTO to CHICAGO");
		path = graph.getPath("SACRAMENTO", "CHICAGO");
		for (String each : path) 
			System.out.println(each);
	}
}