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
        Literal.Type litAstType = literal.getType(); // AST Literal Type
        pl.edu.pw.slish.codegen.Type codegenType; // codegen.Type

        if (litAstType == Literal.Type.STRING) {
            String instr = stringManager.generateStringPointerInstruction(register,
                (String) literal.getValue());
            instructions.add(new RawInstruction(instr));
            return register;
        }

        switch (litAstType) {
            case INTEGER:
                codegenType = pl.edu.pw.slish.codegen.Type.INTEGER;
                break;
            case FLOAT32: // NEW
                codegenType = pl.edu.pw.slish.codegen.Type.FLOAT32;
                break;
            case FLOAT64: // NEW
                codegenType = pl.edu.pw.slish.codegen.Type.FLOAT64;
                break;
            // case FLOAT: // OLD - remove
            //    codegenType = pl.edu.pw.slish.codegen.Type.FLOAT64; // Default old AST float to codegen.FLOAT64
            //    break;
            case BOOLEAN:
                codegenType = pl.edu.pw.slish.codegen.Type.BOOLEAN;
                break;
            default:
                codegenType = pl.edu.pw.slish.codegen.Type.DYNAMIC; // Should not happen for valid literals
                instructions.add(new CommentInstruction(
                    "Warning: Unexpected literal type in codegen: " + litAstType));
        }

        instructions.add(new LoadConstantInstruction(register, literal.getValue(), codegenType));
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
        String resultReg = generateRegister(); // Register for the result of the function call
        List<String> argValueRegisters = new ArrayList<>(); // Registers holding evaluated argument values
        List<Type> argActualTypes = new ArrayList<>(); // Actual types of the arguments

        // Evaluate arguments, store their registers and actual types
        for (Expression argExpr : functionCall.getArguments()) {
            argValueRegisters.add(argExpr.accept(this));
            argActualTypes.add(typeChecker.getTypeOf(argExpr));
        }

        String functionName = functionCall.getFunctionName();

        // Special handling for built-in functions that map to binary operations
        if ((functionName.equals("add") || functionName.equals("gt") || functionName.equals("lt"))
            &&
            functionCall.getArguments().size() == 2) {

            String leftArgInitialReg = argValueRegisters.get(0);
            String rightArgInitialReg = argValueRegisters.get(1);
            Type leftArgActualType = argActualTypes.get(0);
            Type rightArgActualType = argActualTypes.get(1);

            String currentLeftReg = leftArgInitialReg;
            String currentRightReg = rightArgInitialReg;

            Type llvmInstructionOperandType; // The type the LLVM instruction will operate on (after promotion)
            Type operationActualResultType;  // The final type of the result register from this operation

            // Determine LLVM operand type and actual result type based on the function
            if (functionName.equals("add")) {
                // Promotion logic for ADD: Result is F64 if any F64, F32 if any F32, else INT. Operands promoted accordingly.
                if (leftArgActualType == Type.FLOAT64 || rightArgActualType == Type.FLOAT64) {
                    llvmInstructionOperandType = Type.FLOAT64;
                    operationActualResultType = Type.FLOAT64;
                } else if (leftArgActualType == Type.FLOAT32
                    || rightArgActualType == Type.FLOAT32) {
                    llvmInstructionOperandType = Type.FLOAT32;
                    operationActualResultType = Type.FLOAT32;
                } else if (leftArgActualType == Type.INTEGER
                    && rightArgActualType == Type.INTEGER) {
                    llvmInstructionOperandType = Type.INTEGER;
                    operationActualResultType = Type.INTEGER;
                } else { // Handle cases involving DYNAMIC or type errors (which TypeChecker should catch)
                    // Default to FLOAT64 if DYNAMIC is involved for arithmetic.
                    llvmInstructionOperandType = Type.FLOAT64;
                    operationActualResultType = Type.FLOAT64;
                    if (leftArgActualType != Type.DYNAMIC && rightArgActualType != Type.DYNAMIC) {
                        // This indicates a type mismatch not caught or allowed by TypeChecker.
                        instructions.add(new CommentInstruction(
                            "Warning: Type mismatch for 'add' builtin: " + leftArgActualType + ", "
                                + rightArgActualType + ". Defaulting to f64."));
                    }
                }
            } else { // For "gt", "lt" (comparisons) - result is always BOOLEAN
                operationActualResultType = Type.BOOLEAN;
                // Determine operand type for comparison (fcmp vs icmp)
                if (leftArgActualType == Type.FLOAT64 || rightArgActualType == Type.FLOAT64) {
                    llvmInstructionOperandType = Type.FLOAT64;
                } else if (leftArgActualType == Type.FLOAT32
                    || rightArgActualType == Type.FLOAT32) {
                    llvmInstructionOperandType = Type.FLOAT32;
                } else if (leftArgActualType == Type.INTEGER
                    && rightArgActualType == Type.INTEGER) {
                    llvmInstructionOperandType = Type.INTEGER;
                } else if (leftArgActualType == Type.BOOLEAN
                    && rightArgActualType == Type.BOOLEAN) {
                    llvmInstructionOperandType = Type.BOOLEAN;
                } else { // Handle DYNAMIC or other potentially comparable types (e.g. strings, if supported by gt/lt)
                    // Default to FLOAT64 for numeric-like DYNAMIC comparisons.
                    // TypeChecker should have ensured comparability.
                    llvmInstructionOperandType = Type.FLOAT64; // A general default for unknown numeric comparison
                    if ((leftArgActualType == Type.BOOLEAN || rightArgActualType == Type.BOOLEAN)
                        && !(leftArgActualType == Type.BOOLEAN
                        && rightArgActualType == Type.BOOLEAN) && !(
                        leftArgActualType == Type.DYNAMIC || rightArgActualType == Type.DYNAMIC)) {
                        instructions.add(new CommentInstruction(
                            "Warning: Comparing boolean with non-boolean for '" + functionName
                                + "': " + leftArgActualType + ", " + rightArgActualType));
                        // Defaulting to float64 might be wrong here, but type checker should prevent this.
                    } else if (leftArgActualType == Type.BOOLEAN
                        && rightArgActualType == Type.BOOLEAN) {
                        llvmInstructionOperandType = Type.BOOLEAN;
                    } else if (leftArgActualType != Type.DYNAMIC
                        && rightArgActualType != Type.DYNAMIC) {
                        instructions.add(new CommentInstruction(
                            "Warning: Unusual type combination for '" + functionName
                                + "' comparison: " + leftArgActualType + ", " + rightArgActualType
                                + ". Defaulting operand type to f64."));
                    }
                }
            }

            // --- Perform necessary type casts for operands ---
            // Cast left operand if its actual type doesn't match the determined LLVM operation type
            if (leftArgActualType != llvmInstructionOperandType
                && leftArgActualType != Type.DYNAMIC) {
                // Check for valid numeric promotions/conversions (int->float, f32->f64)
                if ((llvmInstructionOperandType == Type.FLOAT64 && (
                    leftArgActualType == Type.FLOAT32 || leftArgActualType == Type.INTEGER)) ||
                    (llvmInstructionOperandType == Type.FLOAT32
                        && leftArgActualType == Type.INTEGER) ||
                    (llvmInstructionOperandType == Type.INTEGER && (
                        leftArgActualType == Type.FLOAT32
                            || leftArgActualType == Type.FLOAT64))) { // Last case is fptosi
                    String castedLeft = generateRegister();
                    instructions.add(new TypeCastInstruction(castedLeft, currentLeftReg,
                        llvmInstructionOperandType, leftArgActualType));
                    currentLeftReg = castedLeft;
                } else if (llvmInstructionOperandType
                    != Type.DYNAMIC) { // Avoid redundant warning if target is dynamic
                    instructions.add(new CommentInstruction(
                        "Warning: Potentially unhandled cast from " + leftArgActualType + " to "
                            + llvmInstructionOperandType + " for left operand of " + functionName));
                }
            }

            // Cast right operand
            if (rightArgActualType != llvmInstructionOperandType
                && rightArgActualType != Type.DYNAMIC) {
                if ((llvmInstructionOperandType == Type.FLOAT64 && (
                    rightArgActualType == Type.FLOAT32 || rightArgActualType == Type.INTEGER)) ||
                    (llvmInstructionOperandType == Type.FLOAT32
                        && rightArgActualType == Type.INTEGER) ||
                    (llvmInstructionOperandType == Type.INTEGER && (
                        rightArgActualType == Type.FLOAT32
                            || rightArgActualType == Type.FLOAT64))) {
                    String castedRight = generateRegister();
                    instructions.add(new TypeCastInstruction(castedRight, currentRightReg,
                        llvmInstructionOperandType, rightArgActualType));
                    currentRightReg = castedRight;
                } else if (llvmInstructionOperandType != Type.DYNAMIC) {
                    instructions.add(new CommentInstruction(
                        "Warning: Potentially unhandled cast from " + rightArgActualType + " to "
                            + llvmInstructionOperandType + " for right operand of "
                            + functionName));
                }
            }

            BinaryOperationInstruction.Operation opCode = null;
            if (functionName.equals("add")) {
                opCode = BinaryOperationInstruction.Operation.ADD;
            } else if (functionName.equals("gt")) {
                opCode = BinaryOperationInstruction.Operation.GREATER_THAN;
            } else if (functionName.equals("lt")) {
                opCode = BinaryOperationInstruction.Operation.LESS_THAN;
            }

            if (opCode != null) {
                Type finalInstructionOperandType = llvmInstructionOperandType;
                // If after all logic, llvmInstructionOperandType is DYNAMIC, default to a concrete type for the instruction.
                if (llvmInstructionOperandType == Type.DYNAMIC) {
                    finalInstructionOperandType = Type.FLOAT64; // Default to double for operations involving dynamic types.
                    instructions.add(new CommentInstruction("Info: operands for " + functionName
                        + " involved DYNAMIC, defaulting instruction operand type to "
                        + finalInstructionOperandType));
                }

                instructions.add(new BinaryOperationInstruction(
                    resultReg, currentLeftReg, currentRightReg,
                    opCode,
                    finalInstructionOperandType, // The type for LLVM IR (e.g., i32, float, double)
                    operationActualResultType
                    // The type checker's view of the result (e.g. Type.INTEGER, Type.BOOLEAN)
                ));
                return resultReg;
            }
            // If opCode remained null (e.g., wrong arg count handled by initial if but not specific error), fall through.
            // Or, if arg count was wrong, we'd fall through from the start.
            instructions.add(new CommentInstruction(
                "Warning: Builtin '" + functionName + "' with arg count "
                    + functionCall.getArguments().size() + " fell through to default call."));
        }

        // Default function call handling for user-defined functions or other built-ins
        Type returnTypeFromChecker = typeChecker.getFunctionReturnType(functionName);
        instructions.add(
            new CallInstruction(resultReg, functionName, argValueRegisters, returnTypeFromChecker));
        return resultReg;
    }

    // In CodeGenerator.java
    @Override
    public String visit(BinaryOperation binOp) {
        String leftReg = binOp.getLeft().accept(this);
        switch (binOp.getOperator()) {
            case AND:
                return genShortCircuitAnd(binOp, leftReg);
            case OR:
                return genShortCircuitOr(binOp, leftReg);
            case XOR: { // Assuming XOR is for boolean types as per TypeChecker logic
                String rightReg = binOp.getRight().accept(this);
                String resReg = generateRegister();
                // TypeChecker ensures left and right are boolean for XOR.
                // The result of XOR is also boolean.
                instructions.add(new BinaryOperationInstruction(
                    resReg, leftReg, rightReg,
                    BinaryOperationInstruction.Operation.XOR,
                    Type.BOOLEAN, // Operand type for LLVM 'xor' instruction (i1 for boolean)
                    Type.BOOLEAN  // Actual result type of the operation
                ));
                return resReg;
            }
            default:
                return genRegularBinary(binOp, leftReg);
        }
    }

    // In CodeGenerator.java
    private String genRegularBinary(BinaryOperation binOp, String leftReg) {
        String rightRegRaw = binOp.getRight().accept(this); // Renamed to avoid conflict
        String resultReg = generateRegister();

        Type leftType = typeChecker.getTypeOf(binOp.getLeft());
        Type rightType = typeChecker.getTypeOf(binOp.getRight());
        Type resultBinOpType = typeChecker.getTypeOf(binOp); // Overall result type from TypeChecker

        String currentLeftReg = leftReg;
        String currentRightReg = rightRegRaw;

        // Handle type promotions for LLVM (float/double)
        // If result is Float64, and one operand is Float32, extend it
        if (resultBinOpType == Type.FLOAT64) {
            if (leftType == Type.FLOAT32
                && rightType != Type.FLOAT32) { // left is F32, right is F64 or Int
                String extendedLeft = generateRegister();
                instructions.add(new TypeCastInstruction(extendedLeft, currentLeftReg, Type.FLOAT64,
                    Type.FLOAT32)); // fpext
                currentLeftReg = extendedLeft;
                leftType = Type.FLOAT64;
            }
            if (rightType == Type.FLOAT32
                && leftType != Type.FLOAT32) { // right is F32, left is F64 or Int
                String extendedRight = generateRegister();
                instructions.add(
                    new TypeCastInstruction(extendedRight, currentRightReg, Type.FLOAT64,
                        Type.FLOAT32)); // fpext
                currentRightReg = extendedRight;
                rightType = Type.FLOAT64;
            }
            // If one is Integer and target is Float64
            if (leftType == Type.INTEGER) {
                String convertedLeft = generateRegister();
                instructions.add(
                    new TypeCastInstruction(convertedLeft, currentLeftReg, Type.FLOAT64,
                        Type.INTEGER)); // sitofp
                currentLeftReg = convertedLeft;
                leftType = Type.FLOAT64;
            }
            if (rightType == Type.INTEGER) {
                String convertedRight = generateRegister();
                instructions.add(
                    new TypeCastInstruction(convertedRight, currentRightReg, Type.FLOAT64,
                        Type.INTEGER)); // sitofp
                currentRightReg = convertedRight;
                rightType = Type.FLOAT64;
            }
        } else if (resultBinOpType == Type.FLOAT32) {
            // If result is Float32, operands should be compatible.
            // Convert integers to float32
            if (leftType == Type.INTEGER) {
                String convertedLeft = generateRegister();
                instructions.add(
                    new TypeCastInstruction(convertedLeft, currentLeftReg, Type.FLOAT32,
                        Type.INTEGER)); // sitofp
                currentLeftReg = convertedLeft;
                leftType = Type.FLOAT32;
            } else if (leftType == Type.FLOAT64) { // NEW: Handle F64 -> F32 if result is F32
                String truncatedLeft = generateRegister();
                instructions.add(
                    new TypeCastInstruction(truncatedLeft, currentLeftReg, Type.FLOAT32,
                        Type.FLOAT64)); // fptrunc
                currentLeftReg = truncatedLeft;
                leftType = Type.FLOAT32;
            }

            if (rightType == Type.INTEGER) {
                String convertedRight = generateRegister();
                instructions.add(
                    new TypeCastInstruction(convertedRight, currentRightReg, Type.FLOAT32,
                        Type.INTEGER)); // sitofp
                currentRightReg = convertedRight;
                rightType = Type.FLOAT32;
            } else if (rightType == Type.FLOAT64) { // NEW: Handle F64 -> F32 if result is F32
                String truncatedRight = generateRegister();
                instructions.add(
                    new TypeCastInstruction(truncatedRight, currentRightReg, Type.FLOAT32,
                        Type.FLOAT64)); // fptrunc
                currentRightReg = truncatedRight;
                rightType = Type.FLOAT32;
            }
        }

        BinaryOperationInstruction.Operation llvmOp = switch (binOp.getOperator()) {
            case ADD -> BinaryOperationInstruction.Operation.ADD;
            case SUBTRACT -> BinaryOperationInstruction.Operation.SUBTRACT;
            case MULTIPLY -> BinaryOperationInstruction.Operation.MULTIPLY;
            case DIVIDE -> BinaryOperationInstruction.Operation.DIVIDE;
            case MODULO -> BinaryOperationInstruction.Operation.MODULO; // Stays integer
            case EQUAL -> BinaryOperationInstruction.Operation.EQUAL;
            case NOT_EQUAL -> BinaryOperationInstruction.Operation.NOT_EQUAL;
            case GREATER_THAN -> BinaryOperationInstruction.Operation.GREATER_THAN;
            case LESS_THAN -> BinaryOperationInstruction.Operation.LESS_THAN;
            case GREATER_EQUAL -> BinaryOperationInstruction.Operation.GREATER_EQUAL;
            case LESS_EQUAL -> BinaryOperationInstruction.Operation.LESS_EQUAL;
            // AND, OR, XOR handled by short-circuit or direct bitwise for integers
            default -> throw new IllegalArgumentException(
                "Nieznany operator dla genRegularBinary: " + binOp.getOperator());
        };

        // The type for the BinaryOperationInstruction should be the type of the operands *after* promotion
        // For comparisons, the result is boolean, but operands might be float/double.
        Type instructionOperandType = leftType; // Assume left and right are same type after promotion
        if (llvmOp == BinaryOperationInstruction.Operation.EQUAL ||
            llvmOp == BinaryOperationInstruction.Operation.NOT_EQUAL ||
            llvmOp == BinaryOperationInstruction.Operation.GREATER_THAN ||
            llvmOp == BinaryOperationInstruction.Operation.LESS_THAN ||
            llvmOp == BinaryOperationInstruction.Operation.GREATER_EQUAL ||
            llvmOp == BinaryOperationInstruction.Operation.LESS_EQUAL) {
            // For comparisons, operand types are important for fcmp/icmp. Result is BOOLEAN.
            // We need to ensure currentLeftReg and currentRightReg are of compatible types for comparison.
            // If one is float64 and other is float32, promote float32 to float64 for comparison.
            if (leftType == Type.FLOAT64 && rightType == Type.FLOAT32) {
                String extendedRight = generateRegister();
                instructions.add(
                    new TypeCastInstruction(extendedRight, currentRightReg, Type.FLOAT64,
                        Type.FLOAT32));
                currentRightReg = extendedRight;
                instructionOperandType = Type.FLOAT64;
            } else if (leftType == Type.FLOAT32 && rightType == Type.FLOAT64) {
                String extendedLeft = generateRegister();
                instructions.add(new TypeCastInstruction(extendedLeft, currentLeftReg, Type.FLOAT64,
                    Type.FLOAT32));
                currentLeftReg = extendedLeft;
                instructionOperandType = Type.FLOAT64;
            } else if ((leftType == Type.FLOAT32 || leftType == Type.FLOAT64)
                && rightType == Type.INTEGER) {
                String convertedRight = generateRegister();
                instructions.add(new TypeCastInstruction(convertedRight, currentRightReg, leftType,
                    Type.INTEGER)); // int to float matching left
                currentRightReg = convertedRight;
                instructionOperandType = leftType;
            } else if ((rightType == Type.FLOAT32 || rightType == Type.FLOAT64)
                && leftType == Type.INTEGER) {
                String convertedLeft = generateRegister();
                instructions.add(new TypeCastInstruction(convertedLeft, currentLeftReg, rightType,
                    Type.INTEGER)); // int to float matching right
                currentLeftReg = convertedLeft;
                instructionOperandType = rightType;
            } else {
                instructionOperandType = leftType; // Or rightType, should be same numeric category
            }
            instructions.add(
                new BinaryOperationInstruction(resultReg, currentLeftReg, currentRightReg, llvmOp,
                    instructionOperandType, Type.BOOLEAN));

        } else {
            // For arithmetic operations, resultBinOpType is the type of the result register
            instructions.add(
                new BinaryOperationInstruction(resultReg, currentLeftReg, currentRightReg, llvmOp,
                    resultBinOpType, resultBinOpType));
        }
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
        int numElements = elems.size();

        // 1. Determine the Slish Element Type from TypeChecker
        // typeChecker.getTypeOf(arrayLiteral) should return the Slish array type (e.g., Type for int[], float32[], etc.)
        Type slishArrayOverallType = typeChecker.getTypeOf(arrayLiteral);
        Type slishElementType; // This will be the Slish type of the elements (e.g., Type.INTEGER, Type.FLOAT32)

        if (slishArrayOverallType != null && slishArrayOverallType.isArray()) {
            slishElementType = slishArrayOverallType.getElementType();
        } else {
            // Fallback if TypeChecker didn't provide a proper array type. This indicates an issue.
            instructions.add(new CommentInstruction(
                "Error: ArrayLiteral node did not resolve to an array type via TypeChecker. Found: "
                    +
                    (slishArrayOverallType != null ? slishArrayOverallType.getTypeName() : "null")
                    + ". Defaulting element type to DYNAMIC."
            ));
            slishElementType = Type.DYNAMIC;
        }

        // If the determined element type is DYNAMIC, try to infer a more concrete type
        // from the first element, especially for LLVM code generation.
        if (slishElementType == Type.DYNAMIC && numElements > 0) {
            Expression firstElementExpr = elems.get(0);
            Type firstElementActualType = typeChecker.getTypeOf(firstElementExpr);
            if (firstElementActualType != Type.DYNAMIC) {
                slishElementType = firstElementActualType; // Use the type of the first element if it's concrete
                instructions.add(new CommentInstruction(
                    "Info: ArrayLiteral's element type was DYNAMIC. Refined to " +
                        slishElementType.getTypeName()
                        + " based on the first element for LLVM code gen."
                ));
            } else if (firstElementExpr instanceof Literal) {
                // If first element is a literal and still typed as DYNAMIC by checker, use its AST literal type.
                slishElementType = determineTypeFromLiteral((Literal) firstElementExpr);
                if (slishElementType != Type.DYNAMIC) {
                    instructions.add(new CommentInstruction(
                        "Info: ArrayLiteral's element type was DYNAMIC. Refined to " +
                            slishElementType.getTypeName()
                            + " based on the first AST Literal type for LLVM code gen."
                    ));
                }
            }
        }
        // If still DYNAMIC after fallbacks, it will map to i8* or similar generic pointer.
        if (slishElementType == Type.DYNAMIC) {
            instructions.add(new CommentInstruction(
                "Warning: Element type for array literal remains DYNAMIC. LLVM type will be generic (e.g., i8*)."));
        }

        // 2. Get the LLVM type string for a single element
        String llvmElementTyStr = mapToLLVMType(
            slishElementType); // e.g., "i32", "float", "double", "i8*"

        // 3. Build the LLVM array type string: "[N x T]"
        String llvmArrayTyStr = "[" + numElements + " x " + llvmElementTyStr + "]";

        // 4. Allocate memory for the array on the stack
        String arrayAllocReg = generateRegister(); // e.g., %r0
        int alignment = 16; // Default alignment. Can be refined based on llvmElementTyStr.
        // Example refinement for alignment (LLVM often handles this well if unspecified for `alloca`):
        if (llvmElementTyStr.equals("double") || llvmElementTyStr.endsWith("**")) {
            alignment = 8; // double or array of pointers
        } else if (llvmElementTyStr.equals("float") || llvmElementTyStr.equals("i32") || (
            llvmElementTyStr.endsWith("*") && !llvmElementTyStr.endsWith("**"))) {
            alignment = 4; // float, i32, or single pointer
        } else if (llvmElementTyStr.equals("i1") || llvmElementTyStr.equals("i8")) {
            alignment = 1;
        }

        instructions.add(new RawInstruction(
            "%" + arrayAllocReg + " = alloca " + llvmArrayTyStr + ", align " + alignment
        ));

        // 5. Populate the array: Evaluate each element, cast if necessary, and store it
        for (int i = 0; i < numElements; i++) {
            Expression currentElementExpr = elems.get(i);
            String elementValueReg = currentElementExpr.accept(
                this); // Register holding the element's value
            Type actualElementSlishType = typeChecker.getTypeOf(
                currentElementExpr); // Slish type of the current element

            String regToStore = elementValueReg;

            // If the actual Slish type of the element differs from the array's target element type,
            // and a cast is necessary and possible.
            if (!actualElementSlishType.equals(slishElementType) &&
                actualElementSlishType != Type.DYNAMIC && slishElementType != Type.DYNAMIC) {

                boolean requiresExplicitCast = false;
                // Common numeric promotions/conversions for storage:
                if (slishElementType == Type.FLOAT64 && (actualElementSlishType == Type.FLOAT32
                    || actualElementSlishType == Type.INTEGER)) {
                    requiresExplicitCast = true; // fpext float to double, sitofp i32 to double
                } else if (slishElementType == Type.FLOAT32
                    && actualElementSlishType == Type.INTEGER) {
                    requiresExplicitCast = true; // sitofp i32 to float
                } else if (slishElementType == Type.FLOAT32
                    && actualElementSlishType == Type.FLOAT64) {
                    requiresExplicitCast = true; // fptrunc double to float
                } else if (slishElementType == Type.INTEGER && (
                    actualElementSlishType == Type.FLOAT32
                        || actualElementSlishType == Type.FLOAT64)) {
                    requiresExplicitCast = true; // fptosi float/double to i32
                }

                if (requiresExplicitCast) {
                    String castedElementReg = generateRegister();
                    instructions.add(
                        new TypeCastInstruction(castedElementReg, elementValueReg, slishElementType,
                            actualElementSlishType));
                    regToStore = castedElementReg;
                    instructions.add(new CommentInstruction(
                        "Casted array element from " + actualElementSlishType.getTypeName() + " to "
                            + slishElementType.getTypeName() + " for storage."));
                } else {
                    // If types are different but no common explicit cast rule matched above,
                    // it implies either TypeChecker allowed an unsafe/unhandled implicit conversion or there's an issue.
                    instructions.add(new CommentInstruction(
                        "Warning: Storing element of Slish type "
                            + actualElementSlishType.getTypeName() +
                            " into array of Slish type " + slishElementType.getTypeName() +
                            " without an explicit LLVM cast. TypeChecker should ensure compatibility."
                    ));
                }
            } else if (actualElementSlishType == Type.DYNAMIC && slishElementType != Type.DYNAMIC) {
                // Storing a DYNAMIC typed value into a statically typed array.
                // This is inherently risky at the LLVM level without runtime type info.
                // We assume the DYNAMIC value is assignment-compatible. No LLVM cast is generated here.
                instructions.add(new CommentInstruction(
                    "Warning: Storing DYNAMIC Slish type element into statically typed array of " +
                        slishElementType.getTypeName() + ". Assuming runtime compatibility."
                ));
            }

            // Get a pointer to the i-th element of the array
            String gepReg = generateRegister();
            instructions.add(new RawInstruction(
                "%" + gepReg + " = getelementptr inbounds " + llvmArrayTyStr + ", " +
                    llvmArrayTyStr + "* %" + arrayAllocReg + ", i32 0, i32 " + i
            ));

            // Store the element's value (possibly after casting) into the array slot
            instructions.add(new RawInstruction(
                "store " + llvmElementTyStr + " %" + regToStore + ", " +
                    llvmElementTyStr + "* %" + gepReg
            ));
        }

        // 6. Bitcast the array pointer [N x T]* to T*
        // This provides a pointer to the first element, a common way to represent arrays.
        String basePtrReg = generateRegister();
        instructions.add(new RawInstruction(
            "%" + basePtrReg + " = bitcast " + llvmArrayTyStr + "* %" + arrayAllocReg + " to "
                + llvmElementTyStr + "*"
        ));

        return basePtrReg; // This register holds a T* (e.g., i32*, float*, i8**)
    }

    private String mapToLLVMType(Type type) {
        if (type == null) {
            instructions.add(new CommentInstruction(
                "Warning: mapToLLVMType received null type, defaulting to i8* (generic pointer)"));
            return "i8*";
        }
        if (type == Type.INTEGER) {
            return "i32";
        }
        if (type == Type.FLOAT32) {
            return "float";    // LLVM 'float' is 32-bit
        }
        if (type == Type.FLOAT64) {
            return "double";   // LLVM 'double' is 64-bit
        }
        if (type == Type.BOOLEAN) {
            return "i1";
        }
        if (type == Type.STRING) {
            return "i8*";     // Slish string is char*
        }
        if (type == Type.VOID) {
            return "void";
        }
        if (type.isArray()) {
            // This is the type of a variable holding an array, so it's a pointer to the element type.
            return mapToLLVMType(type.getElementType()) + "*";
        }
        if (type == Type.DYNAMIC) {
            instructions.add(new CommentInstruction(
                "Info: mapToLLVMType mapping Slish DYNAMIC type to i8* (generic pointer)"));
            return "i8*";
        }
        // Fallback for other complex types (e.g., custom types, function pointers)
        // This might need expansion based on language features.
        instructions.add(
            new CommentInstruction("Warning: mapToLLVMType encountered unhandled Slish type: " +
                type.getTypeName() + ". Defaulting to i8* (generic pointer)."));
        return "i8*";
    }

    private Type determineTypeFromLiteral(Literal literal) {
        switch (literal.getType()) {
            case INTEGER:
                return Type.INTEGER;
            case FLOAT32:
                return Type.FLOAT32;
            case FLOAT64:
                return Type.FLOAT64;
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

            if (type == Type.FLOAT32) {
                // Promote float to double for printf, as per C variadic function rules
                String promotedReg = generateRegister();
                instructions.add(new TypeCastInstruction(promotedReg, valueReg, Type.FLOAT64,
                    Type.FLOAT32)); // fpext
                valueReg = promotedReg; // <--- Update valueReg here to promoted register
                type = Type.FLOAT64; // Treat as double for printf formatting
            }

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
            instructions.add(
                new CommentInstruction("Error: array \"" + arrayName + "\" not found"));
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
            "%" + elemPtrReg + " = getelementptr inbounds i32, i32* %" + basePtrReg + ", i32 %"
                + indexReg
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