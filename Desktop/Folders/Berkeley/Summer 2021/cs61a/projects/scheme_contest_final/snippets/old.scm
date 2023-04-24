;;; Scheme Recursive Art Contest Entry
;;;
;;; Please do not include your name or personal info in this file.
;;;
;;; Title: re:denero
;;;
;;; Description:
;;;   recursion is hard but its ok
;;;   shh papas warm embrace
;;;   no tears only papa now

(define (repeat k fn) (if (> k 0)
    (begin (fn) (repeat (- k 1) fn)) nil))

(define unit 300)

(define colors (list "#199524" "#055A24" "#005F92" "#073772" "#CF874E" "#B3D0DD"))

; Siers
(define (sier d k shade)
    (color shade)
    (begin_fill)
    (repeat 3 (lambda () (if (= k 1) (fd d) (leg d k shade)) (right 120)))
    (end_fill)
)

(define (leg d k shade)
      (sier (/ d 2) (- k 1) shade)
      (forward d)
      (pendown))

; Hexagon
(define (hexagon size colors)
    (sier size 2 colors)
    (left 60)
    (sier size 1 shade2)
    (left 60)
    (sier size 1 shade3)
    (left 60)
    (sier size 1 shade1)
    (left 60)
    (sier size 1 shade2)
    (left 60)
    (sier size 1 shade3)
    (left 60)
)


(define (sier-hexagon size level shade1 shade2 shade3 shade4 shade5 shade6)
    (penup)
    (backward size)
    (pendown)
    (hexagon size shade1 shade2 shade3)
    (penup)
    (forward size)
    (if (> level 1)
        (repeat 3 (lambda ()
                     (sier-hexagon (/ size 2) (- level 1) shade4 shade5 shade6 shade1 shade2 shade3)
                     (repeat 2 (lambda ()
                          (right 120)
                          (forward size)
                          (right 60)
                          (forward size)
                          (left 60)
                          ))))))

(define (stamp-row k x y)
    (cond
        ((> k 4)
            nil
        )
        (else
            (goto x y)
            (stamp)
            (stamp-row (+ k 1) (+ x 200) y)
        )
    )
)

(define (new-row k x y)
    (cond
        ((> k 5)
            nil
        )
        (else
            (stamp-row 0 x y)
            (new-row (+ k 1) x (- y 160))
        )
    )
)
(define (draw)
  (pu)
  (addshape 'denero.gif)
  (new-row 0 (- 300) 300)
  (goto 0 0)
  (pd)
  (forward unit)
  (sier-hexagon unit 4 "#199524" "#055A24" "#005F92" "#073772" "#CF874E" "#B3D0DD")
  (backward (/ unit 2))
  (sier-hexagon (/ unit 2) 5 "#262525" "#083068" "#FFBD07" "#062A5B" "#FFBD07" "#04295B")
  (backward (/ unit 2))
  (reg-hexagon (/ unit 16) "#BA1F1F" "#C91A1A" "#FF1616")
  (exitonclick))

; Please leave this last line alone.  You may add additional procedures above
; this line.
(draw)
