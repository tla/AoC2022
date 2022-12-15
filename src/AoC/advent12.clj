(ns AoC.advent12 
  (:require [clojure.string :as str]
            [ubergraph.core :as uber]
            [ubergraph.alg :as alg]))

(defn get-val
  "Return the integer value of a character, with special cases for S[tart] and E[nd]"
  [ch]
  (case ch
    \S 96
    \E 123
    (int ch)))

(defn make-matrix
  "Turn the input into a 2D matrix of characters."
  [input]
  (loop [matrix []
         line (first input)
         remaining (rest input)]
    (if (empty? remaining)
      (conj matrix (vec (map int line)))
      (recur (conj matrix (vec line)) (first remaining) (rest remaining)))
    ))

(defn find-char
  "Find the first instance of a given character. We use this for S and E"
  [matrix ch]
  (loop [x 0
         y 0
         xmax (count (get matrix 0))
         ymax (count matrix)]
    (if (and (= x xmax) (= y ymax))
      nil ;; We didn't find the start
      (if (= ch (get (get matrix y) x))
        [x y]
        (if (= x xmax)
          (recur 0 (inc y) xmax ymax)
          (recur (inc x) y xmax ymax))))))
      
(defn get-coord-val
  [terrain [x y]]
  (if (and (get terrain y) (get (get terrain y) x))
    (get-val (get (get terrain y) x))
    nil)
  )

(defn is-step?
  "If c2 is lower, equal, or one higher than c1, it's a step"
  [terrain c1 c2]
  (let [v1 (get-coord-val terrain c1)
        v2 (get-coord-val terrain c2)]
    (and v1 v2 (< (- v2 v1) 2)))
  )

(defn find-steps
  "Return true if we go up a step between this value and the given coordinate"
  [terrain coord]
  (let [x (first coord)
        y (second coord)
        north [x (dec y)]
        south [x (inc y)]
        west [(dec x) y]
        east [(inc x) y]]
    ; (println "Finding steps for" coord)
    (filter #(is-step? terrain coord %) [north south east west])
    ))

(defn get-nodestr
  [terrain coord]
  (let [x (first coord)
        y (second coord)]
    (format "%c/%d.%d" (get (get terrain y) x) x y))
  )

(defn edges-for-point
  [terrain x y]
  (let [steps (find-steps terrain [x y])]
    (map #(do [(get-nodestr terrain [x y]) (get-nodestr terrain %)]) steps)))

(defn graph-matrix
  "Turn a terrain matrix into a directed graph."
  [terrain]
  (let [xmag (count (get terrain 1))
        ymag (count terrain)]
    (loop [edgelist '()
           x 0
           y 0]
      (if (and (= x xmag) (= y ymag))
        (apply uber/digraph edgelist)
        (if (= x xmag)
          (recur (into edgelist (edges-for-point terrain x y)) 0 (inc y))
          (recur (into edgelist (edges-for-point terrain x y)) (inc x) y))))
    ))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Step counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        terrain (make-matrix input)
        g (graph-matrix terrain)
        start (get-nodestr terrain (find-char terrain \S))
        end (get-nodestr terrain (find-char terrain \E))]
    ; (uber/pprint g)
    (println "First solution is" (alg/shortest-path g start end))
  ))