// Static members for the memoization tables and recursive functions
    static double[][] memo_f;
    static double[][] memo_g;

    /**
     * This method contains the complete logic for the problem.
     * It sets up the memoization tables and calls the main recursive function.
     * @return The probability that Nina wins as a float.
     */
    public static float calculate_probability() {
        // The problem description implies the Scanner is used within the main method,
        // which then calls this function. We create it here to read the input.
        Scanner scan = new Scanner(System.in);
        int w = scan.nextInt();
        int b = scan.nextInt();

        // Initialize memoization tables with a sentinel value (-1.0)
        memo_f = new double[w + 1][b + 1];
        memo_g = new double[w + 1][b + 1];
        for (int i = 0; i <= w; i++) {
            Arrays.fill(memo_f[i], -1.0);
            Arrays.fill(memo_g[i], -1.0);
        }

        return (float) solve_f(w, b);
    }

    /**
     * Calculates the probability Nina wins, given it is Nina's turn.
     * Corresponds to the function f(w, b).
     * @param w The number of white candies.
     * @param b The number of black candies.
     * @return The probability Nina wins from this state.
     */
    private static double solve_f(int w, int b) {
        // Base Cases
        if (w <= 0) return 0.0;
        if (b < 0) return 0.0; // Should not be reached but good for safety
        if (b == 0) return 1.0;
        
        // Return cached result if available
        if (memo_f[w][b] != -1.0) return memo_f[w][b];

        double totalCandies = w + b;
        double probNinaWinsNow = (double) w / totalCandies;
        double probTurnContinues = (double) b / totalCandies;
        
        // If Nina picks black, the game moves to Sam's turn with (w, b-1) candies.
        double result = probNinaWinsNow + probTurnContinues * solve_g(w, b - 1);

        // Cache and return the result
        return memo_f[w][b] = result;
    }

    /**
     * Calculates the probability Nina wins, given it is Sam's turn.
     * Corresponds to the function g(w, b).
     * @param w The number of white candies.
     * @param b The number of black candies.
     * @return The probability Nina wins from this state.
     */
    private static double solve_g(int w, int b) {
        // Base Cases
        if (w <= 0) return 0.0;
        if (b < 0) return 0.0;
        // If total candies <= 1, one falls, jar is empty. Sam wins.
        if (w + b <= 1) return 0.0;

        // Return cached result if available
        if (memo_g[w][b] != -1.0) return memo_g[w][b];

        double result = 0.0;
        double totalCandies = w + b;
        double totalAfterFall = w + b - 1;

        // Case 1: A white candy falls out
        double probWfalls = (double) w / totalCandies;
        // For Nina to win, Sam must then pick a black candy
        double probNinaWinsAfterWfalls = (b / totalAfterFall) * solve_f(w - 1, b - 1);
        result += probWfalls * probNinaWinsAfterWfalls;

        // Case 2: A black candy falls out
        // This can only happen if there is at least 1 black candy to fall
        // and at least 1 more for Sam to pick (total b >= 2).
        if (b >= 2) {
             double probBfalls = (double) b / totalCandies;
             // For Nina to win, Sam must then pick another black candy
             double probNinaWinsAfterBfalls = ((b - 1) / totalAfterFall) * solve_f(w, b - 2);
             result += probBfalls * probNinaWinsAfterBfalls;
        }
        
        // Cache and return the result
        return memo_g[w][b] = result;
    }
