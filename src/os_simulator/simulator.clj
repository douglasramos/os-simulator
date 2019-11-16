(ns os-simulator.simulator
  (:require [os-simulator.event-engine :as eg]
            [os-simulator.event :as e]
            [os-simulator.memory :as m]
            [os-simulator.processor :as p]
            [os-simulator.io :as io]))

(def max-sim-duration-time 20)

(defn loop-sim
  "Execute simulation loop and return the sim results "
  [event-engine memory processor io]
  (if (eg/sim-ended? event-engine)
    event-engine
    (apply loop-sim (eg/execute event-engine memory processor io))))

(defn automatic-run []
  "Mode 1: runs a simulation with a auto-generated jobs
  and runs without user interaction"
  (let [startup (e/new-event 0 :spooling)
        event-engine (eg/add-new-event (eg/new-event-engine max-sim-duration-time) startup)
        memory (m/new-memory 1000)
        processor (p/new-processor)
        io (io/mew-io)]
    (println (loop-sim event-engine memory processor io))
    (println "\nBye Bye \uD83D\uDE0E")))

(defn step-run []
  "Mode 2: runs a simulation step-by-step wit
  jobs fetched from disk.txt. User interaction is allowed between steps"
  )