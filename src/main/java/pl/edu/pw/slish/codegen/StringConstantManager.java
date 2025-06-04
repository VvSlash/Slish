package pl.edu.pw.slish.codegen;

import java.util.HashMap;
import java.util.Map;

import static pl.edu.pw.slish.codegen.Type.FLOAT32; // NEW
import static pl.edu.pw.slish.codegen.Type.FLOAT64; // NEW

/**
 * Manages string constants to avoid duplication in generated LLVM IR. Ensures each unique string
 * literal gets exactly one global declaration.
 */
public class StringConstantManager {

    private final Map<String, String> stringToGlobalName = new HashMap<>();
    private int stringCounter = 0;

    /**
     * Gets or creates a global name for the given string constant.
     *
     * @param value The string literal value
     * @return The global symbol name for this string
     */
    public String getOrCreateStringConstant(String value) {
        return stringToGlobalName.computeIfAbsent(value, v ->
            ".str" + (stringCounter++)
        );
    }

    /**
     * Generates all global string declarations. Call this once at the beginning of your LLVM
     * module.
     */
    public String generateAllGlobalDeclarations() {
        StringBuilder sb = new StringBuilder();
        sb.append("; Global string constants\n");

        for (Map.Entry<String, String> entry : stringToGlobalName.entrySet()) {
            String value = entry.getKey();
            String globalName = entry.getValue();

            String escapedStr = value
                .replace("\\", "\\5C")
                .replace("\n", "\\0A")
                .replace("\"", "\\22");

            int length = escapedStr.length() + 1; // +1 for null terminator

            sb.append("@").append(globalName)
                .append(" = private unnamed_addr constant [")
                .append(length).append(" x i8] c\"")
                .append(escapedStr).append("\\00\", align 1\n");
        }

        return sb.toString();
    }

    /**
     * Generates a getelementptr instruction to get pointer to string.
     */
    public String generateStringPointerInstruction(String register, String value) {
        String globalName = getOrCreateStringConstant(value);
        int length = value.length() + 1;

        return "%" + register + " = getelementptr inbounds [" + length +
            " x i8], [" + length + " x i8]* @" + globalName + ", i32 0, i32 0";
    }

    public String getFormatPointer(Type type) {
        if (type == Type.INTEGER) {
            return "getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0";
        }
        if (type == FLOAT32) { // Handle FLOAT32
            return "getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float32, i32 0, i32 0";
        }
        if (type == FLOAT64) { // Handle FLOAT64
            return "getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0";
        }
        if (type == Type.STRING) {
            return "getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_str, i32 0, i32 0";
        }
        if (type == Type.BOOLEAN) {
            return "getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_bool, i32 0, i32 0";
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    public String generateBuiltinFormatStrings() {
        return """
            @.str_fmt_int = private unnamed_addr constant [4 x i8] c"%d\\0a\\00", align 1
            @.str_fmt_float32 = private unnamed_addr constant [4 x i8] c"%f\\0a\\00", align 1
            @.str_fmt_float64 = private unnamed_addr constant [5 x i8] c"%lf\\0a\\00", align 1
            @.str_fmt_str = private unnamed_addr constant [4 x i8] c"%s\\0a\\00", align 1
            @.str_fmt_bool = private unnamed_addr constant [4 x i8] c"%d\\0a\\00", align 1
            """;
    }
}