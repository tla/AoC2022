(ns advent2.core
  (:gen-class)
  (:require [clojure.string :as str]))

;; I wanted to do this one without making a boring map like
;; {"A Z" -> 3, "A X" -> 7} and so on. So I reduced each letter to 
;; the value 0, 1, 2 for rock, paper, scissors respectively and then
;; used some mod-3 arithmetic to determine the winner.

(defn win-points
  "Return the number of points for a given play. 
   6 for victory, 3 for tie, 0 for loss plus (cardval + 1)"
  [their-play my-play]
  (let [result (mod (+ 1 (- my-play their-play)) 3)]
    (+ my-play 1 (* result 3))))

;; Alternatively we have to figure out how many points we got
;; based on whether we won, lost, or tied as indicated by the 
;; X Y Z. This means we have to reverse some of the math in
;; win-points above.

(defn gaming-points
  "Figure out how many points we get if we win, draw, or lose on spec"
  [their-play my-play]
  (+ (* 3 my-play) ;; Get the win/draw/loss points, easy
     (case my-play ;; Figure out what card we must have played.
                   ;; Not going to try to avoid `case`; it's late.
       0 (+ 1 (mod (+ their-play 2) 3))
       1 (+ 1 their-play)
       2 (+ 1 (mod (+ their-play 1) 3)))
     ))

;; The score is, for some reason, a combination of the win points
;; and the value of the play itself. Well that's easy, just add 1
;; to the numerical value we were using to calculate the win.

(defn score
  "Return the score for a given play such as 'A Y'"
  [play point-calculator]
  ;; Turn the letters into integers
  (let [their-play (- (int (get play 0)) 65)
        my-play (- (int (get play 2)) 88)
        score (point-calculator their-play my-play)] 
    (println "Play was" play ", score was" score)
    score))

;; Now just sum it all up for the whole input.

(defn -main
  "Card counting"
  [& args] 
  (println "Running on file" (first args))
  (let [input (str/split (slurp (first args)) #"\n")] 
    (println "We would have" (apply + (map #(score % win-points) input)) "points at first.")
    (println "We would have" (apply + (map #(score % gaming-points) input)) "points in the end."))
)