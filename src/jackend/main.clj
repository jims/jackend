(ns jackend.main
  (:require jackend.handler)
  (:gen-class))

(defn -main
  [& args]
  (jackend.handler/run-server 3030))
