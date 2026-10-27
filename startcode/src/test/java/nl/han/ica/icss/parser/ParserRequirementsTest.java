package nl.han.ica.icss.parser;

import nl.han.ica.icss.Pipeline;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ParserRequirementsTest {

    @Test
    void parsesSelectorsVariablesCalculationsAndNestedConditionals() throws IOException {
        String source;
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("assignment-features.icss")) {
            assertNotNull(input, "Missing assignment parser fixture");
            source = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }

        Pipeline pipeline = new Pipeline();
        pipeline.parseString(source);

        assertTrue(pipeline.isParsed(), pipeline.getErrors().toString());
        AST ast = pipeline.getAST();
        assertEquals(5, ast.root.body.size());
        assertTrue(ast.root.body.get(0) instanceof VariableAssignment);
        assertTrue(((VariableAssignment) ast.root.body.get(0)).expression instanceof PixelLiteral);
        assertTrue(ast.root.body.get(1) instanceof VariableAssignment);
        assertTrue(((VariableAssignment) ast.root.body.get(1)).expression instanceof BoolLiteral);

        Stylerule tagRule = (Stylerule) ast.root.body.get(2);
        assertEquals("p", tagRule.selectors.get(0).toString());
        assertTrue(tagRule.body.get(0) instanceof Declaration);
        assertTrue(((Declaration) tagRule.body.get(1)).expression instanceof ColorLiteral);

        IfClause outerIf = (IfClause) tagRule.body.get(2);
        assertTrue(outerIf.conditionalExpression instanceof VariableReference);
        assertTrue(((Declaration) outerIf.body.get(0)).expression instanceof PercentageLiteral);
        assertTrue(outerIf.body.get(1) instanceof IfClause);
        IfClause nestedIf = (IfClause) outerIf.body.get(1);
        assertTrue(nestedIf.conditionalExpression instanceof BoolLiteral);
        assertNotNull(nestedIf.elseClause);
        assertEquals(1, nestedIf.elseClause.body.size());

        assertEquals("#menu", ((Stylerule) ast.root.body.get(3)).selectors.get(0).toString());
        assertEquals(".active", ((Stylerule) ast.root.body.get(4)).selectors.get(0).toString());
    }

    @Test
    void rejectsSelectorsOutsideTheLanguageGrammar() {
        assertSyntaxError("P { color: #ff0000; }");
        assertSyntaxError("p.active { color: #ff0000; }");
        assertSyntaxError("div > a { color: #ff0000; }");
        assertSyntaxError("p { color: #fff; }");
        assertSyntaxError("if [TRUE] { p { width: 10px; } }");
    }

    private void assertSyntaxError(String source) {
        Pipeline pipeline = new Pipeline();
        pipeline.parseString(source);
        assertFalse(pipeline.isParsed(), source);
        assertFalse(pipeline.getErrors().isEmpty(), source);
    }
}
