(ns AoC.advent10
  (:require [clojure.string :as str]))

;; Cycles and registers, oh my. Modelled as a big array of register values; the
;; value in array[n] is the value of the register when cycle n is completed. Handily
;; we have an initial value that can go into array[0].

(defn add-cycle
  "Return a vector of cycles with the addition of the results
   of processing the instruction"
  [cycles-so-far instruction]
  (if (= instruction "noop")
    (conj cycles-so-far (last cycles-so-far))
    (let [n (Integer/parseInt (str/replace instruction "addx " ""))
          lastx (last cycles-so-far)]
      (conj cycles-so-far lastx (+ n lastx)))))

(defn signal-strength
  "Get the signal strength (cycle no. * register value before cycle is complete,
   i.e. from previous completed cycle)"
  [all-cycles cycle]
  (* cycle (get all-cycles (dec cycle))))

(defn get-pixel
  "Given the register values and a position, return the pixel that would be
   displayed at this index: # for on and . for off."
  [all-cycles pos]
  (let [x (get all-cycles pos)
        sprite (range (dec x) (+ 2 x))]
    (if (some #{(mod pos 40)} sprite)
      "#"
      ".")))

(defn construct-lines
  "Make six lines of output given a register and a pixel routine"
  [all-cycles]
  (map
   (fn [i] (str/join "" (map
                 #(get-pixel all-cycles %)
                 (range (* i 40) (+ 40 (* i 40))))))
   (range 0 6)))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Cycle counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        allcycles (reduce add-cycle [1] input)
        measurepoints (map #(* 20 %) (filter odd? (range 1 (int (/ (count allcycles) 20)))))]
    (println "Ran" (count allcycles) "cycles")
    (println "Measuring at" measurepoints)
    (println "Cycle strengths sum is"
             (reduce + (map #(signal-strength allcycles %) measurepoints)))
    (println (str/join "\n" (construct-lines allcycles)))
    ))