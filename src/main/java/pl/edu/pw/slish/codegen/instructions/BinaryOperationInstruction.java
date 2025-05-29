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
        String operandType = mapToLLVMType(resultType);
        boolean isFloat = resultType == Type.FLOAT;
        boolean isComparison = isComparisonOp(operation);

        String llvmOp;

        if (isComparison) {
            if (isFloat) {
                // Floating-point comparisons use 'fcmp' + predicate
                llvmOp = switch (operation) {
                    case EQUAL -> "fcmp oeq";
                    case NOT_EQUAL -> "fcmp one";
                    case GREATER_THAN -> "fcmp ogt";
                    case LESS_THAN -> "fcmp olt";
                    case GREATER_EQUAL -> "fcmp oge";
                    case LESS_EQUAL -> "fcmp ole";
                    default -> throw new IllegalArgumentException(
                        "Unsupported float comparison: " + operation);
                };
                operandType = "double"; // explicitly ensure float type
            } else {
                // Integer comparisons use 'icmp' + predicate
                llvmOp = switch (operation) {
                    case EQUAL -> "icmp eq";
                    case NOT_EQUAL -> "icmp ne";
                    case GREATER_THAN -> "icmp sgt";
                    case LESS_THAN -> "icmp slt";
                    case GREATER_EQUAL -> "icmp sge";
                    case LESS_EQUAL -> "icmp sle";
                    default -> throw new IllegalArgumentException(
                        "Unsupported int comparison: " + operation);
                };
                operandType = "i32"; // explicitly ensure int type
            }
        } else if (isFloat) {
            // Floating-point arithmetic ops
            llvmOp = switch (operation) {
                case ADD -> "fadd";
                case SUBTRACT -> "fsub";
                case MULTIPLY -> "fmul";
                case DIVIDE -> "fdiv";
                default -> throw new IllegalArgumentException("Unsupported float op: " + operation);
            };
        } else {
            // Integer arithmetic and logical ops
            llvmOp = switch (operation) {
                case ADD -> "add";
                case SUBTRACT -> "sub";
                case MULTIPLY -> "mul";
                case DIVIDE -> "sdiv";
                case MODULO -> "srem";
                case XOR -> "xor";
                default -> throw new IllegalArgumentException("Unsupported int op: " + operation);
            };
        }

        return "%" + resultRegister
            + " = " + llvmOp
            + " " + operandType
            + " %" + leftRegister
            + ", %" + rightRegister;
    }

    private String mapToLLVMType(Type type) {
        if (type == Type.INTEGER) {
            return "i32";
        } else if (type == Type.FLOAT) {
            return "double";
        } else if (type == Type.BOOLEAN) {
            return "i1";
        } else if (type == Type.STRING) {
            return "i8*";
        } else if (type == Type.DYNAMIC) {
            return "i8*";
        } else if (type.isArray()) {
            return mapToLLVMType(type.getElementType()) + "*";
        }
        throw new IllegalArgumentException(
            "Unsupported type in BinaryOperationInstruction: " + type);
    }

    private boolean isComparisonOp(Operation op) {
        return switch (op) {
            case EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL -> true;
            default -> false;
        };
    }
}
