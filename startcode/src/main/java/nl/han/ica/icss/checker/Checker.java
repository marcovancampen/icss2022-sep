package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private HashMap<VariableReference, Literal> VariableReferences;

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();
        VariableReferences = new HashMap<>();
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
            VariableReferences.put(variableAssignment.name, (Literal) variableAssignment.expression);
        }
    }

    private void checkVariableReference(VariableReference Variable) {
        if(!VariableReferences.containsKey(Variable)) {
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
        if(getLiteral(addOperation.lhs).getClass() != getLiteral(addOperation.rhs).getClass()){
            addOperation.setError(getLiteral(addOperation.lhs).getClass().getSimpleName() + " Is not the same type as " + getLiteral(addOperation.rhs).getClass().getSimpleName());
        }
        if (getLiteral(addOperation.lhs).getClass() == ColorLiteral.class || getLiteral(addOperation.rhs).getClass() == ColorLiteral.class) {
            addOperation.setError("ColorLiteral is not allowed to be used in Calculation");
        }
    }
    private void checkMultiplyOperation(MultiplyOperation Multiplyoperation) {
        for(ASTNode child : Multiplyoperation.getChildren()){
            if (child instanceof ColorLiteral) {
                Multiplyoperation.setError("ColorLiteral is not allowed to be used in Calculation");
            }
            if(child instanceof ScalarLiteral){
                return;
            }
        }
        Multiplyoperation.setError("Multiply operation does not contain a ScalarLiteral");
    }
    private Literal getVariableExpression(Expression expression) {
        return VariableReferences.get(expression);
    }

    private void checkOperation(Operation operation) {
        if(operation instanceof AddOperation){
            checkAddOperation((AddOperation) operation);
        }
        if(operation instanceof MultiplyOperation){
            checkMultiplyOperation((MultiplyOperation) operation);
        }
    }

    private Literal getLiteral(Expression expression) {
        if(expression instanceof Operation){
            checkOperation((Operation) expression);
            for(ASTNode ex: expression.getChildren()){
                if (!(ex instanceof ScalarLiteral)) {
                    return getLiteral((Expression) ex);
                }

            }
        }
        if(expression instanceof VariableReference){
            return getVariableExpression((VariableReference) expression);
        }
        return (Literal) expression;
    }


    private void checkDeclaration(Declaration Declaration) {
        if (Declaration.expression.getClass() == VariableReference.class){
            checkVariableReference((VariableReference) Declaration.expression);
        }
        if(Declaration.expression instanceof Operation){
            checkOperation((Operation) Declaration.expression);
        }
        if(Declaration.property.name.equalsIgnoreCase("width") &&  getLiteral(Declaration.expression).getClass() != PixelLiteral.class) {
            Declaration.setError("Property 'width' does not allow type of: " + getLiteral(Declaration.expression).getClass().getSimpleName());
        }
        if (Declaration.property.name.equalsIgnoreCase("background-color") && getLiteral(Declaration.expression).getClass() != ColorLiteral.class) {
            Declaration.setError("Property 'background-color' does not allow type of: " + getLiteral(Declaration.expression).getClass().getSimpleName());
        }
        if (Declaration.property.name.equalsIgnoreCase("color") && getLiteral(Declaration.expression).getClass() != ColorLiteral.class) {
            Declaration.setError("Property 'color' does not allow type of: " + getLiteral(Declaration.expression).getClass().getSimpleName());
        }

    }




}


