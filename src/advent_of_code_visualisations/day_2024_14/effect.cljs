(ns advent-of-code-visualisations.day-2024-14.effect
  (:require [advent-of-code-visualisations.day-2024-14.core :as core]))

(defn loop-update-states!
  [db-atom]
  (let [state (deref db-atom)]
    (when (< (:n state) core/end-n)
      (js/setTimeout (fn []
                       (swap! db-atom (fn [state]
                                        (core/n-seconds-with-new-guards-if-wraparound state core/step-size)))
                       (loop-update-states! db-atom))
                     core/frame-time))))
