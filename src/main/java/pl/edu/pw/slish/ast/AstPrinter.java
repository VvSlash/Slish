package pl.edu.pw.slish.ast;

import pl.edu.pw.slish.ast.expr.*;
import pl.edu.pw.slish.ast.stmt.*;

/**
 * Klasa do wyświetlania drzewa AST w czytelnej formie
 */
public class AstPrinter extends BaseNodeVisitor<String> {
    private int indentLevel = 0;
    private final String indentString = "  ";
    
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append(indentString);
        }
        return sb.toString();
    }
    
    @Override
    public String visit(pl.edu.pw.slish.ast.stmt.Program program) {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        indentLevel++;
        
        for (Node stmt : program.getStatements()) {
            sb.append(getIndent()).append(stmt.accept(this)).append("\n");
        }
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(Block block) {
        StringBuilder sb = new StringBuilder();
        sb.append("Block:\n");
        indentLevel++;
        
        for (Statement stmt : block.getStatements()) {
            sb.append(getIndent()).append(stmt.accept(this)).append("\n");
        }
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(Declaration declaration) {
        StringBuilder sb = new StringBuilder();
        sb.append("Declaration: type=").append(declaration.getType())
          .append(", name=").append(declaration.getName());
        
        if (declaration.getInitializer() != null) {
            sb.append("\n");
            indentLevel++;
            sb.append(getIndent()).append("initializer: ")
              .append(declaration.getInitializer().accept(this));
            indentLevel--;
        }
        
        return sb.toString();
    }
    
    @Override
    public String visit(Assignment assignment) {
        StringBuilder sb = new StringBuilder();
        sb.append("Assignment:\n");
        indentLevel++;
        
        sb.append(getIndent()).append("target: ")
          .append(assignment.getTarget().accept(this)).append("\n");
        sb.append(getIndent()).append("value: ")
          .append(assignment.getValue().accept(this));
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(IfStatement ifStatement) {
        StringBuilder sb = new StringBuilder();
        sb.append("IfStatement:\n");
        indentLevel++;
        
        sb.append(getIndent()).append("condition: ")
          .append(ifStatement.getCondition().accept(this)).append("\n");
        sb.append(getIndent()).append("thenBlock: ")
          .append(ifStatement.getThenBlock().accept(this));
        
        if (ifStatement.getElseBlock() != null) {
            sb.append("\n").append(getIndent()).append("elseBlock: ")
              .append(ifStatement.getElseBlock().accept(this));
        }
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(WhileLoop whileLoop) {
        StringBuilder sb = new StringBuilder();
        sb.append("WhileLoop:\n");
        indentLevel++;
        
        sb.append(getIndent()).append("condition: ")
          .append(whileLoop.getCondition().accept(this)).append("\n");
        sb.append(getIndent()).append("body: ")
          .append(whileLoop.getBody().accept(this));
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(ForLoop forLoop) {
        StringBuilder sb = new StringBuilder();
        
        if (forLoop.isForeach()) {
            sb.append("ForeachLoop:\n");
            indentLevel++;
            
            sb.append(getIndent()).append("collection: ")
              .append(forLoop.getCollection().accept(this)).append("\n");
            sb.append(getIndent()).append("body: ")
              .append(forLoop.getBody().accept(this));
        } else {
            sb.append("ForLoop:\n");
            indentLevel++;
            
            if (forLoop.getInitialization() != null) {
                sb.append(getIndent()).append("initialization: ")
                  .append(forLoop.getInitialization().accept(this)).append("\n");
            }
            
            if (forLoop.getCondition() != null) {
                sb.append(getIndent()).append("condition: ")
                  .append(forLoop.getCondition().accept(this)).append("\n");
            }
            
            if (forLoop.getIteration() != null) {
                sb.append(getIndent()).append("iteration: ")
                  .append(forLoop.getIteration().accept(this)).append("\n");
            }
            
            sb.append(getIndent()).append("body: ")
              .append(forLoop.getBody().accept(this));
        }
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(ReturnStatement returnStatement) {
        if (returnStatement.getValue() == null) {
            return "ReturnStatement";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("ReturnStatement:\n");
        indentLevel++;
        
        sb.append(getIndent()).append("value: ")
          .append(returnStatement.getValue().accept(this));
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(Variable variable) {
        return "Variable: " + variable.getName();
    }
    
    @Override
    public String visit(Literal literal) {
        return "Literal: type=" + literal.getType() + ", value=" + literal.getValue();
    }
    
    @Override
    public String visit(BinaryOperation binaryOperation) {
        StringBuilder sb = new StringBuilder();
        sb.append("BinaryOperation: operator=").append(binaryOperation.getOperator()).append("\n");
        indentLevel++;
        
        sb.append(getIndent()).append("left: ")
          .append(binaryOperation.getLeft().accept(this)).append("\n");
        sb.append(getIndent()).append("right: ")
          .append(binaryOperation.getRight().accept(this));
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(FunctionCall functionCall) {
        StringBuilder sb = new StringBuilder();
        sb.append("FunctionCall: name=").append(functionCall.getName());
        
        if (!functionCall.getArguments().isEmpty()) {
            sb.append("\n");
            indentLevel++;
            sb.append(getIndent()).append("arguments:");
            
            for (Expression arg : functionCall.getArguments()) {
                sb.append("\n");
                indentLevel++;
                sb.append(getIndent()).append(arg.accept(this));
                indentLevel--;
            }
            
            indentLevel--;
        }
        
        return sb.toString();
    }
    
    @Override
    public String visit(ArrayAccess arrayAccess) {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayAccess: array=").append(arrayAccess.getArrayName()).append("\n");
        indentLevel++;
        
        sb.append(getIndent()).append("index: ")
          .append(arrayAccess.getIndex().accept(this));
        
        indentLevel--;
        return sb.toString();
    }
    
    @Override
    public String visit(ArrayLiteral arrayLiteral) {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayLiteral:");
        
        if (!arrayLiteral.getElements().isEmpty()) {
            sb.append("\n");
            indentLevel++;
            sb.append(getIndent()).append("elements:");
            
            for (Expression element : arrayLiteral.getElements()) {
                sb.append("\n");
                indentLevel++;
                sb.append(getIndent()).append(element.accept(this));
                indentLevel--;
            }
            
            indentLevel--;
        }
        
        return sb.toString();
    }
    
    @Override
    public String visit(ReadExpression readExpression) {
        return "ReadExpression";
    }
    
    @Override
    public String visit(StringInterpolation stringInterpolation) {
        StringBuilder sb = new StringBuilder();
        sb.append("StringInterpolation: template=").append(stringInterpolation.getTemplate());
        
        if (!stringInterpolation.getVariables().isEmpty()) {
            sb.append("\n");
            indentLevel++;
            sb.append(getIndent()).append("variables:");
            
            for (var entry : stringInterpolation.getVariables().entrySet()) {
                sb.append("\n");
                indentLevel++;
                sb.append(getIndent()).append(entry.getKey()).append(": ")
                  .append(entry.getValue().accept(this));
                indentLevel--;
            }
            
            indentLevel--;
        }
        
        return sb.toString();
    }
    
    @Override
    public String visit(PipeExpression pipeExpression) {
        StringBuilder sb = new StringBuilder();
        sb.append("PipeExpression:");
        
        if (!pipeExpression.getElements().isEmpty()) {
            sb.append("\n");
            indentLevel++;
            sb.append(getIndent()).append("elements:");
            
            for (PipeElementNode element : pipeExpression.getElements()) {
                sb.append("\n");
                indentLevel++;
                sb.append(getIndent()).append(element.accept(this));
                indentLevel--;
            }
            
            indentLevel--;
        }
        
        return sb.toString();
    }
} 