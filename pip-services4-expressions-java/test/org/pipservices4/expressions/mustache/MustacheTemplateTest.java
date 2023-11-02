package org.pipservices4.expressions.mustache;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MustacheTemplateTest {
    @Test
    public void testTemplate1() throws Exception {
        var template = new MustacheTemplate();
        template.setTemplate("Hello, {{{NAME}}}{{ #if ESCLAMATION }}!{{/if}}{{{^ESCLAMATION}}}.{{{/ESCLAMATION}}}");
        HashMap<String, Object> variables = new HashMap<>(Map.of(
                "NAME", "Alex", "ESCLAMATION", "1"
        ));
        var result = template.evaluateWithVariables(variables);
        assertEquals("Hello, Alex!", result);

        template.getDefaultVariables().clear();

        template.getDefaultVariables().put("name", "Mike");
        template.getDefaultVariables().put("esclamation", false);

        result = template.evaluate();
        assertEquals("Hello, Mike.", result);
    }
}
