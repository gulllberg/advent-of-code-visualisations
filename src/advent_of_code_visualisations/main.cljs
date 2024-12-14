(ns ^:figwheel-hooks advent-of-code-visualisations.main
  (:require [replicant.dom :as d]
            [advent-of-code-visualisations.day-2024-14.view :refer [view]]
            [advent-of-code-visualisations.day-2024-14.core :as core]))

(defonce db-atom (atom nil))

(defn render!
  []
  (d/render (js/document.getElementById "app")
            (view (deref db-atom))))

(defn handle-event
  [replicant-data actions]
  (js/console.log "handle-event" actions replicant-data)
  (doseq [{event :event data :data} actions]
    (condp = event
      (println "No event handler for event" event)
      )))


(defn loop-update-states!
  []
  (let [db (deref db-atom)
        current-index (:state-index db)
        max-index (dec (count (:states db)))]
    (when (< current-index max-index)
      (js/setTimeout (fn []
                       (swap! db-atom update :state-index inc)
                       (loop-update-states!))
                     1350))))

(when (nil? (deref db-atom))
  (d/set-dispatch! handle-event)
  (add-watch db-atom :render (fn [_ _ _ _]
                               (render!)))
  (-> (js/fetch "/assets/inputs/day14.txt")
      (.then #(.text %))
      (.then (fn [body]
               (let [initial-state (core/create-state body 101 103)
                     states (core/part-2 initial-state 8263 1 8270)]
                 (reset! db-atom {:states      states
                                  :state-index 0})
                 (loop-update-states!)))))
  (render!))


(defn on-js-reload
  {:after-load true}
  []
  (d/set-dispatch! handle-event)
  (render!))
