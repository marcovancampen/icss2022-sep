package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Evaluator implements Transform {

    @Override
    public void apply(AST ast) {
        transformNodes(ast.root.body, new HashMap<>());
    }

    private void transformNodes(ArrayList<ASTNode> nodes, Map<String, Literal> variables) {
        int index = 0;
        while (index < nodes.size()) {
            ASTNode node = nodes.get(index);
            if (node instanceof VariableAssignment) {
                VariableAssignment assignment = (VariableAssignment) node;
                Literal value = evaluate(assignment.expression, variables);
                if (value != null) {
                    assignment.expression = copyLiteral(value);
                    variables.put(assignment.name.name, copyLiteral(value));
                }
                nodes.remove(index);
            } else if (node instanceof Stylerule) {
                Stylerule stylerule = (Stylerule) node;
                transformNodes(stylerule.body, new HashMap<>(variables));
                index++;
            } else if (node instanceof IfClause) {
                IfClause ifClause = (IfClause) node;
                Literal condition = evaluate(ifClause.conditionalExpression, variables);
                if (!(condition instanceof BoolLiteral)) {
                    index++;
                    continue;
                }

                ArrayList<ASTNode> selectedBody;
                if (((BoolLiteral) condition).value) {
                    selectedBody = ifClause.body;
                } else if (ifClause.elseClause != null) {
                    selectedBody = ifClause.elseClause.body;
                } else {
                    selectedBody = new ArrayList<>();
                }

                transformNodes(selectedBody, variables);
                nodes.remove(index);
                nodes.addAll(index, selectedBody);
                index += selectedBody.size();
            } else if (node instanceof Declaration) {
                Declaration declaration = (Declaration) node;
                Literal value = evaluate(declaration.expression, variables);
                if (value != null) {
                    declaration.expression = value;
                }
                index++;
            } else {
                index++;
            }
        }
        
    }

    private Literal evaluate(Expression expression, Map<String, Literal> variables) {
        if (expression instanceof Literal) {
            return copyLiteral((Literal) expression);
        }
        if (expression instanceof VariableReference) {
            Literal value = variables.get(((VariableReference) expression).name);
            return value == null ? null : copyLiteral(value);
        }
        if (!(expression instanceof Operation)) {
            return null;
        }

        Operation operation = (Operation) expression;
        Literal left = evaluate(operation.lhs, variables);
        Literal right = evaluate(operation.rhs, variables);
        if (left == null || right == null) {
            return null;
        }

        if (operation instanceof AddOperation) {
            return calculate(left, right, '+');
        }
        if (operation instanceof SubtractOperation) {
            return calculate(left, right, '-');
        }
        if (operation instanceof MultiplyOperation) {
            return multiply(left, right);
        }
        return null;
    }

    private Literal calculate(Literal left, Literal right, char operator) {
        if (left.getClass() != right.getClass()) {
            return null;
        }
        int leftValue;
        int rightValue;
        if (left instanceof PixelLiteral) {
            leftValue = ((PixelLiteral) left).value;
            rightValue = ((PixelLiteral) right).value;
            return new PixelLiteral(operator == '+' ? leftValue + rightValue : leftValue - rightValue);
        }
        if (left instanceof PercentageLiteral) {
            leftValue = ((PercentageLiteral) left).value;
            rightValue = ((PercentageLiteral) right).value;
            return new PercentageLiteral(operator == '+' ? leftValue + rightValue : leftValue - rightValue);
        }
        if (left instanceof ScalarLiteral) {
            leftValue = ((ScalarLiteral) left).value;
            rightValue = ((ScalarLiteral) right).value;
            return new ScalarLiteral(operator == '+' ? leftValue + rightValue : leftValue - rightValue);
        }
        return null;
    }

    private Literal multiply(Literal left, Literal right) {
        if (left instanceof ScalarLiteral) {
            return scale(right, ((ScalarLiteral) left).value);
        }
        if (right instanceof ScalarLiteral) {
            return scale(left, ((ScalarLiteral) right).value);
        }
        return null;
    }

    private Literal scale(Literal literal, int factor) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) literal).value * factor);
        }
        if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) literal).value * factor);
        }
        if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) literal).value * factor);
        }
        return null;
    }

    private Literal copyLiteral(Literal literal) {
        if (literal instanceof BoolLiteral) {
            return new BoolLiteral(((BoolLiteral) literal).value);
        }
        if (literal instanceof ColorLiteral) {
            return new ColorLiteral(((ColorLiteral) literal).value);
        }
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) literal).value);
        }
        if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) literal).value);
        }
        if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) literal).value);
        }
        return null;
    }
}
