(ns advent-of-code-visualisations.day-2024-14.view)

(defn view
  [db]
  (let [states (get db :states)
        state-index (:state-index db)
        state (nth states state-index)
        width 101]
    [:div
     (map-indexed (fn [i row]
            [:div {:style {:display "flex"}}
             (map-indexed (fn [j c]
                            [:div {:replicant/key (str i "-" j)
                                   :style         {:height     "5px"
                                                   :width      "5px"
                                                   :background (if (= c \#) "black" "white")}}])
                          row)])
                  (partition (inc width) state))]))
