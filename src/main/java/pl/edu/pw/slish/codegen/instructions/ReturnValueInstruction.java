package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja powrotu z funkcji z wartością zwracaną.
 */
public class ReturnValueInstruction implements Instruction {
    private final String valueRegister;
    private final Type type;
    
    public ReturnValueInstruction(String valueRegister, Type type) {
        this.valueRegister = valueRegister;
        this.type = type;
    }
    
    @Override
    public String generateCode() {
        return "ret " + type.toString() + " %" + valueRegister;
    }
} 