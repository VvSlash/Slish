# Roadmap projektu Slish

## Przegląd projektu
Slish to język programowania inspirowany składnią systemów Unix/Linux, gdzie znak "/" (slash) odgrywa kluczową rolę w reprezentacji ścieżek i poleceń. Nazwa bezpośrednio nawiązuje do tego znaku, co ma odzwierciedlenie w filozofii języka - przejrzystości i intuicyjności składni.

### Główne założenia języka
- Brak średników (`;`) jako terminatorów linii
- Wykorzystanie operatora `|` do wyświetlania danych (inspirowane potokami Linux)
- Specjalna składnia dla operacji dzielenia (`div(a, b)` lub `//`) aby uniknąć konfliktu ze znakiem `/`
- Składnia inspirowana drzewiastą strukturą katalogów i potokami systemu Linux

## Etapy rozwoju

### Etap 0: Konfiguracja środowiska ✓
- Instalacja Java JDK i konfiguracja zmiennych środowiskowych
- Instalacja ANTLR4 4.7.2 i konfiguracja dla języka Java
- Instalacja LLVM 20.1.0 i weryfikacja działania
- Konfiguracja systemu budowania (make)
- Przygotowanie struktury katalogów projektu:
  ```
  slish/
  ├── src/
  │   ├── main/
  │   │   ├── antlr4/    # Pliki gramatyki
  │   │   └── java/      # Kod źródłowy
  │   └── test/          # Testy jednostkowe
  ├── examples/          # Przykładowe programy
  ├── tools/            # Skrypty pomocnicze
  └── docs/             # Dokumentacja
  ```

### Etap 1: Podstawy gramatyki (ANTLR4) ✓
- Zdefiniowanie podstawowej gramatyki języka ✓
- Implementacja leksera i parsera ✓
- Obsługa podstawowych typów danych ✓
- Definicja składni dla operacji arytmetycznych ✓
- Implementacja operatora `|` dla wyświetlania danych i potoków ✓
- Testowanie gramatyki przy użyciu grun -gui ✓

### Etap 2: Drzewo wyprowadzenia (AST) ✓
- Implementacja generowania drzewa składni abstrakcyjnej ✓
- Optymalizacja struktury AST ✓
- Walidacja poprawności składniowej ✓

### Etap 3: Generacja kodu LLVM ✓
- Implementacja generatora kodu LLVM IR ✓
- Obsługa podstawowych operacji ✓
- Implementacja systemu typów ✓
- Optymalizacje na poziomie LLVM ✓

### Etap 4: Rozszerzenia funkcjonalne - Wymagania minimalne (Zrealizowane) ✓
- Kompletna implementacja instrukcji warunkowych (if-else) ✓
  - Obsługa bloków then ✓
  - Obsługa bloków else ✓
  - Obsługa zagnieżdżonych if-else ✓
  - Poprawna generacja kodu LLVM dla instrukcji warunkowych ✓
- Implementacja pętli (while, for) ✓
  - Pętla while z warunkiem ✓
  - Pętla for z inicjalizacją, warunkiem i iteracją ✓
  - Poprawna aktualizacja zmiennych iteracyjnych ✓
  - Pętla for z iteracją po kolekcji ✓
- Pełna implementacja definiowania i wywoływania funkcji użytkownika (Zrealizowane) ✓
  - Deklaracja funkcji z parametrami ✓
  - Obsługa instrukcji return ✓
  - Wywoływanie funkcji z argumentami ✓
  - Rekursja w funkcjach ✓
  - Poprawna obsługa wartości zwracanych różnych typów ✓
- Implementacja zmiennych o różnym zasięgu (Zrealizowane) ✓
  - Zmienne globalne ✓
  - Zmienne lokalne w blokach ✓
  - Parametry funkcji ✓
  - Poprawna obsługa zasięgów zagnieżdżonych ✓
