(ns advent-of-code-visualisations.day-2019-13.core
  (:require [advent-of-code-visualisations.day-2019-13.intcode :refer [parse-program run-intcode-program]]
            [advent-of-code-visualisations.day-2019-13.autoplay :refer [autoplay-user-inputs]]))

(defn create-state
  [input]
  (let [autoplay false
        program (-> (parse-program input)
                    (assoc 0 2))
        user-input (if autoplay [(first autoplay-user-inputs)] [0])]
    {:program                    program
     :user-inputs                user-input
     :user-input-for-next-frame  nil
     :high-score                 0
     :frames-per-second          3
     :autoplay                   autoplay
     :autoplay-user-inputs-index 1
     :program-result             (run-intcode-program program user-input)}))

(defn get-frames-per-second
  [state]
  (:frames-per-second state))

(defn set-frames-per-second
  [state frames-per-second]
  (assoc state :frames-per-second frames-per-second))

(defn get-frame-time
  [state]
  (if (:autoplay state)
    25
    (/ 1000 (get-frames-per-second state))))

(defn restart
  [state]
  (let [user-input [0]]
    (assoc state :user-inputs user-input
                 :user-input-for-next-frame nil
                 :program-result (run-intcode-program (:program state) user-input))))

(defn undo
  [state]
  (let [user-inputs (into [] (drop-last 10 (:user-inputs state)))]
    (if (empty? user-inputs)
      (restart state)
      (assoc state :user-inputs user-inputs
                   :user-input-for-next-frame nil
                   :program-result (run-intcode-program (:program state) user-inputs)))))

(defn get-score
  [state]
  (->> (get-in state [:program-result :program-output])
       (partition 3)
       (filter (fn [[x y _]]
                 (and (= x -1) (zero? y))))
       (last)
       (last)))

(defn get-high-score
  [state]
  (:high-score state))

(defn game-over?
  [state]
  (-> (get-in state [:program-result :reason])
      (= :halted)))

(defn get-next-frame
  [state]
  (if (game-over? state)
    state
    (let [autoplay (:autoplay state)
          user-input (if autoplay
                       (nth autoplay-user-inputs (:autoplay-user-inputs-index state))
                       (or (:user-input-for-next-frame state) 0))
          state (update state :program-result (fn [{memory :memory program-output :program-output instruction-pointer :instruction-pointer relative-base :relative-base}]
                                                (run-intcode-program memory instruction-pointer [user-input] program-output relative-base)))
          current-score (get-score state)]
      (-> state
          (update :user-inputs conj user-input)
          (assoc :user-input-for-next-frame nil)
          (update :autoplay-user-inputs-index inc)
          (update :high-score max current-score)))))

(defn handle-arrow-left
  [state]
  (assoc state :user-input-for-next-frame -1))

(defn handle-arrow-right
  [state]
  (assoc state :user-input-for-next-frame 1))

(defn get-draw-state
  [state]
  (->> (get-in state [:program-result :program-output])
       (partition 3)
       (reduce (fn [a [x y id]]
                 (if-let [c ({0 :empty
                              1 :wall
                              2 :block
                              3 :paddle
                              4 :ball} id)]
                   (assoc a [x y] c)
                   a))
               {})))

(defn get-number-of-blocks
  [draw-state]
  (->> (vals draw-state)
       (filter #{:block})
       (count)))

(defn get-x-range
  [draw-state]
  (let [ks (->> (keys draw-state)
                (map first))
        to (apply max ks)]
    (range 0 (inc to))))

(defn get-y-range
  [draw-state]
  (let [ks (->> (keys draw-state)
                (map second))
        to (apply max ks)]
    (range 0 (inc to))))
