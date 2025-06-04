package pl.edu.pw.slish.codegen;

import java.util.ArrayList;
import java.util.List;
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
import pl.edu.pw.slish.codegen.instructions.AllocaInstruction;
import pl.edu.pw.slish.codegen.instructions.BinaryOperationInstruction;
import pl.edu.pw.slish.codegen.instructions.BranchInstruction;
import pl.edu.pw.slish.codegen.instructions.Instruction;
import pl.edu.pw.slish.codegen.instructions.LoadConstantInstruction;
import pl.edu.pw.slish.codegen.instructions.LoadInstruction;
import pl.edu.pw.slish.codegen.instructions.PhiInstruction;
import pl.edu.pw.slish.codegen.instructions.PrintInstruction;
import pl.edu.pw.slish.codegen.instructions.RawInstruction;
import pl.edu.pw.slish.codegen.instructions.ReturnInstruction;
import pl.edu.pw.slish.codegen.instructions.StoreInstruction;
import pl.edu.pw.slish.codegen.instructions.TypeCastInstruction;
import pl.edu.pw.slish.codegen.instructions.UnaryOperationInstruction;
import pl.edu.pw.slish.codegen.instructions.UncondBranchInstruction;
import pl.edu.pw.slish.codegen.util.Pair;

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
    private StringConstantManager stringManager = new StringConstantManager();
    private final TypeChecker typeChecker;

    public CodeGenerator(TypeChecker typeChecker) {
        this.typeChecker = typeChecker;
    }

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
        stringManager = new StringConstantManager(); // <--- FIX: Ensure stringManager is reset

        visit((pl.edu.pw.slish.ast.stmt.Program) ast);

        return emitFullCode();
    }

    /**
     * Generuje kod pośredni dla całego programu.
     */
    public String generateCode(Program program) {
        // Resetujemy liczniki i managery dla nowej generacji kodu
        registerCounter = 0;
        labelCounter = 0;
        instructions.clear();
        stringManager = new StringConstantManager(); // <--- FIX: Ensure stringManager is reset

        // Rozpoczynamy od generacji kodu programu
        visit(program);

        return emitFullCode();
    }

    /**
     * Łączy sekcję globalnych deklaracji i instrukcji w pełny kod LLVM.
     */
    private String emitFullCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("; Slish Intermediate Code\n");
        sb.append("; =====================================================\n\n");

        // 2) External declarations (e.g. printf) if you need
        sb.append("declare i32 @printf(i8*, ...)\n\n");

        // 3) Global string constants
        sb.append(stringManager.generateAllGlobalDeclarations());
        sb.append(stringManager.generateBuiltinFormatStrings());
        sb.append("\n");

        // 4) Begin main function
        sb.append("define i32 @main() {\n");
        sb.append("entry:\n");

        // 5) Emit every instruction you collected
        for (Instruction instr : instructions) {
            sb.append("  ")                  // indent for readability
                .append(instr.generateCode())
                .append("\n");
        }

        // 6) Return 0 at end of main
        sb.append("  ret i32 0\n");
        sb.append("}\n");

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
        Literal.Type litType = literal.getType();

        if (litType == Literal.Type.STRING) {
            String instr = stringManager.generateStringPointerInstruction(register,
                (String) literal.getValue());
            instructions.add(new RawInstruction(instr));
            return register;
        }

        // numeric or boolean
        Type type = switch (litType) {
            case INTEGER -> Type.INTEGER;
            case FLOAT -> Type.FLOAT;
            case BOOLEAN -> Type.BOOLEAN;
            default -> Type.DYNAMIC;
        };

        instructions.add(new LoadConstantInstruction(register, literal.getValue(), type));
        return register;
    }

    @Override
    public String visit(Variable variable) {
        String name = variable.getName();

        // Loop‐iterator vars are already in a register
        if (loopManager.isIterationVariable(name)) {
            String ptr = loopManager.getIterationVariableRegister(name);
            Type type = scopeManager.getVariableType(name);
            String value = generateRegister();
            instructions.add(new LoadInstruction(value, ptr, type));
            return value; // return loaded value, not pointer
        }

        // Look up the pointer register in scope
        Type type = scopeManager.getVariableType(name);
        if (type == null) {
            // Implicit “dynamic” var: alloca + store default 0
            type = Type.DYNAMIC;
            String ptrReg = generateRegister();
            instructions.add(new CommentInstruction(
                "Implicitly declared variable: " + name));
            instructions.add(new AllocaInstruction(ptrReg, type));
            scopeManager.declareVariable(name, type, ptrReg);

            // default-initialize to 0
            String zeroReg = generateRegister();
            instructions.add(new LoadConstantInstruction(zeroReg, 0, type));
            instructions.add(new StoreInstruction(zeroReg, ptrReg, type));

            // now load it back so visitor returns its **value**
            String valReg = generateRegister();
            instructions.add(new LoadInstruction(valReg, ptrReg, type));
            return valReg;
        }

        // Normal case: load from the existing slot
        String ptrRegister = scopeManager.getVariableRegister(name);
        String valueRegister = generateRegister();
        instructions.add(new LoadInstruction(valueRegister, ptrRegister, type));
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

        // Handle built-in functions (excluding print, which is now handled separately)
        String functionName = functionCall.getFunctionName();

        switch (functionName) {
            case "add" -> {
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
            }
            case "gt" -> {
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
            }
            case "lt" -> {
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
        }

        // Default function call handling
        instructions.add(new CallInstruction(register, functionName, argRegisters, Type.DYNAMIC));
        return register;
    }

    @Override
    public String visit(BinaryOperation binOp) {
        String leftReg = binOp.getLeft().accept(this);
        switch (binOp.getOperator()) {
            case AND:
                return genShortCircuitAnd(binOp, leftReg);
            case OR:
                return genShortCircuitOr(binOp, leftReg);
            case XOR: {
                String rightReg = binOp.getRight().accept(this);
                String resReg = generateRegister();
                instructions.add(new BinaryOperationInstruction(
                    resReg, leftReg, rightReg,
                    BinaryOperationInstruction.Operation.XOR,
                    Type.BOOLEAN
                ));
                return resReg;
            }
            default:
                return genRegularBinary(binOp, leftReg);
        }
    }

    private String genRegularBinary(BinaryOperation binOp, String leftReg) {
        String rightReg = binOp.getRight().accept(this);
        String resultReg = generateRegister();
        BinaryOperationInstruction.Operation op = switch (binOp.getOperator()) {
            case ADD -> BinaryOperationInstruction.Operation.ADD;
            case SUBTRACT -> BinaryOperationInstruction.Operation.SUBTRACT;
            case MULTIPLY -> BinaryOperationInstruction.Operation.MULTIPLY;
            case DIVIDE -> BinaryOperationInstruction.Operation.DIVIDE;
            case MODULO -> BinaryOperationInstruction.Operation.MODULO;
            case EQUAL -> BinaryOperationInstruction.Operation.EQUAL;
            case NOT_EQUAL -> BinaryOperationInstruction.Operation.NOT_EQUAL;
            case GREATER_THAN -> BinaryOperationInstruction.Operation.GREATER_THAN;
            case LESS_THAN -> BinaryOperationInstruction.Operation.LESS_THAN;
            case GREATER_EQUAL -> BinaryOperationInstruction.Operation.GREATER_EQUAL;
            case LESS_EQUAL -> BinaryOperationInstruction.Operation.LESS_EQUAL;
            default ->
                throw new IllegalArgumentException("Nieznany operator: " + binOp.getOperator());
        };

        Type resultType = switch (op) {
            case EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL ->
                Type.BOOLEAN;
            default -> typeChecker.getTypeOf(binOp); // fixed here
        };

        instructions.add(new BinaryOperationInstruction(
            resultReg, leftReg, rightReg, op, resultType
        ));
        return resultReg;
    }


    private String genShortCircuitAnd(BinaryOperation binOp, String leftReg) {
        // if left == 0 → skip rhs, result = 0
        // if left == 1 → eval rhs, result = rhs
        String evalLabel = generateLabel("and_eval_rhs");
        String shortLabel = generateLabel("and_short_false");
        String mergeLabel = generateLabel("and_merge");
        String resultReg = generateRegister();

        // 1) dispatch on left
        instructions.add(new BranchInstruction(leftReg, evalLabel, shortLabel));

        // 2) evalLabel: evaluate RHS
        instructions.add(new LabelInstruction(evalLabel));
        String rightReg = binOp.getRight().accept(this);
        instructions.add(new UncondBranchInstruction(mergeLabel));

        // 3) shortLabel: skip RHS, false
        instructions.add(new LabelInstruction(shortLabel));
        instructions.add(new UncondBranchInstruction(mergeLabel));

        // 4) mergeLabel: phi([rhs, evalLabel], [0, shortLabel])
        instructions.add(new LabelInstruction(mergeLabel));
        instructions.add(new PhiInstruction(
            resultReg,
            Type.BOOLEAN,
            List.of(
                Pair.of(rightReg, evalLabel),
                Pair.of("0", shortLabel)
            )
        ));

        return resultReg;
    }

    private String genShortCircuitOr(BinaryOperation binOp, String leftReg) {
        // if left == 1 → skip rhs, result = 1
        // if left == 0 → eval rhs, result = rhs
        String evalLabel = generateLabel("or_eval_rhs");
        String shortLabel = generateLabel("or_short_true");
        String mergeLabel = generateLabel("or_merge");
        String resultReg = generateRegister();

        // 1) dispatch on left
        instructions.add(new BranchInstruction(leftReg, shortLabel, evalLabel));

        // 2) shortLabel: skip RHS, true
        instructions.add(new LabelInstruction(shortLabel));
        instructions.add(new UncondBranchInstruction(mergeLabel));

        // 3) evalLabel: evaluate RHS
        instructions.add(new LabelInstruction(evalLabel));
        String rightReg = binOp.getRight().accept(this);
        instructions.add(new UncondBranchInstruction(mergeLabel));

        // 4) mergeLabel: phi([1, shortLabel], [rhs, evalLabel])
        instructions.add(new LabelInstruction(mergeLabel));
        instructions.add(new PhiInstruction(
            resultReg,
            Type.BOOLEAN,
            List.of(
                Pair.of("1", shortLabel),
                Pair.of(rightReg, evalLabel)
            )
        ));

        return resultReg;
    }


    @Override
    public String visit(StringInterpolation stringInterpolation) {
        // Uproszczona implementacja bez faktycznej interpolacji
        String register = generateRegister();
        instructions.add(
            new LoadConstantInstruction(register, stringInterpolation.getTemplate(), Type.STRING));
        return register;
    }

    @Override
    public String visit(Declaration declaration) {
        String name = declaration.getName();
        Type type;
        try {
            type = Type.fromTypeName(declaration.getType());
        } catch (IllegalArgumentException e) {
            type = Type.DYNAMIC;
            instructions.add(new CommentInstruction(
                "Warning: unknown type " + declaration.getType() + ", using dynamic type"));
        }

        // 1️⃣ Allocate space for the new variable
        String ptrRegister = generateRegister();
        instructions.add(new CommentInstruction(
            "Alloca variable: " + name + " of type " + type.getTypeName()));
        instructions.add(new AllocaInstruction(ptrRegister, type));

        // record the pointer in the scope
        scopeManager.declareVariable(name, type, ptrRegister);

        // 2️⃣ If there’s an initializer, compute it and store into the slot
        if (declaration.getInitializer() != null) {
            String valueRegister = declaration.getInitializer().accept(this);
            if (declaration.getInitializer() instanceof PipeExpression) {
                valueRegister = currentPipeValueRegister;
            }
            instructions.add(new StoreInstruction(valueRegister, ptrRegister, type));
            // if it’s a loop iterator, tell the loop manager
            if (loopManager.isInLoop()) {
                loopManager.registerIterationVariable(name, ptrRegister);
            }
        }

        currentPipeValueRegister = null;
        // return the pointer register so further code knows where it lives
        return ptrRegister;
    }


    @Override
    public String visit(Assignment assignment) {
        String name = assignment.getTarget().getName();

        // Get the result register of the value expression
        String valueRegister = assignment.getValue().accept(this);
        if (assignment.getValue() instanceof PipeExpression) {
            valueRegister = this.currentPipeValueRegister;
        }

        // Check if the variable exists in scope
        Type type = scopeManager.getVariableType(name);
        String pointerRegister = scopeManager.getVariableRegister(name);

        if (type == null || pointerRegister == null) {
            // If not declared yet, treat as dynamic and declare it
            type = Type.DYNAMIC;
            pointerRegister = generateRegister();
            scopeManager.declareVariable(name, type, pointerRegister);
        }

        // ✅ Store value into variable using StoreInstruction
        instructions.add(new StoreInstruction(valueRegister, pointerRegister, type));

        // If this is a loop iteration variable, update register
        if (loopManager.isInLoop() && loopManager.isIterationVariable(name)) {
            loopManager.registerIterationVariable(name, pointerRegister);
        }

        currentPipeValueRegister = null; // Cleanup
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
            if (this.currentPipeValueRegister == null && !(
                elements.get(i) instanceof ExpressionAsPipeElement
                    && ((ExpressionAsPipeElement) elements.get(
                    i)).getExpression() instanceof ReadExpression)) {
                // Jeśli poprzedni element nie wyprodukował wartości (np. samo przypisanie bez dalszego przepływu), 
                // a obecny nie jest ReadExpression (które samo generuje wartość), zgłoś błąd lub ostrzeżenie.
                // Chyba że element sam w sobie jest np. ReadExpression, które generuje wartość.
                instructions.add(new CommentInstruction("Warning: Pipe element at index " + i
                    + " expects a value from previous element, but none was provided."));
                // Można tu rzucić wyjątek lub spróbować kontynuować, jeśli to ma sens.
                // Na razie pozwalamy kontynuować, ale currentPipeValueRegister pozostaje null.
            }
            this.currentPipeValueRegister = elements.get(i)
                .accept(this); // Wynik accept nadpisuje currentPipeValueRegister
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
            instructions.add(new CommentInstruction(
                "Error: TypeCast expects a value from pipe, but currentPipeValueRegister is null."));
            return generateRegister(); // Zwróć pusty/nowy rejestr, aby uniknąć NullPointer, ale to błąd
        }
        String inputRegister = this.currentPipeValueRegister;
        String targetTypeName = typeCastPipeExpression.getTargetType();
        Type targetType;
        try {
            targetType = Type.fromTypeName(targetTypeName);
        } catch (IllegalArgumentException e) {
            instructions.add(
                new CommentInstruction("Error: Unknown target type for cast: " + targetTypeName));
            targetType = Type.DYNAMIC; // Użyj dynamicznego jako fallback
        }

        String resultRegister = generateRegister();
        // Założenie: istnieje TypeCastInstruction(result, input, targetType, sourceType) lub podobna.
        // Potrzebujemy sourceType, który nie jest łatwo dostępny bez analizy typu inputRegister.
        // Na razie uproszczenie: zakładamy, że TypeCastInstruction poradzi sobie z typem DYNAMIC lub typ zostanie określony później.
        instructions.add(new TypeCastInstruction(resultRegister, inputRegister, targetType,
            Type.DYNAMIC /* Placeholder for source type */));
        instructions.add(new CommentInstruction(
            "Casting " + inputRegister + " to " + targetType.getTypeName() + " into "
                + resultRegister));
        return resultRegister;
    }

    @Override
    public String visit(VariableAssignPipeStatement variableAssignPipeStatement) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction(
                "Error: VariableAssignPipeStatement expects a value from pipe, but currentPipeValueRegister is null."));
            // We still continue for robustness
        }

        String varName = variableAssignPipeStatement.getVariableName();
        String declaredTypeName = variableAssignPipeStatement.getDeclaredType();
        Type varType;

        if (declaredTypeName != null && !declaredTypeName.equals("_")) {
            try {
                varType = Type.fromTypeName(declaredTypeName);
            } catch (IllegalArgumentException e) {
                instructions.add(new CommentInstruction(
                    "Warning: Unknown type " + declaredTypeName + " for variable " + varName
                        + ", using dynamic type."));
                varType = Type.DYNAMIC;
            }
        } else {
            varType = Type.DYNAMIC;
        }

        // Declare variable if it doesn't exist
        if (scopeManager.getVariableType(varName) == null) {
            String varRegister = generateRegister();
            scopeManager.declareVariable(varName, varType, varRegister);
            instructions.add(new CommentInstruction(
                "Declared variable in pipe: " + varName + " of type " + varType.getTypeName()));
        } else {
            Type existingType = scopeManager.getVariableType(varName);
            if (!existingType.equals(varType) && varType != Type.DYNAMIC) {
                instructions.add(new CommentInstruction(
                    "Warning: Re-assigning variable " + varName + " with a different type ("
                        + varType.getTypeName() + ") than original (" + existingType.getTypeName()
                        + ")."));
            }
        }

        // ✅ Store the value from the pipe
        if (this.currentPipeValueRegister != null) {
            String pointerRegister = scopeManager.getVariableRegister(varName);
            instructions.add(
                new StoreInstruction(currentPipeValueRegister, pointerRegister, varType));
            instructions.add(new CommentInstruction(
                "Assigned value from pipe (reg: " + this.currentPipeValueRegister + ") to variable "
                    + varName));
        } else {
            instructions.add(new CommentInstruction(
                "Skipping assignment to " + varName + " due to null value in pipe."));
        }

        return this.currentPipeValueRegister;
    }


    @Override
    public String visit(IfPipeStatement ifPipeStatement) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction(
                "Error: IfPipeStatement expects a boolean value from pipe, but currentPipeValueRegister is null."));
            // Można wygenerować skok do końca bloku if lub zachować się inaczej
            String endLabel = generateLabel("endif_pipe_err");
            instructions.add(new LabelInstruction(endLabel));
            return this.currentPipeValueRegister; // Lub null
        }

        String condRegister = this.currentPipeValueRegister;
        String thenLabel = generateLabel("then_pipe");
        String endLabel = generateLabel("endif_pipe");

        instructions.add(
            new CommentInstruction("Piped if condition from register: " + condRegister));
        instructions.add(new BranchInstruction(condRegister, thenLabel, endLabel));

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

        instructions.add(new UncondBranchInstruction(endLabel));
        instructions.add(new LabelInstruction(endLabel));

        // Zwracamy currentPipeValueRegister, który mógł (lub nie) zostać zmieniony przez blok 'then'.
        // Jeśli blok then ma jednoznacznie ustawiać wynik dla potoku, powinien to zrobić poprzez currentPipeValueRegister.
        return this.currentPipeValueRegister;
    }

    @Override
    public String visit(ReturnPipeStatement returnPipeStatement) {
        if (this.currentPipeValueRegister == null) {
            instructions.add(new CommentInstruction(
                "Warning: ReturnPipeStatement expects a value from pipe, but currentPipeValueRegister is null. Returning void/default."));
            instructions.add(new ReturnInstruction()); // Zwraca void
            return null;
        }
        instructions.add(new ReturnValueInstruction(this.currentPipeValueRegister));
        instructions.add(new CommentInstruction(
            "Returned value from pipe (reg: " + this.currentPipeValueRegister + ")"));
        return this.currentPipeValueRegister; // Rejestr jest zwracany, ale wykonanie funkcji się kończy
    }

    @Override
    public String visit(UnaryOperation unaryOperation) {
        // Generujemy rejestr dla operand i wyniku
        String operandReg = unaryOperation.getOperand().accept(this);
        String resultReg = generateRegister();

        // Dodajemy instrukcję negacji logicznej
        // Zakładamy istnienie klasy instrukcji UnaryOperationInstruction
        // z enumem Operation.NOT oraz typem BOOLEAN
        instructions.add(
            new UnaryOperationInstruction(
                resultReg,
                operandReg,
                UnaryOperationInstruction.Operation.NOT,
                Type.BOOLEAN
            )
        );
        return resultReg;
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

        String thenLabel = generateLabel("then");
        String endLabel = generateLabel("endif");

        if (ifStatement.hasElse()) {
            String elseLabel = generateLabel("else");

            instructions.add(new CommentInstruction("if condition: " + condRegister));
            instructions.add(new BranchInstruction(condRegister, thenLabel, elseLabel));

            // Blok then
            instructions.add(new LabelInstruction(thenLabel));
            ifStatement.getThenBlock().accept(this);
            instructions.add(new UncondBranchInstruction(endLabel));

            // Blok else
            instructions.add(new LabelInstruction(elseLabel));
            ifStatement.getElseBlock().accept(this);
            instructions.add(new UncondBranchInstruction(endLabel));
        } else {
            // Tylko if bez else
            instructions.add(new CommentInstruction("if condition: " + condRegister));
            instructions.add(new BranchInstruction(condRegister, thenLabel, endLabel));

            // Blok then
            instructions.add(new LabelInstruction(thenLabel));
            ifStatement.getThenBlock().accept(this);
            instructions.add(new UncondBranchInstruction(endLabel));
        }

        // Koniec instrukcji if
        instructions.add(new LabelInstruction(endLabel));
        currentPipeValueRegister = null;
        return null;
    }

    @Override
    public String visit(WhileLoop whileLoop) {
        String condLabel = generateLabel("while_cond");
        String bodyLabel = generateLabel("while_body");
        String endLabel = generateLabel("while_end");

        // Push a new scope for the loop. Variables declared within the loop's body
        // will belong to this scope.
        scopeManager.enterScope();
        loopManager.enterLoop(condLabel, endLabel);

        // CRITICAL FIX: Add an unconditional branch to jump to the loop condition label
        // from the block where the while loop is initially placed (e.g., 'entry' block).
        instructions.add(new UncondBranchInstruction(condLabel)); // <--- ADDED THIS LINE

        // Etykieta warunku
        instructions.add(new LabelInstruction(condLabel));
        String condRegister = whileLoop.getCondition().accept(this);
        instructions.add(new BranchInstruction(condRegister, bodyLabel, endLabel));

        // Ciało pętli
        instructions.add(new LabelInstruction(bodyLabel));
        whileLoop.getBody().accept(this);
        instructions.add(new UncondBranchInstruction(condLabel)); // Branch back to condition

        // Koniec pętli
        instructions.add(new LabelInstruction(endLabel));

        // Kończymy kontekst pętli
        loopManager.exitLoop();
        scopeManager.exitScope();
        currentPipeValueRegister = null;
        return null;
    }


    @Override
    public String visit(ForLoop forLoop) {
        String initLabel = generateLabel("for_init");
        String condLabel = generateLabel("for_cond");
        String bodyLabel = generateLabel("for_body");
        String iterLabel = generateLabel("for_iter"); // Used for standard for loop's iteration step
        // For foreach, 'continue' might go to its own iter_idx_increment label
        String endLabel = generateLabel("for_end");

        // For 'continue' in foreach, it should ideally jump to the increment of the hidden iterator.
        // Let's define a specific label for foreach iterator increment.
        String foreachIterIncLabel = generateLabel("foreach_iter_inc");

        loopManager.enterLoop(iterLabel,
            endLabel); // 'continue' goes to iteration expression, 'break' to end

        // --- Standard For Loop Implementation ---
        instructions.add(new CommentInstruction("Standard for loop"));

        // Crucial: Terminate the current block (e.g., 'entry') before starting the loop's init block
        instructions.add(new UncondBranchInstruction(initLabel));

        // Initialization block
        instructions.add(new LabelInstruction(initLabel));
        String loopControlVarPointer = null;
        if (forLoop.getInitialization() != null) {
            Node initNode = forLoop.getInitialization();
            String acceptanceResult = initNode.accept(
                this); // Execute declaration/assignment. Returns ptr for Decl, valReg for Assign.

            if (initNode instanceof Declaration) {
                Declaration decl = (Declaration) initNode;
                loopControlVarPointer = acceptanceResult;
                loopManager.registerIterationVariable(decl.getName(), loopControlVarPointer);
            } else if (initNode instanceof Assignment) {
                Assignment assign = (Assignment) initNode;
                String varName = assign.getTarget().getName();
                loopControlVarPointer = scopeManager.getVariableRegister(varName);
                if (loopControlVarPointer == null) {
                    throw new IllegalStateException("Variable '" + varName
                        + "' in for-loop initializer assignment not found in scope.");
                }
                loopManager.registerIterationVariable(varName, loopControlVarPointer);
            } else {
                instructions.add(new CommentInstruction(
                    "For-loop initialization is an expression; loop control variable pointer not set from init."));
            }
        } else {
            instructions.add(
                new CommentInstruction("No initialization expression for standard for-loop."));
        }
        instructions.add(new UncondBranchInstruction(condLabel));

        // Condition block
        instructions.add(new LabelInstruction(condLabel));
        if (forLoop.getCondition() != null && !forLoop.getCondition().toString().trim().isEmpty()) {
            String conditionResultRegister = forLoop.getCondition().accept(this);
            instructions.add(new BranchInstruction(conditionResultRegister, bodyLabel, endLabel));
        } else {
            instructions.add(new CommentInstruction(
                "No condition expression for standard for-loop (infinite loop)."));
            instructions.add(new UncondBranchInstruction(bodyLabel));
        }

        // Body block
        instructions.add(new LabelInstruction(bodyLabel));
        forLoop.getBody().accept(this);
        instructions.add(new UncondBranchInstruction(iterLabel));

        // Iteration block
        instructions.add(new LabelInstruction(iterLabel));
        if (forLoop.getIteration() != null) { // <--- THIS IS THE CRITICAL LINE
            forLoop.getIteration()
                .accept(this); // Execute iteration expression (e.g., i++, assignment)
        } else {
            instructions.add(
                new CommentInstruction("No iteration expression for standard for-loop."));
        }
        instructions.add(new UncondBranchInstruction(condLabel));

        // End block
        instructions.add(new LabelInstruction(endLabel));
        loopManager.exitLoop();
        currentPipeValueRegister = null;
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

        instructions.add(
            new CommentInstruction("Reading input from user, expected type: " + expectedType));
        instructions.add(new ReadInstruction(register, expectedType));

        return register;
    }

    @Override
    public String visit(ArrayAccess arrayAccess) {
        // 1) Compute index → this returns e.g. "r5"
        String indexReg = arrayAccess.getIndex().accept(this);

        // 2) Find the alloca slot for “array”
        String arrayVarName = arrayAccess.getArrayName();
        String allocaSlotReg = scopeManager.getVariableRegister(arrayVarName);
        // e.g. "r0" if you did: %r0 = alloca i32*, align 8
        if (allocaSlotReg == null) {
            instructions.add(new CommentInstruction(
                "Error: array \"" + arrayVarName + "\" not found"
            ));
            return generateRegister(); // give a dummy so codegen continues
        }

        // 3) Load the base‐pointer (i32*) from that slot:
        //    %rX = load i32*, i32** %r0
        String arrPtrReg = generateRegister();
        instructions.add(new RawInstruction(
            "%" + arrPtrReg
                + " = load i32*, i32** %" + allocaSlotReg
        ));

        // 4) GEP to compute element’s address:
        //    %rY = getelementptr inbounds i32, i32* %rX, i32 %indexReg
        String elemPtrReg = generateRegister();
        instructions.add(new RawInstruction(
            "%" + elemPtrReg
                + " = getelementptr inbounds i32, i32* %" + arrPtrReg
                + ", i32 %" + indexReg
        ));

        // 5) Load the actual element:
        //    %rZ = load i32, i32* %rY
        String loadedReg = generateRegister();
        instructions.add(new RawInstruction(
            "%" + loadedReg
                + " = load i32, i32* %" + elemPtrReg
        ));

        // Return e.g. "rZ" (but later callers should remember to prefix “%” if they embed it
        // into their own RawInstruction). By convention, visitor returns the _bare_ name
        // (no “%”), but all emissions do prefix it.
        return loadedReg;
    }


    @Override
    public String visit(ArrayLiteral arrayLiteral) {


        List<Expression> elems = arrayLiteral.getElements();
        int n = elems.size();

        // 2) Determine LLVM element type. Default to i32 if “dynamic”:
        Type elementType = Type.DYNAMIC;
        if (n > 0 && elems.get(0) instanceof Literal) {
            elementType = determineTypeFromLiteral((Literal) elems.get(0));
        }
        String llvmElemTy = "i32";
        if (elementType == Type.FLOAT)   llvmElemTy = "float";
        if (elementType == Type.BOOLEAN) llvmElemTy = "i1";
        if (elementType == Type.STRING)  llvmElemTy = "i8*";
        // … add other cases if needed …

        // 3) Build “[N x T]”
        String llvmArrayTy = "[" + n + " x " + llvmElemTy + "]";

        // 4) alloca [N x T]:
        String arrayAllocReg = generateRegister(); // e.g. “r3”
        instructions.add(new RawInstruction(
            "%" + arrayAllocReg
                + " = alloca " + llvmArrayTy + ", align 16"
        ));

        // 5) For each element, GEP + store:
        for (int i = 0; i < n; i++) {
            // a) evaluate element → e.g. “r5”
            String valueReg = elems.get(i).accept(this);

            // b) getelementptr to slot i:
            String gepReg = generateRegister();
            instructions.add(new RawInstruction(
                "%" + gepReg
                    + " = getelementptr inbounds " + llvmArrayTy + ", "
                    + llvmArrayTy + "* %" + arrayAllocReg
                    + ", i32 0, i32 " + i
            ));

            // c) store into that slot:
            instructions.add(new RawInstruction(
                "store " + llvmElemTy + " %" + valueReg
                    + ", " + llvmElemTy + "* %" + gepReg
            ));
        }

        // 6) Bitcast [N x T]* → T*:
        String basePtrReg = generateRegister(); // e.g. “r10”
        instructions.add(new RawInstruction(
            "%" + basePtrReg
                + " = bitcast " + llvmArrayTy + "* %" + arrayAllocReg
                + " to " + llvmElemTy + "*"
        ));

        // Return the _bare_ register name (e.g. “r10”); the caller (e.g. assignment) will do:
        //    store i32* %r10, i32** %someSlot
        return basePtrReg;
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
    public String visit(PrintStatement printStatement) {
        for (Expression expr : printStatement.getArguments()) {
            // Generate the value (register holding the result of the expression)
            String valueReg = expr.accept(this);
            Type type = typeChecker.getTypeOf(expr);

            // If it's a boolean, we need to zero-extend it to i32
            if (type == Type.BOOLEAN) {
                String extendedReg = generateRegister();
                instructions.add(new RawInstruction(
                    "%" + extendedReg + " = zext i1 %" + valueReg + " to i32"
                ));
                valueReg = extendedReg;
                type = Type.INTEGER; // For printf, treat it as an integer now
            }

            // Get the format string pointer instruction
            String formatReg = generateRegister();
            String formatPtrCode = stringManager.getFormatPointer(type);
            instructions.add(new RawInstruction("%" + formatReg + " = " + formatPtrCode));

            // Add a typed PrintInstruction
            instructions.add(new PrintInstruction(formatReg, valueReg, type));
        }

        currentPipeValueRegister = null;
        return null;
    }

    @Override
    public String visit(ArrayAssignment arrayAssignment) {
        String arrayName = arrayAssignment.getArrayName();

        // Compute index expression
        String indexReg = arrayAssignment.getIndex().accept(this);

        // Lookup array pointer
        String arrayPtrReg = scopeManager.getVariableRegister(arrayName);
        if (arrayPtrReg == null) {
            instructions.add(new CommentInstruction("Error: array \"" + arrayName + "\" not found"));
            return generateRegister();
        }

        // Load base pointer
        String basePtrReg = generateRegister();
        instructions.add(new RawInstruction(
            "%" + basePtrReg + " = load i32*, i32** %" + arrayPtrReg
        ));

        // Compute element pointer
        String elemPtrReg = generateRegister();
        instructions.add(new RawInstruction(
            "%" + elemPtrReg + " = getelementptr inbounds i32, i32* %" + basePtrReg + ", i32 %" + indexReg
        ));

        // Compute value to store
        String valueReg = arrayAssignment.getValue().accept(this);

        // Store it
        instructions.add(new RawInstruction(
            "store i32 %" + valueReg + ", i32* %" + elemPtrReg
        ));

        return valueReg;
    }



    @Override
    public String visit(FunctionDeclaration functionDeclaration) {
        String functionName = functionDeclaration.getName();

        instructions.add(new CommentInstruction("Function declaration: " + functionName));

        // Rozpoczęcie nowego zakresu dla parametrów funkcji
        scopeManager.enterScope();

        String currentEntryLabel = generateLabel("entry");
        instructions.add(new LabelInstruction(currentEntryLabel));

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
            instructions.add(new CommentInstruction(
                "Function parameter: " + paramName + " of type " + paramType));
        }

        // Generujemy kod dla ciała funkcji
        functionDeclaration.getBody().accept(this);

        // Kończymy zakres funkcji
        scopeManager.exitScope();
        currentPipeValueRegister = null; // Resetuj po każdej instrukcji najwyższego poziomu
        return null;
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
     * Instrukcja wywołania funkcji.
     */
    private static class CallInstruction implements Instruction {

        private final String resultRegister;
        private final String functionName;
        private final List<String> argumentRegisters;
        private final Type returnType;

        public CallInstruction(String resultRegister, String functionName,
            List<String> argumentRegisters, Type returnType) {
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
} 