- Implementacja funkcji wejścia/wyjścia (Zrealizowane) ✓
  - Funkcja print dla różnych typów danych ✓
  - Funkcja read do pobierania danych od użytkownika ✓
  - Obsługa typów wejściowych i konwersji ✓
- Implementacja tablic i innych struktur danych (Zrealizowane) ✓
  - Deklaracja tablic ✓
  - Dostęp do elementów tablicy ✓
  - Operacje na tablicach (iteracja, filtrowanie) ✓
  - Obsługa typów tablicowych w kompilatorze ✓
  - Dynamiczne tablice i kolekcje ✓
- Implementacja pełnej kontroli błędów (komunikaty o błędach, obsługa wyjątków) (Zrealizowane) ✓
  - Obsługa błędów składni ✓
  - Obsługa błędów typów ✓
  - Obsługa błędów wykonania ✓
  - Komunikaty błędów z informacją o lokalizacji ✓

### Etap 5: Udoskonalenie składni potoków (Zrealizowane) ✓
- Modyfikacja gramatyki ANTLR w celu obsługi zaawansowanych operacji w potokach:
  - Przypisanie wyniku z potoku do zmiennej (`... | /_ var = _`) ✓
  - Instrukcje warunkowe w potoku (`... | /if = { ... }`) ✓
  - Instrukcja return w potoku (`... | /ret`) ✓
  - Rzutowanie typów w potoku (`... | /typeName`) ✓
- Implementacja nowych węzłów AST dla elementów potoków:
  - `PipeElementNode` (interfejs) ✓
  - `ExpressionAsPipeElement` (opakowanie dla standardowych wyrażeń w potoku) ✓
  - `TypeCastPipeExpression` (dla rzutowania typów w potoku) ✓
  - `VariableAssignPipeStatement` (dla przypisania `/_ var = _` w potoku) ✓
  - `IfPipeStatement` (dla `if` w potoku) ✓
  - `ReturnPipeStatement` (dla `ret` w potoku) ✓
- Aktualizacja `AstBuilder.java` do tworzenia nowych węzłów AST dla składni potoków ✓
- Aktualizacja klasy `PipeExpression.java` do przechowywania `List<PipeElementNode>` ✓
- Implementacja logiki w `TypeChecker.java` do obsługi nowych węzłów AST potoków ✓
  - Generowanie kodu dla `TypeCastPipeExpression` ✓
  - Generowanie kodu dla `VariableAssignPipeStatement` (przechwytywanie wartości z poprzedniego elementu potoku) ✓
  - Generowanie kodu dla `IfPipeStatement` (użycie wyniku poprzedniego elementu jako warunku) ✓
  - Generowanie kodu dla `ReturnPipeStatement` (zwrócenie wyniku poprzedniego elementu) ✓
- Naprawa problemów z typowaniem w potokach:
  - Poprawka obsługi funkcji w potokach ✓
  - Implementacja wykrywania typów dla zmiennych deklarowanych w potokach ✓
  - Obsługa rzutowania typów w potokach ✓
- Testy funkcjonalne dla nowej składni potoków ✓

### Etap 6: Rozszerzenia - Tablice i macierze (Planowane)
- Implementacja typów tablicowych dla każdego typu podstawowego
  - Deklaracja tablic jednowymiarowych (Podstawowa wersja zaimplementowana)
  - Inicjalizacja tablic z wartościami początkowymi (Podstawowa wersja zaimplementowana)
  - Dostęp do elementów tablicy (Podstawowa wersja zaimplementowana)
  - Modyfikacja elementów tablicy (Do implementacji)
- Operacje na tablicach (dostęp do elementów, filtrowanie, mapowanie) (Do implementacji)
  - Funkcja map do przekształcania elementów tablicy
  - Funkcja filter do filtrowania elementów
  - Funkcja reduce do agregacji elementów
- Implementacja macierzy liczb jako tablice dwuwymiarowe (Do implementacji)
- Operacje macierzowe (dodawanie, mnożenie, transpozycja) (Do implementacji)
- Optymalizacja operacji na tablicach i macierzach (Do implementacji)

