package pl.edu.pw.slish.codegen.instructions;

/**
 * Instrukcja reprezentująca komentarz w kodzie LLVM IR.
 */
public class CommentInstruction implements Instruction {
    private final String comment;
    
    public CommentInstruction(String comment) {
        this.comment = comment;
    }
    
    @Override
    public String generateCode() {
        return "; " + comment;
    }
} 