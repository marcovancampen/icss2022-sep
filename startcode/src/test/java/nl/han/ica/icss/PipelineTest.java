package nl.han.ica.icss;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    @Test
    void compilesLevel0Stylesheet() throws IOException {
        assertEquals(
                "p {\n" +
                        "  background-color: #ffffff;\n" +
                        "  width: 500px;\n" +
                        "}\n\n" +
                        "a {\n" +
                        "  color: #ff0000;\n" +
                        "}\n\n" +
                        "#menu {\n" +
                        "  width: 520px;\n" +
                        "}\n\n" +
                        ".menu {\n" +
                        "  color: #000000;\n" +
                        "}\n\n",
                compileResource("level0.icss")
        );
    }

    @Test
    void compilesLevel1Stylesheet() throws IOException {
        assertEquals(
                "p {\n" +
                        "  background-color: #ffffff;\n" +
                        "  width: 500px;\n" +
                        "}\n\n" +
                        "a {\n" +
                        "  color: #ff0000;\n" +
                        "}\n\n" +
                        "#menu {\n" +
                        "  width: 520px;\n" +
                        "}\n\n" +
                        ".menu {\n" +
                        "  color: #000000;\n" +
                        "}\n\n",
                compileResource("level1.icss")
        );
    }

    @Test
    void compilesLevel2Stylesheet() throws IOException {
        assertEquals(
                "p {\n" +
                        "  background-color: #ffffff;\n" +
                        "  width: 500px;\n" +
                        "}\n\n" +
                        "a {\n" +
                        "  color: #ff0000;\n" +
                        "}\n\n" +
                        "#menu {\n" +
                        "  width: 520px;\n" +
                        "}\n\n" +
                        ".menu {\n" +
                        "  color: #000000;\n" +
                        "}\n\n",
                compileResource("level2.icss")
        );
    }

    @Test
    void compilesLevel3Stylesheet() throws IOException {
        assertEquals(
                "p {\n" +
                        "  background-color: #ffffff;\n" +
                        "  width: 500px;\n" +
                        "  color: #124532;\n" +
                        "  background-color: #000000;\n" +
                        "  height: 20px;\n" +
                        "}\n\n" +
                        "a {\n" +
                        "  color: #ff0000;\n" +
                        "}\n\n" +
                        "#menu {\n" +
                        "  width: 520px;\n" +
                        "}\n\n" +
                        ".menu {\n" +
                        "  color: #000000;\n" +
                        "  background-color: #ff0000;\n" +
                        "}\n\n",
                compileResource("level3.icss")
        );
    }

    @Test
    void compilesArithmeticAndConditionalStylesheet() throws IOException {
        String css = compileResource("level4.icss");

        assertEquals(
                ".card {\n" +
                        "  width: 110px;\n" +
                        "  height: 50%;\n" +
                        "  background-color: #112233;\n" +
                        "}\n\n" +
                        "#footer {\n" +
                        "  width: 90px;\n" +
                        "}\n\n",
                css
        );
    }

    @Test
    void compilesPercentageArithmeticAndVariables() throws IOException {
        String css = compileResource("level5.icss");

        assertEquals(
                ".container {\n" +
                        "  width: 70%;\n" +
                        "  color: #336699;\n" +
                        "}\n\n" +
                        "aside {\n" +
                        "  height: 24px;\n" +
                        "}\n\n",
                css
        );
    }

    @Test
    void reportsSemanticErrors() {
        Pipeline pipeline = new Pipeline();
        pipeline.parseString("p { width: #ffffff; }");

        assertTrue(pipeline.isParsed());
        assertFalse(pipeline.check());
        assertFalse(pipeline.getErrors().isEmpty());
    }

    @Test
    void reportsSyntaxErrors() {
        Pipeline pipeline = new Pipeline();
        pipeline.parseString("p { width 10px; }");

        assertFalse(pipeline.isParsed());
        assertFalse(pipeline.getErrors().isEmpty());
    }

    private String compileResource(String resource) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(resource);
        assertNotNull(input, "Missing test resource: " + resource);

        String source;
        try (InputStream stream = input) {
            source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }

        Pipeline pipeline = new Pipeline();
        pipeline.parseString(source);
        assertTrue(pipeline.isParsed(), pipeline.getErrors().toString());
        assertTrue(pipeline.check(), pipeline.getErrors().toString());
        assertTrue(pipeline.isChecked());

        pipeline.transform();
        assertTrue(pipeline.isTransformed());
        return pipeline.generate();
    }
}
