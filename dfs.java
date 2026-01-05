class Solution {
    // Function to return a list containing the DFS traversal of the graph.
    public ArrayList<Integer> dfsOfGraph(ArrayList<ArrayList<Integer>> adj) {
        int V = adj.size(); // Number of vertices
        boolean[] vis = new boolean[V]; // Boolean array to track visited nodes
        ArrayList<Integer> ls = new ArrayList<>(); // List to store DFS traversal
        dfs(0, vis, adj, ls); // Start DFS from node 0
        return ls;
    }


    
    // Helper function for DFS traversal
    private void dfs(int node, boolean[] vis, ArrayList<ArrayList<Integer>> adj, ArrayList<Integer> ls) {
        vis[node] = true; // Mark the current node as visited
        ls.add(node); // Add the node to the result list

        // Traverse all adjacent nodes
        for (Integer neighbor : adj.get(node)) {
            if (!vis[neighbor]) {
                dfs(neighbor, vis, adj, ls);
            }
        }
    }