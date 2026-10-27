package nl.han.ica.icss.generator;

import nl.han.ica.icss.Pipeline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorRequirementsTest {

    @Test
    void generatesCssWithTwoSpacesPerDeclaration() {
        Pipeline pipeline = new Pipeline();
        pipeline.parseString("p { width: 10px; color: #ff0000; }");
        assertTrue(pipeline.isParsed(), pipeline.getErrors().toString());
        assertTrue(pipeline.check(), pipeline.getErrors().toString());
        pipeline.transform();

        assertEquals("p {\n  width: 10px;\n  color: #ff0000;\n}\n\n", pipeline.generate());
    }
}
