; Slish Intermediate Code
; =====================================================

declare i32 @printf(i8*, ...)

; Global string constants
@.str0 = private unnamed_addr constant [3 x i8] c"AA\00", align 1
@.str_fmt_int = private unnamed_addr constant [4 x i8] c"%d\0a\00", align 1
@.str_fmt_float32 = private unnamed_addr constant [4 x i8] c"%f\0a\00", align 1
@.str_fmt_float64 = private unnamed_addr constant [5 x i8] c"%lf\0a\00", align 1
@.str_fmt_str = private unnamed_addr constant [4 x i8] c"%s\0a\00", align 1
@.str_fmt_bool = private unnamed_addr constant [4 x i8] c"%d\0a\00", align 1

define i32 @main() {
entry:
  ; Alloca variable: a of type int
  %r0 = alloca i32, align 4
  %r1 = add i32 0, 5
  %r2 = add i32 0, 3
  %r3 = add i32 %r1, %r2
  store i32 %r3, i32* %r0
  %r4 = load i32, i32* %r0
  %r5 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r5, i32 %r4)
  ; Alloca variable: b of type int
  %r6 = alloca i32, align 4
  %r7 = add i32 0, 10
  %r8 = add i32 0, 4
  %r9 = sub i32 %r7, %r8
  store i32 %r9, i32* %r6
  %r10 = load i32, i32* %r6
  %r11 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r11, i32 %r10)
  ; Alloca variable: c of type int
  %r12 = alloca i32, align 4
  %r13 = add i32 0, 6
  %r14 = add i32 0, 5
  %r15 = mul i32 %r13, %r14
  store i32 %r15, i32* %r12
  %r16 = load i32, i32* %r12
  %r17 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r17, i32 %r16)
  ; Alloca variable: d of type int
  %r18 = alloca i32, align 4
  %r19 = add i32 0, 15
  %r20 = add i32 0, 3
  %r21 = sdiv i32 %r19, %r20
  store i32 %r21, i32* %r18
  %r22 = load i32, i32* %r18
  %r23 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r23, i32 %r22)
  ; Alloca variable: e of type int
  %r24 = alloca i32, align 4
  %r25 = add i32 0, 2
  %r26 = add i32 0, 3
  %r27 = add i32 0, 4
  %r28 = mul i32 %r26, %r27
  %r29 = add i32 %r25, %r28
  store i32 %r29, i32* %r24
  %r30 = load i32, i32* %r24
  %r31 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r31, i32 %r30)
  ; Alloca variable: f of type int
  %r32 = alloca i32, align 4
  %r33 = add i32 0, 20
  %r34 = add i32 0, 4
  %r35 = sdiv i32 %r33, %r34
  %r36 = add i32 0, 1
  %r37 = add i32 %r35, %r36
  store i32 %r37, i32* %r32
  %r38 = load i32, i32* %r32
  %r39 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r39, i32 %r38)
  ; Alloca variable: g of type int
  %r40 = alloca i32, align 4
  %r41 = add i32 0, 1
  %r42 = add i32 0, 2
  %r43 = add i32 %r41, %r42
  %r44 = add i32 0, 3
  %r45 = add i32 %r43, %r44
  %r46 = add i32 0, 4
  %r47 = add i32 %r45, %r46
  store i32 %r47, i32* %r40
  %r48 = load i32, i32* %r40
  %r49 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r49, i32 %r48)
  ; Alloca variable: h of type int
  %r50 = alloca i32, align 4
  %r51 = add i32 0, 10
  %r52 = add i32 0, 3
  %r53 = sub i32 %r51, %r52
  %r54 = add i32 0, 2
  %r55 = sub i32 %r53, %r54
  %r56 = add i32 0, 1
  %r57 = sub i32 %r55, %r56
  store i32 %r57, i32* %r50
  %r58 = load i32, i32* %r50
  %r59 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_int, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r59, i32 %r58)
  ; Alloca variable: x of type float64
  %r60 = alloca double, align 8
  %r61 = fadd double 0.0, 5.5000000000000000
  %r62 = fadd double 0.0, 2.3000000000000000
  %r63 = fadd double %r61, %r62
  store double %r63, double* %r60
  %r64 = load double, double* %r60
  %r65 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r65, double %r64)
  ; Alloca variable: y of type float64
  %r66 = alloca double, align 8
  %r67 = fadd double 0.0, 10.700000000000000
  %r68 = fadd double 0.0, 3.2000000000000000
  %r69 = fsub double %r67, %r68
  store double %r69, double* %r66
  %r70 = load double, double* %r66
  %r71 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r71, double %r70)
  ; Alloca variable: z of type float64
  %r72 = alloca double, align 8
  %r73 = fadd double 0.0, 4.5000000000000000
  %r74 = fadd double 0.0, 2.0000000000000000
  %r75 = fmul double %r73, %r74
  store double %r75, double* %r72
  %r76 = load double, double* %r72
  %r77 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r77, double %r76)
  ; Alloca variable: w of type float64
  %r78 = alloca double, align 8
  %r79 = fadd double 0.0, 15.600000000000000
  %r80 = fadd double 0.0, 3.0000000000000000
  %r81 = fdiv double %r79, %r80
  store double %r81, double* %r78
  %r82 = load double, double* %r78
  %r83 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r83, double %r82)
  ; Alloca variable: p of type float64
  %r84 = alloca double, align 8
  %r85 = fadd double 0.0, 2.5000000000000000
  %r86 = fadd double 0.0, 3.0000000000000000
  %r87 = fadd double 0.0, 4.0000000000000000
  %r88 = fmul double %r86, %r87
  %r89 = fadd double %r85, %r88
  store double %r89, double* %r84
  %r90 = load double, double* %r84
  %r91 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r91, double %r90)
  ; Alloca variable: q of type float64
  %r92 = alloca double, align 8
  %r93 = fadd double 0.0, 20.000000000000000
  %r94 = fadd double 0.0, 4.0000000000000000
  %r95 = fdiv double %r93, %r94
  %r96 = fadd double 0.0, 1.5000000000000000
  %r97 = fadd double %r95, %r96
  store double %r97, double* %r92
  %r98 = load double, double* %r92
  %r99 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r99, double %r98)
  ; Alloca variable: r of type float64
  %r100 = alloca double, align 8
  %r101 = fadd double 0.0, 1.1000000000000000
  %r102 = fadd double 0.0, 2.2000000000000000
  %r103 = fadd double %r101, %r102
  %r104 = fadd double 0.0, 3.3000000000000000
  %r105 = fadd double %r103, %r104
  store double %r105, double* %r100
  %r106 = load double, double* %r100
  %r107 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r107, double %r106)
  ; Alloca variable: s of type float64
  %r108 = alloca double, align 8
  %r109 = fadd double 0.0, 3.5000000000000000
  %r110 = fadd double 0.0, 0.80000000000000000
  %r111 = fsub double %r109, %r110
  %r112 = fadd double 0.0, 1.5000000000000000
  %r113 = fsub double %r111, %r112
  store double %r113, double* %r108
  %r114 = load double, double* %r108
  %r115 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r115, double %r114)
  ; Alloca variable: t of type float64
  %r116 = alloca double, align 8
  %r117 = fadd double 0.0, 2.0000000000000000
  %r118 = fadd double 0.0, 3.1400000000000000
  %r119 = fmul double %r117, %r118
  %r120 = fadd double 0.0, 0.50000000000000000
  %r121 = fmul double %r119, %r120
  store double %r121, double* %r116
  %r122 = load double, double* %r116
  %r123 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r123, double %r122)
  ; Alloca variable: u of type float64
  %r124 = alloca double, align 8
  %r125 = fadd double 0.0, 3.1400000000000000
  %r126 = fadd double 0.0, 2.0000000000000000
  %r127 = fdiv double %r125, %r126
  %r128 = fadd double 0.0, 0.50000000000000000
  %r129 = fdiv double %r127, %r128
  store double %r129, double* %r124
  %r130 = load double, double* %r124
  %r131 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r131, double %r130)
  ; Alloca variable: fa of type float32
  %r132 = alloca float, align 4
  %r133 = fadd float 0.0, 5.5000000000000000
  %r134 = fadd float 0.0, 2.2999999523162840
  %r135 = fadd float %r133, %r134
  store float %r135, float* %r132
  %r136 = load float, float* %r132
  %r137 = fpext float %r136 to double
  %r138 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r138, double %r137)
  ; Alloca variable: fb of type float32
  %r139 = alloca float, align 4
  %r140 = fadd float 0.0, 10.699999809265137
  %r141 = fadd float 0.0, 3.2000000476837160
  %r142 = fsub float %r140, %r141
  store float %r142, float* %r139
  %r143 = load float, float* %r139
  %r144 = fpext float %r143 to double
  %r145 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r145, double %r144)
  ; Alloca variable: fc of type float32
  %r146 = alloca float, align 4
  %r147 = fadd float 0.0, 4.5000000000000000
  %r148 = fadd float 0.0, 2.0000000000000000
  %r149 = fmul float %r147, %r148
  store float %r149, float* %r146
  %r150 = load float, float* %r146
  %r151 = fpext float %r150 to double
  %r152 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r152, double %r151)
  ; Alloca variable: fd of type float32
  %r153 = alloca float, align 4
  %r154 = fadd float 0.0, 15.600000381469727
  %r155 = fadd float 0.0, 3.0000000000000000
  %r156 = fdiv float %r154, %r155
  store float %r156, float* %r153
  %r157 = load float, float* %r153
  %r158 = fpext float %r157 to double
  %r159 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r159, double %r158)
  ; Alloca variable: fe of type float32
  %r160 = alloca float, align 4
  %r161 = fadd float 0.0, 2.5000000000000000
  %r162 = fadd float 0.0, 3.0000000000000000
  %r163 = fadd float 0.0, 4.0000000000000000
  %r164 = fmul float %r162, %r163
  %r165 = fadd float %r161, %r164
  store float %r165, float* %r160
  %r166 = load float, float* %r160
  %r167 = fpext float %r166 to double
  %r168 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r168, double %r167)
  ; Alloca variable: ff of type float32
  %r169 = alloca float, align 4
  %r170 = fadd float 0.0, 20.000000000000000
  %r171 = fadd float 0.0, 4.0000000000000000
  %r172 = fdiv float %r170, %r171
  %r173 = fadd float 0.0, 1.5000000000000000
  %r174 = fadd float %r172, %r173
  store float %r174, float* %r169
  %r175 = load float, float* %r169
  %r176 = fpext float %r175 to double
  %r177 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r177, double %r176)
  ; Alloca variable: fg of type float32
  %r178 = alloca float, align 4
  %r179 = fadd float 0.0, 1.1000000238418580
  %r180 = fadd float 0.0, 2.2000000476837160
  %r181 = fadd float %r179, %r180
  %r182 = fadd float 0.0, 3.2999999523162840
  %r183 = fadd float %r181, %r182
  store float %r183, float* %r178
  %r184 = load float, float* %r178
  %r185 = fpext float %r184 to double
  %r186 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r186, double %r185)
  ; Alloca variable: fh of type float32
  %r187 = alloca float, align 4
  %r188 = fadd float 0.0, 3.5000000000000000
  %r189 = fadd float 0.0, 0.80000001192092900
  %r190 = fsub float %r188, %r189
  %r191 = fadd float 0.0, 1.5000000000000000
  %r192 = fsub float %r190, %r191
  store float %r192, float* %r187
  %r193 = load float, float* %r187
  %r194 = fpext float %r193 to double
  %r195 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r195, double %r194)
  ; Alloca variable: fi of type float32
  %r196 = alloca float, align 4
  %r197 = fadd float 0.0, 2.0000000000000000
  %r198 = fadd float 0.0, 3.1400001049041750
  %r199 = fmul float %r197, %r198
  %r200 = fadd float 0.0, 0.50000000000000000
  %r201 = fmul float %r199, %r200
  store float %r201, float* %r196
  %r202 = load float, float* %r196
  %r203 = fpext float %r202 to double
  %r204 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r204, double %r203)
  ; Alloca variable: fj of type float32
  %r205 = alloca float, align 4
  %r206 = fadd float 0.0, 3.1400001049041750
  %r207 = fadd float 0.0, 2.0000000000000000
  %r208 = fdiv float %r206, %r207
  %r209 = fadd float 0.0, 0.50000000000000000
  %r210 = fdiv float %r208, %r209
  store float %r210, float* %r205
  %r211 = load float, float* %r205
  %r212 = fpext float %r211 to double
  %r213 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_float64, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r213, double %r212)
  %r214 = fadd float 0.0, 3.1400001049041750
  %r215 = fadd float 0.0, 2.0000000000000000
  %r216 = fcmp olt float %r214, %r215
  ; if condition: r216
  br i1 %r216, label %then_0, label %endif_1
  then_0:
  %r217 = getelementptr inbounds [3 x i8], [3 x i8]* @.str0, i32 0, i32 0
  %r218 = getelementptr inbounds [4 x i8], [4 x i8]* @.str_fmt_str, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* %r218, i8* %r217)
  br label %endif_1
  endif_1:
  ret i32 0
}
