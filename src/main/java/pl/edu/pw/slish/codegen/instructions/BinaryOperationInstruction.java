package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

public class BinaryOperationInstruction implements Instruction {

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
        EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL,
        XOR, // For integers/booleans
        // AND, OR are typically handled by branching logic (short-circuit)
        // or bitwise ops for integers. If you have direct AND/OR instructions:
        // AND, OR
    }

    private final String resultRegister;
    private final String leftRegister;
    private final String rightRegister;
    private final Operation operation;
    private final Type operandTypeForInstruction; // Type of operands as they are passed to LLVM op (after any promotion for the op itself)
    private final Type actualResultType; // The type that will be stored in resultRegister

    // Modified constructor
    public BinaryOperationInstruction(String resultRegister, String leftRegister,
        String rightRegister, Operation operation,
        Type operandTypeForInstruction, Type actualResultType) {
        this.resultRegister = resultRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
        this.operation = operation;
        this.operandTypeForInstruction = operandTypeForInstruction;
        this.actualResultType = actualResultType;
    }


    @Override
    public String generateCode() {
        String llvmOperandTypeStr = mapToLLVMType(operandTypeForInstruction);
        // actualResultType is used to determine the nature of operation (e.g. float vs int)
        // but llvmOperandTypeStr is what's written in the instruction for the operand types.

        boolean isFloatOp = operandTypeForInstruction == Type.FLOAT32 || operandTypeForInstruction == Type.FLOAT64;
        boolean isComparison = isComparisonOp(operation);
        String llvmOpPrefix = "";

        if (isComparison) {
            llvmOpPrefix = (operandTypeForInstruction == Type.INTEGER || operandTypeForInstruction == Type.BOOLEAN) ? "icmp" : "fcmp";
        } else { // Arithmetic
            if (operandTypeForInstruction == Type.FLOAT32 || operandTypeForInstruction == Type.FLOAT64) llvmOpPrefix = "f";
        }


        String llvmOpCode;
        switch (operation) {
            case ADD: llvmOpCode = isFloatOp ? "fadd" : "add"; break;
            case SUBTRACT: llvmOpCode = isFloatOp ? "fsub" : "sub"; break;
            case MULTIPLY: llvmOpCode = isFloatOp ? "fmul" : "mul"; break;
            case DIVIDE: llvmOpCode = isFloatOp ? "fdiv" : "sdiv"; break; // udiv for unsigned
            case MODULO: llvmOpCode = "srem"; break; // urem for unsigned. No fmod instruction directly, usually a libcall.
            case XOR: llvmOpCode = "xor"; break; // Typically for integers/booleans

            // Comparisons
            case EQUAL: llvmOpCode = isFloatOp ? "fcmp oeq" : "icmp eq"; break;
            case NOT_EQUAL: llvmOpCode = isFloatOp ? "fcmp one" : "icmp ne"; break;
            case GREATER_THAN: llvmOpCode = isFloatOp ? "fcmp ogt" : "icmp sgt"; break;
            case LESS_THAN: llvmOpCode = isFloatOp ? "fcmp olt" : "icmp slt"; break;
            case GREATER_EQUAL: llvmOpCode = isFloatOp ? "fcmp oge" : "icmp sge"; break;
            case LESS_EQUAL: llvmOpCode = isFloatOp ? "fcmp ole" : "icmp sle"; break;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }

        return "%" + resultRegister + " = " + llvmOpCode + " " + llvmOperandTypeStr +
            " %" + leftRegister + ", %" + rightRegister;
    }

    private String mapToLLVMType(Type type) {
        if (type == null) return "i32"; // Fallback, should not happen
        if (type == Type.INTEGER) return "i32";
        if (type == Type.FLOAT32) return "float"; // LLVM 'float' is 32-bit
        if (type == Type.FLOAT64) return "double"; // LLVM 'double' is 64-bit
        if (type == Type.BOOLEAN) return "i1";
        if (type == Type.STRING) return "i8*"; // Pointer to char
        if (type == Type.VOID) return "void";
        if (type.isArray()) return mapToLLVMType(type.getElementType()) + "*";
        if (type == Type.DYNAMIC) return "i8*"; // Or handle appropriately
        throw new IllegalArgumentException("Unsupported type for LLVM mapping: " + type.getTypeName());
    }

    private boolean isComparisonOp(Operation op) {
        return switch (op) {
            case EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL -> true;
            default -> false;
        };
    }
}