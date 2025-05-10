package pl.edu.pw.slish.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Zarządza zakresami zmiennych w programie.
 * Pozwala na deklarowanie zmiennych w różnych zakresach i śledzenie ich typów oraz rejestrów.
 */
public class ScopeManager {
    private final List<Map<String, VariableInfo>> scopes = new ArrayList<>();
    
    public ScopeManager() {
        // Inicjalizacja zakresu globalnego
        enterScope();
    }
    
    /**
     * Tworzy nowy zakres (np. dla bloku kodu)
     */
    public void enterScope() {
        scopes.add(new HashMap<>());
    }
    
    /**
     * Opuszcza bieżący zakres
     */
    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.remove(scopes.size() - 1);
        }
    }
    
    /**
     * Deklaruje nową zmienną w bieżącym zakresie.
     * 
     * @param name Nazwa zmiennej
     * @param type Typ zmiennej
     * @param register Rejestr przypisany do zmiennej
     * @return true jeśli zmienna została zadeklarowana, false jeśli już istnieje w bieżącym zakresie
     */
    public boolean declareVariable(String name, Type type, String register) {
        Map<String, VariableInfo> currentScope = getCurrentScope();
        
        // Sprawdzamy czy zmienna już istnieje w bieżącym zakresie
        if (currentScope.containsKey(name)) {
            return false;
        }
        
        // Dodajemy nową zmienną
        currentScope.put(name, new VariableInfo(type, register));
        return true;
    }
    
    /**
     * Aktualizuje rejestr zmiennej
     * 
     * @param name Nazwa zmiennej
     * @param register Nowy rejestr
     * @return true jeśli zmienna istnieje i została zaktualizowana
     */
    public boolean updateVariableRegister(String name, String register) {
        VariableInfo info = findVariable(name);
        if (info != null) {
            info.register = register;
            return true;
        }
        return false;
    }
    
    /**
     * Sprawdza czy zmienna istnieje w jakimkolwiek zakresie
     * 
     * @param name Nazwa zmiennej
     * @return true jeśli zmienna istnieje
     */
    public boolean variableExists(String name) {
        return findVariable(name) != null;
    }
    
    /**
     * Zwraca typ zmiennej
     * 
     * @param name Nazwa zmiennej
     * @return Typ zmiennej lub null jeśli zmienna nie istnieje
     */
    public Type getVariableType(String name) {
        VariableInfo info = findVariable(name);
        return info != null ? info.type : null;
    }
    
    /**
     * Zwraca rejestr zmiennej
     * 
     * @param name Nazwa zmiennej
     * @return Rejestr zmiennej lub null jeśli zmienna nie istnieje
     */
    public String getVariableRegister(String name) {
        VariableInfo info = findVariable(name);
        return info != null ? info.register : null;
    }
    
    /**
     * Wyszukuje informację o zmiennej we wszystkich zakresach, od najbardziej lokalnego do globalnego
     */
    private VariableInfo findVariable(String name) {
        // Przeszukujemy zakresy od najbardziej lokalnego (ostatniego) do globalnego (pierwszego)
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, VariableInfo> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
    
    /**
     * Zwraca bieżący (najbardziej lokalny) zakres
     */
    private Map<String, VariableInfo> getCurrentScope() {
        return scopes.get(scopes.size() - 1);
    }
    
    /**
     * Klasa przechowująca informacje o zmiennej
     */
    private static class VariableInfo {
        private final Type type;
        private String register;
        
        public VariableInfo(Type type, String register) {
            this.type = type;
            this.register = register;
        }
    }
} 