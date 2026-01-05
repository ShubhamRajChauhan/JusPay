import java.io.*;
import java.util.*;
import java.lang.Math;

public class Golden {

    // --- Start of code ---

    // Static block for efficient pre-computation of Fibonacci numbers and their sums.
    // This runs once when the class is loaded.
    static final int MAX_FIB_INDEX = 92; // F_92 is the largest that fits in a long.
    static long[] fibs = new long[MAX_FIB_INDEX + 1];
    static Map<Long, Integer> fibPrefixSumToN = new HashMap<>();

    static {
        fibs[0] = 0;
        fibs[1] = 1;
        fibs[2] = 1;

        long currentSum = 1; // Sum of F_1
        fibPrefixSumToN.put(currentSum, 1);
        currentSum = 2; // Sum of F_1 + F_2
        fibPrefixSumToN.put(currentSum, 2);

        for (int i = 3; i <= MAX_FIB_INDEX; i++) {
            // Prevent overflow during calculation
            if (fibs[i - 1] > Long.MAX_VALUE - fibs[i - 2] || currentSum > Long.MAX_VALUE - (fibs[i - 1] + fibs[i - 2])) {
                break; 
            }
            fibs[i] = fibs[i - 1] + fibs[i - 2];
            currentSum += fibs[i];
            fibPrefixSumToN.put(currentSum, i);
        }
    }

    /**
     * This is the main method to fill. It reads all test cases and solves them.
     */
    public static List<String> checkFibonacciAllocation() {
        Scanner scan = new Scanner(System.in);
        List<String> results = new ArrayList<>();
        
        // Handle cases where there might be no input
        if (!scan.hasNextInt()) {
            scan.close();
            return results;
        }
        
        int T = scan.nextInt(); // Number of test cases
        
        // Process each test case
        for (int t = 0; t < T; t++) {
            int k = scan.nextInt(); // Number of distinct character counts
            
            // A max-heap to always get the largest available character count
            PriorityQueue<Long> counts = new PriorityQueue<>(Collections.reverseOrder());
            long totalSum = 0;

            for (int i = 0; i < k; i++) {
                long c = scan.nextLong();
                if (c > 0) { // Only positive counts matter
                    counts.add(c);
                }
                totalSum += c;
            }
            
            results.add(solveSingleCase(counts, totalSum));
        }
        
        scan.close();
        return results;
    }

    /**
     * Helper method to solve a single test case using a greedy approach.
     */
    private static String solveSingleCase(PriorityQueue<Long> counts, long totalSum) {
        // Step 1: Check if the total number of characters can form a Fibonacci block sequence.
        // If the sum is not a prefix sum of Fibonacci numbers, it's impossible.
        if (!fibPrefixSumToN.containsKey(totalSum)) {
            return "NO";
        }
        
        int n = fibPrefixSumToN.get(totalSum); // The number of blocks needed (F_1, F_2, ..., F_n)

        // Step 2: Greedily try to form the required blocks, starting from the largest.
        for (int i = n; i >= 1; i--) {
            long requiredFibBlock = fibs[i];

            // If we've run out of character counts but still need to form blocks, fail.
            if (counts.isEmpty()) {
                return "NO";
            }

            // Get the largest available character count.
            long largestCount = counts.poll();

            // If our largest count is smaller than the required block, we can't make it.
            if (largestCount < requiredFibBlock) {
                return "NO";
            }

            // Subtract the used characters and add the remainder back to the pool.
            long remainder = largestCount - requiredFibBlock;
            if (remainder > 0) {
                counts.add(remainder);
            }
        }

        // If we successfully formed all required blocks, it's possible.
        return "YES";
    }

    // --- End of code ---

    public static void main(String[] args) {
        // This part is already provided in your compiler.
        Scanner scan = new Scanner(System.in);
        List<String> result = checkFibonacciAllocation();
        for (int j = 0; j < result.size(); j++) {
            System.out.println(result.get(j));
        }
    }
}