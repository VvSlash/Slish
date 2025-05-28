package pl.edu.pw.slish;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import pl.edu.pw.slish.ast.AstBuilder;
import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.codegen.CodeGenerator;
import pl.edu.pw.slish.codegen.TypeChecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Główna klasa kompilatora języka Slish
 */
public class SlishCompiler {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Użycie: SlishCompiler <plik_źródłowy> [<plik_wyjściowy>]");
            System.exit(1);
        }
        
        String inputFile = args[0];
        String outputFile = args.length > 1 ? args[1] : inputFile.replaceFirst("\\.slish$", ".ll");
        
        try {
            // Parsowanie kodu źródłowego
            SlishLexer lexer = new SlishLexer(CharStreams.fromFileName(inputFile));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SlishParser parser = new SlishParser(tokens);
            ParseTree tree = parser.program();
            
            // Budowanie AST
            AstBuilder astBuilder = new AstBuilder();
            Node ast = astBuilder.visit(tree);
            
            // Sprawdzanie typów
            TypeChecker typeChecker = new TypeChecker();
            boolean typesValid = typeChecker.typeCheck(ast);
            if (!typesValid) {
                for (String error : typeChecker.getErrors()) {
                    System.err.println(error);
                }
                System.err.println("Błędy typów podczas kompilacji.");
                System.exit(1);
            }
            
            // Generowanie kodu LLVM
            CodeGenerator codeGenerator = new CodeGenerator(typeChecker);
            String llvmCode = codeGenerator.generateCode(ast);
            
            // Zapisywanie kodu LLVM do pliku
            Files.write(Paths.get(outputFile), llvmCode.getBytes());
            
            System.out.println("Kompilacja zakończona sukcesem. Kod LLVM zapisany do: " + outputFile);
            
        } catch (IOException e) {
            System.err.println("Błąd I/O: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Błąd podczas kompilacji: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 