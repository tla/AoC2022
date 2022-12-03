(ns AoC.core
  (:gen-class))

;; This is the main executable; it takes as arguments a day of the advent calendar
;; and the string 'example' or 'input', and runs the appropriate module with the
;; appropriate input.

(defn -main
  "Run an advent calendar puzzle"
  [& args]
  (let [day (first args)
        runns (format "advent%s" day)
        data (first (rest args))
        fn (format "resources/%s/%s.txt" runns data) ]
    ;; We need to require the library for the day 
    (println (format "Running code for day %s on %s data" day data))
    (require (symbol (format "AoC.%s" runns)))
    ((resolve (symbol (format "AoC.%s/solve" runns))) fn)
    ))