(ns advent-of-code-visualisations.day-2019-13.intcode)

(defn third [[_ _ z]] z)

(def opcode->number-of-parameters {1  3
                                   2  3
                                   3  1
                                   4  1
                                   5  2
                                   6  2
                                   7  3
                                   8  3
                                   9  1
                                   99 0})

(def opcode->parameter-types {1  [:read :read :write]
                              2  [:read :read :write]
                              3  [:write]
                              4  [:read]
                              5  [:read :read]
                              6  [:read :read]
                              7  [:read :read :write]
                              8  [:read :read :write]
                              9  [:read]
                              99 []})

(defn parse-program
  [input]
  (->> (re-seq #"-?\d+" input)
       (map js/parseInt)
       (into [])))

(defn program->memory
  [program]
  (zipmap (range) program))

(defn read-from-memory
  [memory position]
  (if (neg? position)
    (println "Error: Cannot read negative memory position: " position)
    (get memory position 0)))

(defn write-to-memory
  [memory position value]
  (assoc memory position value))

(defn get-opcode
  [instruction]
  (mod instruction 100))

;; 0 - position
;; 1 - immediate (value)
;; 2 - relative (like position, but with relative base)
(defn get-parameter-modes
  [instruction]
  (let [opcode (get-opcode instruction)
        remaining (quot instruction 100)]
    (->> (opcode->number-of-parameters opcode)
         (range)
         (reduce (fn [[parameter-modes remaining] _]
                   [(conj parameter-modes (mod remaining 10)) (quot remaining 10)])
                 [[] remaining])
         (first))))

(defn get-parameters
  [memory instruction instruction-pointer relative-base]
  (let [opcode (get-opcode instruction)
        parameter-modes (get-parameter-modes instruction)
        parameter-types (opcode->parameter-types opcode)]
    (->> (range (count parameter-modes))
         (map (fn [index]
                (let [parameter-mode (nth parameter-modes index)
                      parameter-type (nth parameter-types index)
                      memory-value (read-from-memory memory (+ instruction-pointer 1 index))
                      relative-base-adjusted-memory-value (if (= 2 parameter-mode) (+ memory-value relative-base) memory-value)]
                  (if (or (= :write parameter-type)
                          (= 1 parameter-mode))
                    relative-base-adjusted-memory-value
                    (read-from-memory memory relative-base-adjusted-memory-value))))))))

(defn run-intcode-program
  ([program]
   (run-intcode-program program []))
  ([program program-input]
   (run-intcode-program (program->memory program) 0 program-input [] 0))
  ([memory instruction-pointer program-input program-output relative-base]
   (loop [memory memory
          instruction-pointer instruction-pointer
          program-input program-input
          program-output program-output
          relative-base relative-base]
     (let [instruction (read-from-memory memory instruction-pointer)
           opcode (get-opcode instruction)
           parameters (get-parameters memory instruction instruction-pointer relative-base)]
       (condp = opcode
         99 {:memory         memory
             :program-output program-output
             :reason         :halted}
         1 (recur (write-to-memory memory (third parameters) (+ (first parameters) (second parameters)))
                  (+ 4 instruction-pointer)
                  program-input
                  program-output
                  relative-base)
         2 (recur (write-to-memory memory (third parameters) (* (first parameters) (second parameters)))
                  (+ 4 instruction-pointer)
                  program-input
                  program-output
                  relative-base)
         3 (if-let [input (first program-input)]
             (recur (write-to-memory memory (first parameters) input)
                    (+ 2 instruction-pointer)
                    (rest program-input)
                    program-output
                    relative-base)
             {:memory              memory
              :program-output      program-output
              :instruction-pointer instruction-pointer
              :relative-base       relative-base
              :reason              :waiting-for-input})
         4 (recur memory
                  (+ 2 instruction-pointer)
                  program-input
                  (conj program-output (first parameters))
                  relative-base)
         5 (recur memory
                  (if (not (zero? (first parameters)))
                    (second parameters)
                    (+ 3 instruction-pointer))
                  program-input
                  program-output
                  relative-base)
         6 (recur memory
                  (if (zero? (first parameters))
                    (second parameters)
                    (+ 3 instruction-pointer))
                  program-input
                  program-output
                  relative-base)
         7 (recur (write-to-memory memory (third parameters) (if (< (first parameters) (second parameters)) 1 0))
                  (+ 4 instruction-pointer)
                  program-input
                  program-output
                  relative-base)
         8 (recur (write-to-memory memory (third parameters) (if (= (first parameters) (second parameters)) 1 0))
                  (+ 4 instruction-pointer)
                  program-input
                  program-output
                  relative-base)
         9 (recur memory
                  (+ 2 instruction-pointer)
                  program-input
                  program-output
                  (+ relative-base (first parameters)))
         (println "Invalid instruction" instruction))))))
