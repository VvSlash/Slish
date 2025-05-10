@echo off
setlocal enabledelayedexpansion

REM Ustawienie ścieżek
set SRC_DIR=src\main\java
set ANTLR_PATH=lib\antlr-4.13.1-complete.jar
set GRAMMAR_DIR=src\main\antlr4\pl\edu\pw\slish
set TARGET_DIR=target
set TARGET_CLASSES=target\classes

REM Tworzenie katalogów, jeśli nie istnieją
if not exist %TARGET_DIR% mkdir %TARGET_DIR%
if not exist %TARGET_CLASSES% mkdir %TARGET_CLASSES%

REM Sprawdzamy czy istnieje plik JAR ANTLR
if not exist %ANTLR_PATH% (
    echo Pobieranie ANTLR...
    powershell -Command "Invoke-WebRequest -Uri 'https://www.antlr.org/download/antlr-4.13.1-complete.jar' -OutFile '%ANTLR_PATH%'"
    if !ERRORLEVEL! neq 0 (
        echo Nie udało się pobrać ANTLR.
        exit /b 1
    ) else (
        echo ANTLR został pomyślnie pobrany.
    )
)

if "%1"=="clean" (
    echo Czyszczenie...
    if exist %TARGET_DIR% rmdir /s /q %TARGET_DIR%
    if exist %SRC_DIR%\pl\edu\pw\slish\Slish*.java del /q %SRC_DIR%\pl\edu\pw\slish\Slish*.java
    exit /b 0
)

echo Generowanie parsera ANTLR...
java -jar %ANTLR_PATH% -visitor -listener -package pl.edu.pw.slish -o %SRC_DIR%\pl\edu\pw\slish %GRAMMAR_DIR%\Slish.g4
if %ERRORLEVEL% neq 0 (
    echo Błąd podczas generowania parsera.
    exit /b 1
)

echo Kompilowanie kodu źródłowego...
javac -cp ".;%ANTLR_PATH%" -d %TARGET_CLASSES% %SRC_DIR%\pl\edu\pw\slish\*.java %SRC_DIR%\pl\edu\pw\slish\ast\*.java %SRC_DIR%\pl\edu\pw\slish\ast\expr\*.java %SRC_DIR%\pl\edu\pw\slish\ast\stmt\*.java %SRC_DIR%\pl\edu\pw\slish\codegen\*.java %SRC_DIR%\pl\edu\pw\slish\codegen\instructions\*.java
if %ERRORLEVEL% neq 0 (
    echo Błąd podczas kompilacji.
    exit /b 1
)

echo Kompilacja zakończona pomyślnie!
exit /b 0 