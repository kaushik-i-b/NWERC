package staticvoid.graphs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

public class EdgeGraph implements Graph {
	private static final int estimatedEdges = 100;
	ArrayList<Node> allNodes;
	
	public EdgeGraph(int numberOfNodes) {
		this.allNodes = new ArrayList<Node>(numberOfNodes);
		for(int i = 0; i < numberOfNodes; i++) {
			this.addNode();
		}
	}
	
	public void addNode() {
		this.allNodes.add(new Node(estimatedEdges,this.allNodes.size()));
	}
	
	public int getNodeCount() {
		return this.allNodes.size();
	}
	
	public boolean hasEdge(int s, int e) {
		Node n = allNodes.get(s);
		for(int i = 0; i < n.getNumberOfEdges(); i++) {
			if(n.getEdge(i).getTargetNode().nodeID == e)
				return true;
		}
		return false;
	}
	
	public boolean createEdge(int startID, int targetID, double weight, boolean createBothWays) {
		if(startID == targetID || this.hasEdge(startID, targetID))
			return false;
		
		this.allNodes.get(startID).addEdge(new Edge(this.allNodes.get(targetID), weight));
		if(createBothWays)
			this.allNodes.get(targetID).addEdge(new Edge(this.allNodes.get(startID), weight));
		return true;
	}
	
	public ShortestWay getShortestPathDjikstra(int startID, int endID) {
		double[] distance = new double[this.getNodeCount()];
		Node[] prevNode = new Node[this.getNodeCount()];
		boolean[] visited = new boolean[this.getNodeCount()];
		DjikstraCmp cmp = new DjikstraCmp(distance);
		
		for (int i = 0; i < prevNode.length; i++) {
			distance[i] = Double.MAX_VALUE;
			prevNode[i] = null;
		}
		distance[startID] = 0;
		
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>(100, cmp);
		queue.add(startID);
		
		int currentNode = -1;
		
		while(!queue.isEmpty()) {
			currentNode = queue.poll();
			visited[currentNode] = true;
			if(currentNode == endID) {
				ArrayList<Integer> way = new ArrayList<Integer>();
				way.add(endID);
				double cost = distance[currentNode];
				while(currentNode != startID) {
					way.add(prevNode[currentNode].nodeID);
					currentNode = prevNode[currentNode].nodeID;
				}
				return new ShortestWay(way, cost);
			}
			int numEdges = allNodes.get(currentNode).getNumberOfEdges();
			for(int i = 0; i < numEdges; i++) {
				Edge currentEdge = allNodes.get(currentNode).getEdge(i);
				if(distance[currentEdge.getTargetNode().nodeID] > 
				distance[currentNode] + currentEdge.getWeight() && 
				!visited[currentEdge.getTargetNode().nodeID]) {
					distance[currentEdge.getTargetNode().nodeID] = 
							distance[currentNode] + currentEdge.getWeight(); 
					queue.add(currentEdge.targetNode.nodeID);
					prevNode[currentEdge.targetNode.nodeID] = allNodes.get(currentNode);
				}
			}
		}
		
		return null;
	}
	
	
	
	public double[][] getShortestPaths() {
		double[][] allDist = new double[this.getNodeCount()][this.getNodeCount()];
		for (int l = 0; l < allDist.length; l++) {
			for (int l2 = 0; l2 < allDist[0].length; l2++) {
				allDist[l][l2] = Double.POSITIVE_INFINITY;
			}
		}
		for (int l = 0; l < allDist.length; l++) {
			allDist[l][l] = 0;
		}
		
		for (int l = 0; l < allDist.length; l++) {
			for (int j = 0; j < allNodes.get(l).getNumberOfEdges(); j++) {
				allDist[l][allNodes.get(l).getEdge(j).targetNode.nodeID] =
						allNodes.get(l).getEdge(j).weight;
			}
		}

		for (int k = 0; k < allDist.length; k++) {
			for (int i = 0; i < allDist.length; i++) {
				for (int j = 0; j < allDist.length; j++) {
					if (allDist[i][k] + allDist[k][j] < allDist[i][j]) { 
		            allDist[i][j] =  allDist[i][k] + allDist[k][j];
					}
				}
			}
		}
		return allDist;
	}
	
	

	
	public static class DjikstraCmp implements Comparator<Integer> {
		double[] distance;
		public DjikstraCmp(double[] distance) {
			this.distance = distance;
		}

		@Override
		public int compare(Integer a1, Integer a2) {
			return Double.compare(distance[a1.intValue()], distance[a2.intValue()]);
		}
		
	}
	
	public static class ShortestWay {
		public ArrayList<Integer> nodes;
		public double totalCost;
		
		public ShortestWay(ArrayList<Integer> theWay, double theCost) {
			this.nodes = theWay;
			this.totalCost = theCost;
		}
	}
	
	protected static class Edge {
		double weight;
		Node targetNode;
		
		public Edge(Node target, double weight) {
			this.weight = weight;
			this.targetNode = target;
		}
		
		public double getWeight() {
			return this.weight;
		}
		
		public Node getTargetNode() {
			return this.targetNode;
		}
		
		public void setTargetNode(Node newTarget) {
			this.targetNode = newTarget;
		}
	}
	
	protected static class Node {
		private ArrayList<Edge> edges;
		public final int nodeID;
		
		public Node(int initialEdges, int nodeID) {
			this.nodeID = nodeID;
			this.edges = new ArrayList<Edge>(initialEdges);
		}
		
		public Edge getEdge(int index) {
			return this.edges.get(index);
		}
		
		public int getNumberOfEdges() {
			return this.edges.size();
		}
		
		public void addEdge(Edge e) {
			this.edges.add(e);
		}
	}
	/*
	public static void main(String[] args) {
		EdgeGraph g = new EdgeGraph(10);
		g.addNode();
		g.addNode();
		g.addNode();
		g.addNode();
		g.addNode();
		g.addNode();
		g.addNode();
		g.addNode();
		
		g.createEdge(0, 1, 140, false);
		g.createEdge(1, 2, 10, false);
		g.createEdge(2, 3, 1530, true);
		g.createEdge(3, 5, 102, false);
		g.createEdge(0, 4, 10163, true);
		g.createEdge(4, 5, 104, false);
		
		g = getGraph(1000000, 3000000, 40);
		long start = System.nanoTime();
		ShortestWay w = g.getShortestPathDjikstra(0, 5);
		long end = System.nanoTime();
		System.out.println(w.totalCost);
		for (Iterator<Integer> it = w.nodes.iterator(); it.hasNext();) {
			int type = (int) it.next();
			System.out.println(type);
		}
		System.out.println((end - start)/1e9);
		
	}
	
	public static EdgeGraph getGraph(int nodes, int edges, double maxWeight) {
		EdgeGraph g = new EdgeGraph(nodes);
		Random r = new Random();
		for (int i = 0; i < nodes; i++) {
			g.addNode();
		}
		for (int i = 0; i < edges; i++) {
			while(!g.createEdge(r.nextInt(nodes),r.nextInt(nodes), r.nextDouble() * maxWeight, false));
		}
		return g;
	}
	*/
}
