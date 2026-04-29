(ns advent-of-code-visualisations.day-2019-13.view
  (:require [advent-of-code-visualisations.day-2019-13.core :as core]))

(def block-colors ["#FF595E"
                   "#FF7A59"
                   "#FF9F1C"
                   "#FFCA3A"
                   "#F4E409"
                   "#C5E01A"
                   "#8AC926"
                   "#52B788"
                   "#2EC4B6"
                   "#00B4D8"
                   "#4CC9F0"
                   "#3A86FF"
                   "#4361EE"
                   "#5E60CE"
                   "#9D4EDD"
                   "#C77DFF"
                   "#F15BB5"
                   "#FF85A1"])

(defn ball
  []
  [:svg {:viewBox "0 0 20 20"}
   [:circle {:cx "10" :cy "10" :r "8" :fill "#b63232"}]])

(defn block
  [x y]
  [:div {:replicant/key        (str x "-" y "-block")
         :replicant/unmounting {:style {:opacity 0}}
         :style                {:position    "absolute"
                                :top         0
                                :left        0
                                :padding     "0 1px"
                                :height      "100%"
                                :width       "100%"
                                :opacity     1
                                :will-change "opacity"
                                :transition  "opacity 0.4s"}}
   [:svg {:viewBox "0 0 60 20"}
    [:rect {:x "0" :y "0" :width "60" :height "20" :rx "3" :fill (nth block-colors (- y 2))}]]])

(defn paddle
  []
  [:svg {:viewBox "0 0 100 25"}
   [:rect {:x "0" :y "0" :width "100" :height "25" :rx "8" :fill "#122fa2"}]])

(defn component
  [c x y]
  (condp = c
    :wall [:div {:style {:background "black" :width "100%" :height "100%"}} ""]
    :empty ""
    :block (block x y)
    :paddle (paddle)
    :ball (ball)))

(defn view
  [state]
  (when state
    (let [draw-state (core/get-draw-state state)
          y-range (core/get-y-range draw-state)
          x-range (core/get-x-range draw-state)
          number-of-blocks (core/get-number-of-blocks draw-state)]
      [:div {:style {:position "relative"}
             :replicant/on-render (fn [{remember :replicant/remember previous-number :replicant/memory dispatch :replicant/dispatch}]
                                    (when (< number-of-blocks (or previous-number 0))
                                      (dispatch {} [{:event :play-sound}]))
                                    (remember number-of-blocks))}
       (->> y-range
            (map (fn [y]
                   [:div {:replicant/key y
                          :style         {:display "flex"}}
                    (->> x-range
                         (map (fn [x]
                                (let [c (get draw-state [x y])]
                                  [:div {:replicant/key x
                                         :style         {:width    "20px"
                                                         :height   "20px"
                                                         :position "relative"}}
                                   (component c x y)]))))])))
       [:div {:style {:position    "absolute"
                      :left        0
                      :bottom      -30
                      :padding     "0 20px"
                      :display     "flex"
                      :align-items "center"
                      :gap         "20px"}}
        [:button {:on {:click [{:event :restart}]}}
         "Restart"]
        [:button {:on {:click [{:event :undo}]}}
         "Undo"]
        [:label {:for "game-speed"}
         "Game Speed"]
        [:input {:type  "range"
                 :id    "game-speed"
                 :value (core/get-frames-per-second state)
                 :min   2
                 :max   5
                 :step  1
                 :on    {:change [{:event :frame-time-change :data :event/target.value}]}}]
        [:div "High Score: " (core/get-high-score state)]
        [:div "Score: " (core/get-score state)]]])))
