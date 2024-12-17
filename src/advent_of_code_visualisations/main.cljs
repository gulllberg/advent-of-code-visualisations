(ns ^:figwheel-hooks advent-of-code-visualisations.main
  (:require [replicant.dom :as d]
            [advent-of-code-visualisations.day-2024-14.view :refer [view]]
            [advent-of-code-visualisations.day-2024-14.effect :refer [start!]]))

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

(when (nil? (deref db-atom))
  (d/set-dispatch! handle-event)
  (add-watch db-atom :render (fn [_ _ _ _]
                               (render!)))
  (start! db-atom)
  (render!))


(defn on-js-reload
  {:after-load true}
  []
  (d/set-dispatch! handle-event)
  (render!))
