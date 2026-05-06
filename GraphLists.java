// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.Scanner;

class QueueException extends Exception {
    public QueueException(String s) {
        super(s);
    }
}
// Queue class required in BF()
class Queue {
    private int[] q;
    private int front, rear, size, capacity;

    public Queue(int capacity) {
        this.capacity = capacity;
        size = 0;
        front = 0;
        rear = 0;
        q = new int[capacity];
    }

    public void enqueue(int x) throws QueueException {
        if (size != capacity) {
            q[rear] = x;
            rear = (rear + 1) % capacity;
            size++;
        } else {
            throw new QueueException("Queue is full");
        }
    }

    public int dequeue() throws QueueException {
        int dequeued;
        if (size != 0) {
            dequeued = q[front];
            front = (front + 1) % capacity;
            size--;
            return dequeued;
        } else {
            throw new QueueException("Queue is empty");
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void display() {
        for(int i = 0; i < size; i++) {
            System.out.print(q[(front + i) % capacity] + ", ");
        }
    }
}

class Heap
{
    private int[] a;	   // heap array
    private int[] hPos;	   // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v

    private int N;         // heap size
   
    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) 
    {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }


    public boolean isEmpty() 
    {
        return N == 0;
    }


    public void siftUp( int k) 
    {
        int v = a[k];

        a[0] = 0;

        // priority = the distance value of the vertex at position k in the heap
        int priority = dist[v];

        // order the heap by priority
        while (priority < dist[a[k / 2]]) {
            int parent = a[k / 2];
            a[k] = parent;
            hPos[parent] = k;
            k = k / 2;
        }

        // place v in its correct position in the heap
        a[k] = v;

        // update hPos[] to reflect new position
        hPos[v] = k;
    }


    public void siftDown( int k) 
    {
        int v, j;
       
        v = a[k];
        
        int priority = dist[v];
        
        while (k <= N / 2) {
            j = 2 * k; // j is the child node

            // increment j if left node is bigger
            if ((j < N) && (dist[a[j]] > dist[a[j + 1]])) {
                j++;
            }

            // break if distance of parent < child
            if (priority <= dist[a[j]]) {
                break;
            }

            // child moved to parent position if parent < child
            a[k] = a[j];

            hPos[a[k]] = k; // update hPos[] to reflect new position

            k = j;
        }

        a[k] = v; // place node in correct position

        hPos[v] = k; // update hPos[] to reflect new position
    }


    public void insert( int x) 
    {
        a[++N] = x;
        siftUp( N);
    }


    public int remove() 
    {   
        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N+1] = 0;  // put null node into empty spot
        
        a[1] = a[N--];
        siftDown(1);
        
        return v;
    }

}

