package pl.edu.pw.slish.ast;

import pl.edu.pw.slish.ast.expr.*;
import pl.edu.pw.slish.ast.stmt.*;

/**
 * Interfejs wizytatora dla węzłów AST według wzorca projektowego Visitor
 */
public interface NodeVisitor<T> {
    // Wyrażenia
    T visit(Variable variable);
    T visit(Literal literal);
    T visit(BinaryOperation binaryOperation);
    T visit(FunctionCall functionCall);
    T visit(ArrayAccess arrayAccess);
    T visit(ArrayLiteral arrayLiteral);
    T visit(ReadExpression readExpression);
    T visit(StringInterpolation stringInterpolation);
    T visit(PipeExpression pipeExpression);
    T visit(ExpressionAsPipeElement expressionAsPipeElement);
    T visit(TypeCastPipeExpression typeCastPipeExpression);
    
    // Instrukcje
    T visit(Block block);
    T visit(Declaration declaration);
    T visit(Assignment assignment);
    T visit(IfStatement ifStatement);
    T visit(WhileLoop whileLoop);
    T visit(ForLoop forLoop);
    T visit(ReturnStatement returnStatement);
    T visit(FunctionDeclaration functionDeclaration);
    T visit(pl.edu.pw.slish.ast.stmt.Program program);
    T visit(VariableAssignPipeStatement variableAssignPipeStatement);
    T visit(IfPipeStatement ifPipeStatement);
    T visit(ReturnPipeStatement returnPipeStatement);
    T visit(UnaryOperation unaryOperation);
    T visit(PrintStatement printStatement);

    T visit(ArrayAssignment arrayAssignment);
}