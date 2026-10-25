package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;

import java.util.ArrayList;

public class Generator {

	public String generate(AST ast) {
		return styleSheetGenerator(ast.root);
	}

	private String styleSheetGenerator(Stylesheet root) {
		StringBuilder result = new StringBuilder();
		for(ASTNode stylerule : root.getChildren()){
			result.append(styleRuleGenerator((Stylerule) stylerule) + "\n\n");
		}
		return result.toString();
	}

	private String styleRuleGenerator(Stylerule stylerule) {
		StringBuilder result = new StringBuilder();
		for(ASTNode styler : stylerule.selectors){
			result.append(styler.toString()).append(" ");
		}
		result.append("{" + "\n");
		for(ASTNode declaration : stylerule.body){
			result.append(declarationGenerator((Declaration) declaration));
		}
		result.append("}");
		return result.toString();
	}

	private String declarationGenerator(Declaration declaration) {
		String result = "\t";
		result += declaration.property.name + ": ";
		result += ((Literal)declaration.expression).getValueAsString() + ";" + "\n";
		return result;
	}
	
}
