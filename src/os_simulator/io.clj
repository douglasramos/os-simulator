(ns os-simulator.io
  (:require [os-simulator.event :as e]))

(defn io-process-time
  [eg]
  (+ 2 (rand-int 12) (:current-time eg)))

(defn mew-io
  "Creates a new io operation handler"
  []
  {:available? true :queue []})

(defn io-interruption
  "Handles io interruption."
  [[eg mem cpu io]]
  (if (:available? io)
    (let [new-eg (e/add-new-event eg (io-process-time eg) :release-disk (get-in eg [:current-job :id]))]
      [new-eg mem cpu io])
    (let [new-io (-> io
                     (assoc :available? false)
                     (update :queue conj (:current-job eg)))]
      [eg mem cpu new-io])))


(defn release-disk
  "Release disk resource from the io-interruption"
  [[eg mem cpu io]]
  (let [eg (e/add-new-event eg (io-process-time eg) :proc-scheduling (get-in eg [:current-job :id]))]
    (if (not-empty (:queue io))
      (let [new-eg (e/add-new-event eg (io-process-time eg) :release-disk (peek (:queue io)))
            new-io (update io :queue pop)]
        [new-eg mem cpu new-io])
      (let [new-io (assoc io :available? true)]
        [eg mem cpu new-io]))))