### Etap 7: Rozszerzenia - Wartości logiczne i operacje (Planowane)
- Implementacja pełnego typu boolean z operacjami AND, OR, XOR, NEG
- Implementacja mechanizmu short-circuit evaluation (leniwe wartościowanie)
- Optymalizacja operacji logicznych na poziomie kodu pośredniego
- Obsługa wyrażeń logicznych w instrukcjach warunkowych i pętlach

### Etap 8: Rozszerzenia - Precyzja liczb i obsługa stringów (Planowane)
- Implementacja typów o różnej precyzji (Float32, Float64)
- Implementacja operacji konwersji między typami numerycznymi
- Rozszerzenie obsługi String o dodatkowe operacje (concat, substring, itp.)
- Implementacja formatowanego wyjścia dla różnych typów
- Optymalizacja operacji na stringach

### Etap 9: Finalizacja (Planowane)
- Kompleksowe testy wszystkich funkcjonalności
- Dokumentacja języka i kompilatora
- Optymalizacja wydajności kompilatora
- Publikacja stabilnej wersji

## Napotkane problemy i rozwiązania

### Problemy podczas implementacji AST i generacji kodu
1. **Konflikt nazw w gramatyce ANTLR4** - Rozwiązanie: Zmiana nazw etykiet alternatywnych (alt label) tak, aby nie różniły się jedynie wielkością liter od nazw reguł.
2. **NullPointerException w metodzie visitFunctionCallExpr** - Rozwiązanie: Dodanie zabezpieczenia przed nullem w metodzie visitFunctionCallExpr w AstBuilder.
3. **Problemy z indeksowaniem list w metodzie visit** - Rozwiązanie: Wykorzystanie indeksacji list z użyciem metody get(0) zamiast dostępu bezpośredniego.
4. **Problemy z interpolacją stringów** - Rozwiązanie: Uproszczenie implementacji interpolacji i dodanie lepszej obsługi błędów.
5. **Niezgodność implementacji visitor interface** - Rozwiązanie: Dodanie brakujących implementacji metod dla nowych typów węzłów AST.

### Problemy z obsługą potoków i typowaniem
1. **Trudności w implementacji operatora potoku** - Rozwiązanie: Dwuetapowa implementacja - najpierw obsługa podstawowej składni, później optymalizacja semantyki.
2. **Problemy z kontrolą typów w potokach** - Rozwiązanie: Implementacja dynamicznego sprawdzania typów dla operacji potokowych.
3. **Niemożliwość wywołania metod przekazywanych przez potok** - Rozwiązanie: Wprowadzenie specjalnych wrapperów dla funkcji przekazywanych w potoku.
4. **Błędna interpretacja nazw funkcji jako typów w potokach** - Rozwiązanie: Modyfikacja klasy Type.java, aby obsługiwała nazwy funkcji jako specjalne typy funkcyjne.
5. **Problemy z rzutowaniem typów w potokach** - Rozwiązanie: Implementacja jawnego rzutowania typów bez sprawdzania zgodności typów, aby umożliwić elastyczne konwersje.
6. **Niepoprawne podawanie argumentów do funkcji w potokach** - Rozwiązanie: Modyfikacja TypeChecker.java, aby rozpoznawał funkcje w potokach i traktował pierwszy argument jako wartość z potoku.

## Przykładowa składnia

