(ns os-simulator.event-engine
  (:require [os-simulator.processor :as p]
            [os-simulator.memory :as m]))

(defn sim-ended?
  "returns if the simulation has ended"
  [event-engine]
  (or (>= (:current-time event-engine) (:duration event-engine))
      (empty? (:events event-engine))))


(defn new-event-engine [max-duration]
  {:duration       max-duration
   :current-time   0
   :events         ()
   :handled-events []
   :jobs           []})

(defn get-first-event
  "return the first event from the events list"
  [event-engine]
  (peek (:events event-engine)))

(defn set-current-event
  [event-engine event]
  (assoc event-engine :current-event event))

(defn set-current-time
  [event-engine current-time]
  (assoc event-engine :current-time current-time))

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

(defn next-event
  "Return a new event-engine a new current-event state"
  [event-engine]
  (let [current-event (get-first-event event-engine)]
    (-> event-engine
        (set-current-event current-event)
        (current-event->handled-list)
        (set-current-time (:time current-event)))))

;(def get-action
;  {:spooling          p/spooling
;   :job-scheduling    p/job-scheduling
;   :memory-allocation m/memory-allocation
;   :cpu-execution
;                      :disk-release
;   :io-interruption
;                      :job-finishing}
;  )

(def get-action
  {:spooling             p/spooling
   :job-scheduling       m/job-scheduling
   :processor-allocation m/processor-allocation})

;; TODO improve this method. Make more clear
(defn execute
  "Execute the event engine by one iteration and return a new
  event engine that represents the new state os the simulation"
  [event-engine memory processor io]
  (let [event-engine (next-event event-engine)
        key-word (get-in event-engine [:current-event :type])
        action (key-word get-action)]
    (action [event-engine memory processor io])))