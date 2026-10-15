package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();
        checkStyleSheet(ast.root);

        }

    private void checkStyleSheet(Stylesheet root) {
        checkStyleRule((Stylerule) root.getChildren().get(0));
    }

    private void checkStyleRule(Stylerule stylerule) {
        for (ASTNode child : stylerule.getChildren()){
            if (child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration Declaration) {
        if(Declaration.property.name.equalsIgnoreCase("width")){
            Declaration.setError("Property 'width' does not allow type of: " + Declaration.expression.getClass().getSimpleName());
        }
    }


}


