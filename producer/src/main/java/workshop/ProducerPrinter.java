package workshop;

import com.google.common.base.Strings;
import org.apache.commons.text.CaseUtils;

public class ProducerPrinter {
    public static void printMessage() {
        System.out.println(Strings.repeat("p ", 10));
        System.out.println(CaseUtils.toCamelCase("producer", true));
    }
}