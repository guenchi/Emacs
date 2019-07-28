



(load-shared-object "emacs.so")

(define raw-on
  (foreign-procedure "raw_on" () int))

(define raw-off
  (foreign-procedure "raw_off" () int))

(define get-row-size
    (foreign-procedure "get_row" () int))

(define get-col-size
    (foreign-procedure "get_col" () int))


(define *text* (cons (cons (cons #\x00 0) '()) '()))
(define *acts* (cons (cons (cons (cons '() '()) (cons '() '())) '()) '()))

(define row-size (get-row-size))
(define col-size (get-col-size))


(define update-row-size 
  (lambda ()
    (set! row-size (get-row-size)))) 


(define update-col-size 
  (lambda ()
    (set! col-size (get-col-size)))) 



(define row-
  (lambda (r)
    (if (> r 1)
        (- r 1)
        r)))

(define row+
  (lambda (r)
    (if (< r row-size)
        (+ r 1)
        r)))



(define col+
  (lambda ()
    (if (< (col) (col-size))
        (set-col! (+ (col) 1))
        (begin
          (lines+)
          (set-col! 1)
          (set-row! (+ (row) 1))))))


(define col-
  (lambda ()
    (case (col)
      (1
        (lines-)
        (set-col! (col-size))
        (set-row! (- (row) 1)))
      (else
        (set-col! (- (col) 1))))))


(define ioctl
  (lambda keys
    (display #\esc)
    (display #\[)
    (let loop ((l keys))
      (if (not (null? l))
          (begin 
            (display (car l))
            (loop (cdr l)))))))


(define auto-info
  (lambda ()
    (move-to (footer) 1)
    (set-tbgcolor 'white)
    (display " *SchEmacs* [ ")
    (display (row))
    (display ", ")
    (display (col))
    (display " ] L")
    (display (line))
    (display " / ")
    (display (lines))
    (clean-line)
    (set-tbgcolor 'black)))


(define message
  (case-lambda
    (()
      (auto-info)
      (move-to (row-size) 1)
      (clean-line)
      (move-to (row) (col)))
    ((str)
      (auto-info)
      (move-to (row-size) 1)
      (display str)
      (clean-line)
      (move-to (row) (col)))))
  

        
(define clean-screem
  (lambda ()
    (ioctl #\2 #\J)))

(define clean-line
  (lambda ()
    (ioctl #\K)))

(define init-mouse
  (lambda ()
    (ioctl #\0 #\; #\0 #\H)))



(define quit
  (lambda ()
    (raw-off)
    (clean-screem)
    (init-mouse)
    (exit)))

(define start
  (lambda ()
    (clean-screem)
    (init-mouse)
    (set! *text* (cons (cons (cons #\x00 0) '()) '()))
    (set! *acts* (cons (cons (cons (cons '() '()) (cons '() '())) '()) '()))
    (set-row-size! (get-row-size))
    (set-col-size! (get-col-size))
    (set-row! 1)
    (set-col! 1)
    (set-line! 1)
    (set-lines! 1)
    (set-footer! (- (row-size) 1))))


(define set-txtcolor
  (lambda (c)
    (ioctl
      (case c
        ('black 30)
        ('red 31)
        ('green 32)
        ('yellow 33)
        ('blue 34)
        ('purple 35)
        ('dark-green 36)
        ('white 37)) #\m)))


(define set-tbgcolor
  (lambda (c)
    (ioctl
      (case c
        ('black 40)
        ('red 41)
        ('green 42)
        ('yellow 43)
        ('blue 44)
        ('purple 45)
        ('dark-green 46)
        ('white 47)) #\m)))
  


(define welcome
  (lambda ()
    (printf 
"Welcome to SchEmacs.

    ╔══════╗ ╔═════╗╔═╗  ╔═╗╔══════╗╔══╗   ╔══╗╔══════╗
    ║ ╔════╝╔ ╔════╝║ ║  ║ ║║ ╔════╝║  ╚╗ ╔╝  ║║ ╔════╝
    ║ ╚════╗║ ║     ║ ╚══╝ ║║ ╚══╗  ║ ╔╗╚ ╝╔╗ ║║ ╚══╗  
    ╚════╗ ║║ ║     ║ ╔══╗ ║║ ╔══╝  ║ ║╚╗ ╔╝║ ║║ ╔══╝  
    ╔════╝ ║╚ ╚════╗║ ║  ║ ║███████╗███╗╚═╝███╗║█████╗╗ ██████╗███████╗
    ╚══════╝ ╚═════╝╚═╝  ╚═╝██╔════╝████╗ ████║██╔══██╗██╔════╝██╔════╝
                            █████╗  ██╔████╔██║███████║██║     ███████╗
                            ██╔══╝  ██║╚██╔╝██║██╔══██║██║     ╚════██║
                            ███████╗██║ ╚═╝ ██║██║  ██║╚██████╗███████║
                            ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝ ╚═════╝╚══════╝
                                                               


                                                 Author  guenchi@Github
                                                    MIT  License



  To start: C-x C-f
  To quit:  C-x C-c")))



(define previous cdar)
(define next cdr)
(define payload caaar)

(define set-payload! 
  (lambda (t p)
    (set-car! (caar t) p)))

(define position cdaar)

(define set-position! 
  (lambda (t p)
    (set-cdr! (caar t) p)))

(define conbine! set-cdr!)

(define retrace! 
  (lambda (rest pre)
    (set-cdr! (car rest) pre)))


(define action
  (lambda (act a c)
    (define i (cons (cons (cons (cons (line) (col)) (cons a c)) act) '()))
      (set-cdr! act i)))


(define loc-info caaar)
(define acts-info cdaar)


(define line-info
  (lambda (act)
    (car (loc-info act))))


(define col-info
  (lambda (act)
    (cdr (loc-info act))))


(define act-info
  (lambda (act)
    (car (acts-info act))))


(define char-info
  (lambda (act)
    (cdr (acts-info act))))


(define undo-insert
  (lambda (txt act r c l t i)
    (define rest (next txt))
    (define t (cons (cons (cons i (col)) txt) rest))
    (conbine! txt t)
    (case i
      (#\newline
        (lines+)
        (set-col! 1)
        (clean-line)))
    (move-to (row) (col))
    (if (null? rest)   
      (display i)
      (begin 
        (retrace! rest t)
        (display i)
        (col+)
        (update-insert rest)))
    (input-loop t (previous act))))



(define undo-delete
  (lambda (txt act r c l t i)
    (define pre (previous txt))
    (define rest (next txt))
        (case i
          (#\newline
            (conbine! pre rest)
            (move-to (row) (col))
            (set-lines! (- (lines) 1))
            (if (null? rest)
                (begin 
                  (row-)
                  (set-col! (+ (position pre) 1)))
                (begin
                  (retrace! rest pre)
                  (clean-line)
                  (row-)
                  (set-col! (+ (position pre) 1))
                  (update-delete rest))))
          (else
            (conbine! pre rest)
            (move-to (row) (+ (col) 1))
            (display #\backspace)
            (display #\space)
            (display #\backspace)
            (if (not (null? rest))
                  (begin 
                  (retrace! rest pre)
                  (update-delete rest)))))
        (input-loop pre (previous act))))


(define undo
  (lambda (txt act r c l t)
    (define l (line-info act))
    (define c (col-info act))
    (define up
      (lambda (k)
        (if (or (equal? (payload k) #\newline) 
                (= (position k) 80))
            (begin 
              (row-)
              (line-)))))
    (define down
      (lambda (k)
        (if (or (equal? (payload k) #\newline) 
                (= (position k) 80))
            (begin
              (row+)
              (line+)))))
    (define p1
      (lambda (t)
        (if (act-info act)
          (position t)
          (- (position t) 1))))
    (define p2
      (lambda (t)
        (if (act-info act)
          (position t)
          (+ (position t) 1))))
    (let loop ((t txt))
      (cond 
        ((< l (line))
           (up t)
           (loop (previous t)))
        ((> l (line))
           (down t)
           (loop (next t)))
        ((< c (p1 t))
           (loop (previous t)))
        ((> c (p2 t))
           (loop (next t)))
        (else
          (set-col! c)
          (if (act-info act)
              (undo-delete t act (char-info act))
              (undo-insert t act (char-info act))))))))


(define redo-insert
  (lambda (txt act r c l t i)
    (define pre (previous txt))
    (define t (cons (cons (cons i (col)) pre) txt))
    (conbine! pre t)
    (case i
      (#\newline
        (row+)
        (lines+)
        (set-col! 1)
        (clean-line)))
    (move-to (row) (col))
    (if (null? txt)   
      (display i)
      (begin 
        (retrace! txt t)
        (display i)
        (col+)
        (update-insert txt)))
    (input-loop t act)))



(define redo-delete
  (lambda (txt act r c l t i)
    (define pre (previous txt))
    (define rest (next txt))
        (case i
          (#\newline
            (conbine! pre rest)
            (move-to (row) (col))
            (lines-)
            (if (null? rest)
                (begin 
                  (row-)
                  (set-col! (+ (position pre) 1)))
                (begin
                  (retrace! rest pre)
                  (clean-line)
                  (row-)
                  (set-col! (+ (position pre) 1))
                  (update-delete rest))))
          (else
            (conbine! pre rest)
            (move-to (row) (+ (col) 1))
            (display #\backspace)
            (display #\space)
            (display #\backspace)
            (if (not (null? rest))
                (begin
                  (retrace! rest pre)
                  (update-delete rest)))))
    (input-loop pre act)))




(define redo
  (lambda (txt act r c l t)
    (define n (next act))
    (define l (line-info n))
    (define c (col-info n))
    (define up
      (lambda (k)
        (if (or (equal? (payload k) #\newline) 
                (= (position k) 80))
            (begin 
              (row-)
              (line-)))))
    (define down
      (lambda (k)
        (if (or (equal? (payload k) #\newline) 
                (= (position k) 80))
            (begin
              (row+)
              (line+)))))
    (let loop ((t txt))
      (cond 
        ((< l (line))
           (up t)
           (loop (previous t)))
        ((> l (line))
           (down t)
           (loop (next t)))
        ((< c (position t))
           (loop (previous t)))
        ((> c (position t))
           (loop (next t)))
        (else
          (set-col! c)
          (if (act-info n)
              (redo-insert t act (char-info n))
              (redo-delete t act (char-info n))))))))



(define move-up
  (case-lambda 
    (()(ioctl #\A))
    ((n)(ioctl n #\A))))

(define move-down
  (case-lambda 
    (()(ioctl #\B))
    ((n)(ioctl n #\B))))

(define move-right
  (case-lambda 
    (()(ioctl #\C))
    ((n)(ioctl n #\C))))

(define move-left
  (case-lambda 
    (()(ioctl #\D))
    ((n)(ioctl n #\D))))


(define move-to
  (lambda (r c)
    (ioctl r #\; c #\H)))



(define write-out
  (lambda (x)
    (if (not (null? x))
        (begin
          (display (caaar x))
          (write-out (next x))))))


(define next-position
  (lambda (txt c)
    (case (payload txt)
      (#\newline
        1)
      (else
        (if (= c (col-size))
            1 
            (+ 1 c))))))



(define update-insert
  (lambda (txt)
    (let l ((t txt)
            (c (col)))
      (if (not (null? t))
          (begin
            (case (payload t)
              (#\newline 
                (clean-line)))
            (display (payload t))
            (set-position! t c)
            (l (next t) (next-position t c)))))
    (move-to (row) (col))))


(define update-delete
  (lambda (txt)
    (let l ((t txt)
            (c (col)))
      (if (or (null? t) 
              (equal? (payload t) 
                      #\newline))
          (display #\space)
          (begin
            (display (payload t))
            (set-position! t c)
            (l (next t) (next-position t c)))))
    (move-to (row) (col))))


(define alarm
  (lambda (txt act r c l t)
    (display #\alarm)
    (message "Operating fail~") 
    (input-loop txt act r c l t)))


(define insert
  (lambda (txt act r c l t i)
    (define rest (next txt))
    (define t (cons (cons (cons i (col)) txt) rest))    ;; << rename t
    (action act #t i)
    (conbine! txt t)
    (case i
      (#\newline
        (row+)
        (lines+)
        (set-col! 1)
        (clean-line))
      (else 
        (col+)))
    (if (null? rest)   
        (display i)
        (begin 
          (retrace! rest t)
          (display i)
          (update-insert rest)))
    (message) 
    (input-loop t (next act))))


(define delete
  (lambda (txt act r c l t)
    (define pre (previous txt))
    (define rest (next txt))
    (define p (payload txt))
    (if (null? pre)
        (alarm txt act r c l t)
        (case p
          (#\newline
            (conbine! pre rest)
            (lines-)
            (if (null? rest)
                (begin
                  (set-cdr! pre '())
                  (row-)
                  (set-col! (+ (position pre) 1))
                  (move-to (row) (col)))
                (begin
                  (retrace! rest pre)
                  (clean-line)
                  (row-)
                  (set-col! (+ (position pre) 1))
                  (move-to (row) (col))
                  (update-delete rest)))
            (message)
            (action act #f p)
            (input-loop pre (next act)))
          (else
            (conbine! pre rest)
            (display #\backspace)
            (display #\space)
            (if (> (col) 1)
                    (display #\backspace))
            (col-)
            (if (null? rest)
                (set-cdr! pre '())
                (begin
                  (retrace! rest pre)
                  (update-delete rest)))
            (message) 
            (action act #f p)
            (input-loop pre (next act)))))))


(define switch-row-up
  (lambda (txt)
    (let loop ((c (col-size))(t txt))
      (if (> c 0)
          (loop (- c 1)(previous t))
          t))))


(define switch-row-down
  (lambda (txt)
    (let loop ((c (col-size))(t txt))
      (if (> c 0)
          (loop (- c 1)(next t))
          t))))

(define up
  (lambda (txt act r c l t)
    (move-up)
    (input-loop (switch-row-up txt) act)))

(define down
  (lambda (txt act r c l t)
    (move-down)
    (input-loop (switch-row-up txt) act)))

(define right
  (lambda (txt act r c l t)
    (define rest (next txt))
    (if (null? rest)
        (alarm txt act r c l t)
        (begin 
          (case (payload rest)
            (#\newline
              (row+)
              (set-col! 1))
            (else
              (col+)))
          (message) 
          (move-to (row) (col))
          (input-loop rest act)))))


(define left
  (lambda (txt act r c l t)
    (define pre (previous txt))
    (if (null? pre)
        (alarm txt act r c l t)
        (begin
          (case (payload txt)
            (#\newline 
              (row-)
              (set-col! (position txt))
              (line-))
            (else
              (col-)))
          (message) 
          (move-to (row) (col))
          (input-loop pre act)))))


(load "init.sc")



(define  input-loop
  (lambda (txt act r c l t)
    (define i (read-char))
    (case i 
      (#\x01
        (message "C-a") 
        (c-a txt act r c l t))
      (#\x02
        (message "C-b Backward") 
        (left txt act r c l t))
      (#\x06
        (message "C-f Forward")
        (right txt act r c l t))
      (#\x10
        (message "C-p Previous line")
        (up txt act r c l t))
      (#\x15
        (undo txt act r c l t))
      (#\x18
        (message "C-x")  
        (case (read-char)
          (#\x03
            (quit))
          (#\x06
            (start)
            (message "C-x C-f")
            (input-loop *text* *acts*))
          (#\r
            (redo txt act r c l t))
          (#\u
            (undo txt act r c l t))
          (else
            (message "C-x : command not found")
            (input-loop txt act r c l t))))
      (#\xE
        (message "C-n Next line")
        (down txt act r c l t))
      (#\tab
        (input-loop txt))
      (#\esc
        (case (read-char)
          (#\[
            (case (read-char)
              (#\A
                (up txt act r c l t))
              (#\B
                (down txt act r c l t))
              (#\C
                (right txt act r c l t))
              (#\D
                (left txt act r c l t))))
        (#\esc
          (esc-esc txt act r c l t))))
      (#\delete
        (delete txt act r c l t))
      (else 
        (insert txt act r c l t i)))))

(let ()
  (raw-on)
  (start)
  (welcome)
  (message)        
  (input-loop *text* *acts* 1 1 1 1))




