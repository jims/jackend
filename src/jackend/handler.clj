(ns jackend.handler
  (:use compojure.core
        ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as ring]
            [ring.middleware.json]))

(defroutes app-routes
  (GET "/" [] {:status 200 :body {:anchors-left 100}})
  (route/not-found {:status 404
                    :body "Not supported."}))

(def service-handler
  (-> 
    (handler/api app-routes)
    ring.middleware.json/wrap-json-response))

(defn run-server [port]
  (ring/run-jetty service-handler {:port port}))
