(ns robot-vs-dino.handler-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [robot-vs-dino.core :as dino]
            [robot-vs-dino.handler :as handler]
            [ring.mock.request :as mock]))

;; Definitions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def board-dimension dino/board-dimension)
(def board-total-size dino/board-total-size)

;; Body parser
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

;; Endpoints Tests
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
      body => expected-body))

  (fact "Test GET request to /simulations inserting a new simulation and returning it as expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (vec (repeat board-total-size "⛶"))}
                         :message "Simulation was returned successfully"}
          response (handler/app (mock/request :get "/simulations/1"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test GET request to /simulations/:simulationId/elements/:col/:row returns element at that position as expected response"
    (let [expected-body {:data {:identifier "s1-col5-row7"
                                :element "⛶"}
                         :message "Element was returned successfully"}
          response (handler/app (mock/request :get "/simulations/1/elements/5/7"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test POST request to /simulations/:simulationId/dinos/:col/:row to insert a new dino and returns expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (assoc-in (vec (repeat board-total-size "⛶")) [55] "D")}
                         :message "Dino created with success"}
          response (handler/app (mock/request :post "/simulations/1/dinos/6/2"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test POST request to /simulations/:simulationId/robots/:col/:row to insert a new robot and returns expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (-> (repeat board-total-size "⛶")
                                                  (vec)
                                                  (assoc-in [55] "D")
                                                  (assoc-in [56] "L"))}
                         :message "Robot created with success"}
          response (handler/app (mock/request :post "/simulations/1/robots/7/2?direction=lookingLeft"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test POST request to /simulations/:simulationId/instructions/:col/:row to send an instruction to a robot and returns expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (-> (repeat board-total-size "⛶")
                                                  (vec)
                                                  (assoc-in [55] "D")
                                                  (assoc-in [56] "T"))}
                         :message "Action was executed successfully"}
          response (handler/app (mock/request :post "/simulations/1/instructions/7/2?instruction=turnRight"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test POST request to /simulations/:simulationId/attacks/:col/:row to send an attack command to a robot and returns expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (-> (repeat board-total-size "⛶")
                                                  (vec)
                                                  (assoc-in [56] "T"))}
                         :message "Action was executed successfully"}
          response (handler/app (mock/request :post "/simulations/1/attacks/7/2?attackDirection=toTheLeft"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test DELETE request to /simulations/:simulationId/elements/:col/:row to delete an element inside a simulation and returns expected response"
    (let [expected-body {:data {:id 1
                                :identifier "simulation-1"
                                :currentState (vec (repeat board-total-size "⛶"))}
                         :message "Element was deleted successfully"}
          response (handler/app (mock/request :delete "/simulations/1/elements/7/2"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body))

  (fact "Test DELETE request to /simulations/:simulationId to delete an simulation and returns expected response"
    (let [expected-body {:data {:identifier "simulation-1"}
                         :message "Simulation was deleted successfully"}
          response (handler/app (mock/request :delete "/simulations/1"))
          body     (parse-body (:body response))]
      (:status response) => 200
      body => expected-body)))
