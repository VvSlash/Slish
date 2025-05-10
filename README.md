# Projekt Slish Compiler

<p align="center"><img src="slish.png" alt="Slish Banner" width="100%"/>

## O języku Slish

Slish to innowacyjny język programowania, którego filozofia i składnia czerpią inspirację z systemów Unix/Linux. Kluczowym elementem języka jest znak `/` (slash), wykorzystywany do reprezentacji ścieżek, poleceń oraz operacji wewnątrz języka, co ma na celu zapewnienie przejrzystości i intuicyjności.

### Główne założenia i cechy języka Slish:

*   **Brak średników:** Linie kodu nie wymagają terminatorów w postaci średników (`;`).
*   **Operator potoku `|`:** Inspirowany potokami Linux, służy do przekazywania danych między operacjami oraz do ich wyświetlania (np. `dana | /stdout`).
*   **Specjalna składnia dla dzielenia:** Aby uniknąć konfliktu ze znakiem `/`, operacja dzielenia realizowana jest za pomocą `div(a, b)` lub `//`.
*   **Składnia inspirowana systemami Unix/Linux:** Struktura języka nawiązuje do drzewiastej struktury katalogów i mechanizmu potoków.
*   **Typowanie:** Język wspiera zarówno typowanie statyczne (np. `/int x = 10`), jak i dynamiczne (`/_ y = "hello"`).
*   **Funkcje:** Definiowanie funkcji z typowanymi parametrami i typem zwracanym (np. `/fun nazwa(/typ arg1) = { ... } | /typZwracany`) lub funkcji `void`.
*   **Struktury kontrolne:** Implementacja instrukcji warunkowych (`/if ... {}`) oraz pętli (`/while`, `/for`).
*   **Operacje w potokach:** Zaawansowane możliwości manipulacji danymi w potokach, w tym przypisania (`... | /_ var = _`), instrukcje warunkowe (`... | /if = { ... }`), instrukcje `return` (`... | /ret`) oraz rzutowanie typów (`... | /typeName`).
*   **Tablice i kolekcje:** Wsparcie dla tablic i operacji na nich (deklaracja, inicjalizacja, dostęp do elementów).
*   **Interpolacja stringów:** Możliwość osadzania wartości zmiennych i wyrażeń w ciągach tekstowych (np. `"Wynik: {zmienna}"`).
*   **Obsługa błędów:** Mechanizmy kontroli błędów składniowych, typów oraz błędów wykonania.

### Przykładowa składnia

```slish
# Deklaracja funkcji
/fun add(/int a, /int b) = {
    a | /add(b) | /ret
}

# Deklaracja funkcji void
/void /fun print(/string msg) = {
    msg | /stdout
}

# Deklaracja zmiennej z określonym typem
/int x = 42

# Deklaracja zmiennej bez określonego typu (dynamiczne typowanie)
/_ y = 42
/_ z = "Hello"

# Przykład operacji dzielenia
/float x = /div(10, 2)  # lub
/_ y = 10 // 2

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

# Pętle while z warunkiem
/while(/lt(i, 10)) {
    i | /print
    i | /add(1) | /int i = _  # zwiększenie wartości i
}

# Deklaracja i użycie tablic
/int[] numbers = [1, 2, 3, 4, 5]
numbers[2] | /print  # wyświetli 3
numbers | /len | /print  # wyświetli 5
```

## Struktura Projektu

Struktura katalogów projektu została zorganizowana w następujący sposób:

```
slish/
├── .git/            # Katalog Gita
├── .idea/           # Pliki konfiguracyjne IntelliJ IDEA (ignorowane przez .gitignore)
├── .vscode/         # Pliki konfiguracyjne Visual Studio Code (ignorowane przez .gitignore)
├── docs/            # Dokumentacja projektu
├── examples/        # Przykładowe programy napisane w języku Slish
├── src/             # Główny katalog kodu źródłowego
│   ├── main/
│   │   ├── antlr4/    # Pliki gramatyki ANTLR4 (*.g4)
│   │   │   └── .antlr/  # Pliki generowane przez ANTLR podczas kompilacji
│   │   ├── java/      # Kod źródłowy kompilatora w Javie
│   │   │   └── pl/edu/pw/slish/  # Główny pakiet projektu
│   │   │       ├── ast/         # Abstrakcyjne drzewo składniowe
│   │   │       ├── codegen/     # Generator kodu LLVM
│   │   │       └── instructions/ # Instrukcje kodu LLVM
│   │   └── generated-sources/ # Wygenerowane źródła (przez ANTLR)
│   └── test/         # Kod testów jednostkowych
├── target/          # Katalog generowany przez Maven (ignorowany przez .gitignore)
├── tools/           # Narzędzia pomocnicze, w tym ANTLR JAR
├── .gitignore       # Lista plików ignorowanych przez Gita
├── build.bat        # Skrypt budowania dla Windows
├── Makefile         # Plik konfiguracyjny dla narzędzia make
├── packages.config  # Konfiguracja pakietów
├── pom.xml          # Plik konfiguracyjny Maven (Project Object Model)
├── README.md        # Ten plik - podstawowe informacje o projekcie
├── roadmap.md       # Szczegółowy plan rozwoju projektu
└── test.bat         # Skrypt testowy dla Windows
```