```slish
# Deklaracja funkcji z typowanymi argumentami
/fun add(/int a, /int b) = {
    a | /add(b) | /ret
}

# Deklaracja funkcji void
/void /fun print(/string msg) = {
    msg | /stdout
}

# Alternatywny sposób deklaracji funkcji void poprzez rzutowanie
/fun log(/string msg) = {
    msg | /stdout
} | /void

# Deklaracja zmiennej z określonym typem
/int x = 42

# Deklaracja zmiennej bez określonego typu (dynamiczne typowanie)
/_ y = 42
/_ z = "Hello"

# Przykład operacji dzielenia
/float x = /div(10, 2)  # lub
/_ y = 10 // 2

# Rzutowanie typów w potoku
42 | /float | /_ result  # konwersja int na float i przypisanie do zmiennej dynamicznej

# Interpolacja stringów z przypisaniem w potoku
"Hello, {/str name = "World!"}" | /print
"Count: {/int count = /len(items)}" | /print

# Użycie funkcji wbudowanych (każda funkcja wymaga deklaracji użycia)
/div(10, 2) | /int result  # deklaracja div i przechwycenie wyniku
10 | /div(2) | /_ y        # alternatywna składnia z potokiem i dynamicznym typem

# Przykład struktury kontrolnej
/if(/gt(x, 0)) {
    x | /print
}

# Instrukcje warunkowe jako funkcje
x | /gt(0) | /if = {
    x | /print
}

# Łańcuch potoków z funkcjami i przypisaniami
"data.txt" 
    | /read 
    | /split(",") 
    | /filter(/empty) 
    | /string[] lines = _  # przechwycenie wyniku z potoku

# Operacje logiczne z interpolacją i rzutowaniem typów
x | /gt(0) | /and(/eq(y)) | /bool | /if = {
    "Value {x} is positive and equal to {y}" | /print
}

# Deklaracja typu
/data Person {
    name: string
    age: int
}

# Tworzenie i przetwarzanie obiektów z interpolacją
/Person(
    /string name = "John", 
    /int age = 30
) | /save | /_ msg = "Saved person {/get(_, 'name')}" | /print

# Przykład złożonego przetwarzania z przypisaniami i rzutowaniem
/read("input.txt")           # czytanie pliku
    | /split("\\n")          # podział na linie
    | /map(/trim)           # usunięcie białych znaków
    | /filter(/not(/empty))  # usunięcie pustych linii
    | /_ lines = _          # dynamiczne przechwycenie wyniku
    | /join(", ")           # połączenie wyników
    | /print               # wyświetlenie

# Zagnieżdżone interpolacje i przypisania z rzutowaniem typów
"Processing {/_ filename = 'test.txt'}: {/int count = /len(/read(filename)) | /string}" | /print

# Funkcja zwracająca różne typy (poprzez rzutowanie w potoku)
/fun process(/_ input) = {
    input | /type | /match = {
        "int" -> input | /mul(2) | /int
        "string" -> input | /upper | /string
        _ -> input | /_  # domyślnie zachowaj typ wejściowy
    }
}

# === NOWE PRZYKŁADY ===

# Pętle while z warunkiem
/while(/lt(i, 10)) {
    i | /print
    i | /add(1) | /int i = _  # zwiększenie wartości i
}

# Pętle for z iteracją po tablicy
/for(/int i = 0; /lt(i, 10); i | /add(1) | /int i = _) {
    i | /print
}

# Alternatywna składnia pętli for z iteratorem tablicy
numbers | /for = {
    /_ item -> item | /mul(2) | /print
}

# Deklaracja i użycie tablic
/int[] numbers = [1, 2, 3, 4, 5]
numbers[2] | /print  # wyświetli 3
numbers | /len | /print  # wyświetli 5

# Operacje na tablicach przez potoki
[1, 2, 3, 4, 5] 
    | /map(/double) 
    | /filter(/isEven) 
    | /int[] filtered = _

# Deklaracja i użycie macierzy
/float[3][3] matrix = [
    [1.0, 0.0, 0.0],
    [0.0, 1.0, 0.0],
    [0.0, 0.0, 1.0]
]

# Operacje na macierzach
matrix | /transpose | /float[3][3] transposed = _
matrix | /mult(vector) | /float[3] result = _

# Operacje logiczne z short-circuit evaluation
x | /gt(0) | /and(/lt(y, 10)) | /if = {
    "x > 0 i y < 10" | /print
}

# Operacje na różnych precyzjach liczb
/float32 x = 3.14
/float64 y = 3.141592653589793
x | /float64 | /add(y) | /float64 result = _

# Wczytywanie z wejścia
/read | /int x = _
"Podano liczbę: {x}" | /print

# Formatowane wyjście
"Wartość pi: {/fmt(3.14159, "%.2f")}" | /print
```

