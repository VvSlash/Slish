package pl.edu.pw.slish.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;
import pl.edu.pw.slish.ast.Program;
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
 * Analizator typów dla języka Slish. Odpowiada za sprawdzanie poprawności typów w AST.
 */
public class TypeChecker implements NodeVisitor<Type> {

    private final Map<String, Type> variables = new HashMap<>();
    private final List<String> errors = new ArrayList<>();
    private final Map<String, FunctionType> functions = new HashMap<>();
    private final Map<Expression, Type> inferredTypes = new HashMap<>();


    /**
     * Sprawdza poprawność typów w drzewie AST.
     *
     * @param ast Drzewo AST do sprawdzenia
     * @return true jeśli program jest poprawny typowo, false w przeciwnym przypadku
     */
    public boolean typeCheck(Node ast) {
        // Resetujemy stan przed każdym sprawdzeniem
        variables.clear();
        errors.clear();
        functions.clear();

        // Rejestrujemy wbudowane funkcje
        registerBuiltinFunctions();

        // Rozpoczynamy sprawdzanie od korzenia drzewa
        if (ast instanceof Program) {
            return checkTypes((Program) ast);
        } else if (ast instanceof pl.edu.pw.slish.ast.stmt.Program) {
            pl.edu.pw.slish.ast.stmt.Program program = (pl.edu.pw.slish.ast.stmt.Program) ast;

            // Pierwsza faza - rejestracja wszystkich deklaracji funkcji
            for (Node statement : program.getStatements()) {
                if (statement instanceof FunctionDeclaration) {
                    visit((FunctionDeclaration) statement);
                }
            }

            // Druga faza - rejestracja wszystkich deklaracji zmiennych
            for (Node statement : program.getStatements()) {
                if (statement instanceof Declaration) {
                    Declaration decl = (Declaration) statement;
                    Type type = Type.fromTypeName(decl.getType());
                    variables.put(decl.getName(), type);
                }
            }

            // Trzecia faza - sprawdzanie typów
            visit(program);

            // Sprawdzamy czy są błędy
            return errors.isEmpty();
        }

        // Jeśli ast nie jest programem, nie możemy poprawnie sprawdzić typów
        errors.add("Błąd: Oczekiwano programu do sprawdzenia typów");
        return false;
    }

    /**
     * Sprawdza poprawność typów w programie.
     *
     * @param program Program AST do sprawdzenia
     * @return true jeśli program jest poprawny typowo, false w przeciwnym przypadku
     */
    public boolean checkTypes(Program program) {
        // Rejestrujemy wbudowane funkcje
        registerBuiltinFunctions();

        // Przeprowadzamy pierwszą fazę - rejestracja wszystkich deklaracji
        for (Node statement : program.getStatements()) {
            if (statement instanceof Declaration) {
                Declaration decl = (Declaration) statement;
                Type type = Type.fromTypeName(decl.getType());
                variables.put(decl.getName(), type);
            }
        }

        // Druga faza - sprawdzanie typów
        visit((pl.edu.pw.slish.ast.stmt.Program) program);

        // Sprawdzamy czy są błędy
        return errors.isEmpty();
    }

    /**
     * Zwraca listę błędów znalezionych podczas analizy typów.
     */
    public List<String> getErrors() {
        return errors;
    }

    private void registerBuiltinFunctions() {
        // Wbudowana funkcja print
        FunctionType printFunction = new FunctionType(Type.VOID);
        printFunction.addParam(Type.DYNAMIC);
        functions.put("print", printFunction);

        // Wbudowana funkcja add (dodawanie)
        FunctionType addFunction = new FunctionType(Type.INTEGER);
        addFunction.addParam(Type.INTEGER);
        addFunction.addParam(Type.INTEGER);
        functions.put("add", addFunction);

        // Funkcja rzutowania na string
        FunctionType strFunction = new FunctionType(Type.STRING);
        strFunction.addParam(Type.DYNAMIC);
        functions.put("str", strFunction);

        // Funkcja wczytywania danych od użytkownika
        FunctionType readFunction = new FunctionType(Type.DYNAMIC);
        functions.put("read", readFunction);

        // Funkcje porównania
        FunctionType ltFunction = new FunctionType(Type.BOOLEAN);
        ltFunction.addParam(Type.DYNAMIC);
        ltFunction.addParam(Type.DYNAMIC);
        functions.put("lt", ltFunction);

        FunctionType gtFunction = new FunctionType(Type.BOOLEAN);
        gtFunction.addParam(Type.DYNAMIC);
        gtFunction.addParam(Type.DYNAMIC);
        functions.put("gt", gtFunction);

        FunctionType eqFunction = new FunctionType(Type.BOOLEAN);
        eqFunction.addParam(Type.DYNAMIC);
        eqFunction.addParam(Type.DYNAMIC);
        functions.put("eq", eqFunction);

        // Funkcje konwersji typów
        FunctionType strFromIntFunction = new FunctionType(Type.STRING);
        strFromIntFunction.addParam(Type.INTEGER);
        functions.put("strFromInt", strFromIntFunction);
    }

