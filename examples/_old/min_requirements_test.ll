; Slish Intermediate Code
; =====================================================

; Variable: x of type int
r1 = int 42
store int r1 to x
; Variable: y of type float
r3 = float 3.14
store float r3 to y
; Variable: dynamicVar of type _
r5 = string tekst
store _ r5 to dynamicVar
; Variable: a of type int
r7 = int 5
store int r7 to a
; Variable: b of type int
r9 = int 3
store int r9 to b
; Variable: result of type int
r12 = load int from a
r13 = load int from b
r11 = add _ r12, r13
store int r11 to result
r15 = load int from a
r16 = int 5
r14 = add _ r15, r16
r17 = load int from a
r19 = load int from b
r18 = call _ add(r19)
r20 = cast _ r18 to fun
; Casting r18 to fun into r20
r22 = load int from x
r23 = int 10
r21 = gt bool r22, r23
; if condition: r21
branch r21 then_0 else_1
then_0:
jump endif_2
else_1:
endif_2:
r25 = load int from x
r26 = int 100
r24 = gt bool r25, r26
; if condition: r24
branch r24 then_3 else_4
then_3:
jump endif_5
else_4:
r28 = load int from x
r29 = int 50
r27 = gt bool r28, r29
; if condition: r27
branch r27 then_6 else_7
then_6:
jump endif_8
else_7:
endif_8:
endif_5:
; Variable: i of type int
r31 = int 0
store int r31 to i
while_cond_9:
r33 = load int from i
r34 = int 5
r32 = lt bool r33, r34
branch r32 while_body_10 while_end_11
while_body_10:
; Variable: i of type int
r37 = load int from i
r38 = int 1
r36 = add _ r37, r38
store int r36 to i
jump while_cond_9
while_end_11:
; Variable: j of type int
r40 = int 0
store int r40 to j
; for loop initialization
for_init_12:
; Variable: j of type int
r42 = int 0
store int r42 to j
for_cond_13:
r44 = int 3
r43 = lt bool r41, r44
branch r43 for_body_14 for_end_16
for_body_14:
for_iter_15:
jump for_cond_13
for_end_16:
; Function declaration: dodaj
; Function parameter: p1 of type int
; Function parameter: p2 of type int
; Variable: wynik of type int
r49 = load int from a
r50 = load int from b
r48 = call _ dodaj(r49, r50)
store int r48 to wynik
r51 = string Wynik funkcji: 
r52 = load int from wynik
r53 = string Podaj liczbę: 
; Variable: input of type int
; Reading input from user, expected type: int
r55 = read int
store int r55 to input
r56 = string Podałeś: 
r57 = load int from input
; Function declaration: test_scope
; Variable: lokalnaZmienna of type int
r59 = int 99
store int r59 to lokalnaZmienna
r60 = call _ test_scope()
; Variable: intValue of type int
r62 = int 42
store int r62 to intValue
; Variable: interpolatedString of type string
r64 = string "Wartość: {intValue}"
store string r64 to interpolatedString
r65 = load string from interpolatedString
; Array variable: array of type int[]
r68 = int 1
r69 = int 2
r70 = int 3
r71 = int 4
r72 = int 5
r67 = array array [5] r68, r69, r70, r71, r72
store array r67 to array
r74 = int 2
r73 = load int array[r74]
r75 = load array from array
r77 = int 3
r76 = load int array[r77]
r78 = cast _ r76 to void
; Casting r76 to void into r78
; Function declaration: printVoid
; Function parameter: message of type string
r80 = string Test funkcji void
; Function declaration: addAlt
; Function parameter: a of type int
; Function parameter: b of type int
; Variable: wynikAlt of type int
r85 = int 10
r86 = int 20
r84 = call _ addAlt(r85, r86)
store int r84 to wynikAlt
r87 = string Wynik alternatywnej funkcji: 
r88 = load int from wynikAlt
r89 = int 42
r90 = cast _ r89 to float
; Casting r89 to float into r90
; Declared variable in pipe: floatResult of type _
store _ r90 to floatResult
; Assigned value from pipe (reg: r90) to variable floatResult
r92 = string Rzutowany rezultat: 
r93 = load _ from floatResult
r94 = load int from x
r96 = int 0
r95 = call _ gt(r96)
; Piped if condition from register: r95
branch r95 then_pipe_17 endif_pipe_18
then_pipe_17:
jump endif_pipe_18
endif_pipe_18:
r98 = int 1
r99 = int 2
r100 = int 3
r101 = int 4
r102 = int 5
r97 = array array [5] r98, r99, r100, r101, r102
; Declared variable in pipe: numbers of type _
store _ r97 to numbers
; Assigned value from pipe (reg: r97) to variable numbers
r104 = load _ from numbers
r105 = cast _ r104 to fun
; Casting r104 to fun into r105
; Variable: logiczna of type bool
r108 = load int from x
r109 = int 42
r107 = call _ eq(r108, r109)
store bool r107 to logiczna
r110 = load bool from logiczna
r111 = cast _ r110 to fun
; Casting r110 to fun into r111
; Variable: formattedString of type string
r113 = string "Zmienna x ma wartość: {x}"
store string r113 to formattedString
r114 = load string from formattedString
r115 = load array from array
; Declared variable in pipe: copied of type _
store _ r115 to copied
; Assigned value from pipe (reg: r115) to variable copied
r117 = cast _ r115 to fun
; Casting r115 to fun into r117
