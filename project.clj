(defproject robot-vs-dino "1.0.0"
            :description "Application to simulate a fight between Robots and Dinosaurs"
            :dependencies [[org.clojure/clojure "1.10.0"]
                           [metosin/compojure-api "1.1.12"]]
            :ring {:handler robot-vs-dino.handler/app}
            :main ^:skip-aot robot-vs-dino.core
            :uberjar-name "robot-vs-dino.jar"
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}
                       :dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                            [midje "1.9.8"]
                                            [ring/ring-mock "0.4.0"]
                                            [cheshire "5.8.1"]
                                            [proto-repl "0.3.1"]]
                             :plugins [[lein-ring "0.12.5"]
                                       [lein-midje "3.2.1"]]}})
