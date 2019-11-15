(ns os-simulator.simulator
  (:require [os-simulator.event-engine :as eg]))

(def max-sim-duration-time 20)

(defn loop-sim
  "Execute simulation loop and return the sim results "
  [event-engine]
  (if (eg/sim-ended? event-engine)
    event-engine
    (loop-sim (eg/execute event-engine))))

(defn automatic-run []
  "Mode 1: runs a simulation with a auto-generated jobs
  and runs without user interaction"
  (let [startup {:time 0 :type :spooling}
        event-engine (eg/add-new-event (eg/new-event-engine max-sim-duration-time)
                                       startup)]
    (println (loop-sim event-engine))
    (println "\nBye Bye \uD83D\uDE0E")))

(defn step-run []
  "Mode 2: runs a simulation step-by-step wit
  jobs fetched from disk.txt. User interaction is allowed between steps"
  )