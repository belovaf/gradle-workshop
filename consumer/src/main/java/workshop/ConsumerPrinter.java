package workshop;

import com.google.common.base.Strings;
import org.apache.commons.text.CaseUtils;

public class ConsumerPrinter {
    public static void printMessage() {
        System.out.println("ConsumerSimple");
        System.out.println(CaseUtils.toCamelCase("consumer apache", true));
        System.out.println(Strings.repeat("ConsumerGuava ", 3));
    }
}