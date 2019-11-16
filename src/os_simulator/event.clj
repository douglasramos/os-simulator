(ns os-simulator.event)

(defn new-event
  "Creates a new event"
  ([time type]
   {:time time :type type})
  ([time type job-id]
   {:time time :type type :job-id job-id}))