## Etapy Rozwoju Projektu

Projekt został zrealizowany w kilku etapach:

1. **Etap 0:** Konfiguracja środowiska (✓)
2. **Etap 1:** Podstawy gramatyki (ANTLR4) (✓)
3. **Etap 2:** Implementacja drzewa składni abstrakcyjnej (AST) (✓)
4. **Etap 3:** Generacja kodu LLVM (✓)
5. **Etap 4:** Rozszerzenia funkcjonalne - Wymagania minimalne (✓)
   - Instrukcje warunkowe (if-else)
   - Pętle (while, for)
   - Definiowanie i wywoływanie funkcji
   - Zmienne o różnym zasięgu
   - Funkcje wejścia/wyjścia
   - Tablice i struktury danych
   - Kontrola błędów
6. **Etap 5:** Udoskonalenie składni potoków (✓)
7. **Planowane rozszerzenia:**
   - Zaawansowane operacje na tablicach i macierzach
   - Pełne wsparcie dla operacji logicznych z leniwym wartościowaniem
   - Typy o różnej precyzji i rozszerzona obsługa stringów

## Narzędzia i Technologie

Projekt wykorzystuje następujące narzędzia i technologie:

*   **Java JDK:** Język implementacji kompilatora
*   **ANTLR 4.7.2:** Generator parserów używany do analizy składni języka Slish
*   **LLVM 20.1.0:** Backend kompilatora do generowania kodu wynikowego
*   **Maven:** Zarządzanie zależnościami i procesem budowania
*   **Make:** Alternatywny system budowania

## Jak Zacząć?

1. **Wymagania wstępne:**
   - Java JDK
   - ANTLR 4.7.2
   - LLVM 20.1.0
   - Maven

2. **Klonowanie repozytorium:**
   ```bash
   git clone https://github.com/VvSlash/Slish.git
   cd slish
   ```

3. **Budowanie projektu:**
   - Przy użyciu Maven:
     ```bash
     mvn clean install
     ```
   - Lub przy użyciu Makefile:
     ```bash
     make
     ```
   - Na systemie Windows:
     ```
     build.bat
     ```

4. **Kompilacja programów Slish:**
   ```bash
   java -cp target/slish-1.0-SNAPSHOT.jar pl.edu.pw.slish.SlishCompiler examples/example.slish
   ```
   Lub za pomocą skryptu:
   ```
   test.bat examples/example.slish
   ```

## Testowanie i Debugowanie Projektu

Projekt oferuje kilka możliwości testowania i debugowania kompilatora oraz programów w języku Slish:

### Przy użyciu Makefile

1. **Kompilacja projektu:**
   ```bash
   make compile
   ```

2. **Uruchamianie kompilatora Slish dla konkretnego pliku:**
   ```bash
   make run SLISH_FILE=examples/example.slish
   ```
   To polecenie skompiluje podany plik Slish i wygeneruje kod LLVM w formacie `.ll`.

3. **Testowanie gramatyki z GUI (TestRig ANTLR):**
   ```bash
   make grun
   ```
   To polecenie uruchomi graficzne narzędzie TestRig, pomocne przy debugowaniu gramatyki ANTLR, które wizualizuje drzewo składniowe. Możesz również podać plik do analizy:
   ```bash
   make grun GRAMMAR_FILE_IN=examples/basic_test.slish
   ```
   W oknie TestRig możesz interaktywnie wprowadzać kod Slish i obserwować, jak jest przetwarzany przez parser.

### Przy użyciu skryptów batch (Windows)

1. **Kompilacja i uruchomienie testu:**
   ```
   test.bat examples/example.slish
   ```
   Skrypt skompiluje i uruchomi wybrany plik przykładowy.

2. **Pełna kompilacja projektu:**
   ```
   build.bat
   ```
   Skrypt wykonuje pełną kompilację projektu, włącznie z generowaniem plików ANTLR.

### Testowanie przykładowych programów

W katalogu `examples/` znajduje się zestaw przykładowych programów Slish, które można wykorzystać do testowania różnych funkcjonalności języka:

- `basic_test.slish` - podstawowe konstrukcje języka
- `factorial_test.slish` - przykład rekurencji
- `enhanced_features_test.slish` - zaawansowane funkcjonalności języka

Aby przetestować wszystkie przykłady po kolei:

**Dla Windows (PowerShell):**
```powershell
Get-ChildItem -Path "examples" -Filter "*.slish" | ForEach-Object { make run SLISH_FILE=$_.FullName }
```
lub krócej:
```powershell
Get-ChildItem examples\*.slish | ForEach-Object { make run "SLISH_FILE=$_" }
```

**Dla Unix/Linux (bash):**
```bash
for file in examples/*.slish; do make run SLISH_FILE="$file"; done
```

## Kontrybucja

Jeśli chcesz przyczynić się do rozwoju projektu Slish, zapoznaj się z plikiem `roadmap.md`, który zawiera szczegółowe informacje o planowanych rozszerzeniach i funkcjonalnościach. Możesz również zgłaszać problemy i propozycje przez system issues na GitHubie. 