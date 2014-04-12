(ns songs.core
  (:require [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET PUT]]
            [cabinet.core :as db]))

(def database {:filename "songs.kch"})

(def ^:private defaults {:bought 0 :consumed 0})

(defmacro db-response
  [& forms]
  `(db/with-cabinet database
    (response (do ~@forms))))

(def ^:private update-db (partial db/put "anchors-balance"))

(defroutes routes
  (GET "/songs"
    []
    (db-response ())))
