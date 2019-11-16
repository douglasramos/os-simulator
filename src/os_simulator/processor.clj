(ns os-simulator.processor
  (:require [os-simulator.disk :as d]
            [os-simulator.event :as e]
            [os-simulator.job :as j]
            [clojure.pprint :as pp]))

(defn new-processor
  "Creates a new processor"
  []
  {:current-job {} :queue []})



(defn get-jobs-events
  "Return a list of events from the job list.
  This events has the :job-scheduling type "
  [jobs]
  (map #(e/new-event
          (:last-start-time %1)
          :job-scheduling
          (:id %1))
       jobs))


(defn add-jobs [event-engine jobs]
  (assoc event-engine :jobs jobs))

;; TODO transform to a pure function
(defn spooling
  "spooling event. (1).
  Impure function cause with println side-effect"
  [[event-engine & rest]]
  (let [jobs (d/get-jobs)
        new-event-engine (as-> event-engine eg
                               (reduce e/add-event eg (get-jobs-events jobs))
                               (add-jobs eg jobs))]
    (pp/print-table
      [:id :priority :last-start-time :mem-size :io-operations]
      jobs)
    (into [new-event-engine] rest)))

;(defn job-scheduling
;  "Schedule the current job to be allocated to the memory"
;  [[event-engine & rest]]
;  (let [job (j/current-event->job event-engine)]
;    ()))
