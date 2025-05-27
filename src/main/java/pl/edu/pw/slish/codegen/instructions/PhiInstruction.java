package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;
import pl.edu.pw.slish.codegen.util.Pair;
import java.util.List;

public class PhiInstruction implements Instruction {
    private final String resultReg;
    private final Type type;
    private final List<Pair<String, String>> incoming;  // Pair<valueOrLiteral, blockLabel>

    public PhiInstruction(String resultReg, Type type, List<Pair<String, String>> incoming) {
        this.resultReg = resultReg;
        this.type = type;
        this.incoming = incoming;
    }

    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("%").append(resultReg)
            .append(" = phi ").append(mapToLLVMType(type)).append(" ");

        for (int i = 0; i < incoming.size(); i++) {
            Pair<String, String> p = incoming.get(i);
            String val = p.getLeft();
            // if it's a register already (%rX) or a decimal literal (0,1), leave it; otherwise prefix %
            boolean isLiteral = val.matches("\\d+");
            if (!val.startsWith("%") && !isLiteral) {
                val = "%" + val;
            }

            sb.append("[ ").append(val)
                .append(", %").append(p.getRight())
                .append(" ]");
            if (i < incoming.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private String mapToLLVMType(Type type) {
        if (type.equals(Type.INTEGER))   return "i32";
        if (type.equals(Type.FLOAT))     return "double";
        if (type.equals(Type.BOOLEAN))   return "i1";
        if (type.equals(Type.STRING))    return "i8*";
        if (type.equals(Type.DYNAMIC))   return "i8*";
        if (type.isArray())              return mapToLLVMType(type.getElementType()) + "*";
        throw new IllegalArgumentException("Unsupported type in PhiInstruction: " + type);
    }
}
