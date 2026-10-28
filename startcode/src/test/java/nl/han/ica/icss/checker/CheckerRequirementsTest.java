package nl.han.ica.icss.checker;

import nl.han.ica.icss.Pipeline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckerRequirementsTest {

    @Test
    void rejectsUndefinedAndForwardReferencedVariables() {
        assertSemanticError("p { width: Missing; }");
        assertSemanticError("First := Second; Second := 10px; p { width: First; }");
    }

    @Test
    void validatesOperationOperandTypesAndColorOperations() {
        assertSemanticError("p { width: 10px + 5%; }");
        assertSemanticError("p { height: 10px - 5%; }");
        assertSemanticError("p { width: 10px * 2px; }");
        assertSemanticError("p { width: 2 + 2; }");
        assertSemanticError("p { color: #ff0000 + #000000; }");
        assertSemanticError("p { color: #ff0000 - #000000; }");
        assertSemanticError("p { color: #ff0000 * 2; }");
        assertSemanticSuccess("p { width: 2 * 10px; height: 20% * 3; }");
        assertSemanticSuccess("p { width: 10px * 2; }");
        assertSemanticSuccess("Temp := 10; p { width: Temp * 5px; }");
        assertSemanticSuccess("Temp := 2 + 3; p { width: Temp * 5px; }");
    }

    @Test
    void validatesPropertyValueTypesAndSupportedProperties() {
        assertSemanticError("p { width: #ff0000; }");
        assertSemanticError("p { color: 10px; }");
        assertSemanticError("p { width: 10; }");
        assertSemanticError("p { margin: 10px; }");
        assertSemanticSuccess("p { color: #ff0000; background-color: #ffffff; width: 10px; height: 50%; }");
    }

    @Test
    void allowsReassignmentUsingThePreviousVariableValue() {
        Pipeline pipeline = parse("ParWidth := 500px; ParWidth := ParWidth + 2px; p { width: ParWidth; }");
        assertTrue(pipeline.check(), pipeline.getErrors().toString());

        pipeline.transform();

        assertTrue(pipeline.isTransformed());
        assertEquals("p {\n  width: 502px;\n}\n\n", pipeline.generate());
    }

    @Test
    void requiresBooleanIfConditions() {
        assertSemanticError("Condition := 10px; p { if [Condition] { width: 10px; } }");
        assertSemanticSuccess("Condition := TRUE; p { if [Condition] { width: 10px; } }");
        assertSemanticSuccess("p { if [FALSE] { width: 10px; } }");
    }

    @Test
    void enforcesVariableScopeAndDeclarationOrder() {
        assertSemanticError("p { LocalWidth := 10px; } q { width: LocalWidth; }");
        assertSemanticError("p { width: LocalWidth; LocalWidth := 10px; }");
        assertSemanticError("p { if [TRUE] { BranchWidth := 10px; } width: BranchWidth; }");
        assertSemanticError("p { if [TRUE] { BranchWidth := 10px; } else { width: BranchWidth; } }");
        assertSemanticSuccess("GlobalWidth := 10px; p { LocalWidth := GlobalWidth; width: LocalWidth; }");
    }

    private void assertSemanticError(String source) {
        Pipeline pipeline = parse(source);
        assertFalse(pipeline.check(), source);
        assertFalse(pipeline.getErrors().isEmpty(), source);
    }

    private void assertSemanticSuccess(String source) {
        Pipeline pipeline = parse(source);
        assertTrue(pipeline.check(), pipeline.getErrors().toString());
        assertTrue(pipeline.isChecked());
    }

    private Pipeline parse(String source) {
        Pipeline pipeline = new Pipeline();
        pipeline.parseString(source);
        assertTrue(pipeline.isParsed(), pipeline.getErrors().toString());
        return pipeline;
    }
}
