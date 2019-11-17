(ns os-simulator.job)

(defn get-job
  "Return a job by its id"
  [event-engine id]
  (first (filter #(= (:id %) id) (:jobs event-engine))))

(defn update-time-left
  [job current-time]
  (update job :time-left - (- current-time (:last-start-time job))))

(defn current-event->job
  "Return the job associated with the current-event of a event-engine"
  [event-engine]
  (let [job-id (get-in event-engine [:current-event :job-id])]
    (first (filter #(= (:id %) job-id) (:jobs event-engine)))))
