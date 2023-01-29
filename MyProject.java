import java.util.Stack;
import java.util.Queue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Ron Zatuchny (22984076)
 * @author Divyanshu Siwach(22912646)
 */
public class MyProject implements Project {
    /**
     * 
     * @param adjlist adjecancy list representing a graph
     * @return True if all the devides are connected, false otherwise
     */
    public boolean allDevicesConnected(int[][] adjlist) {
        //declaring and initilsing the visisted array, whichrepresents weather w vertex was already visisted
        int visited[]= new int [adjlist.length];
        for(int i=0;i<adjlist.length;i++){
            visited [i]=0;
        }
        //rperesents the largest strongly connected component that a vertex belongs to(initialised to it's own index,as every element is a strongly connected component with itself)
        int connected[] =new int [adjlist.length];
        for(int i=0;i<adjlist.length;i++){
            connected [i]=i;
        }
        //recursively runs dfs to connect all the indiciesthat could be connected into a single strongly connected component(starting at zero)
        dfs(0,adjlist,visited,connected);
        //checking  weather all the elements in the verticiesa are in the same strongly connected component as 0, if not returns false
        for(int i=0; i<connected.length;i++){
            if(connected[i]!=0){
                return false;
            }
        }


        return true;
    }
/**  
 * @param adjlist adjecency list of the graph
 * @param src the source vertex in the graph
 * @param dst the destination vertex in the graph
 * @return
 */
    public int numPaths(int[][] adjlist, int src, int dst) {
        // calling a function that will reverse the graph and store the result in a list of stacks of integers 
        Stack<Integer>[] reversed= reverse(adjlist);
        //using bfs over the reversed graph to get the minimum distance of a each vertex from the destination(if applicable)
        int[] distance= getDistance(reversed,dst);
        Boolean[] calculated=new Boolean[adjlist.length];
        Arrays.fill(calculated,false);
        int [] values= new int[adjlist.length];
        //using bfs calculating the nmber of paths from the sorce to the destination(more information found below where the function is)
        return getNumPaths(distance,adjlist,src,calculated,values);
    }
/**
 * 
 * @param adjlist adjecency list of the graph
 * @param addrs list of addresses as specified in the project description
 * @param src the source vertex in the graph
 * @param queries list of subnets as specified in the project description
 * @return
 */
    public int[] closestInSubnet(int[][] adjlist, short[][] addrs, int src, short[][] queries) {
        //getting all the distances from all the vertices and storing the result in a n array
        int [] distances=getDistance(adjlist,src);
        //based on the distances above, store the minimum distance to each subnet in a map of arraylist and Integer
        HashMap<ArrayList<Integer>,Integer> subnetsMap= getSubnetsMap(distances,addrs);
        //System.out.println(subnetsMap);
        //iterating threw the queries and finding the minimum distance for that sunet(storedin the map) and return the resul
        return getClosestArray(queries,subnetsMap);
    }  

