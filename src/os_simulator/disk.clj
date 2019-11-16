(ns os-simulator.disk)

(def num-jobs 10)

(defn random-pos-int
  "generate randomly a positive int number"
  [n]
  (+ 1 (rand-int n)))

(defn random-priority
  "generate a priority number randomly (1 to 5)"
  []
  (random-pos-int 5))

(defn random-process-time
  "generate a process-time number randomly (1 to 5)"
  []
  (random-pos-int 50))

(defn new-job-random
  "Create a new job with random information"
  [id]
  (-> {}
      (assoc :id id)
      (assoc :priority random-priority)))

(defn get-jobs
  "return a list of auto-generated jobs"
  [arglist]
  (reduce #(conj %1 %2) [] (range 0 num-jobs)))

