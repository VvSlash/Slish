package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja wczytania wartości stałej w kodzie LLVM IR.
 */
public class LoadConstantInstruction implements Instruction {
    private final String register;
    private final Object value;
    private final Type type;
    
    public LoadConstantInstruction(String register, Object value, Type type) {
        this.register = register;
        this.value = value;
        this.type = type;
    }
    
    @Override
    public String generateCode() {
        if (type == Type.STRING) {
            String stringValue = (String) value;
            stringValue = stringValue.replace("\"", ""); // Usuwamy cudzysłowy
            return "%" + register + " = global [" + (stringValue.length() + 1) + " x i8] c\"" + 
                   stringValue + "\\00\"";
        } else if (type == Type.INTEGER) {
            return "%" + register + " = add i32 " + value + ", 0";
        } else if (type == Type.FLOAT) {
            return "%" + register + " = fadd float " + value + ", 0.0";
        } else if (type == Type.BOOLEAN) {
            int boolVal = Boolean.TRUE.equals(value) ? 1 : 0;
            return "%" + register + " = add i1 " + boolVal + ", 0";
        } else {
            // Domyślnie przyjmujemy, że to integer
            return "%" + register + " = add i32 " + (value != null ? value : 0) + ", 0";
        }
    }
} 