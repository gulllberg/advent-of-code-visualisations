(ns advent-of-code-visualisations.day-2024-14.core)

(defn parse-input
  [input]
  (->> input
       (re-seq #"[\d-]+")
       (map js/parseInt)
       (partition 4)
       (map-indexed (fn [idx [x y vx vy]]
              [[x y] [vx vy] (str "id-" idx)]))))

(defn create-state
  [input width height]
  {:width  width
   :height height
   :guards (parse-input input)})

(defn move-guard-n
  [guard n width height]
  (let [[vx vy] (second guard)]
    (update guard 0 (fn [[x y]]
                      [(mod (+ x (* vx n)) width)
                       (mod (+ y (* vy n)) height)]))))

(defn n-seconds
  [state n]
  (update state :guards (fn [guards]
                          (map (fn [guard]
                                 (move-guard-n guard n (:width state) (:height state)))
                               guards))))

(defn part-2
  [state start-n steps max-n]
  (loop [state (n-seconds state start-n)
         n start-n
         result []]
    (if (<= n max-n)
      (recur (n-seconds state steps)
             (+ n steps)
             (conj result state))
      result)))
