(ns side-scroll-1.core
  (:require))

(enable-console-print!)

(def canvas (.getElementById js/document "app"))
(def ctx (.getContext canvas "2d"))

(def ball-radius 10)
(def h (- (. canvas -height) ball-radius))
(def w (- (. canvas -width) ball-radius))

(def t 0)
(def dt 1)
;;(def g 9.8)
(def g 0.4)
(def dx 5)
(def dy 10)

(def right-pressed false)
(def left-pressed false)
(def down-pressed false)
(def up-pressed false)

(def key-codes {:space "Space"
                :right "ArrowRight"
                :left "ArrowLeft"
                :up "ArrowUp"
                :down "ArrowDown"})

(def is-jump false)

(def MAP [[0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
          [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
          [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
          [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1]
          [0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1]
          [0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1]
          [0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1]
          [0 0 0 0 1 1 0 0 0 0 0 1 1 1 1 1]
          [0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1]
          [0 0 1 0 0 0 0 0 0 1 1 1 1 1 1 1]
          [1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1]])

(def block-row-count (count MAP))
(def block-column-count (count (first MAP)))
(def block-width 30)

(def y (- h block-width))
(def x ball-radius)

(defn key-down-handler [e]
  (let [pressed (. e -code)]
    (condp = pressed
      (:right key-codes) (set! right-pressed true)
      (:left key-codes) (set! left-pressed true)
      (:down key-codes) (set! down-pressed true)
      (:up key-codes) (set! up-pressed true)
      (:space key-codes) (set! up-pressed true)
      nil)))

(defn key-up-handler [e]
  (let [pressed (. e -code)]
    (condp = pressed
      (:right key-codes) (set! right-pressed false)
      (:left key-codes) (set! left-pressed false)
      (:down key-codes) (set! down-pressed false)
      (:up key-codes) (set! up-pressed false)
      (:space key-codes) (set! up-pressed false)
      nil)))

(defn draw-ball []
  (.beginPath ctx)
  (.arc ctx x y ball-radius 0 (* Math.PI 2) false)
  (aset ctx "fillStyle" "#0095DD")
  (.fill ctx)
  (.closePath ctx))

;; (defn draw-block []
;;   (.beginPath ctx)
;;   (.rect ctx 450 450 30 30)
;;   ;;(.rect ctx (/ w 2) (- (+ h ball-radius) 40) 40 40)
;;   (aset ctx "fillStyle" "#8B4513")
;;   (.fill ctx)
;;   (.closePath ctx))

(defn draw-map []
  (dotimes [c block-column-count]
    (dotimes [r block-row-count]
      (if (= 1 (get-in MAP [r c]))
        (do
          (let [block-x (* c block-width)
                block-y (* r block-width)]
            (.beginPath ctx)
            (.rect ctx block-x block-y block-width block-width)
            (aset ctx "fillStyle" "#8B4513")
            (.fill ctx)
            (.closePath ctx)))))))


(defn is-collision [x y]
  (= 1 (get-in MAP [y x])))

(defn get-height [current]
  (if (= current 1)
    (let [current-y (int (/ (+ y block-width) block-width))]
      (println "current current-y: " current-y)
      (println "current height: " (- block-row-count current-y))
      (- block-row-count current-y))
    1))


(defn draw []
  (.clearRect ctx 0 0 (. canvas -width) (. canvas -height))
  (draw-ball)
  (draw-map)

  (if (< y ball-radius)
    (do (*print-fn* "****** ue **********")
        (set! y ball-radius)))

  (if (< x ball-radius)
    (set! x ball-radius))

  (if (< w x)
    (set! x w))

  (if (true? right-pressed)
    (let [xt (+ x dx)]
      (if (not (is-collision (int (/ (+ xt ball-radius)  block-width))
                             (int (/ y block-width))))
        (set! x xt))))

  (if (true? left-pressed)
    (let [xt (- x dx)]
      (if (not (is-collision (int (/ (- xt ball-radius)  block-width))
                             (int (/ y block-width))))
        (set! x xt))))

  (if (and (true? up-pressed)
           (< ball-radius y)
           (false? is-jump)
           )
    (do
      (*print-fn* "↑up pressed!" y)
      (set! t 0)
      (set! is-jump true)))

  (if (true? down-pressed)
    (let [yt (+ y dy)]
      (if(not (is-collision (int (/ x  block-width))
                            (int (/ yt block-width))))
        (set! y yt))))

  ;; t が増えると y も増える
  ;; -> jump のときだけ t が増えてほしい
  (if (true? is-jump)
    ;;(println "is-jump: " is-jump)
    (let [current (get-in MAP [(int (/ (+ y block-width) block-width)) (int (/ x block-width))])
          yt (+ (- (* (/ 1 2) g (* t t)) (* dy t)) (- h (* (get-height current) block-width)) )]
      (if (not (is-collision (int (/ x block-width))
                             (int (/ yt block-width))))
        (do
          (println "++++ jumping!!!!")
          (println (get-height current))
          (set! y yt))
        (do
          (println "---- stopping!!!")
          (set! is-jump false)))))
  (set! t (+ t dt))
  (js/requestAnimationFrame draw))

(js/addEventListener "keydown" key-down-handler false)
(js/addEventListener "keyup" key-up-handler false)
(draw)
