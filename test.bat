@echo off
echo Uruchamiam kompilator Slish na pliku min_requirements_test.slish...
java -cp "target/classes;tools/antlr-4.7.2-complete.jar" pl.edu.pw.slish.SlishCompiler examples/min_requirements_test.slish
pause 