    @Override
    public Type visit(pl.edu.pw.slish.ast.stmt.Program program) {
        for (Node statement : program.getStatements()) {
            statement.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visit(Literal literal) {
        switch (literal.getType()) {
            case INTEGER:
                inferredTypes.put(literal, Type.INTEGER);
                return Type.INTEGER;
            case FLOAT32: // NEW
                inferredTypes.put(literal, Type.FLOAT32);
                return Type.FLOAT32;
            case FLOAT64: // NEW
                inferredTypes.put(literal, Type.FLOAT64);
                return Type.FLOAT64;
            // case FLOAT: // OLD - Remove
            //     inferredTypes.put(literal, Type.FLOAT64); // Default old FLOAT literal to FLOAT64
            //     return Type.FLOAT64;
            case STRING:
                inferredTypes.put(literal, Type.STRING);
                return Type.STRING;
            case BOOLEAN:
                inferredTypes.put(literal, Type.BOOLEAN);
                return Type.BOOLEAN;
            default:
                inferredTypes.put(literal, Type.DYNAMIC);
                return Type.DYNAMIC;
        }
    }

    @Override
    public Type visit(Variable variable) {
        String name = variable.getName();
        if (!variables.containsKey(name)) {
            errors.add("Błąd: Niezadeklarowana zmienna: " + name);
            return Type.DYNAMIC;
        }
        inferredTypes.put(variable, variables.get(name));
        return variables.get(name);
    }

    @Override
    public Type visit(FunctionCall functionCall) {
        String functionName = functionCall.getFunctionName();

        if (!functions.containsKey(functionName)) {
            errors.add("Błąd: Nieznana funkcja: " + functionName);
            return Type.DYNAMIC;
        }

        FunctionType functionType = functions.get(functionName);
        List<Expression> args = functionCall.getArguments();

        // Sprawdzamy liczbę argumentów
        if (args.size() != functionType.getParamTypes().size()) {
            errors.add("Błąd: Niewłaściwa liczba argumentów dla funkcji " + functionName +
                ". Oczekiwano: " + functionType.getParamTypes().size() +
                ", otrzymano: " + args.size());
            return functionType.getReturnType();
        }

        // Sprawdzamy typy argumentów
        for (int i = 0; i < args.size(); i++) {
            Type argType = args.get(i).accept(this);
            Type expectedType = functionType.getParamTypes().get(i);

            if (expectedType != Type.DYNAMIC && !argType.canCastTo(expectedType)) {
                errors.add("Błąd: Niewłaściwy typ argumentu #" + (i + 1) + " dla funkcji " +
                    functionName + ". Oczekiwano: " + expectedType +
                    ", otrzymano: " + argType);
            }
        }

        // Zwracamy typ wartości zwracanej przez funkcję
        return functionType.getReturnType();
    }

    @Override
    public Type visit(BinaryOperation binaryOperation) {
        Type leftType = binaryOperation.getLeft().accept(this);
        Type rightType = binaryOperation.getRight().accept(this);
        Type resultType = Type.DYNAMIC; // Default

        switch (binaryOperation.getOperator()) {
            case ADD:
            case SUBTRACT:
            case MULTIPLY:
            case DIVIDE:
                // Type promotion: int/float32 + float64 -> float64
                // int + float32 -> float32
                // int + int -> int
                if (leftType == Type.FLOAT64 || rightType == Type.FLOAT64) {
                    resultType = Type.FLOAT64;
                } else if (leftType == Type.FLOAT32 || rightType == Type.FLOAT32) {
                    resultType = Type.FLOAT32;
                } else if (leftType == Type.INTEGER && rightType == Type.INTEGER) {
                    resultType = Type.INTEGER;
                } else {
                    // If one is numeric and other is not (excluding dynamic)
                    boolean leftIsNumeric = (leftType == Type.INTEGER || leftType == Type.FLOAT32 || leftType == Type.FLOAT64);
                    boolean rightIsNumeric = (rightType == Type.INTEGER || rightType == Type.FLOAT32 || rightType == Type.FLOAT64);
                    if (leftIsNumeric && rightIsNumeric) { // Should have been caught by above
                        errors.add("Błąd: Mieszane typy numeryczne bez jasnej promocji: " + leftType + " i " + rightType);
                    } else if (leftType != Type.DYNAMIC && rightType != Type.DYNAMIC) {
                        errors.add("Błąd: Operandy arytmetyczne muszą być numeryczne. Otrzymano: " + leftType + " i " + rightType);
                    }
                    // If dynamic is involved, result remains dynamic unless promotion is clear
                    if (leftType == Type.DYNAMIC || rightType == Type.DYNAMIC) resultType = Type.DYNAMIC;
                }
                // Check if operands are numeric or dynamic
                if (!((leftType == Type.INTEGER || leftType == Type.FLOAT32 || leftType == Type.FLOAT64 || leftType == Type.DYNAMIC) &&
                    (rightType == Type.INTEGER || rightType == Type.FLOAT32 || rightType == Type.FLOAT64 || rightType == Type.DYNAMIC))) {
                    if (leftType != Type.DYNAMIC && rightType != Type.DYNAMIC) { // Avoid error if one is dynamic
                        errors.add("Błąd: Operandy operacji " + binaryOperation.getOperator() +
                            " muszą być typu numerycznego. Otrzymano: " + leftType + " i " + rightType);
                    }
                }
                break;

            case MODULO:
                if (!((leftType == Type.INTEGER || leftType == Type.DYNAMIC) &&
                    (rightType == Type.INTEGER || rightType == Type.DYNAMIC))) {
                    if (leftType != Type.DYNAMIC && rightType != Type.DYNAMIC) {
                        errors.add("Błąd: Operandy modulo muszą być całkowite. Otrzymano: " + leftType + " i " + rightType);
                    }
                }
                resultType = (leftType == Type.INTEGER && rightType == Type.INTEGER) ? Type.INTEGER : Type.DYNAMIC;
                break;

            case EQUAL:
            case NOT_EQUAL:
            case GREATER_THAN:
            case LESS_THAN:
            case GREATER_EQUAL:
            case LESS_EQUAL:
                // Allow comparisons between any numeric types or dynamic
                boolean leftIsComparable = (leftType == Type.INTEGER || leftType == Type.FLOAT32 || leftType == Type.FLOAT64 || leftType == Type.BOOLEAN || leftType == Type.STRING || leftType == Type.DYNAMIC);
                boolean rightIsComparable = (rightType == Type.INTEGER || rightType == Type.FLOAT32 || rightType == Type.FLOAT64 || rightType == Type.BOOLEAN || rightType == Type.STRING || rightType == Type.DYNAMIC);

                if (!(leftIsComparable && rightIsComparable)) {
                    errors.add("Błąd: Nie można porównać typów: " + leftType + " i " + rightType);
                }
                // Additionally, ensure they are somewhat compatible for comparison if not dynamic
                if (leftType != Type.DYNAMIC && rightType != Type.DYNAMIC) {
                    boolean numericComparison = (leftType == Type.INTEGER || leftType == Type.FLOAT32 || leftType == Type.FLOAT64) &&
                        (rightType == Type.INTEGER || rightType == Type.FLOAT32 || rightType == Type.FLOAT64);
                    boolean stringComparison = (leftType == Type.STRING && rightType == Type.STRING);
                    boolean booleanComparison = (leftType == Type.BOOLEAN && rightType == Type.BOOLEAN);

                    if (!(numericComparison || stringComparison || booleanComparison)) {
                        errors.add("Błąd: Niezgodne typy w porównaniu: " + leftType + " i " + rightType);
                    }
                }
                resultType = Type.BOOLEAN;
                break;

            case AND:
            case OR:
            case XOR:
                if (!((leftType == Type.BOOLEAN || leftType == Type.DYNAMIC) &&
                    (rightType == Type.BOOLEAN || rightType == Type.DYNAMIC))) {
                    if (leftType != Type.DYNAMIC && rightType != Type.DYNAMIC) {
                        errors.add("Błąd: Operandy logiczne muszą być typu boolean. Otrzymano: " + leftType + " i " + rightType);
                    }
                }
                resultType = (leftType == Type.BOOLEAN && rightType == Type.BOOLEAN) ? Type.BOOLEAN : Type.DYNAMIC;
                break;

            default:
                errors.add("Nieznany operator: " + binaryOperation.getOperator());
                resultType = Type.DYNAMIC;
                break;
        }
        inferredTypes.put(binaryOperation, resultType);
        return resultType;
    }


    @Override
    public Type visit(StringInterpolation stringInterpolation) {
        // Sprawdzamy poprawność interpolacji - uproszczona wersja
        inferredTypes.put(stringInterpolation, Type.STRING);
        return Type.STRING;
    }

    @Override
    public Type visit(Declaration declaration) {
        Type declaredType;
        try {
            declaredType = Type.fromTypeName(declaration.getType());
        } catch (IllegalArgumentException e) {
            errors.add("Błąd: Nieznany typ: " + declaration.getType() + " dla zmiennej " + declaration.getName());
            declaredType = Type.DYNAMIC;
        }

        variables.put(declaration.getName(), declaredType); // Declare variable first

        if (declaration.getInitializer() != null) {
            if (declaration.getInitializer() instanceof ReadExpression) {
                ReadExpression readExpr = (ReadExpression) declaration.getInitializer();
                readExpr.setExpectedType(declaredType); // Pass context to ReadExpression
            }

            Type initType = declaration.getInitializer().accept(this);
            inferredTypes.put(declaration.getInitializer(), initType); // Store inferred type of initializer


            if (declaredType.isArray() && initType.isArray()) {
                Type declaredElementType = declaredType.getElementType();
                Type initElementType = initType.getElementType();
                if (declaredElementType != null && initElementType != null && !initElementType.canCastTo(declaredElementType)) {
                    errors.add("Błąd: Niezgodność typów elementów tablicy w deklaracji " + declaration.getName() +
                        ". Oczekiwano: " + declaredElementType + "[], otrzymano: " + initElementType + "[]");
                }
            } else if (declaredType != Type.DYNAMIC && !initType.canCastTo(declaredType)) {
                errors.add("Błąd: Niezgodność typów w deklaracji " + declaration.getName() +
                    ". Oczekiwano: " + declaredType + ", otrzymano: " + initType);
            }
            // If declaredType is DYNAMIC, it takes the initType
            if (declaredType == Type.DYNAMIC && initType != Type.DYNAMIC) {
                variables.put(declaration.getName(), initType); // Update variable type if inferred
                // Return the inferred type for the declaration statement itself if it was dynamic
                // This is a bit unconventional, visit(Declaration) usually returns VOID or the declared type
                // For simplicity, we'll stick to returning the explicitly declared type or dynamic.
            }
        }
        // The "type" of a declaration statement itself is usually void,
        // or the type of the variable declared if it were an expression.
        // For type checking, its side effect (declaring a variable) is most important.
        return declaredType; // Return the (potentially updated) declared type
    }


    @Override
    public Type visit(Assignment node) {
        Expression target = node.getTarget();
        Expression value = node.getValue();

        Type targetType = target.accept(this);
        Type valueType = value.accept(this);

        if (!targetType.equals(valueType)) {
            errors.add("Type mismatch in assignment: cannot assign " + valueType + " to " + targetType);
        }

        inferredTypes.put(node, targetType); // Assignment expression gets the type of the target
        return targetType;
    }


    @Override
    public Type visit(PipeExpression pipeExpression) {
        List<PipeElementNode> elements = pipeExpression.getElements();
        if (elements.isEmpty()) {
            return Type.VOID;
        }

        // Wstępna logika sprawdzania typów dla potoków - wymaga rozbudowy
        Type currentPipeType = Type.VOID; // Typ wartości przekazywanej w potoku

        for (PipeElementNode elementNode : elements) {
            if (elementNode instanceof ExpressionAsPipeElement) {
                Expression expr = ((ExpressionAsPipeElement) elementNode).getExpression();

                // Jeśli to pierwsze wyrażenie, po prostu pobieramy jego typ
                if (currentPipeType == Type.VOID) {
                    currentPipeType = expr.accept(this);
                }
                // Jeśli to wywołanie funkcji i nie jest to pierwszy element potoku,
                // traktujemy pierwszy argument jako pochodzący z potoku
                else if (expr instanceof FunctionCall) {
                    FunctionCall functionCall = (FunctionCall) expr;
                    String functionName = functionCall.getFunctionName();

                    if (!functions.containsKey(functionName)) {
                        errors.add("Błąd: Nieznana funkcja: " + functionName);
                        continue;
                    }

                    FunctionType functionType = functions.get(functionName);
                    List<Expression> args = functionCall.getArguments();

                    // Sprawdzamy argumenty funkcji, ale pierwszy pochodzi z potoku
                    // więc de facto mamy o jeden argument mniej w wywołaniu
                    if (args.size() + 1 != functionType.getParamTypes().size()) {
                        // Specjalne przypadki dla funkcji, które można wywołać z różną liczbą argumentów
                        if (functionName.equals("print") || functionName.equals("add")) {
                            // Funkcja print może być wywołana z dowolną liczbą argumentów
                            currentPipeType = Type.VOID; // print zwraca void
                            continue;
                        }

                        errors.add(
                            "Błąd: Niewłaściwa liczba argumentów dla funkcji " + functionName +
                                " w potoku. Oczekiwano: " + functionType.getParamTypes().size() +
                                " (wliczając wartość z potoku), otrzymano: " + (args.size() + 1));
                    }

                    // Sprawdzamy zgodność typu z potoku jako pierwszy argument funkcji
                    Type expectedFirstArgType = functionType.getParamTypes().get(0);
                    if (expectedFirstArgType != Type.DYNAMIC && !currentPipeType.canCastTo(
                        expectedFirstArgType)) {
                        errors.add("Błąd: Niewłaściwy typ dla pierwszego argumentu funkcji " +
                            functionName + " w potoku. Oczekiwano: " + expectedFirstArgType +
                            ", otrzymano z potoku: " + currentPipeType);
                    }

                    // Sprawdzamy pozostałe argumenty (o ile są)
                    for (int i = 0; i < args.size(); i++) {
                        Type argType = args.get(i).accept(this);
                        Type expectedType = functionType.getParamTypes()
                            .get(i + 1); // +1 bo pierwszy argument jest z potoku

                        if (expectedType != Type.DYNAMIC && !argType.canCastTo(expectedType)) {
                            errors.add(
                                "Błąd: Niewłaściwy typ argumentu #" + (i + 2) + " dla funkcji " +
                                    functionName + " w potoku. Oczekiwano: " + expectedType +
                                    ", otrzymano: " + argType);
                        }
                    }

                    // Ustawiamy typ wyniku funkcji
                    currentPipeType = functionType.getReturnType();
                } else {
                    // Dla innych wyrażeń, jeśli to nie pierwszy element potoku, 
                    // nie jest jasne jak obsłużyć potokowość - zgłaszamy błąd
                    if (currentPipeType != Type.VOID) {
                        errors.add("Błąd: Nieobsługiwany wzorzec w potoku. Wyrażenie " +
                            expr.getClass().getSimpleName()
                            + " nie może być użyte w środku potoku.");
                    }
                    currentPipeType = expr.accept(this);
                }
            } else if (elementNode instanceof TypeCastPipeExpression) {
                // Tutaj logika dla TypeCastPipeExpression
                // Powinna sprawdzić, czy currentPipeType da się rzutować na targetType
                // i zwrócić targetType
                TypeCastPipeExpression castExpr = (TypeCastPipeExpression) elementNode;
                Type targetType = Type.fromTypeName(castExpr.getTargetType());

                // Nie musimy sprawdzać zgodności typów przy rzutowaniu w potoku
                // Dozwalamy jawne rzutowanie między typami w potoku

                currentPipeType = targetType;
            } else if (elementNode instanceof VariableAssignPipeStatement) {
                // Tutaj logika dla VariableAssignPipeStatement
                // Powinna sprawdzić, czy currentPipeType pasuje do typu zmiennej
                VariableAssignPipeStatement assignStmt = (VariableAssignPipeStatement) elementNode;
                Type varType;

                if (assignStmt.getDeclaredType() == null || assignStmt.getDeclaredType()
                    .equals("_")) {
                    varType = currentPipeType; // Wnioskowanie typu
                } else {
                    varType = Type.fromTypeName(assignStmt.getDeclaredType());
                }

                if (variables.containsKey(assignStmt.getVariableName())) {
                    // Można sprawdzić czy typy są zgodne z już zadeklarowaną zmienną
                    Type existingType = variables.get(assignStmt.getVariableName());
                    if (existingType != Type.DYNAMIC && varType != Type.DYNAMIC
                        && !varType.canCastTo(existingType)) {
                        errors.add(
                            "Błąd: Niezgodność typów przy przypisaniu w potoku do istniejącej zmiennej '"
                                +
                                assignStmt.getVariableName() + "'. Zmienna ma typ " + existingType +
                                ", przypisuje typ " + varType);
                    }
                } else {
                    variables.put(assignStmt.getVariableName(), varType);
                }

                // Wartość płynie dalej - nie zmieniamy currentPipeType
            } else if (elementNode instanceof IfPipeStatement) {
                // Tutaj logika dla IfPipeStatement
                // currentPipeType powinien być BOOLEAN
                if (currentPipeType != Type.BOOLEAN && currentPipeType != Type.DYNAMIC) {
                    errors.add("Błąd: Warunek if w potoku musi być typu BOOLEAN, otrzymano: "
                        + currentPipeType);
                }
                // Typem wyjściowym może być typ z bloku then, lub typ wejściowy jeśli blok then nic nie zmienia
                // To wymaga bardziej zaawansowanej analizy, na razie zachowujemy currentPipeType
                ((IfPipeStatement) elementNode).getThenBlock().accept(this);
            } else if (elementNode instanceof ReturnPipeStatement) {
                // Tutaj logika dla ReturnPipeStatement
                // currentPipeType jest zwracany, dalsze elementy potoku nie mają znaczenia
                // Należy sprawdzić zgodność z typem zwracanym funkcji (jeśli jesteśmy w funkcji)
                return currentPipeType; // Kończy przetwarzanie potoku
            } else {
                // Nieznany element potoku
                errors.add("Błąd: Nieobsługiwany typ elementu w potoku: " + elementNode.getClass()
                    .getSimpleName());
                currentPipeType = Type.DYNAMIC;
            }
        }

        inferredTypes.put(pipeExpression, currentPipeType);

        return currentPipeType; // Zwraca typ ostatniego elementu potoku
    }

    @Override
    public Type visit(Block block) {
        for (Statement statement : block.getStatements()) {
            statement.accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visit(IfStatement ifStatement) {
        // Sprawdzamy, czy warunek jest typu boolean
        Type conditionType = ifStatement.getCondition().accept(this);
        if (conditionType != Type.BOOLEAN) {
            errors.add(
                "Błąd: Warunek instrukcji if musi być typu boolean, otrzymano: " + conditionType);
        }

        // Sprawdzamy blok then
        ifStatement.getThenBlock().accept(this);

        // Sprawdzamy blok else, jeśli istnieje
        if (ifStatement.hasElse()) {
            ifStatement.getElseBlock().accept(this);
        }

        return Type.VOID;
    }

    @Override
    public Type visit(WhileLoop whileLoop) {
        // Sprawdzamy, czy warunek jest typu boolean
        Type conditionType = whileLoop.getCondition().accept(this);
        if (conditionType != Type.BOOLEAN) {
            errors.add(
                "Błąd: Warunek pętli while musi być typu boolean, otrzymano: " + conditionType);
        }

        // Sprawdzamy blok
        whileLoop.getBody().accept(this);

        return Type.VOID;
    }

    @Override
    public Type visit(ForLoop forLoop) {
        // Nowy zakres dla zmiennych
        Map<String, Type> outerScope = new HashMap<>(variables);

        // Sprawdzamy inicjalizację
        if (forLoop.getInitialization() != null) {
            forLoop.getInitialization().accept(this);
        }

        // Sprawdzamy warunek
        if (forLoop.getCondition() != null) {
            Type conditionType = forLoop.getCondition().accept(this);
            if (conditionType != Type.BOOLEAN && conditionType != Type.DYNAMIC) {
                errors.add(
                    "Błąd: Warunek pętli for musi być typu boolean, otrzymano: " + conditionType);
            }
        }

        // Sprawdzamy iterację
        if (forLoop.getIteration() != null) {
            forLoop.getIteration().accept(this);
        }

        // Sprawdzamy blok
        forLoop.getBody().accept(this);

        // Przywracamy poprzedni zakres
        variables.clear();
        variables.putAll(outerScope);

        return Type.VOID;
    }

    @Override
    public Type visit(ReadExpression readExpression) {
        // Funkcja read zwraca wartość oczekiwanego typu jeśli jest ustawiony, w przeciwnym razie typ dynamiczny
        if (readExpression.getExpectedType() != null) {
            return readExpression.getExpectedType();
        }
        inferredTypes.put(readExpression, Type.DYNAMIC);
        return Type.DYNAMIC;
    }

    @Override
    public Type visit(ArrayAccess arrayAccess) {
        String arrayName = arrayAccess.getArrayName();

        // Sprawdzamy, czy tablica została zadeklarowana
        if (!variables.containsKey(arrayName)) {
            errors.add("Błąd: Niezadeklarowana tablica: " + arrayName);
            inferredTypes.put(arrayAccess, Type.DYNAMIC);
            return Type.DYNAMIC;
        }

        // Sprawdzamy, czy indeks jest typu całkowitego
        Type indexType = arrayAccess.getIndex().accept(this);
        if (indexType != Type.INTEGER && indexType != Type.DYNAMIC) {
            errors.add("Błąd: Indeks tablicy musi być typu całkowitego, otrzymano: " + indexType);
        }

        // Sprawdzamy czy zmienna jest faktycznie tablicą
        Type arrayType = variables.get(arrayName);
        if (!arrayType.isArray()) {
            errors.add("Błąd: Zmienna " + arrayName + " nie jest tablicą, jest typu: " + arrayType);
            return Type.DYNAMIC;
        }

        // Zwracamy typ elementu tablicy
        inferredTypes.put(arrayAccess, arrayType.getElementType());
        return arrayType.getElementType();
    }

    @Override
    public Type visit(ReturnStatement returnStatement) {
        if (returnStatement.hasValue()) {
            return returnStatement.getValue().accept(this);
        }
        return Type.VOID;
    }

    @Override
    public Type visit(ArrayLiteral arrayLiteral) {
        List<Expression> elements = arrayLiteral.getElements();
        if (elements.isEmpty()) {
            inferredTypes.put(arrayLiteral, Type.createArrayType(Type.DYNAMIC));
            return Type.createArrayType(Type.DYNAMIC);
        }

        // Sprawdzamy typ pierwszego elementu jako bazowy typ tablicy
        Type baseType = elements.get(0).accept(this);

        // Sprawdzamy czy wszystkie elementy mają ten sam typ
        for (int i = 1; i < elements.size(); i++) {
            Type elementType = elements.get(i).accept(this);
            if (elementType != baseType && baseType != Type.DYNAMIC
                && elementType != Type.DYNAMIC) {
                errors.add("Błąd: Niejednorodne typy w tablicy. Element #" + (i + 1) +
                    " ma typ " + elementType + ", oczekiwano: " + baseType);
            }
        }

        // Zwracamy typ tablicy na podstawie typu elementów
        inferredTypes.put(arrayLiteral, Type.createArrayType(baseType));
        return Type.createArrayType(baseType);
    }

    @Override
    public Type visit(FunctionDeclaration functionDeclaration) {
        String name = functionDeclaration.getName();
        Type returnType;

        try {
            returnType = Type.fromTypeName(functionDeclaration.getReturnType());
        } catch (IllegalArgumentException e) {
            returnType = Type.VOID;
            errors.add("Błąd: Nieznany typ zwracany: " + functionDeclaration.getReturnType()
                + " dla funkcji " + name);
        }

        // Tworzymy nowy typ funkcji
        FunctionType functionType = new FunctionType(returnType);

        // Dodajemy parametry funkcji
        for (Declaration param : functionDeclaration.getParameters()) {
            Type paramType = Type.DYNAMIC;
            try {
                paramType = Type.fromTypeName(param.getType());
            } catch (IllegalArgumentException e) {
                errors.add(
                    "Błąd: Nieznany typ parametru: " + param.getType() + " w funkcji " + name);
            }
            functionType.addParam(paramType);

            // Rejestrujemy parametr jako zmienną lokalną funkcji
            variables.put(param.getName(), paramType);
        }

        // Rejestrujemy funkcję w globalnej tabeli funkcji
        functions.put(name, functionType);

        // Odwiedzamy ciało funkcji
        functionDeclaration.getBody().accept(this);

        // Zwracamy typ wartości funcji dla systemu typów
        return returnType;
    }

    // Implementacje brakujących metod visit dla nowych typów węzłów AST
    @Override
    public Type visit(ExpressionAsPipeElement expressionAsPipeElement) {
        // Sprawdzanie typu dla opakowanego wyrażenia
        return expressionAsPipeElement.getExpression().accept(this);
    }

    @Override
    public Type visit(TypeCastPipeExpression typeCastPipeExpression) {
        // Logika sprawdzania typów dla rzutowania w potoku.
        // Zakładamy, że visit(PipeExpression) obsłuży kontekst potoku.
        // Tutaj zwracamy po prostu docelowy typ rzutowania.
        try {
            return Type.fromTypeName(typeCastPipeExpression.getTargetType());
        } catch (IllegalArgumentException e) {
            errors.add("Błąd: Nieznany typ docelowy rzutowania: "
                + typeCastPipeExpression.getTargetType());
            return Type.DYNAMIC;
        }
    }

    @Override
    public Type visit(VariableAssignPipeStatement variableAssignPipeStatement) {
        // Logika sprawdzania typów dla przypisania w potoku.
        // Zakładamy, że visit(PipeExpression) obsłuży wartość przychodzącą.
        // Tutaj głównie rejestrujemy zmienną i zwracamy jej typ.
        String varName = variableAssignPipeStatement.getVariableName();
        Type varType;
        if (variableAssignPipeStatement.getDeclaredType() != null
            && !variableAssignPipeStatement.getDeclaredType().equals("_")) {
            try {
                varType = Type.fromTypeName(variableAssignPipeStatement.getDeclaredType());
            } catch (IllegalArgumentException e) {
                errors.add("Błąd: Nieznany typ zmiennej " + varName + ": "
                    + variableAssignPipeStatement.getDeclaredType());
                varType = Type.DYNAMIC;
            }
        } else {
            // Typ do wywnioskowania, w visit(PipeExpression) na podstawie currentPipeType
            varType = Type.DYNAMIC;
        }
        variables.put(varName, varType); // Rejestrujemy lub aktualizujemy typ zmiennej
        return varType; // Typ przypisanej zmiennej
    }

    @Override
    public Type visit(IfPipeStatement ifPipeStatement) {
        // Logika sprawdzania dla if w potoku.
        // Zakładamy, że visit(PipeExpression) sprawdzi, czy warunek (currentPipeType) jest boolean.
        // Tutaj sprawdzamy typy wewnątrz bloku 'then'.
        // Typ wynikowy jest skomplikowany; może zależeć od tego, co dzieje się w bloku 'then'.
        // Na razie zwracamy VOID, zakładając, że if w potoku nie zmienia typu wartości w potoku, chyba że blok then to robi.
        ifPipeStatement.getThenBlock().accept(this);
        return Type.VOID; // Lub typ wartości przekazywanej dalej, jeśli jest znany.
    }

    @Override
    public Type visit(ReturnPipeStatement returnPipeStatement) {
        // Logika sprawdzania dla return w potoku.
        // Zakładamy, że visit(PipeExpression) przekaże typ wartości do zwrócenia.
        // Tutaj możemy po prostu zwrócić DYNAMIC, ponieważ faktyczne sprawdzenie typu zwracanego
        // powinno się odbyć w kontekście funkcji, w której ten return występuje.
        return Type.DYNAMIC; // Typ wartości zwracanej przez return, który jest przekazywany przez potok.
    }

    @Override
    public Type visit(UnaryOperation unaryOperation) {
        Type operandType = unaryOperation.getOperand().accept(this);
        if (operandType != Type.BOOLEAN) {
            errors.add("Błąd: Operand operacji negacji musi być bool, otrzymano: " + operandType);
        }
        inferredTypes.put(unaryOperation, Type.BOOLEAN);
        return Type.BOOLEAN;
    }

    @Override
    public Type visit(PrintStatement printStatement) {
        for (Expression arg : printStatement.getArguments()) {
            Type argType = arg.accept(this);
            // Można dodać kontrolę typów, jeśli np. nie chcemy pozwalać na null
            if (argType == Type.VOID) {
                errors.add("Błąd: Nie można wypisać wartości typu void.");
            }
        }

        return Type.VOID; // PrintStatement nic nie zwraca
    }

    @Override
    public Type visit(ArrayAssignment arrayAssignment) {
        String arrayName = arrayAssignment.getArrayName();
        Expression indexExpr = arrayAssignment.getIndex();
        Expression valueExpr = arrayAssignment.getValue();

        // Sprawdź, czy tablica jest zadeklarowana
        if (!variables.containsKey(arrayName)) {
            errors.add("Błąd: Niezadeklarowana tablica: " + arrayName);
            inferredTypes.put(arrayAssignment, Type.DYNAMIC);
            return Type.DYNAMIC;
        }

        Type arrayType = variables.get(arrayName);

        // Indeks musi być typu całkowitego
        Type indexType = indexExpr.accept(this);
        if (indexType != Type.INTEGER && indexType != Type.DYNAMIC) {
            errors.add("Błąd: Indeks tablicy musi być typu całkowitego, otrzymano: " + indexType);
        }

        // Tablica musi być faktycznie tablicą
        if (!arrayType.isArray()) {
            errors.add("Błąd: Zmienna " + arrayName + " nie jest tablicą, jest typu: " + arrayType);
            inferredTypes.put(arrayAssignment, Type.DYNAMIC);
            return Type.DYNAMIC;
        }

        Type elementType = arrayType.getElementType();

        // Sprawdź typ przypisywanej wartości
        Type valueType = valueExpr.accept(this);
        if (!valueType.canCastTo(elementType)) {
            errors.add("Błąd: Nieprawidłowy typ przypisania do elementu tablicy. Oczekiwano: " + elementType + ", otrzymano: " + valueType);
        }

        inferredTypes.put(arrayAssignment, Type.VOID);
        return Type.VOID;
    }




    /**
     * Klasa reprezentująca typ funkcji.
     */
    private static class FunctionType {

        private final Type returnType;
        private final List<Type> paramTypes = new ArrayList<>();

        public FunctionType(Type returnType) {
            this.returnType = returnType;
        }

        public void addParam(Type paramType) {
            paramTypes.add(paramType);
        }

        public Type getReturnType() {
            return returnType;
        }

        public List<Type> getParamTypes() {
            return paramTypes;
        }
    }

    public Type getTypeOf(Expression expr) {
        Type type = inferredTypes.get(expr);
        if (type == null) {
            throw new IllegalArgumentException("Type not found for expression: " + expr);
        }
        return type;
    }
    public Type getFunctionReturnType(String functionName) {
        FunctionType ft = functions.get(functionName); // 'functions' is your Map<String, FunctionType>
        if (ft != null) {
            return ft.getReturnType();
        }
        // This situation should ideally be an error caught during the type checking phase
        // if a function is called without being declared or registered as a built-in.
        // Returning DYNAMIC here is a fallback for codegen if type checking was lenient
        // or for built-ins not explicitly in the 'functions' map but handled by codegen.
        System.err.println("TypeChecker: Return type for function '" + functionName + "' not found in registered functions. Defaulting to DYNAMIC.");
        return Type.DYNAMIC;
    }



} 