package pl.edu.pw.slish.codegen;

import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.Program;
import pl.edu.pw.slish.ast.expr.*;
import pl.edu.pw.slish.ast.stmt.*;
import pl.edu.pw.slish.codegen.instructions.*;
// Potrzebne importy dla nowych klas AST, jeśli nie ma wildcard
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
import java.util.stream.Collectors;

/**
 * Generator kodu pośredniego z AST.
 */
public class CodeGenerator implements NodeVisitor<String> {
    private final List<Instruction> instructions = new ArrayList<>();
    private final ScopeManager scopeManager = new ScopeManager();
    private final LoopVariableManager loopManager = new LoopVariableManager();
    private int registerCounter = 0;
    private int labelCounter = 0;
    private String currentPipeValueRegister = null; // Nowe pole
    
    /**
     * Generuje nowy unikalny rejestr.
     */
    private String generateRegister() {
        return "r" + (registerCounter++);
    }
    
    /**
     * Generuje nową unikalną etykietę z prefiksem.
     */
    private String generateLabel(String prefix) {
        return prefix + "_" + (labelCounter++);
    }
    
    /**
     * Generuje kod pośredni dla drzewa AST.
     * 
     * @param ast Drzewo AST, dla którego ma być wygenerowany kod
     * @return Wygenerowany kod LLVM
     */
    public String generateCode(Node ast) {
        // Resetujemy liczniki i managery dla nowej generacji kodu
        registerCounter = 0;
        labelCounter = 0;
        instructions.clear();
        
        // Rozpoczynamy od generacji kodu programu
        if (ast instanceof Program) {
            return generateCode((Program) ast);
        } else if (ast instanceof pl.edu.pw.slish.ast.stmt.Program) {
            pl.edu.pw.slish.ast.stmt.Program program = (pl.edu.pw.slish.ast.stmt.Program) ast;
            visit(program);
        } else {
            // Jeśli ast nie jest programem, dodajemy komentarz o błędzie
            instructions.add(new CommentInstruction("Error: Expected a Program node, but got " + ast.getClass().getSimpleName()));
        }
        
        // Formatujemy wygenerowany kod
        StringBuilder sb = new StringBuilder();
        sb.append("; Slish Intermediate Code\n");
        sb.append("; =====================================================\n\n");
        
        for (Instruction instruction : instructions) {
            sb.append(instruction.generateCode()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Generuje kod pośredni dla całego programu.
     */
    public String generateCode(Program program) {
        // Resetujemy liczniki i managery dla nowej generacji kodu
        registerCounter = 0;
        labelCounter = 0;
        instructions.clear();
        
        // Rozpoczynamy od generacji kodu programu
        visit((pl.edu.pw.slish.ast.stmt.Program)program);
        
        // Formatujemy wygenerowany kod
        StringBuilder sb = new StringBuilder();
        sb.append("; Slish Intermediate Code\n");
        sb.append("; =====================================================\n\n");
        
        for (Instruction instruction : instructions) {
            sb.append(instruction.generateCode()).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public String visit(pl.edu.pw.slish.ast.stmt.Program program) {
        for (Node statement : program.getStatements()) {
            statement.accept(this);
        }
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return null;
    }
    
    @Override
    public String visit(Literal literal) {
        String register = generateRegister();
        Type type;
        
        Literal.Type literalType = literal.getType();
        if (literalType == Literal.Type.INTEGER) {
            type = Type.INTEGER;
        } else if (literalType == Literal.Type.FLOAT) {
            type = Type.FLOAT;
        } else if (literalType == Literal.Type.STRING) {
            type = Type.STRING;
        } else if (literalType == Literal.Type.BOOLEAN) {
            type = Type.BOOLEAN;
        } else {
            type = Type.DYNAMIC;
        }
        
        instructions.add(new LoadConstantInstruction(register, literal.getValue(), type));
        return register;
    }
    
    @Override
    public String visit(Variable variable) {
        String name = variable.getName();
        
        // Sprawdzamy, czy mamy do czynienia ze zmienną iteracyjną w pętli
        if (loopManager.isIterationVariable(name)) {
            // Używamy rejestru zmiennej iteracyjnej
            return loopManager.getIterationVariableRegister(name);
        }
        
        // Sprawdzamy typ zmiennej w aktualnym zasięgu
        Type type = scopeManager.getVariableType(name);
        if (type == null) {
            // Zmienna nie zadeklarowana, dodajemy domyślną deklarację
            type = Type.DYNAMIC;
            String register = generateRegister();
            instructions.add(new CommentInstruction("Implicitly declared variable: " + name));
            scopeManager.declareVariable(name, type, register);
            instructions.add(new LoadConstantInstruction(register, 0, type));
            return register;
        }
        
        // Pobieramy rejestr zmiennej
        String register = scopeManager.getVariableRegister(name);
        
        // Tworzymy nowy rejestr dla wartości zmiennej
        String valueRegister = generateRegister();
        instructions.add(new LoadVariableInstruction(valueRegister, name, register, type));
        
        return valueRegister;
    }
    
    @Override
    public String visit(FunctionCall functionCall) {
        String register = generateRegister();
        List<String> argRegisters = new ArrayList<>();
        
        for (Expression arg : functionCall.getArguments()) {
            String argRegister = arg.accept(this);
            argRegisters.add(argRegister);
        }
        
        // Obsługa wbudowanych funkcji
        String functionName = functionCall.getFunctionName();
        if (functionName.equals("print")) {
            handlePrintFunction(argRegisters);
            return register;
        } else if (functionName.equals("add")) {
            if (argRegisters.size() == 2) {
                instructions.add(new BinaryOperationInstruction(
                        register,
                        argRegisters.get(0),
                        argRegisters.get(1),
                        BinaryOperationInstruction.Operation.ADD,
                        Type.DYNAMIC
                ));
                return register;
            }
        } else if (functionName.equals("gt")) {
            if (argRegisters.size() == 2) {
                instructions.add(new BinaryOperationInstruction(
                        register,
                        argRegisters.get(0),
                        argRegisters.get(1),
                        BinaryOperationInstruction.Operation.GREATER_THAN,
                        Type.BOOLEAN
                ));
                return register;
            }
        } else if (functionName.equals("lt")) {
            if (argRegisters.size() == 2) {
                instructions.add(new BinaryOperationInstruction(
                        register,
                        argRegisters.get(0),
                        argRegisters.get(1),
                        BinaryOperationInstruction.Operation.LESS_THAN,
                        Type.BOOLEAN
                ));
                return register;
            }
        }
        
        // Domyślna obsługa wywołania funkcji
        instructions.add(new CallInstruction(register, functionName, argRegisters, Type.DYNAMIC));
        return register;
    }
    
    private void handlePrintFunction(List<String> argRegisters) {
        if (argRegisters.isEmpty()) {
            return;
        }
        
        // Drukujemy każdy argument
        for (String argRegister : argRegisters) {
            instructions.add(new PrintInstruction(argRegister, Type.DYNAMIC));
        }
    }
    
    @Override
    public String visit(BinaryOperation binaryOperation) {
        String leftRegister = binaryOperation.getLeft().accept(this);
        String rightRegister = binaryOperation.getRight().accept(this);
        String resultRegister = generateRegister();
        
        BinaryOperationInstruction.Operation op;
        switch (binaryOperation.getOperator()) {
            case ADD:
                op = BinaryOperationInstruction.Operation.ADD;
                break;
            case SUBTRACT:
                op = BinaryOperationInstruction.Operation.SUBTRACT;
                break;
            case MULTIPLY:
                op = BinaryOperationInstruction.Operation.MULTIPLY;
                break;
            case DIVIDE:
                op = BinaryOperationInstruction.Operation.DIVIDE;
                break;
            case MODULO:
                op = BinaryOperationInstruction.Operation.MODULO;
                break;
            case EQUAL:
                op = BinaryOperationInstruction.Operation.EQUAL;
                break;
            case NOT_EQUAL:
                op = BinaryOperationInstruction.Operation.NOT_EQUAL;
                break;
            case GREATER_THAN:
                op = BinaryOperationInstruction.Operation.GREATER_THAN;
                break;
            case LESS_THAN:
                op = BinaryOperationInstruction.Operation.LESS_THAN;
                break;
            case GREATER_EQUAL:
                op = BinaryOperationInstruction.Operation.GREATER_EQUAL;
                break;
            case LESS_EQUAL:
                op = BinaryOperationInstruction.Operation.LESS_EQUAL;
                break;
            default:
                throw new IllegalArgumentException("Nieznany operator: " + binaryOperation.getOperator());
        }
        
        // Określamy typ wyniku operacji
        Type resultType;
        if (op == BinaryOperationInstruction.Operation.EQUAL || 
            op == BinaryOperationInstruction.Operation.NOT_EQUAL ||
            op == BinaryOperationInstruction.Operation.GREATER_THAN ||
            op == BinaryOperationInstruction.Operation.LESS_THAN ||
            op == BinaryOperationInstruction.Operation.GREATER_EQUAL ||
            op == BinaryOperationInstruction.Operation.LESS_EQUAL) {
            resultType = Type.BOOLEAN;
        } else {
            resultType = Type.DYNAMIC; // Dla operacji arytmetycznych
        }
        
        instructions.add(new BinaryOperationInstruction(resultRegister, leftRegister, rightRegister, op, resultType));
        return resultRegister;
    }
    
    @Override
    public String visit(StringInterpolation stringInterpolation) {
        // Uproszczona implementacja bez faktycznej interpolacji
        String register = generateRegister();
        instructions.add(new LoadConstantInstruction(register, stringInterpolation.getTemplate(), Type.STRING));
        return register;
    }
    
    @Override
    public String visit(Declaration declaration) {
        String name = declaration.getName();
        Type type;
        
        try {
            type = Type.fromTypeName(declaration.getType());
        } catch (IllegalArgumentException e) {
            // Dla nieznanych typów używamy typu dynamicznego
            type = Type.DYNAMIC;
            instructions.add(new CommentInstruction("Warning: unknown type " + declaration.getType() + ", using dynamic type"));
        }
        
        String register = generateRegister();
        
        // Dodanie komentarza informującego o typie zmiennej
        if (type.isArray()) {
            instructions.add(new CommentInstruction("Array variable: " + name + " of type " + type));
        } else {
            instructions.add(new CommentInstruction("Variable: " + name + " of type " + type.getTypeName()));
        }
        
        // Rejestrujemy zmienną w bieżącym zasięgu
        boolean isNewVariable = scopeManager.declareVariable(name, type, register);
        
        if (declaration.getInitializer() != null) {
            // Obliczamy wartość inicjalizatora
            // Jeśli inicjalizator to PipeExpression, jego wynik będzie już w currentPipeValueRegister
            String valueRegister = declaration.getInitializer().accept(this);
            if (declaration.getInitializer() instanceof PipeExpression) {
                valueRegister = this.currentPipeValueRegister; 
            }
            
            // Zapisujemy wartość do zmiennej
            instructions.add(new StoreVariableInstruction(name, valueRegister, type));
            
            // Aktualizujemy rejestr zmiennej
            scopeManager.updateVariableRegister(name, register);
            
            // Sprawdzamy, czy jest to zmienna iteracyjna w pętli
            if (loopManager.isInLoop()) {
                loopManager.registerIterationVariable(name, register);
            }
        }
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return register;
    }
    
    @Override
    public String visit(Assignment assignment) {
        String name = assignment.getTarget().getName();
        // Jeśli wartość to PipeExpression, jego wynik będzie już w currentPipeValueRegister
        String valueRegister = assignment.getValue().accept(this);
        if (assignment.getValue() instanceof PipeExpression) {
            valueRegister = this.currentPipeValueRegister;
        }
        
        // Sprawdzamy, czy zmienna istnieje
        Type type = scopeManager.getVariableType(name);
        if (type == null) {
            // Zmienna nie istnieje, tworzymy nową z domyślnym typem
            type = Type.DYNAMIC;
            scopeManager.declareVariable(name, type, generateRegister());
        }
        
        // Zapisujemy wartość do zmiennej
        instructions.add(new StoreVariableInstruction(name, valueRegister, type));
        
        // Sprawdzamy, czy jest to zmienna iteracyjna w pętli
        if (loopManager.isInLoop() && loopManager.isIterationVariable(name)) {
            // Aktualizujemy rejestr zmiennej iteracyjnej
            loopManager.registerIterationVariable(name, valueRegister);
        }
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return valueRegister;
    }
    
    @Override
    public String visit(PipeExpression pipeExpression) {
        List<PipeElementNode> elements = pipeExpression.getElements(); // Zmienione z getElements() zwracającego List<Expression>
        if (elements.isEmpty()) {
            this.currentPipeValueRegister = null;
            return null;
        }
        
        // Przetwarzamy pierwszy element, jego wynik staje się currentPipeValueRegister
        this.currentPipeValueRegister = elements.get(0).accept(this);
        
        // Przetwarzamy pozostałe elementy
        for (int i = 1; i < elements.size(); i++) {
            if (this.currentPipeValueRegister == null && !(elements.get(i) instanceof ExpressionAsPipeElement && ((ExpressionAsPipeElement)elements.get(i)).getExpression() instanceof ReadExpression) ) {
                // Jeśli poprzedni element nie wyprodukował wartości (np. samo przypisanie bez dalszego przepływu), 
                // a obecny nie jest ReadExpression (które samo generuje wartość), zgłoś błąd lub ostrzeżenie.
                // Chyba że element sam w sobie jest np. ReadExpression, które generuje wartość.
                instructions.add(new CommentInstruction("Warning: Pipe element at index " + i + " expects a value from previous element, but none was provided."));
                // Można tu rzucić wyjątek lub spróbować kontynuować, jeśli to ma sens.
                // Na razie pozwalamy kontynuować, ale currentPipeValueRegister pozostaje null.
            }
            this.currentPipeValueRegister = elements.get(i).accept(this); // Wynik accept nadpisuje currentPipeValueRegister
        }
        
        String finalPipeResult = this.currentPipeValueRegister;
        // this.currentPipeValueRegister = null; // Reset po zakończeniu całego potoku - ale nie tutaj, bo wynik potoku może być użyty dalej
        return finalPipeResult; // Zwracamy ostatnią wartość z potoku
    }

    // Nowe metody visit dla elementów potoku
    @Override
    public String visit(ExpressionAsPipeElement expressionAsPipeElement) {
        // Wynik tego wyrażenia staje się currentPipeValueRegister poprzez przypisanie w visit(PipeExpression)
        return expressionAsPipeElement.getExpression().accept(this);
    }

    @Override
    public String visit(TypeCastPipeExpression typeCastPipeExpression) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction("Error: TypeCast expects a value from pipe, but currentPipeValueRegister is null."));
            return generateRegister(); // Zwróć pusty/nowy rejestr, aby uniknąć NullPointer, ale to błąd
        }
        String inputRegister = this.currentPipeValueRegister;
        String targetTypeName = typeCastPipeExpression.getTargetType();
        Type targetType;
        try {
            targetType = Type.fromTypeName(targetTypeName);
        } catch (IllegalArgumentException e) {
            instructions.add(new CommentInstruction("Error: Unknown target type for cast: " + targetTypeName));
            targetType = Type.DYNAMIC; // Użyj dynamicznego jako fallback
                }
                
                String resultRegister = generateRegister();
        // Założenie: istnieje TypeCastInstruction(result, input, targetType, sourceType) lub podobna.
        // Potrzebujemy sourceType, który nie jest łatwo dostępny bez analizy typu inputRegister.
        // Na razie uproszczenie: zakładamy, że TypeCastInstruction poradzi sobie z typem DYNAMIC lub typ zostanie określony później.
        instructions.add(new TypeCastInstruction(resultRegister, inputRegister, targetType, Type.DYNAMIC /* Placeholder for source type */));
        instructions.add(new CommentInstruction("Casting " + inputRegister + " to " + targetType.getTypeName() + " into " + resultRegister));
        return resultRegister;
    }

    @Override
    public String visit(VariableAssignPipeStatement variableAssignPipeStatement) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction("Error: VariableAssignPipeStatement expects a value from pipe, but currentPipeValueRegister is null."));
            // W tym przypadku przypisanie nie ma sensu, ale kontynuujemy, by nie przerwać generacji
            // Można by zwrócić null lub specjalny error register
        }
        
