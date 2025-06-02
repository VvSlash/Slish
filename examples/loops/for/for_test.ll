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
  ; Alloca variable: j of type int
  %r0 = alloca i32, align 4
  %r1 = add i32 0, 0
  store i32 %r1, i32* %r0
  ; Standard for loop
  br label %for_init_0
  for_init_0:
  %r2 = add i32 0, 0
  store i32 %r2, i32* %r0
  br label %for_cond_1
  for_cond_1:
  %r3 = load i32, i32* %r0
  %r4 = add i32 0, 5
  %r5 = icmp sle i32 %r3, %r4
  br i1 %r5, label %for_body_2, label %for_end_4
  for_body_2:
  %r6 = load i32, i32* %r0
  %r7 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r7, i32 %r6)
  br label %for_iter_3
  for_iter_3:
  %r8 = load i32, i32* %r0
  %r9 = add i32 0, 1
  %r10 = add i32 %r8, %r9
  store i32 %r10, i32* %r0
  br label %for_cond_1
  for_end_4:
  ret i32 0
}
