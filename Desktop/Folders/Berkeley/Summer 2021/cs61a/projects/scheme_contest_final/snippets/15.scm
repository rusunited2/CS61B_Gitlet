;;; Scheme Recursive Art Contest Entry
;;;
;;; Please do not include your name or personal info in this file.
;;;
;;; Title: Isometrics
;;;
;;; Description:
;;;    Many hexagons
;;;    With layers of filled colors
;;;    Make isometrics

(define (hax-mod d k lst)
    (color (car lst))
    (begin_fill)
    (repeat leg 6)
    (end_fill)
    (if (= k 0) nil (repeat subhax 3)))

(define repeat (mu (f n) (f) (if (> n 1) (repeat f (- n 1)))))

(define subhax (mu () (hax-mod (/ d 2) (- k 1) (cdr lst)) (repeat leg 2)))

(define leg (mu () (forward d) (left 60)))

(define (draw)
    (pu)
    (ht)
    (speed 0)
    (goto 384 0)
    (color "#000000")
    (begin_fill) (circle 384) (end_fill)
    (circle 384 -30)
    (seth 0)
    (hax-mod 384 5 (list "#181818" "#041e42" "#ffc72c" "#041e42" "#ffc72c" "#041e42"))
    (exitonclick))

; Please leave this last line alone.  You may add additional procedures above
; this line.  All Scheme tokens in this file (including the one below) count
; toward the token limit.
(draw)
