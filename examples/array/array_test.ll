; Slish Intermediate Code
; =====================================================

declare i32 @printf(i8*, ...)

; Global string constants
@.str_fmt_int = private unnamed_addr constant [4 x i8] c"%d\0a\00", align 1
@.str_fmt_float = private unnamed_addr constant [4 x i8] c"%f\0a\00", align 1
@.str_fmt_str = private unnamed_addr constant [4 x i8] c"%s\0a\00", align 1
@.str_fmt_bool = private unnamed_addr constant [4 x i8] c"%d\0a\00", align 1

define i32 @main() {
entry:
  ; Alloca variable: array of type array
  %r0 = alloca i32*, align 8
  %r1 = alloca [5 x i32], align 16
  %r2 = add i32 0, 1
  %r3 = getelementptr inbounds [5 x i32], [5 x i32]* %r1, i32 0, i32 0
  store i32 %r2, i32* %r3
  %r4 = add i32 0, 2
  %r5 = getelementptr inbounds [5 x i32], [5 x i32]* %r1, i32 0, i32 1
  store i32 %r4, i32* %r5
  %r6 = add i32 0, 3
  %r7 = getelementptr inbounds [5 x i32], [5 x i32]* %r1, i32 0, i32 2
  store i32 %r6, i32* %r7
  %r8 = add i32 0, 4
  %r9 = getelementptr inbounds [5 x i32], [5 x i32]* %r1, i32 0, i32 3
  store i32 %r8, i32* %r9
  %r10 = add i32 0, 5
  %r11 = getelementptr inbounds [5 x i32], [5 x i32]* %r1, i32 0, i32 4
  store i32 %r10, i32* %r11
  %r12 = bitcast [5 x i32]* %r1 to i32*
  store i32* %r12, i32** %r0
  %r13 = add i32 0, 0
  %r14 = load i32*, i32** %r0
  %r15 = getelementptr inbounds i32, i32* %r14, i32 %r13
  %r16 = load i32, i32* %r15
  %r17 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r17, i32 %r16)
  %r18 = add i32 0, 2
  %r19 = load i32*, i32** %r0
  %r20 = getelementptr inbounds i32, i32* %r19, i32 %r18
  %r21 = load i32, i32* %r20
  %r22 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r22, i32 %r21)
  %r23 = add i32 0, 4
  %r24 = load i32*, i32** %r0
  %r25 = getelementptr inbounds i32, i32* %r24, i32 %r23
  %r26 = load i32, i32* %r25
  %r27 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r27, i32 %r26)
  ret i32 0
}
