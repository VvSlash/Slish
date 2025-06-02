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
  ; Alloca variable: i of type int
  %r0 = alloca i32, align 4
  %r1 = add i32 0, 1
  store i32 %r1, i32* %r0
  br label %while_cond_0
  while_cond_0:
  %r2 = load i32, i32* %r0
  %r3 = add i32 0, 100
  %r4 = icmp slt i32 %r2, %r3
  br i1 %r4, label %while_body_1, label %while_end_2
  while_body_1:
  %r5 = load i32, i32* %r0
  %r6 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r6, i32 %r5)
  %r7 = load i32, i32* %r0
  %r8 = add i32 0, 2
  %r9 = mul i32 %r7, %r8
  store i32 %r9, i32* %r0
  br label %while_cond_0
  while_end_2:
  ret i32 0
}
