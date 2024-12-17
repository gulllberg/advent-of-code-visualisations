(ns advent-of-code-visualisations.day-2024-14.view
  (:require [advent-of-code-visualisations.day-2024-14.core :as core]))

(def square-size 5)

(defn get-transform
  [[x y] [vx vy] multiple]
  (str "translate3d(" (* square-size (+ x (* core/step-size vx multiple))) "px, " (* square-size (+ y (* core/step-size vy multiple))) "px, 0px)"))

(defn view
  [state]
  [:div {:style {:margin   "20px"
                 :position "relative"
                 :border   "1px solid green"
                 :overflow "hidden"
                 :width    (str (* (:width state) square-size) "px")
                 :height   (str (* (:height state) square-size) "px")}}
   (map (fn [[pos vel id]]
          [:div {:replicant/key        id
                 :replicant/mounting   {:style {:transform (get-transform pos vel -1)}}
                 :replicant/unmounting {:style {:transform (get-transform pos vel 1)}}
                 :style                {:position    "absolute"
                                        :will-change "transform"
                                        :top         0
                                        :left        0
                                        :width       (str square-size "px")
                                        :height      (str square-size "px")
                                        :background  "black"
                                        :transition  (str "transform " (/ core/frame-time 1000) "s linear")
                                        :transform   (get-transform pos vel 0)}}])
        (:guards state))])
