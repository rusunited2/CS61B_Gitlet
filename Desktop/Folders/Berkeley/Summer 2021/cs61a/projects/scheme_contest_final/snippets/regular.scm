
(define (repeat k fn) (if (> k 0)
    (begin (fn) (repeat (- k 1) fn)) nil))

(define (hexagon length)
    (pendown)
    (repeat 6 (lambda ()
                 (fd length)
                 (lt 60)))
    (penup))

(define (hexagons length levels)
    (hexagon length)   ;; Draw big hexagon
    (if (> levels 1)
        (repeat 3 (lambda ()
                     (hexagons (/ length 2) (- levels 1))  ;; Then smaller ones,
                     (repeat 2 (lambda ()                  ;; move to next corner
                          (fd length)
                          (lt 60)))))))

(hexagons 100 3)
