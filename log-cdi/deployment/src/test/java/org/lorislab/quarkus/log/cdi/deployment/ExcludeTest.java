package org.lorislab.quarkus.log.cdi.deployment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ExcludeTest {

    @Test
    public void regexTest() {
        String regex = "(.*?)(ExcludeService.*?$|ExcludeMethodService\\.excludeMethod$)";
        Map<String, Boolean> classes = new LinkedHashMap<>();
        classes.put("org.lorislab.test.TestService", false);
        classes.put("org.lorislab.test.ExcludeService", true);
        classes.put("org.lorislab.test.ExcludeService.start", true);
        classes.put("org.lorislab.test.ExcludeMethodService", false);
        classes.put("org.lorislab.test.ExcludeMethodService.init", false);
        classes.put("org.lorislab.test.ExcludeMethodService.excludeMethod", true);

        Pattern pattern = Pattern.compile(regex);

        classes.forEach((c, r) -> {
            System.out.println(c);
            Assertions.assertEquals(r, pattern.matcher(c).matches(), c);
        });

    }
}