        String varName = variableAssignPipeStatement.getVariableName();
        String declaredTypeName = variableAssignPipeStatement.getDeclaredType();
        Type varType;

        if (declaredTypeName != null && !declaredTypeName.equals("_")) {
            try {
                varType = Type.fromTypeName(declaredTypeName);
            } catch (IllegalArgumentException e) {
                instructions.add(new CommentInstruction("Warning: Unknown type " + declaredTypeName + " for variable " + varName + ", using dynamic type."));
                varType = Type.DYNAMIC;
            }
            } else {
            // Typ dynamiczny lub do wywnioskowania
            // Jeśli currentPipeValueRegister nie jest null, można by spróbować wywnioskować typ stamtąd (bardziej zaawansowane)
            varType = Type.DYNAMIC;
        }

        // Deklaracja zmiennej jeśli nie istnieje
        if (scopeManager.getVariableType(varName) == null) {
            String varRegister = generateRegister(); // Zawsze generuj nowy rejestr dla uproszczenia
            scopeManager.declareVariable(varName, varType, varRegister);
            instructions.add(new CommentInstruction("Declared variable in pipe: " + varName + " of type " + varType.getTypeName()));
        } else {
            // Zmienna istnieje, można sprawdzić zgodność typów jeśli to potrzebne
            Type existingType = scopeManager.getVariableType(varName);
            if (!existingType.equals(varType) && varType != Type.DYNAMIC) {
                 instructions.add(new CommentInstruction("Warning: Re-assigning variable " + varName + " with a different type ("+varType.getTypeName()+") than original ("+existingType.getTypeName()+")."));
                 // Potencjalnie aktualizacja typu w scopeManager, jeśli język na to pozwala
                 // scopeManager.updateVariableType(varName, varType);
            }
        }
        
