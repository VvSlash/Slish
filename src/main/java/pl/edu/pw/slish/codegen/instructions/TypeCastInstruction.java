package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja rzutowania typu.
 */
public class TypeCastInstruction implements Instruction {

    private final String resultRegister;
    private final String sourceRegister;
    private final Type targetType;
    private final Type sourceType; // Może być przydatne dla bardziej zaawansowanego rzutowania

    public TypeCastInstruction(String resultRegister, String sourceRegister, Type targetType,
        Type sourceType) {
        this.resultRegister = resultRegister;
        this.sourceRegister = sourceRegister;
        this.targetType = targetType;
        this.sourceType = sourceType; // Na razie przekazujemy DYNAMIC jako sourceType
    }

    @Override
    public String generateCode() {
        return String.format("%s = cast %s %s to %s",
            resultRegister,
            sourceType.getTypeName(),
            sourceRegister,
            targetType.getTypeName());
    }
}
