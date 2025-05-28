package pl.edu.pw.slish.codegen.instructions;

import java.util.Objects;
import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja operacji unarnej (negacja logiczna) w kodzie LLVM IR.
 */
public class UnaryOperationInstruction implements Instruction {
    public enum Operation { NOT }

    private final String resultRegister;
    private final String operandRegister;
    private final Operation operation;
    private final Type resultType;

    public UnaryOperationInstruction(String resultRegister,
        String operandRegister,
        Operation operation,
        Type resultType) {
        this.resultRegister = resultRegister;
        this.operandRegister = operandRegister;
        this.operation = operation;
        this.resultType = resultType;
    }

    @Override
    public String generateCode() {
        String op;
        // Dla negacji używamy xor z wartością true (1)
        if (Objects.requireNonNull(operation) == Operation.NOT) {
            op = "xor i1";
        } else {
            throw new IllegalArgumentException("Nieznana operacja: " + operation);
        }
        // i1 %operand, true
        return "%" + resultRegister + " = " + op + " %" + operandRegister + ", true";
    }
}