(ns jackend.core
  (:use compojure.core
        ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as ring]
            [ring.middleware.json]))

(def services
  (atom []))

(defn add-service
  "Adds a service middleware function to the jackend backend."
  [service]
  (swap! services conj service))

(defroutes all-routes
  (fn [req] (reduce #(%2 %1) req @services))
  (route/not-found {:status 404 :body "Not supported."}))

(def service-handler
  (-> 
    (handler/api all-routes)
    ring.middleware.json/wrap-json-response))

(defn run-server [port]
  (ring/run-jetty service-handler {:port port}))
