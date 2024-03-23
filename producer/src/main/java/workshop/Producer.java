package workshop;

import com.google.common.base.Strings;
import java.util.Arrays;


public class Producer {
    public static void main(String[] args) {
        System.out.println(Strings.repeat("a ", 10));
        System.out.println("Input args:" + Arrays.toString(args));
    }
}