        if (this.currentPipeValueRegister != null) {
            instructions.add(new StoreVariableInstruction(varName, this.currentPipeValueRegister, varType));
            instructions.add(new CommentInstruction("Assigned value from pipe (reg: " + this.currentPipeValueRegister + ") to variable " + varName));
        } else {
             instructions.add(new CommentInstruction("Skipping assignment to " + varName + " due to null value in pipe."));
        }

        // Wartość płynie dalej przez potok
        return this.currentPipeValueRegister; 
    }

    @Override
    public String visit(IfPipeStatement ifPipeStatement) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction("Error: IfPipeStatement expects a boolean value from pipe, but currentPipeValueRegister is null."));
            // Można wygenerować skok do końca bloku if lub zachować się inaczej
            String endLabel = generateLabel("endif_pipe_err");
            instructions.add(new LabelInstruction(endLabel));
            return this.currentPipeValueRegister; // Lub null
        }

        String condRegister = this.currentPipeValueRegister;
        String thenLabel = generateLabel("then_pipe");
        String endLabel = generateLabel("endif_pipe");

        instructions.add(new CommentInstruction("Piped if condition from register: " + condRegister));
        instructions.add(new ConditionalJumpInstruction(condRegister, thenLabel, endLabel));

        instructions.add(new LabelInstruction(thenLabel));
        // Wartość z currentPipeValueRegister jest zużyta jako warunek.
        // Jeśli blok then ma wygenerować nową wartość dla potoku, musi ją umieścić w currentPipeValueRegister.
        // Na razie zakładamy, że blok then może modyfikować currentPipeValueRegister.
        String thenResultRegister = ifPipeStatement.getThenBlock().accept(this);
        // Jeśli blok `then` zwraca rejestr (np. z instrukcji return wewnątrz), to on staje się nowym currentPipeValueRegister
        // Jednak standardowy .accept(this) dla bloku zwraca null. 
        // Potrzebna jest konwencja lub modyfikacja, aby blok mógł przekazać wynik dalej w potoku.
        // Na razie, jeśli blok then coś obliczył i umieścił w currentPipeValueRegister, to ta wartość zostanie użyta.
        // Jeśli thenResultRegister nie jest nullem (np. blok zawierał return i visit(Return...) ustawił currentPipeValueRegister), to go użyj.
        // To jest obszar do dopracowania w zależności od semantyki języka.

        instructions.add(new JumpInstruction(endLabel)); 
        instructions.add(new LabelInstruction(endLabel));
        
        // Zwracamy currentPipeValueRegister, który mógł (lub nie) zostać zmieniony przez blok 'then'.
        // Jeśli blok then ma jednoznacznie ustawiać wynik dla potoku, powinien to zrobić poprzez currentPipeValueRegister.
        return this.currentPipeValueRegister; 
    }

    @Override
    public String visit(ReturnPipeStatement returnPipeStatement) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction("Warning: ReturnPipeStatement expects a value from pipe, but currentPipeValueRegister is null. Returning void/default."));
            instructions.add(new ReturnInstruction()); // Zwraca void
            return null;
        }
        instructions.add(new ReturnValueInstruction(this.currentPipeValueRegister));
        instructions.add(new CommentInstruction("Returned value from pipe (reg: " + this.currentPipeValueRegister + ")"));
        return this.currentPipeValueRegister; // Rejestr jest zwracany, ale wykonanie funkcji się kończy
    }
    
    @Override
    public String visit(Block block) {
        // Wchodzimy do nowego zasięgu
        scopeManager.enterScope();
        
        // Przetwarzamy wszystkie instrukcje w bloku
        for (Statement statement : block.getStatements()) {
            statement.accept(this);
        }
        
        // Wychodzimy z bieżącego zasięgu
        scopeManager.exitScope();
        
        return null;
    }
    
    @Override
    public String visit(IfStatement ifStatement) {
        String condRegister = ifStatement.getCondition().accept(this);
        
        // Generujemy unikalne etykiety dla bloków if-then-else
        String thenLabel = generateLabel("then");
        String elseLabel = generateLabel("else");
        String endLabel = generateLabel("endif");
        
        instructions.add(new CommentInstruction("if condition: " + condRegister));
        instructions.add(new ConditionalJumpInstruction(condRegister, thenLabel, elseLabel));
        
        // Blok then
        instructions.add(new LabelInstruction(thenLabel));
        ifStatement.getThenBlock().accept(this);
        instructions.add(new JumpInstruction(endLabel));
        
        // Blok else
        instructions.add(new LabelInstruction(elseLabel));
        if (ifStatement.hasElse()) {
            ifStatement.getElseBlock().accept(this);
        }
        
        // Koniec instrukcji if
        instructions.add(new LabelInstruction(endLabel));
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return null;
    }
    
    @Override
    public String visit(WhileLoop whileLoop) {
        // Generujemy unikalne etykiety dla pętli
        String condLabel = generateLabel("while_cond");
        String bodyLabel = generateLabel("while_body");
        String endLabel = generateLabel("while_end");
        
        // Rejestrujemy kontekst pętli
        loopManager.enterLoop("while", condLabel);
        
        // Etykieta warunku
        instructions.add(new LabelInstruction(condLabel));
        String condRegister = whileLoop.getCondition().accept(this);
        instructions.add(new ConditionalJumpInstruction(condRegister, bodyLabel, endLabel));
        
        // Ciało pętli
        instructions.add(new LabelInstruction(bodyLabel));
        whileLoop.getBody().accept(this);
        instructions.add(new JumpInstruction(condLabel));
        
        // Koniec pętli
        instructions.add(new LabelInstruction(endLabel));
        
        // Kończymy kontekst pętli
        loopManager.exitLoop();
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return null;
    }
    
    @Override
    public String visit(ForLoop forLoop) {
        // Generujemy unikalne etykiety dla pętli
        String initLabel = generateLabel("for_init");
        String condLabel = generateLabel("for_cond");
        String bodyLabel = generateLabel("for_body");
        String iterLabel = generateLabel("for_iter");
        String endLabel = generateLabel("for_end");
        
        // Rejestrujemy kontekst pętli
        loopManager.enterLoop("for", initLabel);
        
        if (forLoop.isForEach()) {
            // Obsługa pętli foreach (iteracja po kolekcji)
            instructions.add(new CommentInstruction("foreach loop on collection"));
            
            // Inicjalizacja licznika iteracji
            String iteratorVar = generateRegister() + "_iterator";
            String collectionVar = generateRegister() + "_collection";
            String sizeVar = generateRegister() + "_size";
            
            // Przetwarzamy kolekcję i określamy jej typ
            String collectionRegister = forLoop.getCondition().accept(this);
            
            // Próbujemy ustalić typ kolekcji (jeśli to zmienna, pobieramy jej typ)
            Type collectionType = Type.DYNAMIC;
            if (forLoop.getCondition() instanceof Variable) {
                Variable var = (Variable) forLoop.getCondition();
                collectionType = scopeManager.getVariableType(var.getName());
                
                if (collectionType != null && !collectionType.isArray()) {
                    instructions.add(new CommentInstruction("Warning: iterating over non-array variable " + var.getName()));
                }
            }
            
            instructions.add(new StoreVariableInstruction(collectionVar, collectionRegister, collectionType));
            
            // Inicjalizacja licznika (i = 0)
            instructions.add(new LabelInstruction(initLabel));
            instructions.add(new LoadConstantInstruction(iteratorVar, 0, Type.INTEGER));
            
            // Pobieramy rozmiar kolekcji
            instructions.add(new CommentInstruction("get collection size"));
            instructions.add(new CollectionSizeInstruction(sizeVar, collectionVar));
            
            // Warunek pętli (i < size)
            instructions.add(new LabelInstruction(condLabel));
            String condRegister = generateRegister();
            instructions.add(new BinaryOperationInstruction(
                condRegister, iteratorVar, sizeVar, 
                BinaryOperationInstruction.Operation.LESS_THAN, 
                Type.BOOLEAN
            ));
            instructions.add(new ConditionalJumpInstruction(condRegister, bodyLabel, endLabel));
            
            // Ciało pętli
            instructions.add(new LabelInstruction(bodyLabel));
            
            // Pobieramy bieżący element z kolekcji
            String currentElementVar = generateRegister() + "_element";
            instructions.add(new CommentInstruction("get current element from collection"));
            
            // Określamy typ elementu kolekcji
            Type elementType = Type.DYNAMIC;
            if (collectionType != null && collectionType.isArray()) {
                elementType = collectionType.getElementType();
                instructions.add(new CommentInstruction("element type: " + elementType));
            }
            
            instructions.add(new CollectionElementInstruction(currentElementVar, collectionVar, iteratorVar, elementType));
            
            // Wykonujemy ciało pętli
            // Domyślnie zakładamy, że pierwsza zmienna w bloku to zmienna iteracji
            // W przyszłości można to rozszerzyć o jawne przekazanie zmiennej iteracji
            if (!forLoop.getBody().getStatements().isEmpty() && 
                forLoop.getBody().getStatements().get(0) instanceof Declaration) {
                Declaration iterVarDecl = (Declaration) forLoop.getBody().getStatements().get(0);
                
                // Ustawienie typu elementu zgodnie z typem zmiennej iteracyjnej
                Type iterVarType = Type.DYNAMIC;
                try {
                    iterVarType = Type.fromTypeName(iterVarDecl.getType());
                } catch (IllegalArgumentException e) {
                    instructions.add(new CommentInstruction("Warning: unknown type for iteration variable, using dynamic type"));
                }
                
                instructions.add(new StoreVariableInstruction(
                    iterVarDecl.getName(), currentElementVar, iterVarType
                ));
                
                // Rejestrujemy zmienną jako iteracyjną
                loopManager.registerIterationVariable(iterVarDecl.getName(), currentElementVar);
                
                // Wykonujemy resztę bloku (pomijając pierwszą instrukcję)
                for (int i = 1; i < forLoop.getBody().getStatements().size(); i++) {
                    forLoop.getBody().getStatements().get(i).accept(this);
                }
            } else {
                // Wykonujemy cały blok
                forLoop.getBody().accept(this);
            }
            
            // Iteracja - zwiększamy licznik (i++)
            instructions.add(new LabelInstruction(iterLabel));
            String newIteratorValue = generateRegister();
            instructions.add(new BinaryOperationInstruction(
                newIteratorValue, iteratorVar, "1", 
                BinaryOperationInstruction.Operation.ADD, 
                Type.INTEGER
            ));
            instructions.add(new StoreVariableInstruction(iteratorVar, newIteratorValue, Type.INTEGER));
            
            // Powrót do warunku
            instructions.add(new JumpInstruction(condLabel));
        } else {
            // Standardowa pętla for
            // ... istniejący kod dla standardowej pętli for ...
            
            // Inicjalizacja zmiennych pętli
            instructions.add(new CommentInstruction("for loop initialization"));
            instructions.add(new LabelInstruction(initLabel));
            if (forLoop.getInitialization() != null) {
                // Inicjalizacja zmiennej pętli
                String initRegister = forLoop.getInitialization().accept(this);
                
                // Jeśli to deklaracja lub przypisanie, rejestrujemy zmienną jako iteracyjną
                Node initNode = forLoop.getInitialization();
                if (initNode instanceof Declaration) {
                    Declaration decl = (Declaration) initNode;
                    loopManager.registerIterationVariable(decl.getName(), initRegister);
                } else if (initNode instanceof Assignment) {
                    Assignment assign = (Assignment) initNode;
                    loopManager.registerIterationVariable(assign.getTarget().getName(), initRegister);
                }
            }
            
            // Warunek pętli
            instructions.add(new LabelInstruction(condLabel));
            if (forLoop.getCondition() != null) {
                String condRegister = forLoop.getCondition().accept(this);
                instructions.add(new ConditionalJumpInstruction(condRegister, bodyLabel, endLabel));
            } else {
                // Brak warunku - pętla nieskończona
                instructions.add(new JumpInstruction(bodyLabel));
            }
            
            // Ciało pętli
            instructions.add(new LabelInstruction(bodyLabel));
            forLoop.getBody().accept(this);
            
            // Iteracja
            instructions.add(new LabelInstruction(iterLabel));
            if (forLoop.getIteration() != null) {
                // Przetwarzamy krok iteracji
                String iterRegister = forLoop.getIteration().accept(this);
                
                // Jeśli to wyrażenie to przypisanie (Assignment), rejestrujemy zmienną jako iteracyjną
                Node iterNode = forLoop.getIteration();
                if (iterNode instanceof Assignment) {
                    Assignment assign = (Assignment) iterNode;
                    loopManager.registerIterationVariable(assign.getTarget().getName(), iterRegister);
                }
            }
            
            // Powrót do warunku
            instructions.add(new JumpInstruction(condLabel));
        }
        
        // Koniec pętli
        instructions.add(new LabelInstruction(endLabel));
        
        // Kończymy kontekst pętli
        loopManager.exitLoop();
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return null;
    }
    
    @Override
    public String visit(ReadExpression readExpression) {
        String register = generateRegister();
        
        // Określamy oczekiwany typ dla wczytanej wartości
        Type expectedType = Type.DYNAMIC;
        if (readExpression.getExpectedType() != null) {
            expectedType = readExpression.getExpectedType();
        }
        
        instructions.add(new CommentInstruction("Reading input from user, expected type: " + expectedType));
        instructions.add(new ReadInstruction(register, expectedType));
        
        return register;
    }
    
    @Override
    public String visit(ArrayAccess arrayAccess) {
        String resultRegister = generateRegister();
        String indexRegister = arrayAccess.getIndex().accept(this);
        String arrayName = arrayAccess.getArrayName();
        
        // Sprawdzamy, czy tablica istnieje
        Type arrayType = scopeManager.getVariableType(arrayName);
        if (arrayType == null) {
            // Tablica nie istnieje, tworzymy errorowy komentarz
            instructions.add(new CommentInstruction("Error: Array " + arrayName + " not found"));
            return resultRegister;
        }
        
        Type elementType = Type.DYNAMIC;
        if (arrayType.isArray()) {
            elementType = arrayType.getElementType();
        } else {
            instructions.add(new CommentInstruction("Warning: Variable " + arrayName + " is not an array"));
        }
        
        // Tworzymy instrukcję dostępu do elementu tablicy
        instructions.add(new ArrayLoadInstruction(resultRegister, arrayName, indexRegister, elementType));
        
        return resultRegister;
    }
    
    @Override
    public String visit(ArrayLiteral arrayLiteral) {
        String resultRegister = generateRegister();
        List<String> elementRegisters = new ArrayList<>();
        
        // Przetwarzamy każdy element tablicy
        for (Expression element : arrayLiteral.getElements()) {
            String elementRegister = element.accept(this);
            elementRegisters.add(elementRegister);
        }
        
        // Tworzymy instrukcję utworzenia tablicy
        Type elementType = Type.DYNAMIC;
        if (!arrayLiteral.getElements().isEmpty()) {
            // Próbujemy ustalić typ elementów na podstawie pierwszego elementu
            if (arrayLiteral.getElements().get(0) instanceof Literal) {
                Literal firstElement = (Literal) arrayLiteral.getElements().get(0);
                elementType = determineTypeFromLiteral(firstElement);
            }
        }
        
        Type arrayType = Type.createArrayType(elementType);
        instructions.add(new ArrayCreateInstruction(resultRegister, elementRegisters, arrayType));
        
        return resultRegister;
    }
    
    private Type determineTypeFromLiteral(Literal literal) {
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
    public String visit(ReturnStatement returnStatement) {
        if (returnStatement.hasValue()) {
            String valueRegister = returnStatement.getValue().accept(this);
            instructions.add(new ReturnValueInstruction(valueRegister));
            currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
            return valueRegister;
        } else {
            instructions.add(new ReturnInstruction());
            currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
            return null;
        }
    }
    
    @Override
    public String visit(FunctionDeclaration functionDeclaration) {
        String functionName = functionDeclaration.getName();
        
        instructions.add(new CommentInstruction("Function declaration: " + functionName));
        
        // Rozpoczęcie nowego zakresu dla parametrów funkcji
        scopeManager.enterScope();
        
        // Rejestrujemy parametry funkcji jako zmienne lokalne
        for (Declaration param : functionDeclaration.getParameters()) {
            String paramRegister = generateRegister();
            String paramName = param.getName();
            Type paramType;
            
            try {
                paramType = Type.fromTypeName(param.getType());
            } catch (IllegalArgumentException e) {
                paramType = Type.DYNAMIC;
            }
            
            scopeManager.declareVariable(paramName, paramType, paramRegister);
            instructions.add(new CommentInstruction("Function parameter: " + paramName + " of type " + paramType));
        }
        
        // Generujemy kod dla ciała funkcji
        functionDeclaration.getBody().accept(this);
        
        // Kończymy zakres funkcji
        scopeManager.exitScope();
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return null;
    }
    
    /**
     * Instrukcja wczytania wartości zmiennej.
     */
    private static class LoadVariableInstruction implements Instruction {
        private final String resultRegister;
        private final String variableName;
        private final String variableRegister;
        private final Type type;
        
        public LoadVariableInstruction(String resultRegister, String variableName, String variableRegister, Type type) {
            this.resultRegister = resultRegister;
            this.variableName = variableName;
            this.variableRegister = variableRegister;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            return String.format("%s = load %s from %s", resultRegister, type.getTypeName(), variableName);
        }
    }
    
    /**
     * Instrukcja zapisu wartości do zmiennej.
     */
    private static class StoreVariableInstruction implements Instruction {
        private final String variableName;
        private final String valueRegister;
        private final Type type;
        
        public StoreVariableInstruction(String variableName, String valueRegister, Type type) {
            this.variableName = variableName;
            this.valueRegister = valueRegister;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            return String.format("store %s %s to %s", type.getTypeName(), valueRegister, variableName);
        }
    }
    
    /**
     * Instrukcja komentarza.
     */
    private static class CommentInstruction implements Instruction {
        private final String comment;
        
        public CommentInstruction(String comment) {
            this.comment = comment;
        }
        
        @Override
        public String generateCode() {
            return "; " + comment;
        }
    }
    
    /**
     * Instrukcja etykiety.
     */
    private static class LabelInstruction implements Instruction {
        private final String label;
        
        public LabelInstruction(String label) {
            this.label = label;
        }
        
        @Override
        public String generateCode() {
            return label + ":";
        }
    }
    
    /**
     * Instrukcja skoku bezwarunkowego.
     */
    private static class JumpInstruction implements Instruction {
        private final String target;
        
        public JumpInstruction(String target) {
            this.target = target;
        }
        
        @Override
        public String generateCode() {
            return "jump " + target;
        }
    }
    
    /**
     * Instrukcja skoku warunkowego.
     */
    private static class ConditionalJumpInstruction implements Instruction {
        private final String condition;
        private final String trueTarget;
        private final String falseTarget;
        
        public ConditionalJumpInstruction(String condition, String trueTarget, String falseTarget) {
            this.condition = condition;
            this.trueTarget = trueTarget;
            this.falseTarget = falseTarget;
        }
        
        @Override
        public String generateCode() {
            return "branch " + condition + " " + trueTarget + " " + falseTarget;
        }
    }
    
    /**
     * Instrukcja wczytania stałej.
     */
    private static class LoadConstantInstruction implements Instruction {
        private final String register;
        private final Object value;
        private final Type type;
        
        public LoadConstantInstruction(String register, Object value, Type type) {
            this.register = register;
            this.value = value;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            String valueStr = (value == null) ? "null" : value.toString();
            return register + " = " + type.getTypeName() + " " + valueStr;
        }
    }
    
    /**
     * Instrukcja wywołania funkcji.
     */
    private static class CallInstruction implements Instruction {
        private final String resultRegister;
        private final String functionName;
        private final List<String> argumentRegisters;
        private final Type returnType;
        
        public CallInstruction(String resultRegister, String functionName, List<String> argumentRegisters, Type returnType) {
            this.resultRegister = resultRegister;
            this.functionName = functionName;
            this.argumentRegisters = argumentRegisters;
            this.returnType = returnType;
        }
        
        @Override
        public String generateCode() {
            StringBuilder sb = new StringBuilder();
            sb.append(resultRegister).append(" = call ").append(returnType.getTypeName())
              .append(" ").append(functionName).append("(");
            
            for (int i = 0; i < argumentRegisters.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(argumentRegisters.get(i));
            }
            
            sb.append(")");
            return sb.toString();
        }
    }
    
    /**
     * Instrukcja operacji binarnej.
     */
    private static class BinaryOperationInstruction implements Instruction {
        public enum Operation {
            ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
            EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL
        }
        
        private final String resultRegister;
        private final String leftRegister;
        private final String rightRegister;
        private final Operation operation;
        private final Type resultType;
        
        public BinaryOperationInstruction(String resultRegister, String leftRegister, String rightRegister, Operation operation, Type resultType) {
            this.resultRegister = resultRegister;
            this.leftRegister = leftRegister;
            this.rightRegister = rightRegister;
            this.operation = operation;
            this.resultType = resultType;
        }
        
        @Override
        public String generateCode() {
            String opStr;
            switch (operation) {
                case ADD: opStr = "add"; break;
                case SUBTRACT: opStr = "sub"; break;
                case MULTIPLY: opStr = "mul"; break;
                case DIVIDE: opStr = "div"; break;
                case MODULO: opStr = "mod"; break;
                case EQUAL: opStr = "eq"; break;
                case NOT_EQUAL: opStr = "ne"; break;
                case GREATER_THAN: opStr = "gt"; break;
                case LESS_THAN: opStr = "lt"; break;
                case GREATER_EQUAL: opStr = "ge"; break;
                case LESS_EQUAL: opStr = "le"; break;
                default: opStr = "?"; break;
            }
            
            return resultRegister + " = " + opStr + " " + resultType.getTypeName() + " " + leftRegister + ", " + rightRegister;
        }
    }
    
    /**
     * Instrukcja wczytania z wejścia standardowego.
     */
    private static class ReadInstruction implements Instruction {
        private final String register;
        private final Type type;
        
        public ReadInstruction(String register, Type type) {
            this.register = register;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            return register + " = read " + type.getTypeName();
        }
    }
    
    /**
     * Instrukcja wypisania na wyjście standardowe.
     */
    private static class PrintInstruction implements Instruction {
        private final String register;
        private final Type type;
        
        public PrintInstruction(String register, Type type) {
            this.register = register;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            return "print " + type.getTypeName() + " " + register;
        }
    }
    
    /**
     * Instrukcja wczytania elementu tablicy.
     */
    private static class ArrayLoadInstruction implements Instruction {
        private final String resultRegister;
        private final String arrayName;
        private final String indexRegister;
        private final Type type;
        
        public ArrayLoadInstruction(String resultRegister, String arrayName, String indexRegister, Type type) {
            this.resultRegister = resultRegister;
            this.arrayName = arrayName;
            this.indexRegister = indexRegister;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            return resultRegister + " = load " + type.getTypeName() + " " + arrayName + "[" + indexRegister + "]";
        }
    }
    
    /**
     * Instrukcja utworzenia tablicy.
     */
    private static class ArrayCreateInstruction implements Instruction {
        private final String resultRegister;
        private final List<String> elementRegisters;
        private final Type type;
        
        public ArrayCreateInstruction(String resultRegister, List<String> elementRegisters, Type type) {
            this.resultRegister = resultRegister;
            this.elementRegisters = elementRegisters;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            StringBuilder sb = new StringBuilder();
            sb.append(resultRegister).append(" = array ").append(type.getTypeName())
              .append(" [").append(elementRegisters.size()).append("]");
            
            for (int i = 0; i < elementRegisters.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(" ").append(elementRegisters.get(i));
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Instrukcja return bez wartości.
     */
    private static class ReturnInstruction implements Instruction {
        @Override
        public String generateCode() {
            return "return";
        }
    }
    
    /**
     * Instrukcja return z wartością.
     */
    private static class ReturnValueInstruction implements Instruction {
        private final String valueRegister;
        
        public ReturnValueInstruction(String valueRegister) {
            this.valueRegister = valueRegister;
        }
        
        @Override
        public String generateCode() {
            return "return " + valueRegister;
        }
    }
    
    /**
     * Instrukcja pobrania rozmiaru kolekcji.
     */
    private static class CollectionSizeInstruction implements Instruction {
        private final String resultRegister;
        private final String collectionRegister;
        
        public CollectionSizeInstruction(String resultRegister, String collectionRegister) {
            this.resultRegister = resultRegister;
            this.collectionRegister = collectionRegister;
        }
        
        @Override
        public String generateCode() {
            return resultRegister + " = size " + collectionRegister;
        }
    }
    
    /**
     * Instrukcja pobrania elementu z kolekcji.
     */
    private static class CollectionElementInstruction implements Instruction {
        private final String resultRegister;
        private final String collectionRegister;
        private final String indexRegister;
        private final Type type;
        
        public CollectionElementInstruction(String resultRegister, String collectionRegister, 
                                           String indexRegister, Type type) {
            this.resultRegister = resultRegister;
            this.collectionRegister = collectionRegister;
            this.indexRegister = indexRegister;
            this.type = type;
        }
        
        @Override
        public String generateCode() {
            return resultRegister + " = " + collectionRegister + "[" + indexRegister + "] " + type.getTypeName();
        }
    }

    /**
     * Instrukcja rzutowania typu.
     */
    private static class TypeCastInstruction implements Instruction {
        private final String resultRegister;
        private final String sourceRegister;
        private final Type targetType;
        private final Type sourceType; // Może być przydatne dla bardziej zaawansowanego rzutowania

        public TypeCastInstruction(String resultRegister, String sourceRegister, Type targetType, Type sourceType) {
            this.resultRegister = resultRegister;
            this.sourceRegister = sourceRegister;
            this.targetType = targetType;
            this.sourceType = sourceType; // Na razie przekazujemy DYNAMIC jako sourceType
        }

        @Override
        public String generateCode() {
            return String.format("%s = cast %s %s to %s", 
                                 resultRegister, 
                                 sourceType.getTypeName(),
                                 sourceRegister, 
                                 targetType.getTypeName());
        }
    }
} 