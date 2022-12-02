(ns advent1.core
  (:gen-class))
(require '[clojure.string :as str])

(defn sum
  "Recursively add up items of a list"
  ([items] (sum items 0))
  ([items total]
    (if (empty? items)
      total
      (recur (rest items) (+ (first items) total)))))

(defn collect
  "Flattens two lists of items into one list. There must be a real way to do this"
  ([items] (collect items []))
  ([items collection] 
    (if (empty? items)
      collection
      (recur (rest items) (conj collection (first items))))))

(defn group
  "Split a sequence by a delimiter, eating the delimiter"
  ([delim items] (group delim items '()))
  ([delim items collected]
    (if (empty? items)
      collected
      (if (= delim (first items))
        ; Chomp off this delimiter and continue
        (recur delim (rest items) (conj collected '()))
        ; Add this item to the end of the first list in collected
        (recur delim (rest items) (conj (rest collected) (collect [(Integer. (first items))] (first collected))))))))

(defn -main
  "Calorie counting"
  [& args]
  ; Slurp in the input file
  (println (str "Running on file " (first *command-line-args*)))
  (def input (str/split (slurp (first *command-line-args*)) #"\n"))
  (println (str "Most loaded elf has " (apply max (map sum (group "" input))) " calories"))
  )