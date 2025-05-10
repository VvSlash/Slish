########################################################################
#  Slish – budowanie pod Windows (PowerShell)                          #
########################################################################

# Konfiguracja powłoki dla Windows
SHELL := powershell.exe
.SHELLFLAGS := -NoProfile -Command

# Używamy standardowego PowerShell
PS := powershell

.ONESHELL:                    # jeden proces PS na cały przepis

# ==== KONFIGURACJA ====================================================

ANTLR_VERSION := 4.7.2
ANTLR_JAR     := tools/antlr-$(ANTLR_VERSION)-complete.jar
GRAMMAR_NAME  := Slish
GRAMMAR_FILE  := src/main/antlr4/$(GRAMMAR_NAME).g4
PACKAGE_NAME  := pl.edu.pw.slish

# Katalogi
SRC_DIR       := src/main/java
GEN_DIR       := src/main/java/pl/edu/pw/slish
BIN_DIR       := target/classes
TEST_BIN_DIR  := target/test-classes

# Narzędzia
JAVAC         := javac
JAVA          := java
CLASSPATH     := $(BIN_DIR);$(ANTLR_JAR)   # średnik = separator klas w Windows

# =====================================================================
.PHONY: all clean generate compile run grun

all: compile

########################################################################
#  Porządkowanie (czyszczenie całej implementacji - niebezpieczne)
########################################################################
# clean:
# 	$(PS) -Command "if (Test-Path '$(GEN_DIR)') { Remove-Item '$(GEN_DIR)' -Recurse -Force }"
# 	$(PS) -Command "if (Test-Path '$(BIN_DIR)') { Remove-Item '$(BIN_DIR)' -Recurse -Force }"
# 	$(PS) -Command "if (Test-Path '$(TEST_BIN_DIR)') { Remove-Item '$(TEST_BIN_DIR)' -Recurse -Force }"
# 	$(PS) -Command "if (Test-Path 'gen_sources.txt') { Remove-Item 'gen_sources.txt' -Force }"
# 	$(PS) -Command "if (Test-Path 'src_sources.txt') { Remove-Item 'src_sources.txt' -Force }"

########################################################################
#  Katalogi pośrednie
########################################################################
$(BIN_DIR):
	$(PS) -Command "if (-not (Test-Path '$(BIN_DIR)')) { New-Item -Path '$(BIN_DIR)' -ItemType Directory -Force }"

$(GEN_DIR):
	$(PS) -Command "if (-not (Test-Path '$(GEN_DIR)')) { New-Item -Path '$(GEN_DIR)' -ItemType Directory -Force }"

########################################################################
#  Pobieranie ANTLR-a
########################################################################
$(ANTLR_JAR):
	$(PS) -Command "Invoke-WebRequest -Uri 'https://www.antlr.org/download/antlr-$(ANTLR_VERSION)-complete.jar' -OutFile '$(ANTLR_JAR)'"

########################################################################
#  Generowanie parsera
########################################################################
generate: $(GEN_DIR) $(ANTLR_JAR) $(GRAMMAR_FILE)
	$(PS) -Command "if (-not (Test-Path '$(GEN_DIR)/SlishParser.java')) { & '$(JAVA)' -jar '$(ANTLR_JAR)' -o '$(GEN_DIR)' -package $(PACKAGE_NAME) -no-listener -visitor '$(GRAMMAR_FILE)' }"

########################################################################
#  Kompilacja
########################################################################
compile: generate $(BIN_DIR)
	$(PS) -Command "& '$(JAVAC)' -encoding UTF-8 -cp '$(ANTLR_JAR)' -d '$(BIN_DIR)' (Get-ChildItem -Path '$(SRC_DIR)' -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName)"

########################################################################
#  Uruchamianie kompilatora Slish
########################################################################
run: compile
	$(JAVA) -cp "$(CLASSPATH)" $(PACKAGE_NAME).SlishCompiler $(SLISH_FILE)

########################################################################
#  TestRig ANTLR (GUI) – przydatne do szybkiego debugowania gramatyki
########################################################################
grun: compile
	$(JAVA) -cp "$(CLASSPATH)" org.antlr.v4.gui.TestRig $(PACKAGE_NAME).$(GRAMMAR_NAME) program -gui $(GRAMMAR_FILE_IN)
