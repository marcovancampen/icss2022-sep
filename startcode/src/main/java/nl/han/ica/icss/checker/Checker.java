package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private HashSet<VariableReference> VariableReferences;

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();
        VariableReferences = new HashSet<>();
        checkStyleSheet(ast.root);

        }

    private void checkStyleSheet(Stylesheet root) {
        for (ASTNode child : root.getChildren()){
            if(child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child);
            }
            if (child instanceof Stylerule){
                checkStyleRule((Stylerule) child);
            }


        }

    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        if(variableAssignment.expression == null){
            variableAssignment.setError("VariableAssignment" + variableAssignment.getNodeLabel() + " has no value");
        }else{
            VariableReferences.add(variableAssignment.name);
        }
    }

    private void checkkVariableReference(VariableReference Variable) {
        if(!VariableReferences.contains(Variable)){
            Variable.setError("Variable" + Variable.getNodeLabel() + " has not been initialized");
        }
    }

    private void checkStyleRule(Stylerule stylerule) {
        for (ASTNode child : stylerule.getChildren()){
            if (child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkAddOperation(AddOperation addOperation) {
        if(addOperation.getChildren().get(0).getClass() != addOperation.getChildren().get(1).getClass()){
            addOperation.setError(addOperation.getChildren().get(0).getClass().getSimpleName() + " Is not the same type as " + addOperation.getChildren().get(1).getClass().getSimpleName());
        }
    }

    private void checkDeclaration(Declaration Declaration) {
        if (Declaration.expression.getClass() == VariableReference.class){
            checkkVariableReference((VariableReference) Declaration.expression);
            return;
        }
        if(Declaration.expression instanceof AddOperation){
            checkAddOperation((AddOperation) Declaration.expression);
        }
        if(Declaration.property.name.equalsIgnoreCase("width") && Declaration.expression.getClass() != PixelLiteral.class) {
            Declaration.setError("Property 'width' does not allow type of: " + Declaration.expression.getClass().getSimpleName());
        }
        if (Declaration.property.name.equalsIgnoreCase("background-color") && Declaration.expression.getClass() != ColorLiteral.class) {
            Declaration.setError("Property 'background-color' does not allow type of: " + Declaration.expression.getClass().getSimpleName());
        }
        if (Declaration.property.name.equalsIgnoreCase("color") && Declaration.expression.getClass() != ColorLiteral.class) {
            Declaration.setError("Property 'color' does not allow type of: " + Declaration.expression.getClass().getSimpleName());
        }

    }


}


