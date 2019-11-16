(ns os-simulator.io)

(defn mew-io
  "Creates a new io operation handler"
  []
  {:available? true :queue []})