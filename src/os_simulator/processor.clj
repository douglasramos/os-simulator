(ns os-simulator.processor
  (:require [os-simulator.disk :as d]
            [os-simulator.event :as e]
            [os-simulator.job :as j]
            [clojure.pprint :as pp]))

(defn new-processor
  "Creates a new processor"
  []
  {:current-job {} :queue []})

(def legal {:sim "sim" :nao "nao"})

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


(defn start-job
  "Starts a job execution"
  [job current-time]
  (assoc job :last-start-time current-time))

(defn next-exec-event-time
  [current-time process-time io-op]
  (+ current-time (/ process-time (+ 1 io-op))))

(defn add-exec-event
  [event-engine job]
  (e/add-new-event event-engine
                   (next-exec-event-time (:current-time event-engine)
                                         (:process-time job)
                                         (:io-operations job))
                   :job-execution
                   (:id job)))
(defn remove-job-events
  "Returns a new events list without the next job events"
  [events job-id]
  (filter #(or (not= (:job-id %) job-id) false) events))

(defn remove-exec-events
  "Remove all io events related to a job from the
  event-engine event list"
  [eg job-id]
  (assoc eg :events (remove-job-events (:events eg) job-id)))

;; TODO  make this functions small
(defn cpu-allocation
  "Allocate a job on the processor"
  [[eg memory cpu & rest]]
  (let [job (j/current-event->job eg)]
    (if (empty? (:current-job cpu))
      ;; if
      (let [new-cpu (assoc cpu :current-job (start-job job (:current-time eg)))
            new-eg (add-exec-event eg job)]
        (into [new-eg memory new-cpu ] rest))
      (if (>= (:priority job) (get-in cpu [:current-job :priority]))
        ;; else if
        (let [new-cpu (-> cpu
                          (update :queue conj (:current-job cpu))
                          (assoc :current-job (start-job job (:current-time eg))))
              new-eg (-> eg
                         (remove-exec-events (get-in [:current-job :id] cpu))
                         (add-exec-event job))]
          (into [new-eg memory new-cpu] rest))
        ;; else
        (into [eg memory (update cpu :queue conj job)] rest)))))


(defn job-execution
  "Start the job execution on the processor"
  [[event-engine & rest]]
  (into [event-engine] rest))
