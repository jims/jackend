(ns cabinet.core
  "Contains functions to open/create, modify and query kyoto cabinet databases."
  (:require clojure.core
            cheshire.core)
  (:refer-clojure :exclude [get put])
  (:refer cheshire.core :only [generate-smile parse-smile])
  (:import (kyotocabinet DB Visitor)))

(declare ^:dynamic *kcabinet*) ; used for scoped connections

(defn- check-scope [fn]
  (if (nil? *kcabinet*)
    (throw (IllegalArgumentException. (str fn " has to be called within a with-cabinet scope.")))))

(defn- cursor-records [c]
  (let [key (.get_key_str c false)
        value (.get_value c false)
        record {key (parse-smile value keyword)}]
    (if (.step c)
      (cons record (lazy-seq (cursor-records c)))
      (do (.disable c) (list record)))))

(defn open-cabinet [cabinet]
  (let [dbmode (cabinet :mode (bit-or DB/OREADER DB/OWRITER DB/OCREATE))
        filename (cabinet :filename)
        db (DB. DB/GEXCEPTIONAL)]
    (.open db filename dbmode)
    db))

(defmacro with-cabinet
  "Evaluates forms with a connection to the specified cabinet."
  [cabinet & forms]
  `(binding [*kcabinet* (open-cabinet ~cabinet)]
      (with-open [notused# *kcabinet*]
        (do ~@forms))))

(defn get-all
  "Gets a lazy sequance of all the records in the database."
  []
  (check-scope "get-all")
  (let [c (.cursor *kcabinet*)]
      (.jump c)
      (-> (cursor-records c))))

(defn get
  ""
  ([key]
    (get key nil))
  ([key default]
    (when-not (string? key)
      (throw (IllegalArgumentException. "key must be a string")))
    (when-not (or (map? default) (nil? default))
      (throw (IllegalArgumentException. "The default value must be a map")))
    (check-scope "get")
    (if-let [value (.get *kcabinet* (.getBytes key))]
      (parse-smile value keyword) 
      default)))

(defn put
  "Write the value with the specified key to the database. This will overwrite any existing data."
  [key value]
  (when-not (string? key)
      (throw (IllegalArgumentException. "key must be a string")))
  (when-not (map? value)
    (throw (IllegalArgumentException. "The specified value is not a map")))
  (check-scope "put")
  (if-let [smile (generate-smile value)]
    (.set *kcabinet* (.getBytes key) smile)
    (throw (IllegalArgumentException. (str "The specified value '" value "' is malformed."))))
  value)
