(ns AoC.advent6
  (:require [clojure.string :as str]))

(defn find-unique-quad
  "Return the index of the end character in the first unique sequence of four characters"
  [codestr]
  (loop [i 0]
       (if (= 4 (count (set (seq (subs codestr i (+ 4 i))))))
         (+ 3 i)
         (recur (inc i)))
    ))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Code counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")]
    ;; We need to read lines from the input until we hit the blank line
    (map #(println "Start-of-packet markers found at" (find-unique-quad %)) input)
    (println "Whatever next?")
    ))