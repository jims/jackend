(ns anchors.core
  (:require [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET PUT]]
            [cabinet.core :as db]))

(def database {:filename "anchors.kch"})

(def ^:private defaults {:bought 0 :consumed 0})

(defmacro db-response
  [& forms]
  `(db/with-cabinet database
    (response (do ~@forms))))

(def ^:private update-db (partial db/put "anchors-balance"))

(defroutes routes
  (GET "/anchor"
    []
    (db-response (db/get "anchors-balance" defaults)))

  (GET ["/anchor/drink/:amount" :amount #"[0-9]+"]
    [amount]
    (db-response
      (-> (db/get "anchors-balance" defaults)
          (update-in [:consumed] (partial + (Integer/parseInt amount)))
          (update-db))))
  
  (GET ["/anchor/refill/:amount" :amount #"[0-9]+"]
    [amount]
    (db-response
      (-> (db/get "anchors-balance" defaults)
          (update-in [:bought] (partial + (Integer/parseInt amount)))
          (update-db)))))
