(ns os-simulator.processor
  (:require [os-simulator.disk :as d]))

(defn new-processor
  "Creates a new processor"
  []
  {:current-job {} :queue []})

(defn spooling
  "spooling event. (1)"
  [[event-engine & rest]]
  (into [event-engine] rest))

(defn job-scheduling
  "docstring"
  [event-engine]
  event-engine)