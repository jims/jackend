(ns songs.core
  (:require [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [cabinet.core :as db]))

(def ^:private database {:filename "songs.kch"})
(def ^:private defaults {:bought 0 :consumed 0})

(defmacro db-response
  [& forms]
  `(db/with-cabinet database
    (response (do ~@forms))))

(defroutes routes
  (GET "/songs" []
    (db-response (doall (db/get-all))))

  (POST "/songs" {params :params}
    (db-response 
      (doseq [[key value] params]
        (db/put (str key) value)))))
