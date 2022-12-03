(ns AoC.advent3
  (:gen-class)
  (:require [clojure.set :as set]
            [clojure.string :as str]))

;; Coding from the inside out: Both puzzles need the calculation of an 
;; idiosyncratic priority based on a single character. We write this first.

(defn get-priority
  "Return the priority for a given alphabet character."
  [item]
  (let [val (int item)]
    (if (> val 96)
      ;; Lowercase priorities 1-26
      (- val 96)
      ;; Uppercase priorities 27-52
      (- val 38))))

;; Puzzle 1: given a string, we just have to find the common character in
;; both halves of the string and return its priority.
(defn missorted-priority
  "For the encoded item contents of a bag, return the priority of
   the item that appears in both halves of the bag."
  [contents]
  (let [len (.length contents)
        left (set (map #(get contents %) (range 0 (/ len 2))))
        right (set (map #(get contents %) (range (/ len 2) len)))
        common (first (set/intersection left right))]
    ;; If this were a real program, we would be defensive and check that
    ;; the intersection only has one value. But the puzzle promises, so
    ;; we don't bother.
    (get-priority common)))

;; Puzzle 2: given a list of three strings, we have to find the common
;; character across all the strings and return its priority.

(defn badge-priority
  ([bagset]
   (badge-priority bagset nil))
  ([bagset common]
   (if (empty? bagset)
     (let [badge (first common)]
       ;; (println "Common badge in group is" badge)
       (get-priority badge))
     (let [ours (first bagset)
           remaining (rest bagset)
           bag (set (char-array ours))]
       (if (nil? common)
       ;; Initialize the common set with the contents of the first bag
       ;; and recurse on the rest
         (recur remaining bag)
         (recur remaining (set/intersection bag common)))))))

(defn solve
  "Priority counting"
  [& args]
  ; Slurp in the input file  
  (let [fn (first args)
        input (str/split (slurp fn) #"\n")]
    (println "Running on file" fn)
    (println "Priority sum is" (reduce + (map missorted-priority input)))
    (println "Priority badge sum is"
             (loop [remaining-bags input
                    running-total 0]
               (if (empty? remaining-bags)
                 running-total
                 (recur (drop 3 remaining-bags)
                        (+ running-total (badge-priority (take 3 remaining-bags)))))))))