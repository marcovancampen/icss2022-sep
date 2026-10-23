package nl.han.ica.icss.parser;

import java.util.ArrayList;
import java.util.Stack;


import com.google.errorprone.annotations.Var;
import com.sun.jdi.Value;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
		return ast;
    }

	@Override
	public void enterMultiplyCalculation(ICSSParser.MultiplyCalculationContext ctx) {
		currentContainer.push(new MultiplyOperation());
	}

	@Override
	public void exitMultiplyCalculation(ICSSParser.MultiplyCalculationContext ctx) {
		MultiplyOperation operation = (MultiplyOperation) currentContainer.pop();
		currentContainer.peek().addChild(operation);
	}

	@Override
	public void enterAddSubtractCalculation(ICSSParser.AddSubtractCalculationContext ctx) {
		Operation operation = ctx.operators2().getText().equals("+")
				? new AddOperation()
				: new SubtractOperation();
		currentContainer.push(operation);
	}

	@Override
	public void exitAddSubtractCalculation(ICSSParser.AddSubtractCalculationContext ctx) {
		Operation operation = (Operation) currentContainer.pop();
		currentContainer.peek().addChild(operation);
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		ASTNode styleSheet = new Stylesheet();
		currentContainer.push(styleSheet);
	}


	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
		ast.root = stylesheet;
	}

	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		ASTNode VariableAssignment = new VariableAssignment();
		currentContainer.push(VariableAssignment);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(variableAssignment);
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		ASTNode VariableRefrence = new VariableReference(ctx.getText());
		currentContainer.push(VariableRefrence);
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		VariableReference variableReference = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableReference);
	}


	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		ASTNode stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ASTNode classSelector = new ClassSelector(ctx.getText());
		currentContainer.push(classSelector);
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ASTNode classSelector = (ASTNode) currentContainer.pop();
		currentContainer.peek().addChild(classSelector);
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		ASTNode idSelector = new IdSelector(ctx.getText());
		currentContainer.push(idSelector);
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		ASTNode idSelector = (ASTNode) currentContainer.pop();
		currentContainer.peek().addChild(idSelector);
	}

	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		ASTNode tagSelector = new TagSelector(ctx.getText());
		currentContainer.push(tagSelector);
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		ASTNode tagSelector = (ASTNode) currentContainer.pop();
		currentContainer.peek().addChild(tagSelector);
	}

	@Override
	public void enterIfCondition(ICSSParser.IfConditionContext ctx) {
		ASTNode ifClause = new IfClause(new VariableReference("temporary set"), new ArrayList<>());
		currentContainer.push(ifClause);
	}

	@Override
	public void exitIfElseBlock(ICSSParser.IfElseBlockContext ctx) {
		IfClause ifBlock = (IfClause) currentContainer.pop();
		currentContainer.peek().addChild(ifBlock);
	}

	@Override
	public void enterElseBlock(ICSSParser.ElseBlockContext ctx) {
		ASTNode ElseClause = new ElseClause();
		currentContainer.push(ElseClause);
	}

	@Override
	public void exitElseBlock(ICSSParser.ElseBlockContext ctx) {
		ElseClause elseClause = (ElseClause) currentContainer.pop();
		currentContainer.peek().addChild(elseClause);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = new Declaration();
		currentContainer.push(declaration);
	}
	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		ASTNode propertyName = new PropertyName(ctx.getText());
		currentContainer.push(propertyName);
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		ASTNode propertyName = (ASTNode) currentContainer.pop();
		currentContainer.peek().addChild(propertyName);
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		ASTNode pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}
	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelLiteral);
	}

	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ASTNode colorLiteral = new ColorLiteral(ctx.getText());
		currentContainer.push(colorLiteral);
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ASTNode colorLiteral = (ASTNode) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		ASTNode boolLiteral = new BoolLiteral(ctx.getText());
		currentContainer.push(boolLiteral);
	}

	@Override
	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(boolLiteral);
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		ASTNode percentageLiteral = new PercentageLiteral(ctx.getText());
		currentContainer.push(percentageLiteral);
	}
	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
		currentContainer.peek().addChild(percentageLiteral);
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ASTNode  scalarLiteral = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalarLiteral);
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalarLiteral);
	}
}