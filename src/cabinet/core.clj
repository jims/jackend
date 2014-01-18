(ns cabinet.core
  "Contains functions to open/create, modify and query kyoto cabinet databases."
  (:require clojure.core
            cheshire.core)
  (:refer-clojure :exclude [get put])
  (:refer cheshire.core :only [generate-smile parse-smile])
  (:import (kyotocabinet DB)))

(defn open-cabinet [cabinet]
  (let [dbmode (cabinet :mode (bit-or DB/OREADER DB/OWRITER DB/OCREATE))
        filename (cabinet :filename)
        db (DB. DB/GEXCEPTIONAL)]
    (.open db filename dbmode)
    db))

(declare ^:dynamic *kcabinet*) ; used for scoped connections

(defmacro with-cabinet
  "Evaluates forms with a connection to the specified cabinet."
  [cabinet & forms]
  `(binding [*kcabinet* (open-cabinet ~cabinet)]
      (with-open [notused# *kcabinet*]
        (do ~@forms))))

(defn
  get
  ""
  ([key]
    (get key nil))
  ([key default]
    (when-not (string? key)
      (throw (IllegalArgumentException. "key must be a string")))
    (when-not (map? default)
      (throw (IllegalArgumentException. "The default value must be a map")))
    (if-let [value (.get *kcabinet* (.getBytes key))]
      (parse-smile value)
      default)))

(defn put
  "Write the value with the specified key to the database. This will overwrite any existing data."
  [key value]
  (when-not (string? key)
      (throw (IllegalArgumentException. "key must be a string")))
  (when-not (map? value)
    (throw (IllegalArgumentException. "The specified value is not a map")))
  (if-let [smile (generate-smile value)]
    (.set *kcabinet* (.getBytes key) smile)
    (throw (IllegalArgumentException. (str "The specified value '" value "' is malformed."))))
  value)
