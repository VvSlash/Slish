; Slish Intermediate Code
; =====================================================

declare i32 @printf(i8*, ...)

; Global string constants
@.str_fmt_int = private unnamed_addr constant [4 x i8] c"%d\0a\00", align 1
@.str_fmt_float32 = private unnamed_addr constant [4 x i8] c"%f\0a\00", align 1
@.str_fmt_float64 = private unnamed_addr constant [5 x i8] c"%lf\0a\00", align 1
@.str_fmt_str = private unnamed_addr constant [4 x i8] c"%s\0a\00", align 1
@.str_fmt_bool = private unnamed_addr constant [4 x i8] c"%d\0a\00", align 1

define i32 @main() {
entry:
  ; Alloca variable: x of type float32
  %r0 = alloca float, align 4
  %r1 = fadd float 0.0, 3.1400001049041750
  store float %r1, float* %r0
  %r2 = load float, float* %r0
  %r3 = fpext float %r2 to double
  %r4 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r4, double %r3)
  ret i32 0
}
