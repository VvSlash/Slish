package pl.edu.pw.slish.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import pl.edu.pw.slish.SlishBaseVisitor;
import pl.edu.pw.slish.SlishParser;
import pl.edu.pw.slish.ast.expr.ArrayAccess;
import pl.edu.pw.slish.ast.expr.ArrayLiteral;
import pl.edu.pw.slish.ast.expr.BinaryOperation;
import pl.edu.pw.slish.ast.expr.Expression;
import pl.edu.pw.slish.ast.expr.ExpressionAsPipeElement;
import pl.edu.pw.slish.ast.expr.FunctionCall;
import pl.edu.pw.slish.ast.expr.Literal;
import pl.edu.pw.slish.ast.expr.PipeExpression;
import pl.edu.pw.slish.ast.expr.ReadExpression;
import pl.edu.pw.slish.ast.expr.StringInterpolation;
import pl.edu.pw.slish.ast.expr.TypeCastPipeExpression;
import pl.edu.pw.slish.ast.expr.UnaryOperation;
import pl.edu.pw.slish.ast.expr.Variable;
import pl.edu.pw.slish.ast.stmt.ArrayAssignment;
import pl.edu.pw.slish.ast.stmt.Assignment;
import pl.edu.pw.slish.ast.stmt.Block;
import pl.edu.pw.slish.ast.stmt.Declaration;
import pl.edu.pw.slish.ast.stmt.ForLoop;
import pl.edu.pw.slish.ast.stmt.FunctionDeclaration;
import pl.edu.pw.slish.ast.stmt.IfPipeStatement;
import pl.edu.pw.slish.ast.stmt.IfStatement;
import pl.edu.pw.slish.ast.stmt.PrintStatement;
import pl.edu.pw.slish.ast.stmt.ReturnPipeStatement;
import pl.edu.pw.slish.ast.stmt.ReturnStatement;
import pl.edu.pw.slish.ast.stmt.Statement;
import pl.edu.pw.slish.ast.stmt.VariableAssignPipeStatement;
import pl.edu.pw.slish.ast.stmt.WhileLoop;

/**
 * Buduje AST na podstawie drzewa parsowania wygenerowanego przez ANTLR.
 */
public class AstBuilder extends SlishBaseVisitor<Node> {

    @Override
    public Node visitProgram(SlishParser.ProgramContext ctx) {
        List<Node> statements = new ArrayList<>();

        for (SlishParser.StatementContext stmtCtx : ctx.statement()) {
            Node stmt = visit(stmtCtx);
            if (stmt != null) {
                statements.add(stmt);
            }
        }

        return new Program(statements);
    }

    @Override
    public Node visitStatement(SlishParser.StatementContext ctx) {
        // Używamy indeksów dzieci zamiast specyficznych metod
        if (ctx.getChildCount() > 0) {
            return visit(ctx.getChild(0));
        }
        return super.visitStatement(ctx);
    }

    @Override
    public Node visitTypedDeclaration(SlishParser.TypedDeclarationContext ctx) {
        String type = ctx.type() != null ? ctx.type().getText() : "_";
        String name = ctx.IDENTIFIER().getText();
        Expression initializer = null;

        if (ctx.declarationBody() != null) {
            // Sprawdzamy czy to deklaracja funkcji
            if (ctx.declarationBody() instanceof SlishParser.ParameterDeclarationBodyContext) {
                SlishParser.ParameterDeclarationBodyContext paramBody =
                    (SlishParser.ParameterDeclarationBodyContext) ctx.declarationBody();

                if (paramBody.funcBody() != null) {
                    // To jest deklaracja funkcji
                    Node funcDecl = visit(paramBody);
                    if (funcDecl instanceof FunctionDeclaration) {
                        FunctionDeclaration fd = (FunctionDeclaration) funcDecl;
                        return new FunctionDeclaration(name, type, fd.getParameters(),
                            fd.getBody());
                    }
                }
            }
            // Inicjalizator może być wyrażeniem
            else if (ctx.declarationBody() instanceof SlishParser.AssignmentBodyContext) {
                SlishParser.AssignmentBodyContext body = (SlishParser.AssignmentBodyContext) ctx.declarationBody();
                initializer = (Expression) visit(body.expression());
            }
        }

        return new Declaration(type, name, initializer);
    }

