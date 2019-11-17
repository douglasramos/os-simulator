(ns os-simulator.disk)

(def num-jobs 10)

(defn random-pos-int
  "generate randomly a positive int number"
  [n]
  (+ 1 (rand-int n)))

(defn random-io-op-quantity
  []
  (rand-int 5))

(defn random-size
  []
  (random-pos-int 400))

(defn random-priority
  "generate a priority number randomly (1 to 5)"
  []
  (random-pos-int 5))

(defn random-start-time
  "generate a process-time number randomly (1 to 5)"
  []
  (random-pos-int 50))

(defn new-job-random
  "Create a new job with random information"
  [id]
  (let [start-time (random-start-time)
        io-op-quantity (random-io-op-quantity)]
    (-> {}
        (assoc :id id)
        (assoc :priority (random-priority))
        (assoc :last-start-time start-time)
        (assoc :process-time (* (+ io-op-quantity 1) 10))
        (assoc :time-left start-time)
        (assoc :mem-size (random-size))
        (assoc :io-operations io-op-quantity))))

(defn get-jobs
  "return a list of auto-generated jobs"
  []
  (reduce #(conj %1 (new-job-random %2)) [] (range 0 num-jobs)))

