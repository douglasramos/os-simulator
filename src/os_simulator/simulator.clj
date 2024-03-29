(ns os-simulator.simulator
  (:require [os-simulator.event-engine :as eg]
            [os-simulator.event :as e]
            [os-simulator.memory :as m]
            [os-simulator.processor :as p]
            [os-simulator.io :as io]
            [clojure.pprint :as pp]))

(def max-sim-duration-time 300)

(defn loop-sim!
  "Execute simulation loop and return the sim results "
  [tracing? count event-engine memory processor io]
  (do
    (when tracing?
      (println (str "\nIteration: " count))
      (pp/print-table (:events event-engine)))
    (if (eg/sim-ended? event-engine)
      event-engine
      (apply loop-sim! tracing? (inc count) (eg/execute event-engine memory processor io)))))


(defn automatic-run [tracing?]
  "Mode 1: runs a simulation with a auto-generated jobs
  and runs without user interaction"
  (let [startup (e/new-event 0 :spooling)
        event-engine (e/add-event (eg/new-event-engine max-sim-duration-time) startup)
        memory (m/new-memory 1000)
        processor (p/new-processor)
        io (io/mew-io)]
    (loop-sim! tracing? 1 event-engine memory processor io)))

(defn step-run [tracing?]
  "Mode 2: runs a simulation step-by-step wit
  jobs fetched from disk.txt. User interaction is allowed between steps"
  )