# Advent of Code 2022 solutions

Letting the world watch as I bumble my way through Clojure

## Milestones

* Day 1: figured out how to run anything at all
* Day 2: got the hang of `let` in order to not need `def` all the time
* Day 3: started to figure out namespaces, made runner
* Day 4: trying to minimise use of `let`; trying to figure out Calva in VS Code.
* Day 5: drowning in state manipulation using immutable objects. DROWNING. Also, banging my head against lazy-eval lists
* Day 6: a proper holiday after day 5. Tried out exception handling, for giggles
* Day 7: Holiday over!! The `clojure.zip` library has a pretty large learning curve ("next" is the same as "down" when you are starting? They could have mentioned that? *sigh*)


## If you want

The app's `-main` function takes two arguments: the advent calendar day, 
and a string (usually `"example"` or `"input"`) to indicate which set of
data should be run for the day's puzzle. The relevant data files are stored
in named directories under `resources`.

Thus, to get the solutions for day 3 given the puzzle input (and assuming you have Leiningen installed), you can run

    $ lein run 3 input

and get your answers. Maybe on a later day I will figure out how to make it run independently of Leiningen.