package pl.edu.pw.slish.codegen;

public class Type {
    public static final Type INTEGER = new Type("int");
    public static final Type FLOAT32 = new Type("float32"); // NEW
    public static final Type FLOAT64 = new Type("float64"); // NEW
    // public static final Type FLOAT = new Type("float"); // OLD - Remove or comment out
    public static final Type STRING = new Type("string");
    public static final Type BOOLEAN = new Type("bool");
    public static final Type VOID = new Type("void");
    public static final Type DYNAMIC = new Type("_");
    public static final Type FUNCTION = new Type("fun");

    private final String typeName;
    private final Type elementType;
    private final boolean isArray;

    private Type(String typeName) {
        this.typeName = typeName;
        this.elementType = null;
        this.isArray = false;
    }

    private Type(String typeName, Type elementType) {
        this.typeName = typeName;
        this.elementType = elementType;
        this.isArray = true;
    }

    public String getTypeName() {
        return typeName;
    }

    public static Type createArrayType(Type baseType) {
        return new Type("array", baseType);
    }

    public boolean isArray() {
        return isArray;
    }

    public Type getElementType() {
        return elementType;
    }

    public static Type fromTypeName(String typeName) {
        if (typeName.endsWith("[]")) {
            String baseTypeName = typeName.substring(0, typeName.length() - 2);
            Type baseType = fromTypeName(baseTypeName);
            return createArrayType(baseType);
        }

        if (typeName.equals(INTEGER.typeName)) return INTEGER;
        if (typeName.equals(FLOAT32.typeName)) return FLOAT32; // NEW
        if (typeName.equals(FLOAT64.typeName)) return FLOAT64; // NEW
        // if (typeName.equals(FLOAT.typeName)) return FLOAT; // OLD
        if (typeName.equals(STRING.typeName)) return STRING;
        if (typeName.equals(BOOLEAN.typeName)) return BOOLEAN;
        if (typeName.equals(VOID.typeName)) return VOID;
        if (typeName.equals(DYNAMIC.typeName)) return DYNAMIC;
        if (typeName.equals(FUNCTION.typeName)) return FUNCTION;

        if (typeName.equals("print") || typeName.equals("add") || typeName.equals("read") ||
            typeName.equals("lt") || typeName.equals("gt") || typeName.equals("eq")) {
            return FUNCTION;
        }

        throw new IllegalArgumentException("Nieznany typ: " + typeName);
    }

    @Override
    public String toString() {
        if (isArray && elementType != null) {
            return elementType.toString() + "[]";
        }
        return typeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Type otherType = (Type) obj;
        if (!typeName.equals(otherType.typeName)) return false; // Quick check for base name
        if (isArray != otherType.isArray) return false;
        if (isArray) {
            return elementType != null ? elementType.equals(otherType.elementType) : otherType.elementType == null;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = typeName.hashCode();
        result = 31 * result + (isArray ? 1 : 0);
        if (isArray && elementType != null) {
            result = 31 * result + elementType.hashCode();
        }
        return result;
    }

    public boolean canCastTo(Type targetType) {
        if (this == DYNAMIC || targetType == DYNAMIC) {
            return true;
        }
        if (this.equals(targetType)) {
            return true;
        }

        // Casting between numeric types
        boolean isThisNumeric = (this == INTEGER || this == FLOAT32 || this == FLOAT64);
        boolean isTargetNumeric = (targetType == INTEGER || targetType == FLOAT32 || targetType == FLOAT64);

        if (isThisNumeric && isTargetNumeric) {
            // Allow int to float32/float64
            if (this == INTEGER && (targetType == FLOAT32 || targetType == FLOAT64)) return true;
            // Allow float32 to float64
            if (this == FLOAT32 && targetType == FLOAT64) return true;
            // Allow float64 to float32 (explicit cast, potential precision loss)
            if (this == FLOAT64 && targetType == FLOAT32) return true;
            // Allow float32/float64 to int (explicit cast, potential truncation)
            if ((this == FLOAT32 || this == FLOAT64) && targetType == INTEGER) return true;
            return false; // Default deny other numeric conversions unless explicitly listed
        }

        if (this == FUNCTION && targetType != VOID && targetType != FUNCTION) {
            return true;
        }

        if (isArray && targetType.isArray) {
            return elementType != null && elementType.canCastTo(targetType.elementType);
        }

        return false;
    }
}