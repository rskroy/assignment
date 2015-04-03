package com.router.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.router.DataStoreBean;

/**
 * Routing Type 1
 * @author rskroy
 *
 */
public class OptimalRouting implements IRouting {
	
	private Set<String> nodes = new HashSet<String>();
	private List<LineItem> lineItems = new ArrayList<OptimalRouting.LineItem>();
	
	private void getVertices(String conveyorSystem){
		String lines[] = conveyorSystem.split("[\\r\\n]+");
		
		for(String systemLine : lines){
			LineItem item =null;
			String [] strObjects = systemLine.split(" ");
			if(strObjects.length == 3){
				// Discard if not in the format "source destination time"
				item = new LineItem(strObjects[0], strObjects[1], Double.valueOf(strObjects[2]));
				this.lineItems.add(item);
				// Initiate Map for identifying every available node
				this.nodes.add(item.source);
				this.nodes.add(item.destination);
			}
			
		}

	}
	
	public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
	    vertexQueue.add(source);
	
	    while (!vertexQueue.isEmpty()) {
	        Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
		        if (distanceThroughU < v.minDistance) {
		            vertexQueue.remove(v);
		
		            v.minDistance = distanceThroughU ;
		            v.previous = u;
		            vertexQueue.add(v);
		        }
            }
        }
    }

    private static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
        	if(path.contains(vertex)){
        		break;
        	}
        	path.add(vertex);
        }
            

        Collections.reverse(path);
        
        return path;
    }

    private Vertex getVertexFromSet(String vertexName, Set<Vertex> vertices){
    	for(Vertex vertex : vertices){
    		if(vertex.name.equals(vertexName)){
    			return vertex;
    		}
    	}
    	return null;
    }

    public static void main(String[] args) {
    	String conveyorSystem = "Ticketing A5 5\n" +
    						"A5 BaggageClaim 5\n" +
							"A5 A10 4\n" +
							"A5 A1 6\n" +
							"A1 A2 1\n" +
							"A2 A3 1\n" +
							"A3 A4 1\n" +
							"A10 A9 1\n" +
							"A9 A8 1\n" +
							"A8 A7 1\n" +
							"A7 A6 1";
    	String departures = "UA10 A1 MIA 08:00\n" +
							"UA11 A1 LAX 09:00\n" +
							"UA12 A1 JFK 09:45\n" +
							"UA13 A2 JFK 08:30\n" +
							"UA14 A2 JFK 09:45\n" +
							"UA15 A2 JFK 10:00\n" +
							"UA16 A3 JFK 09:00\n" +
							"UA17 A4 MHT 09:15\n" +
							"UA18 A5 LAX 10:15";
    	String bags = "0001 Ticketing UA12\n" +
							"0002 A5 UA17\n" +
							"0003 A2 UA10\n" +
							"0004 A8 UA18\n" +
							"0005 A7 ARRIVAL";
    	
    	DataStoreBean bean = new DataStoreBean(conveyorSystem, departures, bags, 1);
    	OptimalRouting routing = new OptimalRouting();
    	routing.route(bean);
    	
	}
    
	@Override
	public void route(DataStoreBean dataBean) {
		getVertices(dataBean.getConveyorSystem());
		Set<Vertex> vertices = new HashSet<Vertex>();
		
		
		for(String vertex : this.nodes){
			vertices.add(new Vertex(vertex));
		}
		
		List<Edge> adjacencyList = null;
		for(Vertex vertex : vertices){
			adjacencyList = new ArrayList<Edge>();
			for(LineItem item : this.lineItems){
				if(item.destination.equals(vertex.name) || item.source.equals(vertex.name)){
					if(item.destination.equals(vertex.name)){
						adjacencyList.add(new Edge(getVertexFromSet(item.source, vertices), item.travelTime));
					}else{
						adjacencyList.add(new Edge(getVertexFromSet(item.destination, vertices), item.travelTime));
					}
				}
			}
			vertex.adjacencies = adjacencyList.toArray(new Edge[adjacencyList.size()]);
		}
		
		String bagLines[] = dataBean.getBags().split("[\\r\\n]+");
		String departures[] = dataBean.getDepartures().split("[\\r\\n]+");
		String [] baggage = null;
		String [] currentDeparture = null;
		for(String bagLine : bagLines){
			baggage = bagLine.split(" ");
			if(baggage.length == 3){
				for(String departure : departures){
					currentDeparture = departure.split(" ");
					if(currentDeparture.length == 4){
						String flightId = baggage[2];
						if(baggage[2].equals("ARRIVAL")){
							flightId = "BaggageClaim";
							computePaths(getVertexFromSet(baggage[1], vertices)); // run Dijkstra
					        System.out.println(baggage[0] + " - " +getShortestPathTo(getVertexFromSet(flightId, vertices)) + ": "
					        		+ getVertexFromSet(currentDeparture[1], vertices).minDistance);
					        break;
						}
						if(baggage[2].equals(currentDeparture[0])){
							computePaths(getVertexFromSet(baggage[1], vertices)); // run Dijkstra
					        System.out.println(baggage[0] + " - " +getShortestPathTo(getVertexFromSet(currentDeparture[1], vertices)) + ": "
					        		+ getVertexFromSet(currentDeparture[1], vertices).minDistance);
						}
					}
				}
			}
		}
	}
	
	/**
	 * DS to represent a Path
	 * @author rskroy
	 *
	 */
	class LineItem {
		private String source;
		private String destination;
		private Double travelTime;
		
		public LineItem(String source, String destination, Double travelTime){
			this.source = source;
			this.destination = destination;
			this.travelTime = travelTime;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public String getDestination() {
			return destination;
		}
		public void setDestination(String destination) {
			this.destination = destination;
		}
		public Double getTravelTime() {
			return travelTime;
		}
		public void setTravelTime(Double travelTime) {
			this.travelTime = travelTime;
		}
		
		public boolean equals(Object obj) {
			if (! (obj instanceof LineItem)) return false;
			LineItem other = (LineItem) obj;
			return this.source.equals(other.source) && this.destination.equals(other.destination) &&
					this.travelTime.equals(other.travelTime);
		}
		
		public int hashCode() {
			int hash = 13;
			hash = 7 * hash + this.source.hashCode();
			hash = 7 * hash + this.destination.hashCode();
			hash = 7 * hash + this.travelTime.hashCode();
			return hash;
		}
	}
	
}

class Vertex implements Comparable<Vertex>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() { return name; }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
    
    public boolean equals(Object obj){
    	if (! (obj instanceof Vertex)) return false;
    	Vertex other = (Vertex) obj;
		return this.name.equals(other.name);
    }

}

class Edge
{
    public final Vertex target;
    public final double weight;
    public Edge(Vertex argTarget, double argWeight)
    { target = argTarget; weight = argWeight; }
}
