package pl.edu.pw.slish.codegen.instructions;

/**
 * Instrukcja pobrania rozmiaru kolekcji.
 */
public class CollectionSizeInstruction implements Instruction {

    private final String resultRegister;
    private final String collectionRegister;

    public CollectionSizeInstruction(String resultRegister, String collectionRegister) {
        this.resultRegister = resultRegister;
        this.collectionRegister = collectionRegister;
    }

    @Override
    public String generateCode() {
        return resultRegister + " = size " + collectionRegister;
    }
}
