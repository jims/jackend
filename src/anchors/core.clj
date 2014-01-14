(ns anchors.core
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :as c]))

(c/defroutes routes
  (c/GET "/anchors" [] (response {:anchors-left 100})))
