(ns AoC.advent9
  (:require [clojure.string :as str]
            [clojure.set :as s]))

(defn abs-range
  "Return a range that can go backward or forward"
  [a b]
  (if (< a b)
    (range a b)
    (reverse (map inc (range b a)))))

(defn calc-move
  "Given a new position of one knot and an old position of the following knot, 
   calculate where the following knot now is"
  [headpos tailpos]
  (let [hx (get headpos 0)
        hy (get headpos 1)
        tx (get tailpos 0)
        ty (get tailpos 1)
        xmoved (abs (- hx tx))
        ymoved (abs (- hy ty))]
    ;; If the magnitude of the X move is greater than the magnitude of the Y move,
    ;; then the tail should follow to the right Y axis, and vice versa
    (if (and (<= xmoved 1) (<= ymoved 1))
      tailpos
      (if (= xmoved ymoved)
        [(last (abs-range tx hx)) (last (abs-range ty hy))]
        (if (> xmoved ymoved)
          [(last (abs-range tx hx)) hy]
          [hx (last (abs-range ty hy))])
        ))))

(defn calc-all-moves
  [positions]
  (reduce (fn [r i] 
            (assoc r (inc i) (calc-move (get r i) (get r (inc i))))) 
          positions
          (range 0 (dec (count positions)))))


(defn store-moves
  "Move the head of the rope according to `move`, returning the new rope"
  [rope move]
  (let [hx (get (get rope 0) 0)
        hy (get (get rope 0) 1)
        [dir mag] (str/split move #"\s+")
        sign (if (str/includes? "LD" dir) - +)
        newhead (if (str/includes? "LR" dir)
                  [(sign hx (Integer/valueOf mag)) hy]
                  [hx (sign hy (Integer/valueOf mag))])]
    (calc-all-moves (assoc rope 0 newhead))
    ))

(defn drag-tail
  "Return a list of positions the tail passed through during a move"
  [startpos endpos]
  (let [sx (get startpos 0)
        sy (get startpos 1)
        ex (get endpos 0)
        ey (get endpos 1)]
    (loop [visited (set [startpos endpos])
           xrange (abs-range sx ex)
           yrange (abs-range sy ey)]
      (if (and (empty? xrange) (empty? yrange))
        visited
        (let [nextx (if (empty? xrange) ex (first xrange))
              nexty (if (empty? yrange) ey (first yrange))]
          (recur (conj visited [nextx nexty])
                 (rest xrange)
                 (rest yrange)))
        ))
    ))

(defn watch-tail-length
  "Initialize a rope of the given length, move the head according to input,
   and return a set of all the positions visited by the tail"
  [input length] 
  (loop [initrope (vec (repeat length [0 0]))
         line (first input)
         coming (rest input)
         visited #{}]
    (let [nextrope (store-moves initrope line)
          inittail (get initrope (dec length))
          nexttail (get nextrope (dec length))]
      (println "Rope is now" nextrope)
      (println "Tail visited" (sort (drag-tail inittail nexttail)))
      (if (empty? coming)
        (s/union visited (drag-tail inittail nexttail))
        (recur nextrope 
               (first coming) 
               (rest coming) 
               (s/union visited (drag-tail inittail nexttail)))))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn solve
  "Tree counting"
  [& args]
  (let [fname (first args)
        input (str/split (slurp fname) #"\n")]
    (println "Tail of 2-knot rope passed through"
             (count (watch-tail-length input 2))
             "positions" 
             )
    (println "Tail of 10-knot rope passed through"
             (count (watch-tail-length input 10))
             "positions")))