    @Override
    public Node visitAssignmentBody(SlishParser.AssignmentBodyContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitPipelineExpr(SlishParser.PipelineExprContext ctx) {
        List<PipeElementNode> elements = new ArrayList<>();

        for (SlishParser.PipeElementContext eleCtx : ctx.pipeElement()) {
            Node node = visit(eleCtx);
            if (node instanceof PipeElementNode) {
                elements.add((PipeElementNode) node);
            } else if (node instanceof Expression) {
                elements.add(new ExpressionAsPipeElement((Expression) node));
            } else {
                System.err.println(
                    "Ostrzeżenie: Nieoczekiwany typ węzła w potoku: " + node.getClass().getName());
            }
        }

        return new PipeExpression(elements);
    }

    @Override
    public Node visitExpressionPipeElement(SlishParser.ExpressionPipeElementContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitTypeCastPipeElement(SlishParser.TypeCastPipeElementContext ctx) {
        String targetType = ctx.typeCast().type().getText();
        return new TypeCastPipeExpression(targetType);
    }

    @Override
    public Node visitVariableAssignPipeElement(SlishParser.VariableAssignPipeElementContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        String type = null;

        if (ctx.type() != null) {
            type = ctx.type().getText();
        } else if (ctx.UNDERSCORE() != null) {
            type = "_";
        }

        return new VariableAssignPipeStatement(type, varName);
    }

    @Override
    public Node visitIfPipeElement(SlishParser.IfPipeElementContext ctx) {
        Block thenBlock = (Block) visit(ctx.block());
        return new IfPipeStatement(thenBlock);
    }

    @Override
    public Node visitReturnPipeElement(SlishParser.ReturnPipeElementContext ctx) {
        return new ReturnPipeStatement();
    }

    @Override
    public Node visitBlock(SlishParser.BlockContext ctx) {
        List<Statement> statements = new ArrayList<>();

        for (SlishParser.StatementContext stmtCtx : ctx.statement()) {
            Node node = visit(stmtCtx);
            if (node instanceof Statement) {
                statements.add((Statement) node);
            }
        }

        return new Block(statements);
    }

    @Override
    public Node visitLiteralExpr(SlishParser.LiteralExprContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Node visitFunctionCallExpr(SlishParser.FunctionCallExprContext ctx) {
        Node declNode = visit(ctx.declaration());
        if (declNode == null || !(declNode instanceof Declaration)) {
            // Jeśli nie jest to deklaracja lub jest null, tworzymy funkcję z samej nazwy
            String funcName = ctx.declaration().getText().replace("/", "");

            List<Expression> arguments = new ArrayList<>();
            if (ctx.argumentList() != null) {
                for (SlishParser.ExpressionContext exprCtx : ctx.argumentList().expression()) {
                    Node arg = visit(exprCtx);
                    if (arg instanceof Expression) {
                        arguments.add((Expression) arg);
                    }
                }
            }

            return new FunctionCall(funcName, arguments);
        }

        Declaration funcDecl = (Declaration) declNode;
        String functionName = funcDecl.getName();

        List<Expression> arguments = new ArrayList<>();
        if (ctx.argumentList() != null) {
            for (SlishParser.ExpressionContext exprCtx : ctx.argumentList().expression()) {
                Node arg = visit(exprCtx);
                if (arg instanceof Expression) {
                    arguments.add((Expression) arg);
                }
            }
        }

        return new FunctionCall(functionName, arguments);
    }

    @Override
    public Node visitAddSubExpr(SlishParser.AddSubExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    @Override
    public Node visitMulDivExpr(SlishParser.MulDivExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    @Override
    public Node visitRelationalExpr(SlishParser.RelationalExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    @Override
    public Node visitEqualityExpr(SlishParser.EqualityExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    @Override
    public Node visitXorExpr(SlishParser.XorExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    @Override
    public Node visitAndExpr(SlishParser.AndExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    @Override
    public Node visitOrExpr(SlishParser.OrExprContext ctx) {
        return buildBinaryOperation(ctx.expression(0), ctx.op.getText(), ctx.expression(1));
    }

    private BinaryOperation buildBinaryOperation(ParserRuleContext leftCtx, String opText,
        ParserRuleContext rightCtx) {
        Expression left = (Expression) visit(leftCtx);
        Expression right = (Expression) visit(rightCtx);

        BinaryOperation.Operator operator;
        switch (opText) {
            case "+":
                operator = BinaryOperation.Operator.ADD;
                break;
            case "-":
                operator = BinaryOperation.Operator.SUBTRACT;
                break;
            case "*":
                operator = BinaryOperation.Operator.MULTIPLY;
                break;
            case "//":
                operator = BinaryOperation.Operator.DIVIDE;
                break;
            case "%":
                operator = BinaryOperation.Operator.MODULO;
                break;
            case ">":
                operator = BinaryOperation.Operator.GREATER_THAN;
                break;
            case "<":
                operator = BinaryOperation.Operator.LESS_THAN;
                break;
            case ">=":
                operator = BinaryOperation.Operator.GREATER_EQUAL;
                break;
            case "<=":
                operator = BinaryOperation.Operator.LESS_EQUAL;
                break;
            case "==":
                operator = BinaryOperation.Operator.EQUAL;
                break;
            case "!=":
                operator = BinaryOperation.Operator.NOT_EQUAL;
                break;
            case "and":
                operator = BinaryOperation.Operator.AND;
                break;
            case "or":
                operator = BinaryOperation.Operator.OR;
                break;
            case "xor":
                operator = BinaryOperation.Operator.XOR;
                break;
            default:
                throw new RuntimeException("Unknown operator: " + opText);
        }

        return new BinaryOperation(left, operator, right);
    }

//    @Override
//    public Node visitBinaryExpr(SlishParser.BinaryExprContext ctx) {
//        Expression left = (Expression) visit(ctx.expression(0));
//        Expression right = (Expression) visit(ctx.expression(1));
//        String operatorText = ctx.operator().getText();
//
//        BinaryOperation.Operator operator;
//        switch (operatorText) {
//            case "+":
//                operator = BinaryOperation.Operator.ADD;
//                break;
//            case "-":
//                operator = BinaryOperation.Operator.SUBTRACT;
//                break;
//            case "*":
//                operator = BinaryOperation.Operator.MULTIPLY;
//                break;
//            case "//":
//                operator = BinaryOperation.Operator.DIVIDE;
//                break;
//            case "and":
//                operator = BinaryOperation.Operator.AND;
//                break;
//            case "or":
//                operator = BinaryOperation.Operator.OR;
//                break;
//            case "xor":
//                operator = BinaryOperation.Operator.XOR;
//                break;
//            case ">":
//                operator = BinaryOperation.Operator.GREATER_THAN;
//                break;
//            case "<":
//                operator = BinaryOperation.Operator.LESS_THAN;
//                break;
//            case ">=":
//                operator = BinaryOperation.Operator.GREATER_EQUAL;
//                break;
//            case "<=":
//                operator = BinaryOperation.Operator.LESS_EQUAL;
//                break;
//            case "==":
//                operator = BinaryOperation.Operator.EQUAL;
//                break;
//            case "!=":
//                operator = BinaryOperation.Operator.NOT_EQUAL;
//                break;
//            default:
//                throw new RuntimeException("Nieznany operator: " + operatorText);
//        }
//
//        return new BinaryOperation(left, operator, right);
//    }

    @Override
    public Node visitNotExpr(SlishParser.NotExprContext ctx) {
        Expression inner = (Expression) visit(ctx.expression());
        return new UnaryOperation(UnaryOperation.Operator.NEG, inner);
    }


    @Override
    public Node visitPrintStmt(SlishParser.PrintStmtContext ctx) {
        List<Expression> arguments = new ArrayList<>();

        if (ctx.argumentList() != null) {
            for (SlishParser.ExpressionContext exprCtx : ctx.argumentList().expression()) {
                Node arg = visit(exprCtx);
                if (arg instanceof Expression) {
                    arguments.add((Expression) arg);
                }
            }
        }
        return new PrintStatement(arguments);
    }

    @Override
    public Node visitIdentifierExpr(SlishParser.IdentifierExprContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        return new Variable(name);
    }

    @Override
    public Node visitArrayAccessExpr(SlishParser.ArrayAccessExprContext ctx) {
        String arrayName = ctx.IDENTIFIER().getText();
        Expression index = (Expression) visit(ctx.expression());
        return new ArrayAccess(arrayName, index);
    }

    @Override
    public Node visitReadExpr(SlishParser.ReadExprContext ctx) {
        return new ReadExpression();
    }

    @Override
    public Node visitParenExpr(SlishParser.ParenExprContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitStringInterpExpr(SlishParser.StringInterpExprContext ctx) {
        return visit(ctx.stringInterpolation());
    }

    @Override
    public Node visitStringInterpolation(SlishParser.StringInterpolationContext ctx) {
        // Uproszczona obsługa interpolacji stringów
        if (ctx.STRING_WITH_INTERPOLATION() != null) {
            String text = ctx.STRING_WITH_INTERPOLATION().getText();
            return new StringInterpolation(text, new HashMap<>());
        }

        // Bardziej złożona interpolacja
        StringBuilder template = new StringBuilder();

        if (ctx.STRING_START() != null) {
            template.append(ctx.STRING_START().getText());

            for (int i = 0; i < ctx.interpolationPart().size(); i++) {
                template.append("{...}"); // Uproszczona reprezentacja
                if (i < ctx.STRING_MIDDLE().size()) {
                    template.append(ctx.STRING_MIDDLE(i).getText());
                }
            }

            if (ctx.STRING_END() != null) {
                template.append(ctx.STRING_END().getText());
            }
        }

        return new StringInterpolation(template.toString(), new HashMap<>());
    }

    @Override
    public Node visitConditionalStatement(SlishParser.ConditionalStatementContext ctx) {
        Expression condition = (Expression) visit(ctx.expression());
        Block thenBlock = (Block) visit(ctx.block());
        Block elseBlock = null;

        if (ctx.elseBlock() != null) {
            Node elseNode = visit(ctx.elseBlock());
            if (elseNode instanceof Block) {
                elseBlock = (Block) elseNode;
            } else if (elseNode instanceof Statement) {
                List<Statement> statements = new ArrayList<>();
                statements.add((Statement) elseNode);
                elseBlock = new Block(statements);
            }
        }

        return new IfStatement(condition, thenBlock, elseBlock);
    }

    @Override
    public Node visitElseBlock(SlishParser.ElseBlockContext ctx) {
        if (ctx.block() != null) {
            return visit(ctx.block());
        } else if (ctx.conditionalStatement() != null) {
            return visit(ctx.conditionalStatement());
        }
        return null;
    }

    @Override
    public Node visitWhileLoop(SlishParser.WhileLoopContext ctx) {
        Expression condition = (Expression) visit(ctx.expression());
        Block body = (Block) visit(ctx.block());
        return new WhileLoop(condition, body);
    }

    @Override
    public Node visitForLoop(SlishParser.ForLoopContext ctx) {
        if (ctx.getChildCount()
            > 5) {
            Node initialization = null;
            Expression condition = null;
            Expression iteration = null;

            if (ctx.declaration() != null) {
                initialization = visit(ctx.declaration());
            } else if (!ctx.assignment().isEmpty()) {
                initialization = visit(ctx.assignment(0));
            }

            if (!ctx.expression().isEmpty()) {
                condition = (Expression) visit(ctx.expression(0));
            }

            int semicolonCount = 0;
            int iterNodeIndex = -1;
            for (int i = 0; i < ctx.getChildCount(); i++) {
                if (ctx.getChild(i).getText().equals(";")) {
                    semicolonCount++;
                    if (semicolonCount == 2) {
                        if (i + 1 < ctx.getChildCount()) {
                            iterNodeIndex = i + 1;
                            break;
                        }
                    }
                }
            }

            if (iterNodeIndex != -1) {
                org.antlr.v4.runtime.tree.ParseTree iterParseTree = ctx.getChild(iterNodeIndex);
                if (iterParseTree instanceof SlishParser.ExpressionContext) {
                    iteration = (Expression) visit(iterParseTree);
                } else if (iterParseTree instanceof SlishParser.AssignmentContext) {
                    iteration = (Expression) visit(iterParseTree);
                }
            }

            Block body = (Block) visit(ctx.block());
            return new ForLoop(initialization, condition, iteration, body);
        } else {
            Expression collection = (Expression) visit(ctx.expression(0));
            Block body = (Block) visit(ctx.block());
            return new ForLoop(collection, body);
        }
    }

    @Override
    public Node visitLiteral(SlishParser.LiteralContext ctx) {
        if (ctx.INTEGER() != null) {
            int value = Integer.parseInt(ctx.INTEGER().getText());
            return new Literal(value, Literal.Type.INTEGER);
        } else if (ctx.FLOAT() != null) {
            float value = Float.parseFloat(ctx.FLOAT().getText());
            return new Literal(value, Literal.Type.FLOAT);
        } else if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            // Usunięcie cudzysłowów z początku i końca
            String value = text.substring(1, text.length() - 1);
            return new Literal(value, Literal.Type.STRING);
        } else if (ctx.BOOL() != null) {
            boolean value = Boolean.parseBoolean(ctx.BOOL().getText());
            return new Literal(value, Literal.Type.BOOLEAN);
        } else if (ctx.getText().equals("null")) {
            return new Literal(null, Literal.Type.NULL);
        }
        return null;
    }

    @Override
    public Node visitDeclarationAssignment(SlishParser.DeclarationAssignmentContext ctx) {
        Declaration declaration = (Declaration) visit(ctx.declaration());
        Expression value = (Expression) visit(ctx.expression());
        Variable target = new Variable(declaration.getName());
        return new Assignment(target, value); // or a specialized node if needed
    }

    @Override
    public Node visitVariableAssignment(SlishParser.VariableAssignmentContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Expression value = (Expression) visit(ctx.expression());
        Variable target = new Variable(name);
        return new Assignment(target, value);
    }

    @Override
    public Node visitArrayElementAssignment(SlishParser.ArrayElementAssignmentContext ctx) {
        String arrayName = ctx.IDENTIFIER().getText();
        Expression index = (Expression) visit(ctx.expression(0));
        Expression value = (Expression) visit(ctx.expression(1));
        return new ArrayAssignment(new Variable(arrayName), index, value);
    }



    @Override
    public Node visitArrayLiteralExpr(SlishParser.ArrayLiteralExprContext ctx) {
        return visit(ctx.arrayLiteral());
    }

    @Override
    public Node visitArrayLiteral(SlishParser.ArrayLiteralContext ctx) {
        List<Expression> elements = new ArrayList<>();

        if (ctx.expression() != null) {
            for (SlishParser.ExpressionContext exprCtx : ctx.expression()) {
                Node node = visit(exprCtx);
                if (node instanceof Expression) {
                    elements.add((Expression) node);
                }
            }
        }

        return new ArrayLiteral(elements);
    }

    @Override
    public Node visitReturnStmt(SlishParser.ReturnStmtContext ctx) {
        Expression value = null;
        if (ctx.expression() != null) {
            Node node = visit(ctx.expression());
            if (node instanceof Expression) {
                value = (Expression) node;
            }
        }

        return new ReturnStatement(value);
    }

    @Override
    public Node visitParameterDeclarationBody(SlishParser.ParameterDeclarationBodyContext ctx) {
        // Obsługa deklaracji funkcji lub wywołania funkcji z parametrami
        List<Declaration> parameters = new ArrayList<>();

        if (ctx.paramList() != null) {
            for (SlishParser.TypedParamContext paramCtx : ctx.paramList().typedParam()) {
                String paramType = paramCtx.type().getText();
                String paramName = paramCtx.IDENTIFIER().getText();
                parameters.add(new Declaration(paramType, paramName, null));
            }
        }

        if (ctx.funcBody() != null) {
            // To jest deklaracja funkcji
            Block body;
            if (ctx.funcBody().block() != null) {
                body = (Block) visit(ctx.funcBody().block());
            } else {
                // Pusta funkcja
                body = new Block(new ArrayList<>());
            }

            // Tworzenie deklaracji funkcji - nazwa będzie ustawiona w kontekście nadrzędnym
            return new FunctionDeclaration("", "void", parameters, body);
        }

        return null;
    }


}