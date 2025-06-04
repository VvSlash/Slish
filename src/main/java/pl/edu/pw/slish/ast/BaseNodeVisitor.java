package pl.edu.pw.slish.ast;

import pl.edu.pw.slish.ast.expr.*;
import pl.edu.pw.slish.ast.stmt.*;
import pl.edu.pw.slish.ast.expr.ExpressionAsPipeElement;
import pl.edu.pw.slish.ast.expr.TypeCastPipeExpression;
import pl.edu.pw.slish.ast.stmt.VariableAssignPipeStatement;
import pl.edu.pw.slish.ast.stmt.IfPipeStatement;
import pl.edu.pw.slish.ast.stmt.ReturnPipeStatement;

/**
 * Bazowa implementacja NodeVisitor, która może być rozszerzana.
 * Domyślnie wszystkie metody zwracają null.
 * @param <T> typ zwracany przez wizytatora
 */
public class BaseNodeVisitor<T> implements NodeVisitor<T> {
    protected final T defaultResult = null;

    @Override
    public T visit(Variable variable) {
        return defaultResult;
    }

    @Override
    public T visit(Literal literal) {
        return defaultResult;
    }

    @Override
    public T visit(BinaryOperation binaryOperation) {
        return defaultResult;
    }

    @Override
    public T visit(FunctionCall functionCall) {
        return defaultResult;
    }

    @Override
    public T visit(ArrayAccess arrayAccess) {
        return defaultResult;
    }

    @Override
    public T visit(ArrayLiteral arrayLiteral) {
        return defaultResult;
    }

    @Override
    public T visit(ReadExpression readExpression) {
        return defaultResult;
    }

    @Override
    public T visit(StringInterpolation stringInterpolation) {
        return defaultResult;
    }

    @Override
    public T visit(PipeExpression pipeExpression) {
        return defaultResult;
    }

    @Override
    public T visit(Block block) {
        return defaultResult;
    }

    @Override
    public T visit(Declaration declaration) {
        return defaultResult;
    }

    @Override
    public T visit(Assignment assignment) {
        return defaultResult;
    }

    @Override
    public T visit(IfStatement ifStatement) {
        return defaultResult;
    }

    @Override
    public T visit(WhileLoop whileLoop) {
        return defaultResult;
    }

    @Override
    public T visit(ForLoop forLoop) {
        return defaultResult;
    }

    @Override
    public T visit(ReturnStatement returnStatement) {
        return defaultResult;
    }
    
    @Override
    public T visit(FunctionDeclaration functionDeclaration) {
        return defaultResult;
    }

    @Override
    public T visit(pl.edu.pw.slish.ast.stmt.Program program) {
        return defaultResult;
    }

    @Override
    public T visit(ExpressionAsPipeElement expressionAsPipeElement) {
        return defaultResult;
    }

    @Override
    public T visit(TypeCastPipeExpression typeCastPipeExpression) {
        return defaultResult;
    }

    @Override
    public T visit(VariableAssignPipeStatement variableAssignPipeStatement) {
        return defaultResult;
    }

    @Override
    public T visit(IfPipeStatement ifPipeStatement) {
        return defaultResult;
    }

    @Override
    public T visit(ReturnPipeStatement returnPipeStatement) {
        return defaultResult;
    }

    @Override
    public T visit(UnaryOperation unaryOperation) {
        return defaultResult;
    }

    @Override
    public T visit(PrintStatement printStatement) {
        return defaultResult;
    }

    @Override
    public T visit(ArrayAssignment arrayAssignment) {
        return null;
    }
} 