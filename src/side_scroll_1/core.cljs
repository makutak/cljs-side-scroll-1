(ns ^:figwheel-hooks side-scroll-1.core
  (:require
   [goog.dom :as gdom]))

(enable-console-print!)

(def canvas (.getElementById js/document "app"))
(def ctx (.getContext canvas "2d"))

(def ball-radius 10)
(def x ball-radius)
(def y (- (. canvas -height) ball-radius))


(def t 0)
(def dt 1)
(def v 10)
;;(def g 9.8)
(def g 0.4)
(def dx 10)
(def dy 10)

(def xt 0)
(def yt 0)

(defn draw-ball []
  (.beginPath ctx)
  (.arc ctx xt yt ball-radius 0 (* Math.PI 2) false)
  (aset ctx "fillStyle" "#0095DD")
  (.fill ctx)
  (.closePath ctx))

(defn draw []
  (.clearRect ctx 0 0 (. canvas -width) (. canvas -height))
  (draw-ball)
  (set! t (+ t dt))
  (set! xt (* dx t))
  (set! yt (+ (- (* (/ 1 2) g (* t t)) (* dy t)) y))
  (js/requestAnimationFrame draw))
(draw)
