(ns advent-of-code-visualisations.day-2024-14.view)

(defn view
  [db]
  (let [states (get db :states)
        state-index (:state-index db)
        state (nth states state-index)]
    [:div {:style {:position "relative"}}
     (map (fn [[[x y] _ id]]
            [:div {:replicant/key id
                   :style         {:position "absolute"
                                   :width "5px"
                                   :height "5px"
                                   :background "black"
                                   :transition "top 1.3s, left 1.3s"
                                   :top      (str (* y 5) "px")
                                   :left     (str (* x 5) "px")}}])
          (:guards state))]))
