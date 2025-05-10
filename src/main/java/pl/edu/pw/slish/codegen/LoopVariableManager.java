package pl.edu.pw.slish.codegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Zarządza zmiennymi iteracyjnymi w pętlach.
 * Pozwala śledzić zmienne używane w pętlach i ich rejestry.
 */
public class LoopVariableManager {
    private final Stack<LoopContext> loopStack = new Stack<>();
    
    /**
     * Wchodzi do nowej pętli
     * 
     * @param loopType Typ pętli (for, while)
     * @param loopLabel Etykieta pętli
     */
    public void enterLoop(String loopType, String loopLabel) {
        loopStack.push(new LoopContext(loopType, loopLabel));
    }
    
    /**
     * Wychodzi z bieżącej pętli
     */
    public void exitLoop() {
        if (!loopStack.isEmpty()) {
            loopStack.pop();
        }
    }
    
    /**
     * Sprawdza czy wykonanie jest w kontekście pętli
     * 
     * @return true jeśli jesteśmy wewnątrz pętli
     */
    public boolean isInLoop() {
        return !loopStack.isEmpty();
    }
    
    /**
     * Rejestruje zmienną iteracyjną w bieżącej pętli
     * 
     * @param name Nazwa zmiennej
     * @param register Rejestr zmiennej
     */
    public void registerIterationVariable(String name, String register) {
        if (!loopStack.isEmpty()) {
            loopStack.peek().iterationVariables.put(name, register);
        }
    }
    
    /**
     * Sprawdza czy zmienna jest zmienną iteracyjną w bieżącej pętli
     * 
     * @param name Nazwa zmiennej
     * @return true jeśli zmienna jest zmienną iteracyjną
     */
    public boolean isIterationVariable(String name) {
        return !loopStack.isEmpty() && loopStack.peek().iterationVariables.containsKey(name);
    }
    
    /**
     * Zwraca rejestr zmiennej iteracyjnej
     * 
     * @param name Nazwa zmiennej
     * @return Rejestr zmiennej lub null jeśli zmienna nie jest zmienną iteracyjną
     */
    public String getIterationVariableRegister(String name) {
        if (!loopStack.isEmpty() && loopStack.peek().iterationVariables.containsKey(name)) {
            return loopStack.peek().iterationVariables.get(name);
        }
        return null;
    }
    
    /**
     * Klasa przechowująca kontekst pętli
     */
    private static class LoopContext {
        private final String loopType;
        private final String loopLabel;
        private final Map<String, String> iterationVariables = new HashMap<>();
        
        public LoopContext(String loopType, String loopLabel) {
            this.loopType = loopType;
            this.loopLabel = loopLabel;
        }
    }
} 