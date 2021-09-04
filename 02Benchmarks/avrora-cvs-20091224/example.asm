start:
	jmp begin		; entrypoint of program

.org 0x80			; skip interrupt table
bad_interrupt:
	reti

begin:				; begin main program
	call wait		; wait before we get started
	ldi r17, 1
	ldi r18, 2
	ldi r19, 1
	ldi r20, 10
loop:				; beginning of fibonacci loop
	mov r21, r17
	add r21, r18
	mov r17, r18
	mov r18, r21
	inc r19			; increment trip count
	cpi r19, 10
	breq again		; do it again?
	jmp loop

again:	
	call wait		; wait for a little bit
	call wait		; wait again
	jmp begin		; do it again

wait:
	ldi r16, 1
	inc r16
	cpi r16, 0
	brne wait
	
	ret