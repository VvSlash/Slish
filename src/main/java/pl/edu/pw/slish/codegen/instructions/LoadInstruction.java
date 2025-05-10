package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja wczytania wartości ze zmiennej.
 */
public class LoadInstruction implements Instruction {
    private final String resultRegister;
    private final String pointerRegister;
    private final Type type;
    
    public LoadInstruction(String resultRegister, String pointerRegister, Type type) {
        this.resultRegister = resultRegister;
        this.pointerRegister = pointerRegister;
        this.type = type;
    }
    
    @Override
    public String generateCode() {
        return "%" + resultRegister + " = load " + type.toString() + ", " + 
               type.toString() + "* %" + pointerRegister;
    }
} 