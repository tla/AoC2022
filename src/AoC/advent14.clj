(ns AoC.advent14
  (:require [clojure.string :as str]))

(defn expand-path
  "Given a pair of points lie [475 65] and [475 60], return an inclusive
  list of points that make the line"
  [p1 p2]
  ; Make sure the points are in numeric order for the range call
  (let [[f s] (sort [p1 p2])]
    (if (= (first f) (first s))
      (map #(vector (first f) %) (range (second f) (inc (second s)))) ; traverse Y axis
      (map #(vector % (second f)) (range (first f) (inc (first s))))) ; traverse X axis
    )
  )
(defn add-cave-wall
  "Add points to a set to indicate a cave wall"
  [cave line]
  (let [points (map (fn [x] (mapv #(Integer/parseInt %) (str/split x #","))) (str/split line #" -> "))]
    (loop [p (first points)
           q (second points)
           r (rest points)
           c cave]
      (if (nil? q)
        c
        (recur (first r) (second r) (rest r) (into c (expand-path p q)))
        ))
    ))

(defn drop-sand-until-full
  "Given a cave, return a cave filled with sand according to the algorithm."
  [cave xfloor solid?]
  (loop [c cave
         grain [500 0]]

    ; Have we already reached the floor?
    (if (= (second grain) (- xfloor 1))
      ; If we are working with a solid floor, it is time to drop the
      ; next grain. Otherwise we have spilled into the void and should
      ; return the final cave state.
      (if solid?
        (recur (conj c grain) [500 0])
        c)
      ; Is the spot underneath occupied?
      (if (contains? c [(first grain) (inc (second grain))])
        ; Is the spot diagonally left occupied?
        (if (contains? c [(dec (first grain)) (inc (second grain))])
          ; Is the spot diagonally right occupied?
          (if (contains? c [(inc (first grain)) (inc (second grain))])
            ; If so, come to rest where we are.
            ; If we are grain 500/0 return, otherwise recur.
            (if (= grain [500 0])
              (conj c grain)
              (recur (conj c grain) [500 0]))
            ; The spot to the right is not occupied. Take it
            (recur c [(inc (first grain)) (inc (second grain))])
            )
          ; The spot to the left is not occupied. Take it
          (recur c [(dec (first grain)) (inc (second grain))])
          )
        ; The spot underneath is not occupied. Take it
        (recur c [(first grain) (inc (second grain))]))
      )))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Sand counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        ; Initialise the cave walls from the input
        cave (reduce add-cave-wall #{} input)
        ; Convenience: notice where the highest (lowest) Y of the cave is
        xfloor (+ 2 (apply max (map second cave)))]
       (println "Grains of sand resting on a void"
                (- (count (drop-sand-until-full cave xfloor false)) (count cave)))
       (println "Grains of sand resting on the ground"
                (- (count (drop-sand-until-full cave xfloor true)) (count cave)))
       ))
