package nl.han.ica.icss;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransformRequirementsTest {

    @Test
    void evaluatesArithmeticWithStandardPrecedenceAndVariableValues() {
        assertEquals(
                "p {\n" +
                        "  width: 17px;\n" +
                        "  height: 70%;\n" +
                        "}\n\n",
                compile("Width := 10px + 2 * 5px - 3px;\n" +
                        "Percent := 50% + 10% * 2;\n" +
                        "p { width: Width; height: Percent; }")
        );
    }

    @Test
    void selectsTrueFalseAndNestedConditionalBodiesAndRemovesAssignments() {
        assertEquals(
                "p {\n" +
                        "  color: #00ff00;\n" +
                        "  height: 30px;\n" +
                        "  width: 200px;\n" +
                        "}\n\n",
                compile("Width := 0px;\n" +
                        "p {\n" +
                        "  if [TRUE] {\n" +
                        "    color: #00ff00;\n" +
                        "    if [FALSE] { height: 10px; } else { height: 30px; }\n" +
                        "    Width := 200px;\n" +
                        "  } else {\n" +
                        "    color: #ff0000;\n" +
                        "  }\n" +
                        "  if [FALSE] { height: 20px; }\n" +
                        "  width: Width;\n" +
                        "}")
        );
    }

    @Test
    void transformsSpecificationSection44ExampleWithTrueAndFalseConditions() {
        String trueCase = "AdjustWidth := TRUE;\n" +
                "WidthVar := 0px;\n" +
                "\n" +
                "p {\n" +
                "\tif [AdjustWidth] {\n" +
                "\t\tWidthVar := 200px;\n" +
                "\t} else {\n" +
                "\t\tWidthVar := 250px;\n" +
                "\t}\n" +
                "\n" +
                "\twidth: WidthVar;\n" +
                "}";

        assertEquals(
                "p {\n" +
                        "  width: 200px;\n" +
                        "}\n\n",
                compile(trueCase)
        );

        String falseCase = "AdjustWidth := FALSE;\n" +
                "WidthVar := 0px;\n" +
                "\n" +
                "p {\n" +
                "\tif [AdjustWidth] {\n" +
                "\t\tWidthVar := 200px;\n" +
                "\t} else {\n" +
                "\t\tWidthVar := 250px;\n" +
                "\t}\n" +
                "\n" +
                "\twidth: WidthVar;\n" +
                "}";

        assertEquals(
                "p {\n" +
                        "  width: 250px;\n" +
                        "}\n\n",
                compile(falseCase)
        );
    }

    private String compile(String source) {
        Pipeline pipeline = new Pipeline();
        pipeline.parseString(source);
        assertTrue(pipeline.isParsed(), pipeline.getErrors().toString());
        assertTrue(pipeline.check(), pipeline.getErrors().toString());
        pipeline.transform();
        assertTrue(pipeline.isTransformed());
        return pipeline.generate();
    }
}
