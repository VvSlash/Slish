package pl.edu.pw.slish.codegen.instructions;

import static pl.edu.pw.slish.codegen.Type.BOOLEAN;
import static pl.edu.pw.slish.codegen.Type.FLOAT32;
import static pl.edu.pw.slish.codegen.Type.FLOAT64;
import static pl.edu.pw.slish.codegen.Type.INTEGER;

import java.util.Locale;
import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja wczytania wartości stałej numerycznej w kodzie LLVM IR. (Stringy obsługuje
 * StringConstantManager.)
 */
public class LoadConstantInstruction implements Instruction {

    private final String register;
    private final Object value;
    private final Type type;

    public LoadConstantInstruction(String register, Object value, Type type) {
        this.register = register;
        this.value = value;
        this.type = type;
    }

    @Override
    public String generateCode() {
        if (type.equals(INTEGER)) {
            return "%" + register + " = add i32 0, " + value;
        } else if (type.equals(FLOAT32)) {
            float fval = ((Number) value).floatValue();
            String valueConstant = floatToExactLLVM(fval);
            return "%" + register + " = fadd float 0.0, " + valueConstant;
        } else if (type.equals(FLOAT64)) {
            double dval = ((Number) value).doubleValue();
            String valueConstant = doubleToExactLLVM(dval);
            return "%" + register + " = fadd double 0.0, " + valueConstant;
        } else if (type.equals(BOOLEAN)) {
            int boolVal = Boolean.TRUE.equals(value) ? 1 : 0;
            return "%" + register + " = add i1 0, " + boolVal;
        }

        throw new IllegalArgumentException(
            "LoadConstantInstruction only supports numeric/boolean constants, got " + type);
    }

    private static String floatToExactLLVM(float f) {
        int bits = Float.floatToIntBits(f);
        float exactFloat = Float.intBitsToFloat(bits);
        return String.format(Locale.US, "%.17g", exactFloat);
    }


    private static String doubleToExactLLVM(double d) {
        long bits = Double.doubleToLongBits(d);
        double exactDouble = Double.longBitsToDouble(bits);
        return String.format(Locale.US, "%.17g", exactDouble);
    }

}