import java.util.Arrays;

public class shart {
    public static void main(String[] args) {
        int n = 3;
        int m = 4;
        int min1 = 99999999;
        int best = 99999999;
        int k = 2;

        for(int i = n - 1; i != 0; i--) {

            int[][] arr = new int[i][m];
            int[][] arr2 = new int[n - i][m];

            System.out.println(i);

            System.out.println("    half 1");
            for(int[] a : arr) {
                System.out.println("        " +Arrays.toString(a));
            }

            System.out.println("    half 2");
            for(int[] a : arr2)
                System.out.println("        " + Arrays.toString(a));

            int arr1size = arr.length * arr[0].length;
            int arr2size = arr2.length * arr2[0].length;

            if(arr1size > arr2size) {
                System.out.println("half 2 is smaller");
                if(k <= arr2size && arr2size < min1) {

                    min1 = arr2size;
                    best = arr1size;

                }
            }
            else {
                System.out.println("half 1 is smaller");
                if(arr1size <= k && arr1size < min1) {
                    min1 = arr1size;
                    best = arr2size;
                }
            }
        }

        for(int i = m - 1; i != 0; i--) {

            int[][] arr = new int[n][i];
            int[][] arr2 = new int[n][m - i];

            System.out.println(i);

            System.out.println("    half 1");
            System.out.println("        " +Arrays.deepToString(arr));

            System.out.println("    half 2");
            System.out.println("        " + Arrays.deepToString(arr2));

            int arr1size = arr.length * arr[0].length;
            int arr2size = arr2.length * arr2[0].length;

            if(arr1size > arr2size) {
                System.out.println("half 2 is smaller");
                if(k <= arr2size && arr2size < min1) {

                    min1 = arr2size;
                    best = arr1size;

                }
            }
            else {
                System.out.println("half 1 is smaller");
                if(arr1size <= k && arr1size < min1) {

                    min1 = arr1size;
                    best = arr2size;
                }
            }
        }

        System.out.println(best);

    }
}
