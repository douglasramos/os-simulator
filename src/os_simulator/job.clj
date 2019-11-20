(ns os-simulator.job)

(defn get-job
  "Return a job by its id"
  [event-engine id]
  (first (filter #(= (:id %) id) (:jobs event-engine))))

(defn update-time-left
  [eg job]
  (let [updated-job (update job :time-left - (- (:current-time eg) (:last-start-time job)))
        new-eg (update eg :jobs assoc (:id updated-job) updated-job)]
    [new-eg job]))

(defn done?
  [job]
  (<= (:time-left job) 0))

(defn current-event->job
  "Return the job associated with the current-event of a event-engine"
  [event-engine current-event]
  (let [job-id (:job-id current-event)]
    (when-let [jobs (:jobs event-engine)] (nth jobs job-id))))

;first (filter #(= (:id %) job-id) (:jobs event-engine))
