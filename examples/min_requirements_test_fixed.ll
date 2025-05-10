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
r18 = load int from b
r19 = call _ add(r17, r18)
r21 = load int from x
r22 = int 10
r20 = gt bool r21, r22
; if condition: r20
branch r20 then_0 else_1
then_0:
jump endif_2
else_1:
endif_2:
r24 = load int from x
r25 = int 100
r23 = gt bool r24, r25
; if condition: r23
branch r23 then_3 else_4
then_3:
jump endif_5
else_4:
r27 = load int from x
r28 = int 50
r26 = gt bool r27, r28
; if condition: r26
branch r26 then_6 else_7
then_6:
jump endif_8
else_7:
endif_8:
endif_5:
; Variable: i of type int
r30 = int 0
store int r30 to i
while_cond_9:
r32 = load int from i
r33 = int 5
r31 = lt bool r32, r33
branch r31 while_body_10 while_end_11
while_body_10:
; Variable: i of type int
r36 = load int from i
r37 = int 1
r35 = add _ r36, r37
store int r35 to i
jump while_cond_9
while_end_11:
; Variable: j of type int
r39 = int 0
store int r39 to j
; for loop initialization
for_init_12:
; Variable: j of type int
r41 = int 0
store int r41 to j
for_cond_13:
r43 = int 3
r42 = lt bool r40, r43
branch r42 for_body_14 for_end_16
for_body_14:
for_iter_15:
jump for_cond_13
for_end_16:
; Function declaration: dodaj
; Function parameter: p1 of type int
; Function parameter: p2 of type int
r47 = load int from p1
r48 = load int from p2
r46 = add _ r47, r48
return r46
; Variable: wynik of type int
r51 = load int from a
r52 = load int from b
r50 = call _ dodaj(r51, r52)
store int r50 to wynik
r53 = string Wynik funkcji: 
r54 = load int from wynik
r55 = string Podaj liczbę: 
; Variable: input of type int
; Reading input from user, expected type: int
r57 = read int
store int r57 to input
r58 = string Podałeś: 
r59 = load int from input
; Function declaration: test_scope
; Variable: lokalnaZmienna of type int
r61 = int 99
store int r61 to lokalnaZmienna
r62 = call _ test_scope()
; Variable: intValue of type int
r64 = int 42
store int r64 to intValue
; Variable: interpolatedString of type string
r66 = string "Wartość: {intValue}"
store string r66 to interpolatedString
r67 = load string from interpolatedString
; Array variable: array of type int[]
r70 = int 1
r71 = int 2
r72 = int 3
r73 = int 4
r74 = int 5
r69 = array array [5] r70, r71, r72, r73, r74
store array r69 to array
r76 = int 2
r75 = load int array[r76]
r77 = load array from array
r79 = int 3
r78 = load int array[r79]
