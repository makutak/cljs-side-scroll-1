(ns side-scroll-1.core
  (:require))

(enable-console-print!)

(def canvas (.getElementById js/document "app"))
(def ctx (.getContext canvas "2d"))

(def ball-radius 10)
(def h (- (. canvas -height) ball-radius))
(def w (- (. canvas -width) ball-radius))
(def y h)
(def x ball-radius)


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
          [0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1]
          [0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1]
          [0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1]
          [0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1]])

(def block-row-count (count MAP))
(def block-column-count (count (first MAP)))
(def block-width 30)

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
          (let [block-x (* c 30)
                block-y (* r 30)]
            (.beginPath ctx)
            (.rect ctx block-x block-y block-width block-width)
            (aset ctx "fillStyle" "#8B4513")
            (.fill ctx)
            (.closePath ctx)))))))


(defn is-collision [x y]
  (= 1 (get-in MAP [x y])))

(defn draw []
  (.clearRect ctx 0 0 (. canvas -width) (. canvas -height))
  (draw-ball)
  (draw-map)
  (if (< y ball-radius)
    (do (*print-fn* "****** ue **********")
        (set! y ball-radius)))

  (if (< h y)
    (do (*print-fn* "********* sita ***********" y)
        (set! is-jump false)
        (set! y h)))

  (if (< x ball-radius)
    (set! x ball-radius))

  (if (< w x)
    (set! x w))

  (if (true? right-pressed)
    (do
      (println "x: " x)
      (println "x/30: " (/ x 30))
      (println "y/30: " (/ y 30))
      (if (not (is-collision (int (/ (+ x ball-radius) 30)) (int (/ y 30))))
        (set! x (+ x dx)))))

  (if (true? left-pressed)
    (set! x (- x dx)))

  (if (and (true? up-pressed)
           (< ball-radius y)
           (not is-jump))
    (do
      (*print-fn* "↑up pressed!" y)
      (set! t 0)
      (set! is-jump true)))

  (if (and (true? down-pressed)
           (< y h))
    (set! y (+ y dy)))

  ;; t が増えると y も増える
  ;; -> jump のときだけ t が増えてほしい
  (if (true? is-jump)
    (do (set! y (+ (- (* (/ 1 2) g (* t t)) (* dy t)) h))
        (set! t (+ t dt))))

  (js/requestAnimationFrame draw))

(js/addEventListener "keydown" key-down-handler false)
(js/addEventListener "keyup" key-up-handler false)
(draw)
