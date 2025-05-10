package pl.edu.pw.slish.codegen;

/**
 * Reprezentuje typ danych w języku Slish.
 */
public class Type {
    // Stałe reprezentujące podstawowe typy
    public static final Type INTEGER = new Type("int");
    public static final Type FLOAT = new Type("float");
    public static final Type STRING = new Type("string");
    public static final Type BOOLEAN = new Type("bool");
    public static final Type VOID = new Type("void");
    public static final Type DYNAMIC = new Type("_");
    public static final Type FUNCTION = new Type("fun");
    
    private final String typeName;
    private final Type elementType;  // Typ elementów tablicy, jeśli jest to tablica
    private final boolean isArray;  // Czy jest to typ tablicowy
    
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
    
    /**
     * Tworzy nowy typ tablicowy na podstawie typu elementu
     * 
     * @param baseType Typ elementów tablicy
     * @return Nowy typ tablicowy
     */
    public static Type createArrayType(Type baseType) {
        return new Type("array", baseType);
    }
    
    /**
     * Sprawdza czy typ jest tablicowy
     */
    public boolean isArray() {
        return isArray;
    }
    
    /**
     * Zwraca typ elementów tablicy
     */
    public Type getElementType() {
        return elementType;
    }
    
    /**
     * Konwertuje nazwę typu na obiekt Type.
     */
    public static Type fromTypeName(String typeName) {
        // Sprawdzamy czy to typ tablicowy
        if (typeName.endsWith("[]")) {
            String baseTypeName = typeName.substring(0, typeName.length() - 2);
            Type baseType = fromTypeName(baseTypeName);
            return createArrayType(baseType);
        }
        
        // Standardowe typy
        if (typeName.equals(INTEGER.typeName)) return INTEGER;
        if (typeName.equals(FLOAT.typeName)) return FLOAT;
        if (typeName.equals(STRING.typeName)) return STRING;
        if (typeName.equals(BOOLEAN.typeName)) return BOOLEAN;
        if (typeName.equals(VOID.typeName)) return VOID;
        if (typeName.equals(DYNAMIC.typeName)) return DYNAMIC;
        if (typeName.equals(FUNCTION.typeName)) return FUNCTION;
        
        // Obsługa funkcji (zwracamy FUNCTION dla nazw funkcji)
        if (typeName.equals("print") || 
            typeName.equals("add") || 
            typeName.equals("read") || 
            typeName.equals("lt") || 
            typeName.equals("gt") || 
            typeName.equals("eq")) {
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
        
        // Porównanie typów podstawowych
        if (!isArray && !otherType.isArray) {
            return typeName.equals(otherType.typeName);
        }
        
        // Porównanie typów tablicowych
        if (isArray && otherType.isArray) {
            if (elementType == null && otherType.elementType == null) return true;
            if (elementType == null || otherType.elementType == null) return false;
            return elementType.equals(otherType.elementType);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = typeName.hashCode();
        if (isArray && elementType != null) {
            result = 31 * result + elementType.hashCode();
        }
        return result;
    }
    
    /**
     * Sprawdza czy ten typ może być rzutowany na docelowy typ.
     * 
     * @param targetType Typ docelowy
     * @return true jeśli można rzutować, false w przeciwnym przypadku
     */
    public boolean canCastTo(Type targetType) {
        // Typ dynamiczny może być rzutowany na dowolny inny typ
        if (this == DYNAMIC || targetType == DYNAMIC) {
            return true;
        }
        
        // Każdy typ może być rzutowany na samego siebie
        if (this.equals(targetType)) {
            return true;
        }
        
        // Typ funkcji może być rzutowany na typ zwracany przez funkcję
        if (this == FUNCTION && targetType != VOID && targetType != FUNCTION) {
            return true;
        }
        
        // Typy numeryczne mogą być między sobą konwertowane
        if ((this == INTEGER || this == FLOAT) && (targetType == INTEGER || targetType == FLOAT)) {
            return true;
        }
        
        // Dla tablicy, sprawdzamy czy typy elementów są kompatybilne
        if (isArray && targetType.isArray) {
            return elementType.canCastTo(targetType.elementType);
        }
        
        return false;
    }
} 