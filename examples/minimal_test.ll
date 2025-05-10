; Slish Intermediate Code
; =====================================================

; Variable: x of type int
r1 = int 42
store int r1 to x
; Variable: y of type float
r3 = float 3.14
store float r3 to y
; Variable: text of type string
r5 = string Hello, Slish!
store string r5 to text
; Variable: dynamic of type _
r7 = string dynamic typing
store _ r7 to dynamic
; Variable: a of type int
r9 = int 5
store int r9 to a
; Variable: b of type int
r11 = int 3
store int r11 to b
; Variable: sum of type int
r14 = load int from a
r15 = load int from b
r13 = add _ r14, r15
store int r13 to sum
r17 = load int from a
r18 = load int from b
r16 = gt bool r17, r18
; if condition: r16
branch r16 then_0 else_1
then_0:
jump endif_2
else_1:
endif_2:
; Array variable: numbers of type int[]
r21 = int 1
r22 = int 2
r23 = int 3
r24 = int 4
r25 = int 5
r20 = array array [5] r21, r22, r23, r24, r25
store array r20 to numbers
r27 = int 2
r26 = load int numbers[r27]
r28 = string Wprowadź liczbę:
