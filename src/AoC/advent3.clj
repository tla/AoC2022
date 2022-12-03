(ns AoC.advent3
  (:gen-class)
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn missorted-priority
  "For the encoded item contents of a bag, return the priority of
   the item that appears in both halves of the bag."
  [contents]
  (let [len (.length contents)
        left (set (map #(get contents %) (range 0 (/ len 2))))
        right (set (map #(get contents %) (range (/ len 2) len)))
        common (int (first (set/intersection left right)))] 
    (if (> common 96)
      ;; Lowercase priorities 1-26
      (- common 96)
      ;; Uppercase priorities 27-52
      (- common 38)) 
    ))

(defn solve
  "Priority counting"
  [& args]
  ; Slurp in the input file  
  (let [fn (first args)
        input (str/split (slurp fn) #"\n")]
    (println "Running on file" fn) 
    (println "Priority sum is" (reduce + (map missorted-priority input)))
  ))