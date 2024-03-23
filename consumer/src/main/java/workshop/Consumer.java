package workshop;

import com.google.common.base.Strings;

public class Consumer {
    public static void main(String[] args) {
        Producer.main(args);
        Strings.emptyToNull("sdf");
    }
}
