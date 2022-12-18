(ns AoC.advent18
  (:require [clojure.string :as str]
            [clojure.set :as s]
            [ubergraph.core :as uber]
            [ubergraph.alg :as alg]))

(defn shape-collect
  "Collect the list of cubes into a set"
  [input]
  (reduce (fn [c, l] (conj c (vec (map #(Integer/parseInt %) (str/split l #","))))) #{} input))

(defn neighbours
  "Get the set of cubes that would be on each side of our cube"
  [shape cube]
  (let [[x y z] cube
        left [(dec x) y z]
        right [(inc x) y z]
        down [x (dec y) z]
        up [x (inc y) z]
        front [x y (dec z)]
        back [x y (inc z)]]
    (s/intersection shape #{left right down up front back})))

(defn exposed-faces
  "Return the number of exposed faces for the given cube in the given shape."
  [shape cube]
  (- 6 (count (neighbours shape cube))))

(defn inverse-shape
  "Given a shape, find its inverse. Include an extra layer of 'air' so that the
   external volume is guaranteed to be connected."
  [shape]
  ; Find the rectangle that the shape fits in
  (let [xmin (dec (apply min (map #(get % 0) shape)))
        xmax (inc (apply max (map #(get % 0) shape)))
        ymin (dec (apply min (map #(get % 1) shape)))
        ymax (inc (apply max (map #(get % 1) shape)))
        zmin (dec (apply min (map #(get % 2) shape)))
        zmax (inc (apply max (map #(get % 2) shape)))]
    (set (for [x (range xmin (inc xmax))
               y (range ymin (inc ymax))
               z (range zmin (inc zmax))
               :let [p [x y z]]
               :when (not (contains? shape p))]
      p))
    )
  )

(defn connect-space
  "Make a connected graph out of a space of connected cubes"
  [pointset]
  (let [edges (reduce (fn [a b] (into a (map #(vector b %) (neighbours pointset b)))) #{} pointset)]
    (-> (uber/graph) (uber/add-nodes* pointset) (uber/add-edges* edges))))

(defn surface-area
  "Calculate the surface area of a given shape."
  [shape]
  (reduce + (map #(exposed-faces shape %) shape)))

(defn external-area
  "Calculate the external surface area (excluding any air pockets) of a given shape."
  [shape]
  (let [antishape (connect-space (inverse-shape shape))
        total-area (surface-area shape)
        components (sort-by count > (alg/connected-components antishape))
        loners (alg/loners antishape)]
    (println "Found" (count components) "connected components")
    (println "Found" (count loners) "unconnected nodes")
    (- total-area (reduce + (map #(surface-area (set %)) (rest components))))
    ))

(defn solve
  "Surface counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        shape (shape-collect input)]

    (println "Object has a surface area of" (surface-area shape))
    (println "Object has an external surface area of" (external-area shape)))