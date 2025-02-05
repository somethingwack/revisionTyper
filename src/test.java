import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");

        int numRepeats = 3;
        int size = list.size();
        for(int i = 1; i <= numRepeats - 1; i++) {
            for(int o = 0; o < size - 1; o++) {
                list.add(list.get(o));
            }
        }
        System.out.println(list.toString());

    }
}