class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }
    
    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    public int V; // made public so we can validate user input for starting vertex in main()
    private int E;
    private Node[] adj;
    private Node z;
    private int[] mst;
    
    // used for traversing graph
    private int[] visited;
    private int id;
    private int[] parent;
    private int[] discovery;
    private int[] finish;
    
    
    // default constructor
    public Graph(String graphFile)  throws IOException
    {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // multiple whitespace as delimiter
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create sentinel node
        z = new Node(); 
        z.next = z;
        
        // create adjacency lists, initialised to sentinel node z       
        adj = new Node[V+1];        
        for(v = 1; v <= V; ++v)
            adj[v] = z;
        
        // create arrays and size them to the graph + 1
        visited = new int[V + 1];
        parent = new int[V + 1];
        discovery = new int[V + 1];
        finish = new int[V + 1];
        
       // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));
            
            // create new node (u -> v) and assign its values
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u]; // point new node to first node in list
            adj[u] = t; // point array to new node

            // create new node (v -> u) and assign its values
            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v]; // point new node to first node in list
            adj[v] = t; // point array to new node
        }
        
        reader.close();
    }
   
    // convert vertex into a display label for pretty printing
    private String toChar(int u)
    {  
        return Integer.toString(u);
    }
    
    // method to display the graph representation
    public void display() {
        int v;
        Node n;
        
        for(v=1; v<=V; ++v){
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next) 
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        }
        System.out.println("");
    }


    
	public void MST_Prim(int s)
	{
        int v, u;
        int wgt, wgt_sum = 0;
        int[]  dist, parent, hPos;
        Node t;

        System.out.println("Prim's MST starting from vertex " + toChar(s));

        // initialise arrays
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];

        // assigning default values to arrays
        for(v = 1; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE; // ensures no vertex is less than the initial value
            parent[v] = 0;
            hPos[v] = 0;
        }

        dist[s] = 0; // first vertext has best priority

        Heap h = new Heap(V, dist, hPos);
        h.insert(s);

        while (!h.isEmpty()) {
            v = h.remove();
            dist[v] = -dist[v]; // negative indicates vertex is in MST

            // traverses through adjacency list
            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert; // place next vertex in u
                wgt = t.wgt; // place next weight in wgt

                // if weight of edge (v, u) is less than current weight
                if (wgt < dist[u]) {
                    dist[u] = wgt; // assign new best weight to dist[u]
                    parent[u] = v; // assign new parent to parent[u]

                    // if u is not in heap add it
                    if (hPos[u] == 0) {
                        System.out.println("Adding vertex " + toChar(u) + " to the heap");
                        h.insert(u);
                    }
                    else { // otherwise sift it up
                        System.out.println("Updating vertex " + toChar(u) + " in the heap");
                        h.siftUp(hPos[u]);
                    }
                }
            }
        }

        // finding total weight of MST
        for(v = 1; v <= V; v++) {
            wgt_sum += -dist[v]; // undoing the negative from earlier assignment
        }

        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");

        mst = parent; // assign parent array to mst for display in showMST()
	}
    
    public void showMST()
    {
            System.out.print("\n\nMinimum Spanning tree parent array is:\n");
            for(int v = 1; v <= V; ++v)
                System.out.println(toChar(v) + " -> " + toChar(mst[v]));
            System.out.println("");
    }

    public void SPT_Dijkstra(int s)
    {
        int v, u;
        int wgt;
        int[]  dist, parent, hPos;
        Node t;

        System.out.println("Dijkstra's SPT starting from vertex " + toChar(s));
        System.out.println("\tVertex\t\tParent\t\tDistance");

        // initialise arrays
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];

        // assigning default values to arrays
        for(v = 1; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE; // ensures no vertex is less than the initial value
            parent[v] = 0;
            hPos[v] = 0;
        }

        Heap h = new Heap(V, dist, hPos);
        dist[s] = 0; // first vertex has best priority
        h.insert(s);

        // Iterates through the heap
        while (!h.isEmpty()) {
            v = h.remove();

            // traverses through adjacency list
            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert; // next vertex
                wgt = t.wgt; // next weight

                // if weight of edge (v, u) + distance to v is less than current distance to u
                if (wgt + dist[v] < dist[u]) {
                    dist[u] = dist[v] + wgt; // assign current weight to dist[u]
                    parent[u] = v; // assign current vertex as next's parent

                    if (hPos[u] == 0) { // if u is not in heap then add it
                        h.insert(u);
                    }
                    else { // otherwise sift it up
                        h.siftUp(hPos[u]);
                    }
                }
            }
            System.out.println("\t" + toChar(v) + "\t\t" + toChar(parent[v]) + "\t\t" + dist[v]);
        }
    }

    public void BF(int s) {
        System.out.println("Breadth First Traversal starting from vertex " + toChar(s));

        int v, u;
        Node t;

        // initialise visited[] and parent[] to 0 and discovery[] to MAX_VALUE
        for(v = 1; v <= V; v++) {
            visited[v] = 0;
            parent[v] = 0;
            discovery[v] = Integer.MAX_VALUE;
        }

        visited[s] = 1; // mark first vertex as being seen (not yet visited)
        discovery[s] = 1; // mark discovery time of first vertex
        
        Queue q = new Queue(V);

        // add first vertex to queue
        try {
            q.enqueue(s);
        } catch (QueueException e) {
            System.out.println(e.getMessage());
        }

        // dequeue and store vertex in v
        while(!q.isEmpty()) {
            try {
                v = q.dequeue();
            } catch (QueueException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("BF: Visited vertex " + toChar(v) + " at edge " + toChar(parent[v]) + " -> " + toChar(v));

            // traverse through node list of vertex v
            for(t = adj[v]; t != z; t = t.next) {
                u = t.vert; // place next vertex into u

                if(visited[u] == 0) { // if the vertex has not been discovered
                    System.out.println("BF: Discovered vertex " + toChar(u));

                    visited[u] = 1; // mark vertex as being seen (not yet visited)
                    parent[u] = v; // assign parent of u to be v
                    discovery[u] = discovery[v] + 1; // assign discovery time of u to be one more than v

                    // enqueue u
                    try {
                        q.enqueue(u);
                    } catch (QueueException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            // mark vertex as visited
            visited[v] = 2;
        }
    }

    public void DF(int s) {
        System.out.println("Depth First Traversal starting from vertex " + toChar(s));

        int v;

        // initialise visited[] and parent[] to 0 and discovery[] to MAX_VALUE
        for(v = 1; v <= V; v++) {
            visited[v] = 0;
            parent[v] = 0;
        }
        
        dfVisit(s);
    }

    private void dfVisit(int v) {
        id++; // increment id for discovery time
        discovery[v] = id; // assign discovery time to vertex v
        visited[v] = 1; // mark vertex as being seen (not yet visited)

        System.out.println("DF: Visited vertex " + toChar(v) + " at edge " + toChar(parent[v]) + " -> " + toChar(v));

        // traverse through node list of vertex v
        for(Node t = adj[v]; t != z; t = t.next) {
            if(visited[t.vert] == 0) { // if the vertex has not been visited
                System.out.println("DF: Discovered vertex " + toChar(t.vert));

                parent[t.vert] = v; // assign parent of t.vert to be v
                dfVisit(t.vert); // recursively visit t.vert
            }
        }
        visited[v] = 2; // mark vertex as visited
        id++; // increment id for finish time
        finish[v] = id; // assign finish time to vertex v
    }

}

public class GraphLists {
    public static void main(String[] args) throws IOException
    {
        int s = 1; // starting vertex default
        String fname; // graph file name
        Scanner sc = new Scanner(System.in);

        // get file name
        System.out.println("Enter graph file name (Default: wGraph1.txt):");
        fname = sc.nextLine().trim();
        if (fname.equals("")) { // if user enters nothing use default
            fname = "wGraph1.txt";
        } else if (!new File(fname).exists()) { // ensure file exists
            System.out.println("File not found. Using default wGraph1.txt.");
            fname = "wGraph1.txt";
        }

        // build graph (so we know V for validation)
        Graph g = new Graph(fname);

        // ask for starting vertex (blank -> default 1)
        System.out.println("Enter starting vertex numerically (1, 2, 3 etc.)  (Default: 1):");
        String sLine = sc.nextLine().trim();
        if (sLine.equals("")) {
            s = 1;
        } else if (Integer.parseInt(sLine) < 1 || Integer.parseInt(sLine) > g.V) { // ensure vertex is actually in graph
            System.out.println("Invalid vertex. Using default 1.");
            s = 1;
        } else {
            s = Integer.parseInt(sLine);
        }

        g.display();

       g.DF(s);
       g.BF(s);
       g.MST_Prim(s);  
       g.showMST(); 
       g.SPT_Dijkstra(s);
       
       sc.close();
    }
}
