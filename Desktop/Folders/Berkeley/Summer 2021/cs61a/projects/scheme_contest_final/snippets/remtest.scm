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

(define colors (list "#36393A" "#3E6990" "#CC7178" "#E9E3B4"))

(define (triangle fn size shade)
    (color shade)
    (begin_fill)
    (repeat 3 (lambda () (fn) (right 120)))
    (end_fill)
)

; Siers
(define (sier d k shade)
  (triangle
      (lambda () (if (= k 1) (fd d) (leg d k shade)))
      d
      shade))

(define (leg d k shade)
      (sier (/ d 2) (- k 1) shade)
      (penup)
      (fd d)
      (pendown))

; Hexagon
(define (reg-hex size shade1 shade2 shade3)
    (sier size 1 shade1)
    (left 60)
    (sier size 1 shade2)
    (left 60)
    (sier size 1 shade3)
    (left 60)
)

(define (weird-hex size shade1 shade2 shade3)
    (sier size 2 shade1)
    (left 60)
    (sier size 1 shade2)
    (left 60)
    (sier size 1 shade3)
    (left 60)
)

(define (hexagon size shade1 shade2 shade3)
    (weird-hex size shade1 shade2 shade3)
    (reg-hex size shade1 shade2 shade3)
)

(define (reg-hexagon size shade1 shade2 shade3)
    (reg-hex size shade1 shade2 shade3)
    (reg-hex size shade1 shade2 shade3)
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

(define (draw)
  (speed 0)
  (forward unit)
  (ht)
  (bgpic 'denero.gif)
  (sier-hexagon unit 4 "#199524" "#055A24" "#005F92" "#073772" "#CF874E" "#B3D0DD")
  (backward (/ unit 2))
  (sier-hexagon (/ unit 2) 5 "#262525" "#083068" "#FFBD07" "#062A5B" "#FFBD07" "#04295B")
  (backward (/ unit 2))
  (reg-hexagon (/ unit 16) "#BA1F1F" "#C91A1A" "#FF1616")
  (exitonclick))

; Please leave this last line alone.  You may add additional procedures above
; this line.
(draw)
