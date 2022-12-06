(ns AoC.advent5
  (:require [clojure.string :as str]))

;; First we have to read our input and make our data structures. Each stack of
;; crates will be a linked list, and the stacks will be items in a vector.

(defn add-to-stack
  ;; Given a vector of lists, return the same vector of lists
  ;; with `item` pushed onto the list at index i.
  [cratestacks item idx]
  (vec (map #(if (and item (= % idx))
               (cons item (get cratestacks %))
               (get cratestacks %))
             (range (count cratestacks))))
  )

(defn addn-to-stack
  ;; Given a set of crate stacks, return the set with the given items added to the
  ;; stack with index i.
  [cratestacks itemlist idx]
  (vec (map #(if (= % idx)
               (flatten (cons itemlist (get cratestacks %)))
               (get cratestacks %))
            (range (count cratestacks))))
  )

(defn remove-from-stack
  ;; Given a set of crate stacks, return the set with the given item removed from the given stack.
  [cratestacks idx]
  (vec (map #(if (= % idx)
                (rest (get cratestacks %))
                (get cratestacks %))
             (range (count cratestacks))))
  )

(defn removen-from-stack
  [cratestacks n idx]
  (vec (map #(if (= % idx)
               (drop n (get cratestacks %))
               (get cratestacks %))
            (range (count cratestacks)))))

(defn parse-order
  "Parse the move order, decrementing the origin and target for array indexing"
  [order]
  (let [parsed (map #(Integer/parseInt %) (subvec (re-matches #"move (\d+) from (\d+) to (\d+)" order) 1))]
    [(first parsed) (dec (second parsed)) (dec (last parsed))]))

;; Then we have to carry out orders like 'move 2 from 1 to 5'. We are given the order spec and the
;; existing set of stacks, and we need to return the reordered set of stacks.
(defn move-crates
  "Given a move order, rearrange the given crates in the stacks."
  [order, cratestacks]
  (let [[n origin target] (parse-order order)]
    (loop [x 0
           cs cratestacks]
      (if (= x n)
        cs
        (recur (inc x) (add-to-stack (remove-from-stack cs origin) (first (get cs origin)) target) ))))
  )

(defn moven-crates
  "Given a move order, rearrange the given crates in the stacks as one unit."
  [order cratestacks]
  (let [[n origin target] (parse-order order)]
    ;; (println "Cratestacks before move are" cratestacks)
    (addn-to-stack (removen-from-stack cratestacks n origin) (take n (get cratestacks origin)) target)))

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
  [input movefunc]
  (loop [divider false
         cratespec []
         line (first input)
         following (rest input)]
    (if (empty? following)
      (movefunc line cratespec)
      (if (= line "")
        ;; If we reach the divider line, set up the crates and pass it to the future iterations of this loop.
        (recur true (init-crates cratespec) (first following) (rest following))
        ;; Depending on whether we are before or after the divider line, act appropriately
        (if divider (                                       ;; We are processing move instructions
                      recur divider (movefunc line cratespec) (first following) (rest following))
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
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")]
    ;; We need to read lines from the input until we hit the blank line
    (println "Accessible crates are" (crate-tops (process-input input move-crates)))
    (println "Accessible crates afterward are" (crate-tops (process-input input moven-crates)))
    ))
