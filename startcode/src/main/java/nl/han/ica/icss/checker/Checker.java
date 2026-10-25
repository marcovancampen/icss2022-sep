package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private HashMap<VariableReference, ArrayList<ASTNode>> VariableReferences;

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();
        VariableReferences = new HashMap<>();
        checkStyleSheet(ast.root);

        }

    private void checkStyleSheet(Stylesheet root) {
        for (ASTNode child : root.getChildren()){
            if(child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child, root);
            }
            if (child instanceof Stylerule){
                checkStyleRule((Stylerule) child);
            }
        }

    }

    private void checkVariableAssignment(VariableAssignment variableAssignment, ASTNode Parent) {
        if(variableAssignment.expression == null){
            variableAssignment.setError("VariableAssignment" + variableAssignment.getNodeLabel() + " has no value");

        }else{
            ArrayList<ASTNode> Values = new ArrayList<>();
            Values.add((Literal) variableAssignment.expression);
            Values.add(Parent);
            VariableReferences.put(variableAssignment.name, Values);
        }
    }

    private void checkVariableReference(VariableReference Variable, ASTNode Parent) {
        if(!VariableReferences.containsKey(Variable)) {
            Variable.setError(Variable.getNodeLabel() + " has not been initialized");
            return;
        }
        if(!checkScopeVariable(Variable, Parent)){
            Variable.setError(Variable.getNodeLabel() + " is not in scope");
        }
    }
    private boolean containsNode(ASTNode scope, ASTNode target) {
        if (scope == target) {
            return true;
        }
        for (ASTNode child : scope.getChildren()) {
            if (containsNode(child, target)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkScopeVariable(VariableReference variable, ASTNode parent) {
        ASTNode declarationScope = VariableReferences.get(variable).get(1);
        return containsNode(declarationScope, parent);

    }

    private void checkStyleRule(Stylerule stylerule) {
        for (ASTNode child : stylerule.getChildren()){
            if (child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
            if(child instanceof IfClause){
                checkIfClause((IfClause) child);
            }
        }
    }
    private void checkIfClause(IfClause ifClause) {
        if (ifClause.conditionalExpression instanceof VariableReference) {
            checkVariableReference((VariableReference) ifClause.conditionalExpression, ifClause);
        }
        Literal condition = getLiteral(ifClause.conditionalExpression);
        if (condition == null) {
            return;
        }
        if(condition.getClass() != BoolLiteral.class){
            ifClause.setError(ifClause.conditionalExpression.getNodeLabel() + " is not a boolean literal");
            return;
        }
        CheckIfElseClause(ifClause.body);
        if(ifClause.elseClause != null){
            CheckIfElseClause(ifClause.elseClause.body);
        }

    }


    private void CheckIfElseClause(ArrayList<ASTNode> body) {
        for(ASTNode child: body){
            if(child instanceof IfClause){
                checkIfClause((IfClause) child);
            }
            if(child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkAddOperation(AddOperation addOperation) {
        Literal left = getLiteral(addOperation.lhs);
        Literal right = getLiteral(addOperation.rhs);
        if (left == null || right == null) {
            return;
        }
        if(left.getClass() != right.getClass()){
            addOperation.setError(left.getClass().getSimpleName() + " Is not the same type as " + right.getClass().getSimpleName());
        }
        if (left.getClass() == ColorLiteral.class || right.getClass() == ColorLiteral.class) {
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

    private void checkSubtractOperation(SubtractOperation subtractOperation) {
        Literal left = getLiteral(subtractOperation.lhs);
        Literal right = getLiteral(subtractOperation.rhs);
        if (left == null || right == null) {
            return;
        }
        if(left.getClass() != right.getClass()){
            subtractOperation.setError(left.getClass().getSimpleName() + " Is not the same type as " + right.getClass().getSimpleName());
        }

        if (left.getClass() == ColorLiteral.class || right.getClass() == ColorLiteral.class) {
            subtractOperation.setError("ColorLiteral is not allowed to be used in Calculation");
        }
    }
    private Literal getVariableExpression(Expression expression) {
        ArrayList<ASTNode> node  = VariableReferences.get(expression);
        if (node == null){
            expression.setError("Variable " + expression.getNodeLabel() + " has not been initialized or is not inside scope");
            return null;
        }
        return (Literal) node.get(0);
    }

    private void checkOperation(Operation operation) {
        if(operation instanceof AddOperation){
            checkAddOperation((AddOperation) operation);
        }
        if(operation instanceof MultiplyOperation){
            checkMultiplyOperation((MultiplyOperation) operation);
        }
        if(operation instanceof SubtractOperation){
            checkSubtractOperation((SubtractOperation) operation);
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
            checkVariableReference((VariableReference) Declaration.expression, Declaration);
        }
        if(Declaration.expression instanceof Operation){
            checkOperation((Operation) Declaration.expression);
        }
        Literal literal = getLiteral(Declaration.expression);
        if (literal == null) {
            return;
        }

        if ((Declaration.property.name.equalsIgnoreCase("width")
                || Declaration.property.name.equalsIgnoreCase("height"))
                && !(literal instanceof PixelLiteral || literal instanceof PercentageLiteral)) {
            Declaration.setError("Property '" + Declaration.property.name + "' does not allow type of: " + literal.getClass().getSimpleName());
        }

        if ((Declaration.property.name.equalsIgnoreCase("background-color")
                || Declaration.property.name.equalsIgnoreCase("color"))
                && literal.getClass() != ColorLiteral.class) {
            Declaration.setError("Property '" + Declaration.property.name + "' does not allow type of: " + literal.getClass().getSimpleName());
        }

    }




}


