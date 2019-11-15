(ns os-simulator.event-engine
  (:require [os-simulator.processor :as p]))

(defn sim-ended?
  "returns if the simulation has ended"
  [event-engine]
  (or (>= (:current-time event-engine) (:duration event-engine))
      (empty? (:events event-engine))))


(defn add-new-event
  "return new event-engine with a new list with new event included"
  [event-engine new-event]
  (let [events (conj (:events event-engine) new-event)
        sorted-events (into () (sort-by #(> %1 %2) :time events))]
    (assoc event-engine :events sorted-events)))

(defn new-event-engine [max-duration]
  {:duration       max-duration
   :current-time   0
   :currentEvent   0
   :events         ()
   :handled-events []})

(defn get-first-event
  "return the first event from the events list"
  [event-engine]
  (peek (:events event-engine)))

(defn next-event
  "Return the next event"
  [event-engine]
  (get-first-event event-engine))

(defn event->handled-list
  "add event to handled events list"
  [event-engine event]
  (assoc event-engine :handled-events (conj (:handled-events event-engine) event)))

(defn remove-first-event
  "remove the first event from list"
  [event-engine]
  (assoc event-engine :events (pop (:events event-engine))))

(defn current-event->handled-list
  "move the current-event (first on list) to the handled event list"
  [event-engine]
  (let [event (get-first-event event-engine)]
    (-> event-engine
        (remove-first-event)
        (event->handled-list event))))

(def get-action
  {:spooling p/spooling})

;; TODO improve this method. Make more clear
(defn execute
  "Execute the event engine by one iteration return a new
  event engine that represents the new state os the simulation"
  [event-engine]
  (let [current-event (next-event event-engine)
        event-engine (current-event->handled-list event-engine)
        key-word (:type current-event)
        action (key-word get-action)]
    (action event-engine)))