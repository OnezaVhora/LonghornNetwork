import java.util.*;

/**
 * StudentGraph class represents a graph of UniversityStudent objects.
 * It contains methods to add edges, get neighbors, and display the graph.
 * It is used to model the relationships between students based on their connection strength.
 * The graph is undirected, meaning that if student A is connected to student B,
 */
public class StudentGraph {

    /**
     * Edge class represents an edge in the graph.
     * It contains a reference to the neighbor student and the weight of the edge.
     */
    public static class Edge{
        public UniversityStudent neighbor;
        public int weight;

        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }

        @Override
        public String toString(){
            return "(" + neighbor.getName() + ", " + weight + ")";  
        }
    }

    private Map<UniversityStudent, List<Edge>> adjacencyList;

    /**
     * Constructor for the StudentGraph class.
     * It initializes the adjacency list and adds edges based on the connection strength between students.
     * @param students List of UniversityStudent objects to be added to the graph.
     */
    public StudentGraph(List<UniversityStudent> students) {
        adjacencyList = new HashMap<>();
        //intialize nodes
        for (UniversityStudent s : students) {
            adjacencyList.put(s, new ArrayList<>());
        }
        //create edges b/w every pair of students
        for (int i = 0; i < students.size(); i++) {
            for (int j = i + 1; j < students.size(); j++) {
                UniversityStudent student1 = students.get(i);
                UniversityStudent student2 = students.get(j);
                int weight = student1.calculateConnectionStrength(student2);

                if (weight > 0) {
                    addEdge(student1, student2, weight);                }
            }
        }
    }

    //adds a weighted undirected edge between two students
    public void addEdge(UniversityStudent student1, UniversityStudent student2, int weight) {
        adjacencyList.get(student1).add(new Edge(student2, weight));
        adjacencyList.get(student2).add(new Edge(student1, weight)); // Undirected graph
    }

    //returns the list of edges (neighbors and weights) for a given student
    public List<Edge> getNeighbors(UniversityStudent student) {
        return adjacencyList.get(student);
    }

    //returns the list of all students in the graph
    public Set<UniversityStudent> getAllNodes() {
        return adjacencyList.keySet();
    }

    //displays the graph via console
    public void displayGraph() {
        System.out.println("\nStudent Graph:");
        for(UniversityStudent s: adjacencyList.keySet()){
            System.out.println(s.name + " -> " + adjacencyList.get(s));
        }
    }
}