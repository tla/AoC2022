(ns AoC.advent15
  (:require
    [clojure.set :as set]
    [clojure.string :as str]))

(defn line-tester
  "Return a function that, given a sensor position and its nearest beacon, returns the
  range of X that it can see at a given Y position"
  [sx sy bx by]
  (let [magnitude (+ (abs (- sx bx)) (abs (- sy by)))]
    (fn [y]
      (let [offset (abs (- y sy))
            ; Initial x is sensor x - the difference between offset and magnitude.
            xinit (- sx (- magnitude offset))
            ; Range of x is 2*(magnitude-offset) + 1.
            xrange (+ xinit (+ (* 2 (- magnitude offset)) 1))]
        ; (println "Visiting sensor" sx sy ", magnitude" magnitude ", offset" offset)
        (if (> offset magnitude)
          '()
          (range xinit xrange)
          ))
      )
  ))

(defn outer-bound
  "For a given sensor, find the points just past the edge of its detection range.
   Return these points as a set."
  [sx sy bx by]
  ; Points are ([x, y - (inc mag)] to [x + (inc mag, y]
  ; to [x, y + (inc mag)] to [(x - (inc mag), y]
  (let [radius (+ (abs (- sx bx)) (abs (- sy by)) 1)]
    (set/union
      (set (map #(vector (+ sx %), (+ sy (- radius (abs %)))) (range (- radius) (inc radius))))
      (set (map #(vector (+ sx %), (- sy (- radius (abs %)))) (range (- radius) (inc radius)))))))

(defn point-tester
  "Return a function that, given a point [x y], will return true if that point is
   within range of the given sensor"
  [sx sy bx by]
  (let [magnitude (+ (abs (- sx bx)) (abs (- sy by)))]
    (fn [p]
      ; Find out if p is closer to the beacon
      (<= (+ (abs (- sx (first p))) (abs (- sy (second p)))) magnitude))
    ))

(defn test-boundary
  "Given a set of point candidates, look for any point not covered by any sensor"
  [candidates sensors]
  ; (println "Incoming candidates are" candidates)
  (loop [s (first sensors)
         r (rest sensors)
         missed candidates]
    (if (nil? s)
      missed
      (let [filtered (set (filter #(not ((:point-tester s) %)) missed))]
        ; (println "...filtered to" filtered)
        (recur (first r) (rest r) filtered)
       ))))

(defn register-sensor
  "Register a sensor with its closest beacon and a function that, given
   a Y value, returns all the X points that are beacon-free"
  [line]
  (let [[sx, sy, bx, by] (mapv #(Integer/parseInt %) (drop 1 (re-matches #"Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)" line)))]
    {:beacon [bx by]
     :outer-bound (outer-bound sx sy bx by)
     :line-tester (line-tester sx sy bx by)
     :point-tester (point-tester sx sy bx by)}
  ))

(defn beacons-at
  "Return all beacons with y coordinate y."
  [sensors y]
  (into #{} (filter #(= y (second %)) (map :beacon sensors)))
  )

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Sand counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        testy (if (str/includes? fname "example") 10 2000000)
        ;; Make the sensor functions
        sensors (mapv register-sensor input)
        ;; Get the X positions covered by the sensor at testY
        x-covered (reduce #(into %1 ((:line-tester %2) testy)) #{} sensors)
        ;; Filter to the range for beacon locations based on testY
        inrange (fn [p] (and (>= (first p) 0)
                             (<= (first p) (* 2 testy))
                             (>= (second p) 0)
                             (<= (second p) (* 2 testy))))
        uncovered (loop [s (first sensors)
                         r (rest sensors)
                         candidates #{}]
                    ; For each sensor, get its edge points, add them to the candidates set, then
                    ; filter the set against all point testers
                    (if (nil? s)
                      candidates
                      (recur (first r) (rest r) (test-boundary (into candidates (filter inrange (:outer-bound s))) sensors))
                      ))
        ]
    (println "There are"
             (- (count x-covered) (count (beacons-at sensors testy)))
             "guaranteed beacon-free positions on row" testy)
    (if (= 1 (count uncovered))
      (do (println "Single uncovered point between 0 and" (* 2 testy) "is" uncovered)
        (println "Its tuning frequency is" (+ (second (first uncovered)) (* 4000000 (first (first uncovered))))))
      (println "Something went wrong - uncovered points are" uncovered))
    ))