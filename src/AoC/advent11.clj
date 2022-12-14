(ns AoC.advent11
  (:require [clojure.string :as str]
            [clojure.data.json :as json]))

;; A monkey looks like this
;; {:id 0
;;  :held [79 98]
;;  :op #(mod % 19)
;;  :test #(= 0 (/ % 23))
;;  :true-to 2
;;  :false-to 3}

;; This is absurd; I will store inspection counts in a global variable
;; even though it is not the Clojure way. It is late and I'm tired.

(defn monkey-handoff
  [monkeylist item from to]
  (println "Monkey" from "handing off item" item "to monkey" to)
  (map
   (fn [x] (if (= from (:id x))
             (update (update x :held #(vec (rest %))) :inspected inc)
             (if (= to (:id x))
               (update x :held #(conj % item))
               x)))
   monkeylist))

(defn inspect-next-item
  "Some monkey should inspect the first item on its `held` list and 
   dispose of it. Give back an updated array of monkeys"
  [monkeylist monkey] 
  (let [item (first (:held monkey))
        testop (:test monkey)
        opop (:op monkey)]
    (if item 
      ;; For the first answer we divided (opop item) by 3. 
      (let [inspected (long (/ (opop item) 3))]
        (if (testop inspected)
          (monkey-handoff monkeylist inspected (:id monkey) (:true-to monkey))
          (monkey-handoff monkeylist inspected (:id monkey) (:false-to monkey))))
      monkeylist  ;; The monkey has nothing to throw and its turn ends
      )))

(defn monkey-turn
  [monkeylist monkid] 
    (loop [newlist monkeylist]
      (let [monkey (first (filter #(= (:id %) monkid) newlist))
            item (first (:held monkey))]
        (if item
          (recur (inspect-next-item newlist monkey))
          newlist))))

(defn make-operation
  "Given a string for the operation, make a function that does this"
  [opstring]
  (println "Working on" opstring)
  (let [matches (re-find #"new = old (.) (.*)$" opstring)
        operator (if (= (get matches 1) "+") + *)
        other (get matches 2)]
    (if (= other "old")
      (fn [x] (operator x x))
      (fn [x] (operator x (Integer/parseInt other))))
    ))

(defn monkey-init
  "Initialise a single monkey from its input definition. This hard-codes the format"
  [monkeydef]
  (let [idmatch (Integer/parseInt (re-find #"\d+" (get monkeydef 0)))
        items (str/join ["[" (get (str/split (get monkeydef 1) #": ") 1) "]"])
        oper (make-operation (get monkeydef 2))
        testmod (Integer/parseInt (get (str/split (get monkeydef 3) #" by ") 1))
        iftrue (Integer/parseInt (get (str/split (get monkeydef 4) #"monkey ") 1))
        iffalse (Integer/parseInt (get (str/split (get monkeydef 5) #"monkey ") 1))]
    {:id idmatch
     :held (json/read-str items)
     :inspected 0
     :op oper
     :test #(= 0 (mod % testmod))
     :true-to iftrue
     :false-to iffalse})
  )
;; => #'AoC.advent11/monkey-init


(defn monkeylist-init
  "Initialise a bunch of monkeys from the input lines"
  [input]
  ;; Split the array of input lines according to blanks
  (loop [monkeylist '()
        first-def (vec (take 6 input))
        rest-def (drop 7 input)]
    (if (empty? rest-def)
      (cons (monkey-init first-def) monkeylist)
      (recur (cons (monkey-init first-def) monkeylist) (vec (take 6 rest-def)) (drop 7 rest-def))))
  )

(defn round
  "Have all the monkeys take turns and return the resulting monkey state"
  [monkeylist]
  ; (println "Starting a round of monkey flinging")
  (loop [ml monkeylist
         i 0]
    (if (= i (count monkeylist))
      ml
      (recur (monkey-turn ml i) (inc i)))
    )
  )

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Monkey counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")
        rounds 20
        monkeylist (monkeylist-init input)
        finalml (map :inspected
                     (loop [n 0
                            ml monkeylist]
                       (when (= 0 (mod n 500))
                         (println "Round" n))
                       (if (= n rounds)
                         ml
                         (recur (inc n) (round ml)))))]

      (println "Final state is" finalml)
      (println "Monkey business level is" (apply * (take 2 (sort > finalml))))) 
    )