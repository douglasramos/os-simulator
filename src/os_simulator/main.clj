(ns os-simulator.main
  (:require [os-simulator.simulator :as sim]))

(defn get-input
  "Waits for user to enter text and hit enter, then cleans the input"
  ([] (get-input nil))
  ([default]
   (let [input (clojure.string/trim (read-line))]
     (if (empty? input)
       default
       (clojure.string/lower-case input)))))

(defn handle-sim-mode!
  "Get input from user that tell the mode to run the simulator"
  []
  (do (println "\nChoose a simulator mode")
      (println "1) Automatic (10 Jobs auto generated)")
      (println "2) Step by Step (Jobs from .txt)")
      (get-input)))


(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nWelcome to the OS Simulator")
  (let [sim-mode (handle-sim-mode!)]
    (case sim-mode
      "1" (sim/automatic-run)
      "2" (sim/step-run)
      (println "Invalid option. Sorry! \uD83D\uDE2B"))))