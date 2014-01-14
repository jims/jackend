(ns jackend.main
  (:require [jackend.core :as jackend]
            [anchors.core :as anchors])
  (:gen-class))

(defn -main
  [& args]
  (jackend/add-service anchors/routes)
  (jackend/run-server 3000))
