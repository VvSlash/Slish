package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja rzutowania typu.
 */
public class TypeCastInstruction implements Instruction {
     private String resultReg;
     private String sourceReg;
     private Type targetLlvmType;
     private Type sourceLlvmType;

     public TypeCastInstruction(String resultReg, String sourceReg, Type targetLlvmType, Type sourceLlvmType) {
         this.resultReg = resultReg;
         this.sourceReg = sourceReg;
         this.targetLlvmType = targetLlvmType;
         this.sourceLlvmType = sourceLlvmType;
     }

     @Override
     public String generateCode() {
         String op = "";
         String sourceLlvm = mapToLLVMType(sourceLlvmType);
         String targetLlvm = mapToLLVMType(targetLlvmType);

         if (sourceLlvmType == Type.FLOAT32 && targetLlvmType == Type.FLOAT64) op = "fpext";
         else if (sourceLlvmType == Type.FLOAT64 && targetLlvmType == Type.FLOAT32) op = "fptrunc";
         else if (sourceLlvmType == Type.INTEGER && (targetLlvmType == Type.FLOAT32 || targetLlvmType == Type.FLOAT64)) op = "sitofp";
         else if ((sourceLlvmType == Type.FLOAT32 || sourceLlvmType == Type.FLOAT64) && targetLlvmType == Type.INTEGER) op = "fptosi";
         // Add bitcast etc. if needed for other types of casts
         else { return "; TODO: Unsupported cast from " + sourceLlvmType + " to " + targetLlvmType; }

         return "%" + resultReg + " = " + op + " " + sourceLlvm + " %" + sourceReg + " to " + targetLlvm;
     }
    private String mapToLLVMType(Type type) {
        if (type == null) return "i32";
        if (type == Type.INTEGER) return "i32";
        if (type == Type.FLOAT32) return "float"; // LLVM 'float' is 32-bit
        if (type == Type.FLOAT64) return "double"; // LLVM 'double' is 64-bit
        if (type == Type.BOOLEAN) return "i1";
        if (type == Type.STRING) return "i8*"; // Pointer to char
        if (type == Type.VOID) return "void";
        if (type.isArray()) return mapToLLVMType(type.getElementType()) + "*";
        if (type == Type.DYNAMIC) return "i8*"; // Or handle appropriately
        throw new IllegalArgumentException("Unsupported type for LLVM mapping: " + type.getTypeName());
    }
 }