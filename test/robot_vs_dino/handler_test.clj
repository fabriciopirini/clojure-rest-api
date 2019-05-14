(ns robot-vs-dino.handler-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [robot-vs-dino.core :as dino]
            [robot-vs-dino.handler :as handler]
            [ring.mock.request :as mock]))

(def board-dimension dino/board-dimension)
(def board-total-size dino/board-total-size)

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(facts "Robot VS Dino API tests"
  (fact "Test GET request to /simulations returns expected response"
    (let [expected-body {:data {:simulationsList []}
                         :message "Simulation list was returned successfully"}
          response (handler/app (mock/request :get "/simulations"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test POST request to /simulations returns expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (vec (repeat board-total-size "⛶"))}
                         :message "Simulation created with success"}
          response (handler/app (mock/request :post "/simulations"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body)))
