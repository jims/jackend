(ns cabinet.core
  (:use clojure.core)
  (:import (kyotocabinet DB)))

(defmacro with-cabinet
  "Evaluates forms with a connection to the specified cabinet."
  [cabinet & forms]
  `(let [dbmode (cabinet :mode (bit-or kyotocabinet.DB/OREADER kyotocabinet.DB/OWRITER kyotocabinet.DB/OCREATE))
         filename (cabinet :filename)
         db (.newInstance kyotocabinet.DB)]
    (.open db filename dbmode)
    (binding [*kcabinet* {:db db :mode dbmode}]
      (with-open [db# (*kcabinet* :db)]
        (do ~@forms)))))
