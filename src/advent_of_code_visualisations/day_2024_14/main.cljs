(ns advent-of-code-visualisations.day-2024-14.main
  (:require [replicant.dom :as d]
            [advent-of-code-visualisations.day-2024-14.view :refer [view]]
            [advent-of-code-visualisations.day-2024-14.core :as core]
            [advent-of-code-visualisations.day-2024-14.effect :refer [loop-update-states!]]))

(defonce db-atom (atom nil))

(defn render!
  []
  (d/render (js/document.getElementById "app")
            (view (deref db-atom))))

(defn handle-event
  [actions]
  (doseq [{event :event data :data} actions]
    (condp = event
      (println "No event handler for event" event))))

(defn start!
  [db-atom]
  (-> (js/fetch "/assets/inputs/2024-day14.txt")
      (.then #(.text %))
      (.then (fn [body]
               (let [initial-state (core/create-state body 101 103)]
                 (reset! db-atom (core/n-seconds initial-state core/start-n))
                 (loop-update-states! db-atom))))))