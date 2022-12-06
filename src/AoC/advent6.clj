(ns AoC.advent6
  (:require [clojure.string :as str]))

(defn find-packet
  "Return the index of the end character in the first unique sequence of four characters"
  [codestr size]
  (try (loop [i 0]
       (if (= size (count (set (seq (subs codestr i (+ size i))))))
         (+ size i)
         (recur (inc i)))
    )
       (catch StringIndexOutOfBoundsException e
         (println "Ran out of string"))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Code counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")]
    ;; We need to read lines from the input until we hit the blank line
    (println "Start-of-packet markers found at" (map #(find-packet % 4) input))
    (println "Message markers found at" (map #(find-packet % 14) input))
    ))