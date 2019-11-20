(ns os-simulator.event-engine
  (:require [os-simulator.processor :as p]
            [os-simulator.memory :as m]
            [os-simulator.io :as io]))

(defn get-next-event
  "return the first event from the events list"
  [event-engine]
  (peek (:events event-engine)))

(defn get-next-current-time
  [event-engine]
  (:time (get-next-event event-engine)))

(defn sim-ended?
  "returns if the simulation has ended"
  [event-engine]
  (or (empty? (:events event-engine))
      (>= (get-next-current-time event-engine) (:duration event-engine))))

(defn new-event-engine [max-duration]
  {:duration       max-duration
   :current-time   0
   :events         ()
   :handled-events []
   :jobs           []
   :current-job    {}})

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
  (let [event (get-next-event event-engine)]
    (-> event-engine
        (remove-first-event)
        (event->handled-list event))))

(defn set-current-job
  [eg current-event]
  (assoc eg :current-job (when-let [job-id (:job-id current-event)]
                           (nth (:jobs eg) job-id))))

(defn next-event
  "Return a new event-engine a new current-event state"
  [event-engine]
  (let [current-event (get-next-event event-engine)]
    (-> event-engine
        (set-current-event current-event)
        (set-current-job current-event)
        (current-event->handled-list)
        (set-current-time (:time current-event)))))

(def get-action
  {:spooling        p/spooling!
   :job-scheduling  m/job-scheduling
   :proc-scheduling p/cpu-allocation
   :job-execution   p/job-execution
   :io-interruption io/io-interruption
   :release-disk    io/release-disk
   :job-complete    m/job-finishing})

(defn execute
  "Execute the event engine by one iteration and return a new
  event engine that represents the new state os the simulation"
  [event-engine memory processor io]
  (let [event-engine (next-event event-engine)
        type (get-in event-engine [:current-event :type])
        action (type get-action)]
    (action [event-engine memory processor io])))