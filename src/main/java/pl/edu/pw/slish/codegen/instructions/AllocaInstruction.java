package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja alokacji zmiennej na stosie.
 */
public class AllocaInstruction implements Instruction {
    private final String register;
    private final Type type;
    
    public AllocaInstruction(String register, Type type) {
        this.register = register;
        this.type = type;
    }
    
    @Override
    public String generateCode() {
        return "%" + register + " = alloca " + type.toString();
    }
} 