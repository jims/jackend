(ns cabinet.core
  (:use clojure.core)
  (:import (kyotocabinet DB)))

(defn open-cabinet [cabinet]
  (let [dbmode (cabinet :mode (bit-or kyotocabinet.DB/OREADER kyotocabinet.DB/OWRITER kyotocabinet.DB/OCREATE))
        filename (cabinet :filename)
        db (.newInstance kyotocabinet.DB)]
    (.open db filename dbmode)
    {:db db :mode dbmode}))

(declare ^:dynamic *kcabinet*) ; used for scoped connections

(defmacro with-cabinet
  "Evaluates forms with a connection to the specified cabinet."
  [cabinet & forms]
  `(binding [*kcabinet* (open-cabinet ~cabinet)]
      (with-open [notused# (*kcabinet* :db)]
        (do ~@forms))))
