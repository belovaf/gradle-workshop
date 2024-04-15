package workshop;

import com.google.common.base.Strings;
import org.apache.commons.text.CaseUtils;

public class ConsumerPrinter {
    public static void printMessage() {
        System.out.println(Strings.repeat("c ", 10));
        System.out.println(CaseUtils.toCamelCase("consumer", true));
    }
}