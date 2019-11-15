(defproject os-simulator "0.1.0-SNAPSHOT"
  :description "A Simulator for a generic operating system"
  :url "http://example.com/FIXME"
  :license {:name "MIT Licence"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :repl-options {:init-ns os-simulator.simulator}
  :main ^{:skip-aot true} os-simulator.simulator)

