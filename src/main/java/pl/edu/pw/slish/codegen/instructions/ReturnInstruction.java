package pl.edu.pw.slish.codegen.instructions;

/**
 * Instrukcja powrotu z funkcji bez wartości zwracanej.
 */
public class ReturnInstruction implements Instruction {
    
    @Override
    public String generateCode() {
        return "ret void";
    }
} 