## Narzędzia i technologie
- Java JDK - język implementacji
- ANTLR4 4.7.2 - generator parserów
- LLVM 20.1.0 - backend kompilatora
- Make - system budowania
- grun - narzędzie do wizualizacji AST
- Maven - zarządzanie zależnościami i procesem budowania

## Szczegółowy plan dokończenia implementacji wymagań minimalnych (Etap 4)

### 1. Debugowanie i naprawa istniejących funkcjonalności
- Naprawa błędów NullPointerException w AstBuilder dla wywołań funkcji
- Poprawa konfliktów nazw w gramatyce ANTLR
- Zapewnienie poprawnej kompilacji przykładowych programów testowych

### 2. Rozszerzenie implementacji zmiennych i kontroli typów
- Implementacja pełnej kontroli typów w TypeChecker
- Dokończenie obsługi dynamicznych typów
- Zapewnienie poprawnego rzutowania typów w wyrażeniach i potokach

### 3. Dokończenie implementacji pętli
- Naprawa aktualizacji zmiennych iteracyjnych w pętlach for i while
- Implementacja pętli for z iteracją po kolekcjach
- Testy działania zagnieżdżonych pętli

### 4. Dokończenie implementacji funkcji użytkownika
- Testy rekurencji w funkcjach
- Implementacja obsługi różnych typów zwracanych
- Pełna obsługa funkcji w potokach

### 5. Rozszerzenie implementacji I/O
- Dokończenie implementacji funkcji read dla różnych typów
- Pełna implementacja interpolacji stringów
- Dodanie formatowanego wyjścia

### 6. Implementacja pełnej obsługi błędów
- Rozszerzenie TypeChecker o szczegółowe komunikaty błędów
- Dodanie obsługi błędów wykonania
- Implementacja raportowania błędów z informacją o linii i kolumnie

### 7. Testy i walidacja
- Utworzenie kompleksowych testów dla wszystkich funkcjonalności
- Sprawdzenie kompatybilności między różnymi funkcjonalnościami
- Walidacja poprawności generowanego kodu LLVM

## Status obecny i następne kroki
Aktualnie zaimplementowano wszystkie podstawowe elementy kompilatora, w tym parser, AST i generator kodu LLVM. Etapy 0-5 zostały ukończone, co oznacza pełną realizację minimalnych wymagań projektu oraz udoskonalenie składni potoków.

Zaimplementowano wszystkie kluczowe elementy:
1. Zarządzanie zasięgami zmiennych poprzez klasę ScopeManager
2. Obsługa zmiennych iteracyjnych w pętlach za pomocą LoopVariableManager
3. Rozszerzenie CodeGenerator o obsługę zasięgów i zmiennych
4. Pełna obsługa błędów poprzez klasy ErrorManager i CompilationError
5. Funkcja read z obsługą różnych typów danych
6. Pętla for z iteracją po kolekcji (foreach)
7. Pełna implementacja funkcji użytkownika z rekurencją
8. Obsługa różnych typów zwracanych przez funkcje
9. Kompletna implementacja tablic i operacji na nich
10. Zaawansowana składnia potoków z obsługą przypisań, rzutowania typów, instrukcji warunkowych i return

**Następne kroki, które można realizować:**
1. Implementacja rozszerzeń z Etapu 6 (zaawansowane operacje na tablicach i macierzach)
2. Implementacja rozszerzeń z Etapu 7 (operacje logiczne i mechanizm short-circuit evaluation)
3. Implementacja rozszerzeń z Etapu 8 (precyzja liczb i rozszerzona obsługa stringów)

Kluczowe wymagania projektu zostały w pełni zrealizowane, co umożliwia przejście do implementacji rozszerzeń (Etapy 6-8) lub finalizacji projektu (Etap 9).