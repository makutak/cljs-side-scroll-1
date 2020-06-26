(ns side-scroll-1.core
  (:require))

(enable-console-print!)

(def canvas (.getElementById js/document "app"))
(def ctx (.getContext canvas "2d"))

(def ball-radius 10)
(def x ball-radius)
(def h (- (. canvas -height) ball-radius))
(def y h)


(def t 0)
(def dt 1)
(def v 10)
;;(def g 9.8)
(def g 0.4)
(def dx 10)
(def dy 10)

(def right-pressed false)
(def left-pressed false)
(def space-pressed false)

(defn key-down-handler [e]
  (let [pressed (. e -code)]
    (if (= "Space" pressed)
      (set! space-pressed true))
    (if (or (= "Right" pressed)
            (= "ArrowRight" pressed))
      (set! right-pressed true))
    (if (or (= "Left" pressed)
            (= "ArrowLeft" pressed))
      (set! left-pressed true))))

(defn key-up-handler [e]
  (let [pressed (. e -code)]
    (if (= "Space" pressed)
      (set! space-pressed false))
    (if (or (= "Right" pressed)
            (= "ArrowRight" pressed))
      (set! right-pressed false))
    (if (or (= "Left" pressed)
            (= "ArrowLeft" pressed))
      (set! left-pressed false))))


(defn draw-ball []
  (.beginPath ctx)
  (.arc ctx x y ball-radius 0 (* Math.PI 2) false)
  (aset ctx "fillStyle" "#0095DD")
  (.fill ctx)
  (.closePath ctx))

(defn draw []
  (.clearRect ctx 0 0 (. canvas -width) (. canvas -height))
  (draw-ball)
  (set! t (+ t dt))
  ;;(set! y (+ (- (* (/ 1 2) g (* t t)) (* dy t)) h))

  ;; 右が押されたか
  (if (true? right-pressed)
    (set! x (+ x dx)))

  ;; 左が押されたか
  (if (true? left-pressed)
    (set! x (- x dx)))

  ;; スペースが押されたか
  (if (true? space-pressed)
    (set! y (- y dy)))

  (js/requestAnimationFrame draw))

(js/addEventListener "keydown" key-down-handler false)
(js/addEventListener "keyup" key-up-handler false)
(draw)
