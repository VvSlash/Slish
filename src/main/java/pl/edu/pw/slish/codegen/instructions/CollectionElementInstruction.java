package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja pobrania elementu z kolekcji.
 */
public class CollectionElementInstruction implements Instruction {

    private final String resultRegister;
    private final String collectionRegister;
    private final String indexRegister;
    private final Type type;

    public CollectionElementInstruction(String resultRegister, String collectionRegister,
        String indexRegister, Type type) {
        this.resultRegister = resultRegister;
        this.collectionRegister = collectionRegister;
        this.indexRegister = indexRegister;
        this.type = type;
    }

    @Override
    public String generateCode() {
        return resultRegister + " = " + collectionRegister + "[" + indexRegister + "] "
            + type.getTypeName();
    }
}
