(ns os-simulator.memory
  (:require [os-simulator.event :as e]))

(defn new-memory
  "Creates a new memory"
  [size]
  {:available-space size
   :queue           []})

;(defn memory-allocation
;  "Memory Allocation (2).
;  Try to allocate a job on memory if it has enough available-space"
;  [event-engine job memory io]
;  (if (> (:available-space memory) (:memory-size job))))

(defn memory-allocation
  "docstring"
  [arglist]
  )