(ns advent-of-code-visualisations.day-2024-14.core)

(def step-size 0.1)
(def start-n 8269)
(def end-n 8270)
(def frame-time 500)

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
   :n      0
   :guards (parse-input input)})

(defn move-guard-n
  [guard n width height]
  (let [[vx vy] (second guard)]
    (update guard 0 (fn [[x y]]
                      [(mod (+ x (* vx n)) width)
                       (mod (+ y (* vy n)) height)]))))

(defn n-seconds
  [state n]
  (-> state
      (update :guards (fn [guards]
                        (map (fn [guard]
                               (move-guard-n guard n (:width state) (:height state)))
                             guards)))
      (update :n + n)))

(defn outside?
  [[x y] width height]
  (not (and (<= 0 x (dec width))
            (<= 0 y (dec height)))))

(defn n-seconds-with-new-guards-if-wraparound
  [state n]
  (let [width (:width state)
        height (:height state)]
    (-> state
        (update :guards (fn [guards]
                          (map (fn [[[x y] [vx vy] id]]
                                 (let [new-x (+ x (* vx n))
                                       new-y (+ y (* vy n))]
                                   (if (outside? [new-x new-y] width height)
                                     [[(mod new-x width) (mod new-y height)] [vx vy] (str id "-n")]
                                     [[new-x new-y] [vx vy] id])))
                               guards)))
        (update :n + n))))
