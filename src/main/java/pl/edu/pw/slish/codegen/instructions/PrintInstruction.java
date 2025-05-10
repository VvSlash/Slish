package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja wypisania wartości na standardowe wyjście w kodzie LLVM IR.
 */
public class PrintInstruction implements Instruction {
    private final String register;
    private final Type type;
    
    public PrintInstruction(String register, Type type) {
        this.register = register;
        this.type = type;
    }
    
    @Override
    public String generateCode() {
        String printFunction;
        String formatString;
        
        if (type.equals(Type.INTEGER)) {
            printFunction = "printf";
            formatString = "%d\\00";
        } else if (type.equals(Type.FLOAT)) {
            printFunction = "printf";
            formatString = "%f\\00";
        } else if (type.equals(Type.STRING)) {
            printFunction = "puts";
            formatString = null;
        } else if (type.equals(Type.BOOLEAN)) {
            printFunction = "printf";
            formatString = "%s\\00";
        } else {
            printFunction = "printf";
            formatString = "%d\\00";
        }
        
        if (printFunction.equals("puts")) {
            return "call i32 @" + printFunction + "(i8* %" + register + ")";
        } else {
            return "call i32 @" + printFunction + "(i8* getelementptr inbounds ([" + 
                   (formatString.length() - 2) + " x i8], [" + (formatString.length() - 2) + 
                   " x i8]* @." + register + "_format, i32 0, i32 0), " + 
                   type.toString() + " %" + register + ")";
        }
    }
} 