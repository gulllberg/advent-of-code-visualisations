(ns advent-of-code-visualisations.day-2019-13.main
  (:require [replicant.dom :as d]
            [advent-of-code-visualisations.day-2019-13.view :refer [view]]
            [advent-of-code-visualisations.day-2019-13.effect :refer [game-loop!]]
            [advent-of-code-visualisations.day-2019-13.core :as core]))

(defonce db-atom (atom nil))

(defn render!
  []
  (d/render (js/document.getElementById "app")
            (view (deref db-atom))))

(defn handle-event
  [actions]
  (doseq [{event :event data :data} actions]
    (condp = event
      :restart (swap! db-atom core/restart)
      :undo (swap! db-atom core/undo)
      :frame-time-change (swap! db-atom core/set-frames-per-second (js/parseInt data))
      :play-sound (let [audio (js/Audio. "/assets/sound/glass-crash.mp3")]
                    (.play audio))
      (println "No event handler for event" event))))

(defn keydown-handler
  [db-atom event]
  (condp = (.-key event)
    "ArrowLeft" (swap! db-atom core/handle-arrow-left)
    "ArrowRight" (swap! db-atom core/handle-arrow-right)
    nil))

(defn start!
  [db-atom]
  (-> (js/fetch "/assets/inputs/2019-day13.txt")
      (.then #(.text %))
      (.then (fn [body]
               (let [initial-state (core/create-state body)]
                 (reset! db-atom initial-state)
                 (.addEventListener js/window "keydown" (fn [event] (keydown-handler db-atom event)))
                 (game-loop! db-atom))))))