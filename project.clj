(defproject jackend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [com.fallabs/kyotocabinet-java "1.24"]]
  :plugins [[lein-ring "0.8.10"]]
  :main jackend.main
  :jvm-opts [~(str "-Djava.library.path=lib;" (System/getProperty "java.library.path"))]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})