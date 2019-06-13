public class TestDCT {
    static double calcAlpha(int u, int N) {
        return u == 0 ? Math.sqrt(1.0 / N) : Math.sqrt(2.0 / N);
    }

    public static void main(String args[]) {

        int[][] block = {
                { 139, 144, 149, 153, 155, 155, 155, 155 },
                { 144, 151, 153, 156, 159, 156, 156, 156 },
                { 150, 155, 160, 163, 158, 156, 156, 156 },
                { 159, 161, 162, 160, 160, 159, 159, 159 },
                { 159, 160, 161, 162, 162, 155, 155, 155 },
                { 161, 161, 161, 161, 160, 157, 157, 157 },
                { 162, 162, 161, 163, 162, 157, 157, 157 },
                { 162, 162, 161, 161, 163, 158, 158, 158 }
        };

        double[][] dctBlock = new double[block.length][block[0].length];
        for (int x = 0; x < block.length; ++x) {
            for (int y = 0; y < block[0].length; ++y) {
                block[x][y] -= 128;
            }
        }


        for (int u = 0; u < block.length; ++u) {
            for (int v = 0; v < block[0].length; ++v) {
                dctBlock[u][v] = 0;
                for (int x = 0; x < block.length; ++x) {
                    for (int y = 0; y < block[0].length; ++y) {
                        dctBlock[u][v] += block[x][y] * Math.cos(((2 * x + 1) * u * Math.PI) / (2 * block.length)) * Math.cos(((2 * y + 1) * v * Math.PI) / (2 * block[0].length));
                    }
                }
               dctBlock[u][v] *= calcAlpha(u, block.length) *  calcAlpha(v, block[0].length);
            }
        }

        for (int u = 0; u < block.length; ++u) {
            for (int v = 0; v < block[0].length; ++v) {
                System.out.print(dctBlock[u][v] + " ");
            }
            System.out.print("\n");
        }
    }
}
