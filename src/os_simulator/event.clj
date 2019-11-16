(ns os-simulator.event)

(defn new-event
  "Creates a new event"
  ([time type]
   {:time time :type type})
  ([time type job-id]
   {:time time :type type :job-id job-id}))

(defn add-event
  "return new event-engine with a new list with new event included"
  [event-engine new-event]
  (let [events (conj (:events event-engine) new-event)
        sorted-events (into () (sort-by :time #(> %1 %2) events))]
    (assoc event-engine :events sorted-events)))


(defn add-new-event
  "return new event-engine with a new list with new event included"
  [event-engine & new-event-args]
  (let [new-event (apply new-event new-event-args)
        events (conj (:events event-engine) new-event)
        sorted-events (into () (sort-by :time #(> %1 %2) events))]
    (assoc event-engine :events sorted-events)))