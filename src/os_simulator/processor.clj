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
(defn spooling!
  "spooling event. (1).
  Impure function with println side-effect"
  [[event-engine & rest]]
  (let [jobs (d/get-jobs)
        new-event-engine (as-> event-engine eg
                               (reduce e/add-event eg (get-jobs-events jobs))
                               (add-jobs eg jobs))]
    (println "Jobs from disk:")
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

(defn job->queue
  "Add a job to the cpu queue"
  [cpu job]
  (assoc cpu :queue (into [] (sort-by :priority (conj (:queue cpu) job)))))


;; TODO  make this functions small
(defn cpu-allocation
  "Allocate a job on the processor"
  [[eg memory cpu & rest]]
  (let [job (:current-job eg)]
    (if (empty? (:current-job cpu))
      ;; if
      (let [new-cpu (assoc cpu :current-job (start-job job (:current-time eg)))
            new-eg (add-exec-event eg job)]
        (into [new-eg memory new-cpu] rest))
      (if (> (:priority job) (get-in cpu [:current-job :priority]))
        ;; else if
        (let [new-cpu (-> cpu
                          (job->queue (:current-job cpu))
                          (assoc :current-job (start-job job (:current-time eg))))
              new-eg (-> eg
                         (remove-exec-events (get-in cpu [:current-job :id]))
                         (add-exec-event job))]
          (into [new-eg memory new-cpu] rest))
        ;; else
        (into [eg memory (update cpu :queue conj job)] rest)))))


(defn first-queue-->current-job
  [eg cpu current-time]
  (if (not-empty (:queue cpu))
    (let [job (peek (:queue cpu))
          new-cpu (assoc cpu :current-job (start-job job current-time)
                             :queue (pop (:queue cpu)))
          new-eg (add-exec-event eg job)]
      [new-eg new-cpu])
    (let [new-cpu (assoc cpu :current-job {})] [eg new-cpu])))

(defn job-execution
  "Start the job execution on the processor"
  [[eg mem cpu & rest]]
  (let [[eg updated-job] (j/update-time-left eg (:current-job cpu))
        [middle-eg new-cpu] (first-queue-->current-job eg cpu (:current-time eg))]
    (let [event-type (if (not (j/done? updated-job)) :io-interruption :job-complete)
          new-eg (e/add-new-event middle-eg (:current-time middle-eg) event-type (:id updated-job))]
      (into [new-eg mem new-cpu] rest))))