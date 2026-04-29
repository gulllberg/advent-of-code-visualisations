(ns ^:figwheel-hooks advent-of-code-visualisations.main
  (:require [replicant.dom :as d]
            [clojure.walk :as walk]
            [advent-of-code-visualisations.day-2019-13.main :refer [db-atom render! handle-event start!]]
            ;[advent-of-code-visualisations.day-2024-14.main :refer [db-atom render! handle-event start!]]
            ))

(defn interpolate-actions
  [event actions]
  (walk/postwalk
    (fn [x]
      (case x
        :event/target.value (.. event -target -value)
        x))
    actions))

(defn dispatch-function
  [event-data actions]
  (js/console.log "handle-event" event-data actions)
  (->> actions
       (interpolate-actions (:replicant/dom-event event-data))
       handle-event))

(when (nil? (deref db-atom))
  (d/set-dispatch! dispatch-function)
  (add-watch db-atom :render (fn [_ _ _ _]
                               (render!)))
  (start! db-atom)
  (render!))

(defn on-js-reload
  {:after-load true}
  []
  (d/set-dispatch! dispatch-function)
  (render!))
