(ns advent-of-code-visualisations.day-2019-13.effect
  (:require [advent-of-code-visualisations.day-2019-13.core :as core]))

(defn game-loop!
  [db-atom]
  (let [state (deref db-atom)]
    (js/setTimeout (fn []
                     (when-not (core/game-over? (deref db-atom))
                       (swap! db-atom core/get-next-frame))
                     (game-loop! db-atom))
                   (core/get-frame-time state))))
