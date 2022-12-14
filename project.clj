(defproject AoC "2022.07"
  :description "Advent of Code solutions // learning Clojure"
  :url "http://eccentricity.org/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ubergraph "0.8.2"]
                 [org.clojure/data.json "2.4.0"]]
  :main ^:skip-aot AoC.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
