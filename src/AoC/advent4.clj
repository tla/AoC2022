(ns AoC.advent4
  (:require [clojure.set :as set]
            [clojure.string :as str]))

;; I'm going to do this one by making sets from the ranges, because I am 
;; not in the mood for tedious comparison stuff.

;; Convert a string range (e.g. "36-47") into a set whose members are the range
(defn parse-range
  [rangespec]
  (let [pair (map #(Integer/parseInt %) (str/split rangespec #"-"))]
    (set (range (first pair) (+ 1 (last pair))))
  ))

;; Convert a pair of ranges (e.g. "2-4,6-8" into a pair of integer pairs)
(defn parse-rangepair
  [rangepair]
  (map parse-range (str/split rangepair #",")))

;; Detect whether a range of numbers contains another range
(defn range-subsumed?
  [[range1 range2]]
  (or (set/subset? range1 range2) (set/subset? range2 range1)))


(defn ranges-overlap?
  [[range1 range2]]
  (not-empty (set/intersection range1 range2)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Overlap counting"
  [& args]
  (let [fn (first args)
        input (map parse-rangepair (str/split (slurp fn) #"\n"))]
    (println "Count of subsumed ranges is" 
             (count (filter range-subsumed? input)))
    (println "Count of overlapping ranges is"
             (count (filter ranges-overlap? input)))
    ))