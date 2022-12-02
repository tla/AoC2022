(ns advent1.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn group
  "Split a sequence into multiple sequences by a delimiter, eating the delimiter"
  ([delim items] (group delim items '()))
  ([delim items collected]
    (if (empty? items)
      collected
      (if (= delim (first items))
        ; Chomp off this delimiter and continue
        (recur delim (rest items) (conj collected '()))
        ; Add this item to the end of the first list in collected
        (recur delim (rest items) (conj (rest collected) (conj (first collected) (Integer. (first items)))))))))

(defn -main
  "Calorie counting"
  [& args]
  ; Slurp in the input file  
  (let [fn (first args)
        input (str/split (slurp fn) #"\n")
        bags (map #(reduce + %) (group "" input))]
    (println "Running on file" fn) 
    (println "Most loaded elf has" (apply max bags) "calories") 
    (println "Three most loaded elves have" (reduce + (take 3 (sort > bags))) "calories")))