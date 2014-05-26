(ns jackend.core
  (:use compojure.core
        ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET PUT]]
            [ring.adapter.jetty :as ring]
            [ring.middleware.json]))

(def services
  (atom []))

(defn add-service
  "Adds a service middleware function to the jackend backend."
  [service]
  (if (nil? service)
    (throw (IllegalArgumentException. "service cannot be nil.")))
  (swap! services conj service))

(defroutes all-routes
  (GET "/test" [] (response {:hesan (str @services)}))
  (fn [req] (reduce #(%2 %1) req @services))
  (route/not-found {:status 404 :body "Not supported."}))

(defn- wrap-allow-origin [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Access-Control-Allow-Origin"] "*"))))

(defn- wrap-handle-options-cors [handler]
  (fn [request]
    (if (= (:request-method request) :options)
      (let [response (response {:status 200})]
        (-> response
          (assoc-in [:headers "Access-Control-Allow-Methods"] "GET, POST, PUT")
          (assoc-in [:headers "Access-Control-Allow-Headers"] "Origin, X-Requested-With, Content-Type, Accept")))
      (handler request))))

(def service-handler
  (-> (handler/api all-routes)
      (wrap-handle-options-cors)
      (wrap-allow-origin)
      (ring.middleware.json/wrap-json-body)
      (ring.middleware.json/wrap-json-response)))

(defn run-server [port]
  (ring/run-jetty service-handler {:port port}))
