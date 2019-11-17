(ns os-simulator.memory
  (:require [os-simulator.event :as e]
            [os-simulator.job :as j]))

(defn new-memory
  "Creates a new memory"
  [size]
  {:available-space size
   :queue           []})

(defn decrease-available-space
  [memory value]
  (update memory :available-space - value))

(defn increase-available-space
  [memory value]
  (update memory :available-space + value))

(defn job->queue
  "Add a job to the memory queue"
  [memory job]
  (update memory :queue conj job))

(defn job-scheduling
  "Memory Allocation event handler (2)"
  [[eg memory & rest]]
  (let [job (j/current-event->job eg)]
    (if (> (:available-space memory) (:mem-size job))
      ;; true
      (let [new-eg (e/add-new-event eg (:current-time eg) :cpu-allocation (:id job))
            new-mem (decrease-available-space memory (:mem-size job))]
        (into [new-eg new-mem] rest))
      ;; false
      (let [new-mem (job->queue memory job)]
        (into [eg new-mem] rest)))))

(defn size-first-job-queue
  "Return the memory size of the first job on the queue"
  [memory]
  (:mem-size (peek (:queue memory))))

(defn can-move?
  "Can move the first queue element to the memory?"
  [memory]
  (when-not (empty? (:queue memory))
    (>= (:available-space memory) (size-first-job-queue memory))))

(defn queue->memory
  "Move the first queue element to the memory"
  [memory]
  (-> memory
      (decrease-available-space (:mem-size (peek (:queue memory))))
      (update :queue pop)))

(defn allocate-until-available
  "Allocate job on the queue to the memory until available
  generating new eg events to the processor allocation"
  [eg memory]
  (if (can-move? memory)
    ;;true
    (let [new-eg (e/add-new-event eg (:current-time eg) :cpu-allocation
                                  (:id (j/current-event->job eg)))
          new-memory (queue->memory memory)]
      (allocate-until-available new-eg new-memory))
    ;;false
    [eg memory]))

(defn processor-allocation
  "Processor allocation event handler (3)"
  [[eg memory & rest]]
  (let [job (j/current-event->job eg)
        mem (increase-available-space memory (:mem-size job))
        [new-eg new-mem] (allocate-until-available eg mem)]
    (into [new-eg new-mem] rest)))