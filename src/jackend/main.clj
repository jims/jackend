(ns jackend.main
  (:require [jackend.core :as jackend]
            [anchors.core :as anchors]
            [songs.core :as songs])
  (:gen-class))

;; It seems this only works with one service atm, it's probably due to a reduce not returning what I excpect in jackend.core.
;;(jackend/add-service anchors/routes)
(jackend/add-service songs/routes)

(def handler jackend/service-handler)

(defn -main
  [& args]
  (jackend/run-server (Integer/parseInt (or (first args) "3000"))))
