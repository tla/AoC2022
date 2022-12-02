(ns advent2.core
  (:gen-class)
  (:require [clojure.string :as str]))

;; I wanted to do this one without making a boring map like
;; {"A Z" -> 3, "A X" -> 7} and so on. So I reduced each letter to 
;; the value 0, 1, 2 for rock, paper, scissors respectively and then
;; used some mod-3 arithmetic to determine the winner. 

(defn win-points
  "Return the number of points for a given play. 
   6 for victory, 3 for tie, 0 for loss"
  [their-play my-play]
  (let [result (mod (- my-play their-play) 3)]
    (case result
      0 3
      1 6
      2 0)))

;; The score is, for some reason, a combination of the win points
;; and the value of the play itself. Well that's easy, just add 1
;; to the numerical value we were using to calculate the win.

(defn score
  "Return the score for a given play such as 'A Y'"
  [play]
  ;; Turn the letters into integers
  (let [their-play (- (int (get play 0)) 65)
        my-play (- (int (get play 2)) 88)
        score (+ my-play 1 (win-points their-play my-play))] 
    score))

;; Now just sum it all up for the whole input.

(defn -main
  "Card counting"
  [& args] 
  (println "Running on file" (first args))
  (let [input (str/split (slurp (first args)) #"\n")] 
    (println "We would have" (apply + (map score input)) "points in this game."))
)