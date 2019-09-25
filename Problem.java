public class Problem{
   public static void main(String[] args){
        System.out.print(new Solution().totalNQueens(4));
   }
}
class Solution {
   public int totalNQueens(int n) {
        if (n <= 0)
            return n == 0 ? 0 : 1;
        boolean [] rows = new boolean[n], diags = new boolean[2 * n], antiDiags = new boolean[2 * n];
        return solve(0, n, rows, antiDiags, diags);
    }

    private int solve(int col, int n, boolean[] rows, boolean[] antiDiags, boolean[] diags) {
        if (col == n)
            return 1;
        int sum = 0;
        for (int row = 0; row < n; row++) {
            if (rows[row] || antiDiags[row - col + n] || diags[row + col])
                continue;
            rows[row] = antiDiags[row - col + n] = diags[row + col] = true;
            sum  = solve(col + 1, n, rows, antiDiags, diags);
            rows[row] = antiDiags[row - col + n] = diags[row + col] = false;
        }
        return sum;
    }
 }