    //the following method matches the queries with the corresponding subnets in the map and returns the output for Q3 as specified in the instructions
    public int[] getClosestArray(short[][] queries, HashMap<ArrayList<Integer>,Integer> map){
        //declare the output array
        int[] closestArray= new int[queries.length];
        //for every query,, convert the int[] to ArrayList<Integer>,return the value stored in the map for the specified arrayList(key) or Integer.MAX_VALUE if not in map
        for(int i=0;i<queries.length;i++){
            //System.out.println(Arrays.toString(queries[i])+":"+ map.get(queries[i]));
            ArrayList<Integer> subnet=new ArrayList<Integer>();
            for (int j :queries[i]){
                subnet.add(j);
            }
            closestArray[i]=(map.containsKey(subnet)?map.get(subnet):Integer.MAX_VALUE);
        }
        return closestArray;
    }
    //creates the map with all the subnets and their minimum distances and returns that map
    public HashMap getSubnetsMap(int [] distances,short[][] addrs){
        //declares the output hashmap
        HashMap<short[],Integer> subnetMap= new HashMap<short[],Integer>();
        //updates the map(by calling the function below) for each vertex
        for(int i=0;i<distances.length;i++){
            updateHashMap(distances[i],addrs[i],subnetMap);
        }
        //System.out.println(subnetMap);
        return subnetMap;
    }
    //updatesthe hashmap, creates an entry with this specific vertx network+ updates the minimum distance to all subnetworks the vertexbelongs to if required
    public void updateHashMap(int distance, short[] addrs,HashMap subnetMap){
        //declares and converts the adress from an array to arraylist
        ArrayList<Integer> subnet=new ArrayList<Integer>();
            for (int j :addrs){
                subnet.add(j);
            }
        //if the key(adress) does not exists or holds a greater value update it and call this function for a bigger subnet(without the last element)
        if(!subnetMap.containsKey(subnet) || (Integer)subnetMap.get(subnet)>distance){
            //System.out.println("updating");
            subnetMap.put(subnet,distance);
            //System.out.println ("insert   "+Arrays.toString(subaddrs)+":"+distance);
            //if the size of the address is greater or equal to one recursively call the function with a smaller adress
            if(addrs.length>=1){
                updateHashMap(distance,Arrays.copyOfRange(addrs,0,addrs.length-1),subnetMap);
            }
        }
    
    }
    //starts the search from a given index(0 when calling from outside) in a given graph, while modifing visited and connected arrays
    public void dfs(int index,int[][] adjlist,int[] visited,int[] connected){
        //marking that the following vertex have been visited
        visited[index]=1;
        //for each element that the current vertex is connected to and has not been visited yet call this function recursively+ update connections(explained below)
        for(int i=0; i<adjlist[index].length;i++){
            //System.out.println("initial "+adjlist[index][i]+":"+connected[i]);
            if(visited[adjlist[index][i]]==0){
                dfs(adjlist[index][i],adjlist,visited,connected);
            }
            //System.out.println("after:"+i+":"+connected[i]);
            //if the element that the vertex is connected to this vertex has a smaller connected value, we will replace the connected value of this vertex by that of the vertex it is connected to
            if(connected[adjlist[index][i]]==0){
                //System.out.println("got here");
                connected[index]=connected[adjlist[index][i]];
            }
            //System.out.println("got past");
            //System.out.println("after:"+adjlist[index][i]+":"+connected[i]);

        }
    }
    //reversing the edges if a given adjacency list, returns it as a list of stacks
    private Stack<Integer>[] reverse(int[][] adjlist){
        //creates an empty array of stacjs and adds an empty stack to every vertex in the array
        Stack<Integer>[] reversed=new Stack[adjlist.length];
        for (int i=0;i<adjlist.length;i++){
            reversed[i]=new Stack<Integer>();
        }
        //revervsesthe edges of the graph
        for (int i=0;i<adjlist.length;i++){
            for (int j:adjlist[i]){
                reversed[j].push(i);
            }
        }
        return reversed;
    }
    //returns the distance from the given point to every vertex in the graph if applicable, this function is given a revesed array to find the distance from all points to the destination
    private int[] getDistance(Stack<Integer>[] reversed, int dst){
        //creates an output array, intialise it with Integer.MAX_VALUE and setting the distance to the dst as 0
        int[] distance =new int[reversed.length];
        Arrays.fill(distance,Integer.MAX_VALUE);
        distance[dst]=0;
        //creates a queue of Integers, pushes the src to the queue, this queue will be used for bfs
        Queue<Integer> q=new LinkedList<Integer>();
        q.add(dst);
        //finds the distance of each point from the destination using bfs from the destination, each vertex got a gistance of that of the vertex it was reached from +1(in case it was not seen before)
        while(!q.isEmpty()){
            int vertex=q.remove();
            Iterator it=reversed[vertex].iterator();
            while(it.hasNext()){
                int value=(int)it.next();
                if(distance[value]==Integer.MAX_VALUE){
                    distance[value]=distance[vertex]+1;
                    q.add(value);
                }
            }
        }
        return distance;
    }
    //doing the same as the function above, but takes a non reversed graph(as array of arrays)
    private int[] getDistance(int[][] adjlist,int src){
        int[] distance =new int[adjlist.length];
        Arrays.fill(distance,Integer.MAX_VALUE);
        distance[src]=0;
        Queue<Integer> q=new LinkedList<Integer>();
        q.add(src);
        while(!q.isEmpty()){
            int vertex=q.remove();
            for( int value:adjlist[vertex]){
                if(distance[value]==Integer.MAX_VALUE){
                    distance[value]=distance[vertex]+1;
                    q.add(value);
                }
            }
        }
        return distance;
    }
    //calculates the number of paths and returns ot
    private int getNumPaths(int[] distance,int[][] adjlist,int src,Boolean[] calculated,int counts[]){
        // if the distance from that point to the destination is 0 or 1, there is only one path to get there
        if(calculated[src]){
            return counts[src];
        }
        if(distance[src]==0 || distance[src]==1){
            return 1;
        }
        //the number of paths 
        int count=0;
        //weather there exists a path
        boolean exists=false;
        //for every vertex, checking if any of the vertices it is connected to have a lower shortest path to the 
        for(int i=0;i<adjlist[src].length;i++){
            if(distance[src]>distance[adjlist[src][i]]){
                exists=true;
                count+=getNumPaths(distance,adjlist,adjlist[src][i],calculated,counts);
            }
        }
        calculated[src]=true;
        counts[src]= count;
        if (exists==false){return 0;}
        return count;
    }    
    
