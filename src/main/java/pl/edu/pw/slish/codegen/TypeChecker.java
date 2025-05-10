package pl.edu.pw.slish.codegen;

import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.Program;
import pl.edu.pw.slish.ast.expr.*;
import pl.edu.pw.slish.ast.stmt.*;
import pl.edu.pw.slish.ast.PipeElementNode;
import pl.edu.pw.slish.ast.expr.ExpressionAsPipeElement;
import pl.edu.pw.slish.ast.expr.TypeCastPipeExpression;
import pl.edu.pw.slish.ast.stmt.VariableAssignPipeStatement;
import pl.edu.pw.slish.ast.stmt.IfPipeStatement;
import pl.edu.pw.slish.ast.stmt.ReturnPipeStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analizator typów dla języka Slish.
 * Odpowiada za sprawdzanie poprawności typów w AST.
 */
public class TypeChecker implements NodeVisitor<Type> {
    private final Map<String, Type> variables = new HashMap<>();
    private final List<String> errors = new ArrayList<>();
    private final Map<String, FunctionType> functions = new HashMap<>();
    
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
        visit((pl.edu.pw.slish.ast.stmt.Program)program);
        
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
                return Type.INTEGER;
            case FLOAT:
                return Type.FLOAT;
            case STRING:
                return Type.STRING;
            case BOOLEAN:
                return Type.BOOLEAN;
            default:
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
                errors.add("Błąd: Niewłaściwy typ argumentu #" + (i+1) + " dla funkcji " + 
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
        
        switch (binaryOperation.getOperator()) {
            case ADD:
            case SUBTRACT:
            case MULTIPLY:
            case DIVIDE:
            case MODULO:
                if (leftType != Type.INTEGER && leftType != Type.FLOAT) {
                    errors.add("Błąd: Niewłaściwy typ lewego operandu operacji " + 
                              binaryOperation.getOperator() + ". Oczekiwano liczby, otrzymano: " + leftType);
                }
                if (rightType != Type.INTEGER && rightType != Type.FLOAT) {
                    errors.add("Błąd: Niewłaściwy typ prawego operandu operacji " + 
                              binaryOperation.getOperator() + ". Oczekiwano liczby, otrzymano: " + rightType);
                }
                
                // Dla operacji arytmetycznych typ wyniku zależy od operandów
                if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
                    return Type.FLOAT;
                } else {
                    return Type.INTEGER;
                }
                
            case EQUAL:
            case NOT_EQUAL:
            case GREATER_THAN:
            case LESS_THAN:
            case GREATER_EQUAL:
            case LESS_EQUAL:
                // Operacje porównania zawsze zwracają boolean
                return Type.BOOLEAN;
                
            default:
                errors.add("Nieznany operator: " + binaryOperation.getOperator());
                return Type.DYNAMIC;
        }
    }
    
    @Override
    public Type visit(StringInterpolation stringInterpolation) {
        // Sprawdzamy poprawność interpolacji - uproszczona wersja
        return Type.STRING;
    }
    
    @Override
    public Type visit(Declaration declaration) {
        Type declaredType;
        try {
            declaredType = Type.fromTypeName(declaration.getType());
        } catch (IllegalArgumentException e) {
            errors.add("Błąd: Nieznany typ: " + declaration.getType());
            declaredType = Type.DYNAMIC;
        }
        
        if (declaration.getInitializer() != null) {
            // Jeśli inicjalizator to ReadExpression, ustawiamy dla niego oczekiwany typ
            if (declaration.getInitializer() instanceof ReadExpression) {
                ReadExpression readExpr = (ReadExpression) declaration.getInitializer();
                readExpr.setExpectedType(declaredType);
            }
            
            Type initType = declaration.getInitializer().accept(this);
            
            // Obsługa tablic - jeśli zarówno declaredType jak i initType są tablicami
            if (declaredType.isArray() && initType.isArray()) {
                // Sprawdzamy zgodność typów elementów
                Type declaredElementType = declaredType.getElementType();
                Type initElementType = initType.getElementType();
                
                if (!initElementType.canCastTo(declaredElementType)) {
                    errors.add("Błąd: Niezgodność typów elementów tablicy w deklaracji " + declaration.getName() + 
                              ". Oczekiwano: " + declaredElementType + "[], otrzymano: " + initElementType + "[]");
                }
                
                // Typy są zgodne lub jeden z nich jest dynamiczny
                return declaredType;
            }
            
            // Standardowa kontrola typów dla innych przypadków
            if (declaredType != Type.DYNAMIC && !initType.canCastTo(declaredType)) {
                errors.add("Błąd: Niezgodność typów w deklaracji " + declaration.getName() + 
                          ". Oczekiwano: " + declaredType + ", otrzymano: " + initType);
            }
        }
        
        // Typ deklaracji to typ zadeklarowanej zmiennej
        return declaredType;
    }
    
    @Override
    public Type visit(Assignment assignment) {
        String varName = assignment.getTarget().getName();
        if (!variables.containsKey(varName)) {
            errors.add("Błąd: Przypisanie do niezadeklarowanej zmiennej: " + varName);
            return Type.DYNAMIC;
        }
        
        Type varType = variables.get(varName);
        
        // Jeśli wartość to ReadExpression, ustawiamy dla niej oczekiwany typ
        if (assignment.getValue() instanceof ReadExpression) {
            ReadExpression readExpr = (ReadExpression) assignment.getValue();
            readExpr.setExpectedType(varType);
        }
        
        Type valueType = assignment.getValue().accept(this);
        
        // Podobnie jak w deklaracji, sprawdzamy zgodność typów
        if (varType != Type.DYNAMIC && !valueType.canCastTo(varType)) {
            errors.add("Błąd: Niezgodność typów w przypisaniu do " + varName + 
                      ". Oczekiwano: " + varType + ", otrzymano: " + valueType);
        }
        
        return varType;
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
                        
                        errors.add("Błąd: Niewłaściwa liczba argumentów dla funkcji " + functionName + 
                                  " w potoku. Oczekiwano: " + functionType.getParamTypes().size() + 
                                  " (wliczając wartość z potoku), otrzymano: " + (args.size() + 1));
                    }
                    
                    // Sprawdzamy zgodność typu z potoku jako pierwszy argument funkcji
                    Type expectedFirstArgType = functionType.getParamTypes().get(0);
                    if (expectedFirstArgType != Type.DYNAMIC && !currentPipeType.canCastTo(expectedFirstArgType)) {
                        errors.add("Błąd: Niewłaściwy typ dla pierwszego argumentu funkcji " + 
                                  functionName + " w potoku. Oczekiwano: " + expectedFirstArgType + 
                                  ", otrzymano z potoku: " + currentPipeType);
                    }
                    
                    // Sprawdzamy pozostałe argumenty (o ile są)
                    for (int i = 0; i < args.size(); i++) {
                        Type argType = args.get(i).accept(this);
                        Type expectedType = functionType.getParamTypes().get(i + 1); // +1 bo pierwszy argument jest z potoku
                        
                        if (expectedType != Type.DYNAMIC && !argType.canCastTo(expectedType)) {
                            errors.add("Błąd: Niewłaściwy typ argumentu #" + (i+2) + " dla funkcji " + 
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
                                  expr.getClass().getSimpleName() + " nie może być użyte w środku potoku.");
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
                
                if (assignStmt.getDeclaredType() == null || assignStmt.getDeclaredType().equals("_")) {
                    varType = currentPipeType; // Wnioskowanie typu
                } else {
                    varType = Type.fromTypeName(assignStmt.getDeclaredType());
                }
                
                if (variables.containsKey(assignStmt.getVariableName())) {
                    // Można sprawdzić czy typy są zgodne z już zadeklarowaną zmienną
                    Type existingType = variables.get(assignStmt.getVariableName());
                    if (existingType != Type.DYNAMIC && varType != Type.DYNAMIC && !varType.canCastTo(existingType)) {
                        errors.add("Błąd: Niezgodność typów przy przypisaniu w potoku do istniejącej zmiennej '" + 
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
                    errors.add("Błąd: Warunek if w potoku musi być typu BOOLEAN, otrzymano: " + currentPipeType);
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
                errors.add("Błąd: Nieobsługiwany typ elementu w potoku: " + elementNode.getClass().getSimpleName());
                currentPipeType = Type.DYNAMIC;
            }
        }
        
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
            errors.add("Błąd: Warunek instrukcji if musi być typu boolean, otrzymano: " + conditionType);
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
            errors.add("Błąd: Warunek pętli while musi być typu boolean, otrzymano: " + conditionType);
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
                errors.add("Błąd: Warunek pętli for musi być typu boolean, otrzymano: " + conditionType);
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
        return Type.DYNAMIC;
    }
    
    @Override
    public Type visit(ArrayAccess arrayAccess) {
        String arrayName = arrayAccess.getArrayName();
        
        // Sprawdzamy, czy tablica została zadeklarowana
        if (!variables.containsKey(arrayName)) {
            errors.add("Błąd: Niezadeklarowana tablica: " + arrayName);
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
            return Type.createArrayType(Type.DYNAMIC);
        }
        
        // Sprawdzamy typ pierwszego elementu jako bazowy typ tablicy
        Type baseType = elements.get(0).accept(this);
        
        // Sprawdzamy czy wszystkie elementy mają ten sam typ
        for (int i = 1; i < elements.size(); i++) {
            Type elementType = elements.get(i).accept(this);
            if (elementType != baseType && baseType != Type.DYNAMIC && elementType != Type.DYNAMIC) {
                errors.add("Błąd: Niejednorodne typy w tablicy. Element #" + (i+1) + 
                          " ma typ " + elementType + ", oczekiwano: " + baseType);
            }
        }
        
        // Zwracamy typ tablicy na podstawie typu elementów
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
            errors.add("Błąd: Nieznany typ zwracany: " + functionDeclaration.getReturnType() + " dla funkcji " + name);
        }
        
        // Tworzymy nowy typ funkcji
        FunctionType functionType = new FunctionType(returnType);
        
        // Dodajemy parametry funkcji
        for (Declaration param : functionDeclaration.getParameters()) {
            Type paramType = Type.DYNAMIC;
            try {
                paramType = Type.fromTypeName(param.getType());
            } catch (IllegalArgumentException e) {
                errors.add("Błąd: Nieznany typ parametru: " + param.getType() + " w funkcji " + name);
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
            errors.add("Błąd: Nieznany typ docelowy rzutowania: " + typeCastPipeExpression.getTargetType());
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
        if (variableAssignPipeStatement.getDeclaredType() != null && !variableAssignPipeStatement.getDeclaredType().equals("_")) {
            try {
                varType = Type.fromTypeName(variableAssignPipeStatement.getDeclaredType());
            } catch (IllegalArgumentException e) {
                errors.add("Błąd: Nieznany typ zmiennej " + varName + ": " + variableAssignPipeStatement.getDeclaredType());
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
} 