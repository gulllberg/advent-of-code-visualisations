# Advent of Code Visualisations

## 2024 Day 14 part 2

Put your input in `resources/public/assets/inputs/2024-day14.txt`.

Tweak values `core.cljs` to pick some frames before your solution (`8270` was mine).

```clojure
(def step-size 0.1)
(def start-n 8269)
(def end-n 8270)
(def frame-time 500)
```

[Replicant](https://github.com/cjohansen/replicant) makes it easy to have mount/unmount styles (via ```:replicant/mounting and :replicant/unmounting```) for the robots to keep animations smooth.

## 2019 Day 13 part 2

Put your input in `resources/public/assets/inputs/2019-day13.txt`.

## run it

Select which day you want to run in the imports in `advent-of-code-visualisations.main`.

clojure -M:fig
