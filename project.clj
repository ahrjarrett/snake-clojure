(defproject snake-clojure "0.1.0-SNAPSHOT"
  :description "A Clojure project to make Snake"
  :url "https://github.com/ahrjarrett/snake-clojure/"
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:uberjar {:aot [snake-clojure.app]}}
  :main snake-clojure.app)

