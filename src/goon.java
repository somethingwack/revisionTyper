public class goon {

    public static void main(String[] args) {
        int maxLength = 3; // Limit how long the combinations get (e.g., 'aaa', 'a0z', etc.)

        // Start with length 1 and keep increasing
        for (int length = 1; length <= maxLength; length++) {
            generateCombinations("", length);
        }
    }

    // Recursive method to generate combinations
    private static void generateCombinations(String prefix, int maxLength) {
        // Character set includes a–z and 0–9
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";

        if (prefix.length() == maxLength) {
            System.out.println(prefix);
            return;
        }

        for (int i = 0; i < chars.length(); i++) {
            generateCombinations(prefix + chars.charAt(i), maxLength);
        }
    }
}
