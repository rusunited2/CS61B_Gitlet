(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

; Some utility functions that you may find useful to implement

(define (zip pairs) (list (map (lambda (x) (car x))    pairs) (map (lambda (x) (cadr x))    pairs)))


;; Problem 16
;; Returns a list of two-element lists
(define (enumerate s)
  ; BEGIN PROBLEM 16
  (define (helper s index sofar)
    (if (null? s) sofar (helper (cdr s) (+ index 1) (append sofar (list (list index (car s))))))
  )
  (helper s 0 nil)
  )
  ; END PROBLEM 16

;; Problem 17

;; Merge two lists LIST1 and LIST2 according to COMP and return
;; the merged lists.
(define (merge comp list1 list2)
  ; BEGIN PROBLEM 17
  (define (helper comp list1 list2 sofar)
    (if (or (null? list1) (null? list2)) (if (null? list1) (append sofar list2) (append sofar list1)) (helper comp (cdr list1) (cdr list2) (if (comp (car list1) (car list2)) (append sofar (list (car list1) (car list2))) (append sofar (list (car list2) (car list1)))   )))
  )
  (helper comp list1 list2 nil)
  )
  ; END PROBLEM 17


;; Problem 18

;; Returns a function that checks if an expression is the special form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (let-to-lambda expr)
  (cond ((atom? expr)
         ; BEGIN PROBLEM 18
          expr
         ; END PROBLEM 18
         )
        ((quoted? expr)
         ; BEGIN PROBLEM 18
         (quasiquote (unquote expr))
         ; END PROBLEM 18
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 18
            (cons form (cons params (map let-to-lambda body)))
            ;(cons form (cons params (if (null? (cdr body)) (list (let-to-lambda (car body))) (cons (car body) (list (let-to-lambda (cadr body)))))))
           ; END PROBLEM 18
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 18 
           (cons (cons 'lambda (cons (car (zip values)) (append (map let-to-lambda body) nil))) (map let-to-lambda (cadr (zip values))))
           ; ((a b) ((let ...) 2))
           ;((let ...) 2)
           ;(lambda (car (zip values)) (scheme_eval body))
           
           ; END PROBLEM 18
           ))
        (else
         ; BEGIN PROBLEM 18
         (map let-to-lambda expr)
         ; END PROBLEM 18
         )))

