(ns AoC.advent8
  (:require [clojure.string :as str]))

(defn enforest
  "Take a set of lines and turn it into a forest grid."
  [lines] 
  (loop [l (first lines)
         r (rest lines)
         coll []]
    (let [conv (vec (map #(Integer/parseInt %) (str/split l #"")))]
      (if (empty? r)
        (conj coll conv)
        (recur (first r) (rest r) (conj coll conv))))
    )
  ) 

(defn edge?
  [forest treeloc]
  (or
   (= 0 (first treeloc))
   (= 0 (second treeloc))
   (= (count (first forest)) (inc (first treeloc)))
   (= (count forest) (inc (second treeloc)))))

(defn visible? 
  "Given a grid and a tree on it, say whether the tree is hidden"
  [forest treeloc] 
  (let [[x y] treeloc 
        row (get forest y)
        column (vec (map #(get % x) forest))
        height (get (get forest y) x)
        breadth (count row)
        depth (count column)
        fltr (fn [i] (filter #(>= % height) i))]
    (if (edge? forest treeloc)
      true
      (or
       (empty? (fltr (subvec row 0 x)))
       (empty? (fltr (reverse (subvec row (inc x) breadth))))
       (empty? (fltr (subvec column 0 y)))
       (empty? (fltr (reverse (subvec column (inc y) depth)))))
      )
    ))

(defn can-see
  "Return the number of trees the tree at the given height can see
   when it looks out across the series of trees in viewvec.
   Edge case is zero."
  [height viewvec]
  (loop [t (first viewvec)
         r (rest viewvec)
         c 0]
    (if (empty? r)
      (inc c)
      (if (< t height)
        (recur (first r) (rest r) (inc c))
        (inc c))))) 

(defn score
  "Given a tree at location [x,y] in the forest, return its scenic score"
  [forest treeloc]
  [forest treeloc]
  (let [[x y] treeloc
        row (get forest y)
        column (vec (map #(get % x) forest))
        height (get (get forest y) x)
        breadth (count row)
        depth (count column)]
    (if (edge? forest treeloc)
      0
      (reduce * [(can-see height (subvec row (inc x) breadth))
                 (can-see height (reverse (subvec row 0 x)))
                 (can-see height (subvec column (inc y) depth))
                 (can-see height (reverse (subvec column 0 y)))]))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Tree counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        forest (enforest input)
        xmax (dec (count (first forest)))
        ymax (dec (count forest))]
    (loop [x 0
           y 0
           vis 0
           maxscen 0]
      (if (and (= x xmax)
               (= y ymax))
        (do (println "Number of visible trees is" (inc vis))
            (println "Max viewability score is" maxscen))
        (recur (if (= x xmax) 0 (inc x))
               (if (= x xmax) (inc y) y)
               (if (visible? forest [x y])
                 (inc vis)
                 vis)
               (if (> (score forest [x y]) maxscen)
                 (score forest [x y])
                 maxscen))))
    ))
