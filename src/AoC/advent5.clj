(ns AoC.advent5
  (:require [clojure.string :as str]))

;; First we have to read our input and make our data structures. Each stack of
;; crates will be a linked list, and the stacks will be items in a vector.

(defn add-to-stack
  ;; Until I figure out how to flatten a vector into an argument list...
  ([[cratestacks item] idx] (add-to-stack cratestacks item idx))
  ;; Given a vector of lists, return the same vector of lists
  ;; with `item` pushed onto the list at index i.
  ([cratestacks item idx]
   (vec (map #(if (and item (= % idx))
                (cons item (get cratestacks %))
                (get cratestacks %))
             (range (count cratestacks)))))
  )

(defn remove-from-stack
  ;; Given a stack of crates, return a vector consisting of the stack of crates
  ;; with the item removed, and the item that was removed.
  [cratestacks idx]
  [(vec (map #(if (= % idx)
                (rest (get cratestacks %))
                (get cratestacks %))
             (range (count cratestacks))))
   (first (get cratestacks idx))]
  )

;; Then we have to carry out orders like 'move 2 from 1 to 5'. We are given the order spec and the
;; existing set of stacks, and we need to return the reordered set of stacks.
(defn move-crates
  "Given a move order, rearrange the given crates in the stacks."
  [order, cratestacks]
  ;; I tried to do the following with a destructure but it just set all the variables to nil.
  (let [l (map #(Integer/parseInt %) (subvec (re-matches #"move (\d+) from (\d+) to (\d+)" order) 1))
        n (first l)
        origin (- (first (rest l)) 1)
        target (- (first (drop 2 l)) 1)]
    (loop [x 0
           cs cratestacks]
      (if (= x n)
        cs
        (recur (inc x) (add-to-stack (remove-from-stack cs origin) target))))
    )
  )

(defn stack-crates
  [crates cratestacks]
  (loop [i 0
         s cratestacks]
    (if (= i (count cratestacks))
      s
      (recur (inc i) (add-to-stack s (get crates i) i)))
    ))

(defn every-fourth-letter
  [line]
  (vec (map #(let [c (get line %)] (if (= c \space) nil c)) (filter #(= (mod % 4) 1) (range (count line)))))
  )

(defn init-crates
  "Given a list of lines, initialize the crate lists and return a vector of those lists."
  [spec]
  ;; We need to initialise our crates from the bottom to the top. The letter value of
  ;; each crate will be in respectively the 2nd, 6th, 10th... position of the line.
  ;; Conveniently, we have just handed ourself a spec whose lines are bottom to top.
  ;; The first line is just the number labels on the crates, indexed from 1, and we can
  ;; ignore it after we see how many crates there are..
  (let [numcrates (Integer/parseInt (re-find #"\d" (str/reverse (first spec))))]
    (loop [line (first (drop 1 spec))
           remaining (drop 2 spec)
           cratestacks (vec (repeat numcrates '()))]
      (if (empty? remaining)
        (stack-crates (every-fourth-letter line) cratestacks)
        (recur (first remaining)
               (rest remaining)
               (stack-crates (every-fourth-letter line) cratestacks)))
      ))

  )

(defn process-input
  [input]
  (loop [divider false
         cratespec []
         line (first input)
         following (rest input)]
    (if (empty? following)
      (move-crates line cratespec)
      (if (= line "")
        ;; If we reach the divider line, set up the crates and pass it to the future iterations of this loop.
        (recur true (init-crates cratespec) (first following) (rest following))
        ;; Depending on whether we are before or after the divider line, act appropriately
        (if divider (                                       ;; We are processing move instructions
                      recur divider (move-crates line cratespec) (first following) (rest following))
                    (                                       ;; We are building up the crate specification
                      recur divider (cons line cratespec) (first following) (rest following)))))))


(defn crate-tops
  "Given a set of crate stacks, read off the top crate in each stack"
  [crates]
  (str/join "" (map first crates) ))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Overlap counting"
  [& args]
  (let [fname "resources/advent5/example.txt"
        input (str/split (slurp fname) #"\n")]
    ;; We need to read lines from the input until we hit the blank line
    (println "Accessible crates are " (crate-tops (process-input input)))
    (println "Whatever next??")
    ))

(def input (str/split (slurp "/Users/tla/Projects/AoC2022/resources/advent5/input.txt") #"\n"))
(println (crate-tops (process-input input)))
