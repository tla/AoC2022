(ns AoC.advent7
  (:require [clojure.string :as str]
            [clojure.zip :as z]))

(defn zip-treeseq
  "Initialize a zipper tree from a sequence"
  [treeseq]
  (z/zipper #(not (int? (second %)))
            second
            (fn [x y] (list (first x) y))
            treeseq))

(defn down-tree 
  "Given a directory name, return the tree loc for that directory"
  [tree loc]
  (loop [b (z/next tree)] 
    (let [bname (-> b z/node first)]
      (if (= loc bname)
        b
        (if (nil? b)
          nil 
          (recur (z/right b))))
      ))
  )

(defn process-line
  "Given a line of input, manipulate the tree as commanded"
  [tree line]
  (case (subs line 0 4)
    "$ cd" (let [dn (subs line 5)] ; find the child branch given and return it
             (case dn
               ".." (z/up tree)
               "/" tree ;; this only happens once
               (down-tree tree dn)))  
    "dir " (let [dn (subs line 4)] ; make a new child branch. Return the resulting tree
             (z/insert-child tree (list dn '()))) 
    (if (re-find #"^\d+" line)
      (let [[size fname] (str/split line #"\s+")]  ; add the file and size
        (z/insert-child tree (list fname (Integer/parseInt size))))
      tree)  ; default: just return the tree
    ))

(defn file-sizes
  "Recursively sum the file sizes in a tree sequence"
  [treeseq] 
  (if (int? (second treeseq)) 
    (vec treeseq) 
    [(first treeseq) (reduce + (map second (map file-sizes (second treeseq))))]))

(defn directories
  "Return a vector of all the tree's directory nodes, depth first"
  [tree]
  (loop [n (z/next tree)
         coll []]
    (let [fltr #(if (z/branch? %) (conj coll (z/node %)) coll)]
      ; (println "..." n)
      ; (println "......" coll)
      (if (z/end? n)
        coll
        (recur (z/next n) (fltr n))))
    ))



#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "File size counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        state (reduce process-line (zip-treeseq '("/" ())) input)
        elfsystem (zip-treeseq (z/root state))
        filesizes (map file-sizes (directories elfsystem))
        inuse (second (file-sizes (z/node elfsystem)))]
    ; (println "Made a tree which is" elfsystem)
    (println "Sum of directories under 100000 is"
             (reduce + (map second (filter #(< (second %) 100000) filesizes))))
    (println "Total used space on filesystem is" inuse)
    (println "Clearance needed is" (- inuse 40000000))
    (println "Smallest directory big enough to delete is"
             (first (sort-by second (filter #(> (second %) (- inuse 40000000))
                                     filesizes))))
    ))
