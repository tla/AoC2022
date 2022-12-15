(ns AoC.advent13
  (:require [clojure.string :as str]
            [clojure.data.json :as json]))

(defn make-pair
  "Return a pair of JSON-parsed arrays from two input lines"
  [lines]
  [(json/read-str (first lines)) (json/read-str (second lines))])
    

(defn get-pairs
  "Parse the input and return a vector of pairs of packet sequences"
  [input]
  (loop [pairs '()
         first-def (vec (take 2 input))
         rest-def (drop 3 input)]
    (if (empty? rest-def)
      (reverse (cons (make-pair first-def) pairs))
      (recur (cons (make-pair first-def) pairs) (vec (take 2 rest-def)) (drop 3 rest-def))))) 

(defn cmp-array
  "Return a cmp-like value (0, negative, or positive) to compare the left 
   array to the right one. Recurse into sub-arrays as needed."
  [[aleft aright]]
  (loop [ileft (first aleft)
         iright (first aright)
         rleft (rest aleft)
         rright (rest aright)
         outcome 0]
    ; (println "Comparing" ileft iright)
    (cond
      (not (= 0 outcome)) outcome
      (and (= nil ileft) (= nil iright)) 0
      (= nil ileft) 1
      (= nil iright) -1
      (and (vector? ileft) (vector? iright))
      (recur (first rleft) (first rright) (rest rleft) (rest rright) (cmp-array [ileft iright]))
      (vector? ileft)
      (recur (first rleft) (first rright) (rest rleft) (rest rright) (cmp-array [ileft [iright]]))
      (vector? iright)
      (recur (first rleft) (first rright) (rest rleft) (rest rright) (cmp-array [[ileft] iright]))
      (= ileft iright) 
      (recur (first rleft) (first rright) (rest rleft) (rest rright) 0)
      :else (- iright ileft))))
  

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Packet order counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        pairs (get-pairs input)]
    (println "Sum of indices of ordered pairs is"
             (loop [i 1
                    ordered-indices '()
                    f (first pairs)
                    r (rest pairs)] 
               (if (not f)
                 (reduce + ordered-indices)
                 (do (println "Answer for pair" i "is" (cmp-array f))
                   (recur (inc i) 
                        (if (<= 0 (cmp-array f)) (cons i ordered-indices) ordered-indices)
                        (first r)
                        (rest r))))))))
