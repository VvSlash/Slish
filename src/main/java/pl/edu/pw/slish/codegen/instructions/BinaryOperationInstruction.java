package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja operacji binarnej w kodzie LLVM IR.
 */
public class BinaryOperationInstruction implements Instruction {

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
        EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL,
        XOR
    }

    private final String resultRegister;
    private final String leftRegister;
    private final String rightRegister;
    private final Operation operation;
    private final Type resultType;

    public BinaryOperationInstruction(String resultRegister, String leftRegister,
        String rightRegister, Operation operation, Type resultType) {
        this.resultRegister = resultRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
        this.operation = operation;
        this.resultType = resultType;
    }

    @Override
    public String generateCode() {
        String op;
        boolean isComparison = false;

        switch (operation) {
            case ADD:
                op = "add";
                break;
            case SUBTRACT:
                op = "sub";
                break;
            case MULTIPLY:
                op = "mul";
                break;
            case DIVIDE:
                op = "sdiv";
                break;
            case MODULO:
                op = "srem";
                break;
            case XOR:
                op = "xor";
                break;
            case EQUAL:
                op = "icmp eq";
                isComparison = true;
                break;
            case NOT_EQUAL:
                op = "icmp ne";
                isComparison = true;
                break;
            case GREATER_THAN:
                op = "icmp sgt";
                isComparison = true;
                break;
            case LESS_THAN:
                op = "icmp slt";
                isComparison = true;
                break;
            case GREATER_EQUAL:
                op = "icmp sge";
                isComparison = true;
                break;
            case LESS_EQUAL:
                op = "icmp sle";
                isComparison = true;
                break;
            default:
                throw new IllegalArgumentException("Nieznana operacja: " + operation);
        }

        String operandType = isComparison
            ? "i32" // assuming all comparisons are on i32 (adjust if needed)
            : mapToLLVMType(resultType);

        return "%" + resultRegister
            + " = " + op
            + " " + operandType
            + " %" + leftRegister
            + ", %" + rightRegister;
    }

    private String mapToLLVMType(Type type) {
        if (type == Type.INTEGER) {
            return "i32";
        }
        if (type == Type.FLOAT) {
            return "double";
        }
        if (type == Type.BOOLEAN) {
            return "i1";
        }
        if (type == Type.STRING) {
            return "i8*";
        }
        if (type == Type.DYNAMIC) {
            return "i8*";
        }
        if (type.isArray()) {
            return mapToLLVMType(type.getElementType()) + "*";
        }
        throw new IllegalArgumentException(
            "Unsupported type in BinaryOperationInstruction: " + type);
    }

} 