;;; Scheme Recursive Art Contest Entry
;;;
;;; Please do not include your name or personal info in this file.
;;;
;;; Title: Disciple of Alex
;;;
;;; Description:
;;;   Watched by Alex we recurse
;;;   Nothing left to reimburse
;;;   WeLoveAlex no remorse

(define (repeat k fn) (if (> k 0)
    (begin (fn) (repeat (- k 1) fn)) nil))

(define colors1 (list "#199524" "#055A24" "#005F92" "#199524" "#055A24" "#005F92"))
(define colors2 (list "#073772" "#CF874E" "#B3D0DD" "#073772" "#CF874E" "#B3D0DD"))
(define berkeley1 (list "#262525" "#083068" "#FFBD07" "#262525" "#083068" "#FFBD07"))
(define berkeley2 (list "#062A5B" "#DDA200" "#04295B" "#062A5B" "#DDA200" "#04295B"))
(define reds (list "#BA1F1F" "#BA1F1F" "#C91A1A" "#C91A1A" "#FF1616" "#FF1616"))


; Siers
(define (sier d k shade)
    (color shade)
    (begin_fill)
    (repeat 3 (lambda () (if (= k 1) (fd d) (leg d k shade)) (right 120)))
    (end_fill)
)

(define (leg d k shade)
      (sier (/ d 2) (- k 1) shade)
      (penup)
      (forward d)
      (pendown))

; Hexagon
(define (hexagon size colors legs use_sier)
    (define (hex-leg level)
        (sier size level (car colors))
        (left 60)
        (hexagon size (cdr colors) (- legs 1) #f)
    )
    (cond (use_sier
          (hex-leg 2))
          ((> legs 0)
          (hex-leg 1))
    )
)


(define (sier-hexagon size level colors1 colors2)
    (backward size)
    (pendown)
    (hexagon size colors1 6 #t)
    (penup)
    (forward size)
    (if (> level 1)
        (repeat 3 (lambda ()
                     (sier-hexagon (/ size 2) (- level 1) colors2 colors1)
                     (repeat 2 (lambda ()
                          (right 120)
                          (forward size)
                          (right 60)
                          (forward size)
                          (left 60)
                          ))))))

(define (draw)
  (hideturtle)
  (speed 0)
  (goto 1000 0)
  (color "#524a4a")
  (begin_fill) (circle 1000) (end_fill)
  (penup)
  (goto 300 0)
  (addshape 'alex.gif)
  (stamp)
  (goto (- 300) 0)
  (addshape 'alex2.gif)
  (stamp)
  (goto 0 0)
  (forward 300)
  (sier-hexagon 300 4 colors1 colors2)
  (backward (/ 300 2))
  (sier-hexagon (/ 300 2) 5 berkeley1 berkeley2)
  (backward (/ 300 2))
  (hexagon (/ 300 16) reds 6 #f)
  (exitonclick))

; Please leave this last line alone.  You may add additional procedures above
; this line.
(draw)
