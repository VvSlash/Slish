package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Instrukcja wywołania funkcji w kodzie LLVM IR.
 */
public class CallInstruction implements Instruction {
    private final String resultRegister;
    private final String functionName;
    private final List<String> argumentRegisters;
    private final Type returnType;
    
    public CallInstruction(String resultRegister, String functionName, 
                          List<String> argumentRegisters, Type returnType) {
        this.resultRegister = resultRegister;
        this.functionName = functionName;
        this.argumentRegisters = argumentRegisters;
        this.returnType = returnType;
    }
    
    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        
        if (returnType != Type.VOID) {
            sb.append("%").append(resultRegister).append(" = ");
        }
        
        sb.append("call ").append(returnType.toString()).append(" @").append(functionName).append("(");
        
        if (!argumentRegisters.isEmpty()) {
            String args = argumentRegisters.stream()
                .map(arg -> "i32 %" + arg)
                .collect(Collectors.joining(", "));
            sb.append(args);
        }
        
        sb.append(")");
        
        return sb.toString();
    }
} 