    /**
     * @param adjlist The adjacency list describing the connections between devices
     * @param speeds The list of query row segments
     * @param src The source (transmitting) device
     * @param dst The destination (receiving) device
     * @return The maximum download speed possible from the source to the destination, or -1 if they are the same
     */
    public int maxDownloadSpeed(int[][] adjlist, int[][] speeds, int src, int dst) {
        
        //Returns -1 if the source and destination are same.
        if (src == dst) {
            return -1;
        }
        
        //Using an array to store heights of all the vertices.
        int[] height = new int[adjlist.length];
        //Using an array to store preflows to all the vertices.
        int[] extraflow = new int[adjlist.length];

        //Initialising preflow for every edge to 0 and height of every edge to 0.
        for (int i = 0; i < adjlist.length; i++) {
            height[i] = 0;
            extraflow[i] = 0;
        }

        //setting height of the src as number of vertices in the graph.
        height[src] = adjlist.length;

        //Initialising flow to every vertex with 0.
        int[][] flow = new int[adjlist.length][adjlist.length];
        for (int i = 0; i < adjlist.length; i++) {
            for (int j = 0; j < adjlist.length; j++) {
                flow[i][j] = 0;
            }
        }
        
        //Initialising the preflow. Pushes value from the source vertex to it's edges and changes flow of dest and source.
        for (int i = 0; i < speeds[src].length; i++) {
                int dest = adjlist[src][i];
                flow[src][dest] = speeds[src][i];
                extraflow[dest] += flow[src][dest];
                flow[dest][src] = -flow[src][dest];
        }

        //This is the main loop of this method, it calls getOverFlowVetex, push and relabel methods.
        int overflowvertex = getOverFlowVetex(src, dst, adjlist.length, extraflow, adjlist, speeds, flow);
        while (overflowvertex != -1) {
            if (!push(overflowvertex, adjlist.length, adjlist, speeds, flow, height, extraflow, src)) {
                relabel(overflowvertex, adjlist, speeds, flow, height);
            }
            overflowvertex = getOverFlowVetex(src, dst, adjlist.length, extraflow, adjlist, speeds, flow);
        }
        return extraflow[dst];
    }

    //Returns the current active vertex with overflowing speed.
    private int getOverFlowVetex(int src, int dst, int n, int[] extraflow, int[][] adjlist, int[][] speeds, int[][] flow) {
        //We check for every vertex whether it is an over flow vertex or not.
        for (int i = 0; i < n; i++) {
            //Skipping source and destination vertices
            if (i != dst && i != src) {
                if (extraflow[i] > 0)
                {
                    //For every edge from vertex i.
                    for(int j = 0; j < adjlist[i].length; j++) {
                        if (speeds[i][j] != 0) {
                            //Storing the destination vertex of the edge in loc.
                            int loc = adjlist[i][j];
                            //Returns the index of current vertex if speed is different from flow.
                            if (speeds[i][j] != flow[i][loc]) {
                                return i;
                            }
                        }
                    }
                }
            }
        }
        //It will return -1 if there are no overflowing vertices.
        return -1;
    }

    //Push method is responsible for pushing the speed whenever it's possible to. It returns true if the speed gets pushed, false otherwise.
    private boolean push(int current, int n, int[][] adjlist, int[][] speeds, int[][] flow, int[] height, int[] extraflow, int src) {
        //We will go through all the vertices in the graph, checking their edges.
        for (int i = 0; i < adjlist[current].length; i++) {
            int loc = adjlist[current][i];
            //If the flow equals speed capacity, we skip.
            if(flow[current][loc] == speeds[current][i]) {
                continue;
            }
            //Speed can only be pushed from higher height to lower height.
            if(height[current] > height[loc]) {
                if(speeds[current][i] - flow[current][loc] < extraflow[current]) {
                    extraflow[current] -= speeds[current][i] - flow[current][loc];
                    extraflow[loc] += speeds[current][i] - flow[current][loc];
                    flow[current][loc] += speeds[current][i] - flow[current][loc];
                    flow[loc][current] -= speeds[current][i] - flow[current][loc];
                }
                else {
                    int saver = extraflow[current];
                    extraflow[current] -= saver;
                    extraflow[loc] += saver;
                    flow[current][loc] += saver;
                    flow[loc][current] -= saver;
                }
                return true;
            }
        }
        return false;
    }

    //If the speed is not pushed relabel method will be called, it changes height of the current vertex.
    private void relabel(int current, int[][] adjlist, int[][] speeds, int[][] flow, int[] height) {
        //initializing minimum_height to store height
        int minimum_height = Integer.MAX_VALUE;
        //Checking every edge of the current vertex.
        for (int i = 0; i < adjlist[current].length; i++) {
            int loc = adjlist[current][i]; 
            if(flow[current][loc] == speeds[current][i]) {
                continue;
            }
            //We change the height of the current vertex and increase it so that speed can flow from higher height to lower height when push is called next time.
            if (height[loc] < minimum_height) {
                minimum_height = height[loc];
                height[current] = minimum_height+1;
            }    
        }
    }
}
