(ns jackend.main
  (:require [jackend.core :as jackend]
            [anchors.core :as anchors])
  (:gen-class))

(jackend/add-service anchors/routes)

(def handler jackend/service-handler)

(defn -main
  [& args]
  (jackend/run-server 3000))
