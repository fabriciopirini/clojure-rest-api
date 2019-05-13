(ns robot-vs-dino.handler
  (:require [compojure.api.sweet :refer [api context POST GET DELETE describe]]
            [ring.util.http-response :refer [ok not-found bad-request]]
            [robot-vs-dino.core :as dino]
            [schema.core :as s]))

(s/defschema Board
  {:data {:id Long
          :identifier s/Str
          :currentState [s/Str]}
   :message s/Str})

(s/defschema BoardNoData
  {:id Long
   :identifier s/Str
   :currentState [s/Str]})

(s/defschema FormattedBoard
  {:data {:id Long
          :identifier s/Str
          :currentState s/Str}
   :message s/Str})

(s/defschema FormattedBoardList
  {:id Long
   :identifier s/Str
   :currentState s/Str})

(s/defschema BoardList
  {:data {:simulations_list [BoardNoData]}
   :message s/Str})

(s/defschema Element
  {:data {:identifier s/Str
          :position_state s/Str}
   :message s/Str})


(def app
  (api
   {:swagger
      {:ui "/"
       :spec "/swagger.json"
       :data {:info {:title "Robot VS Dino API"
                     :description "Application to simulate a fight between Robots and Dinosaurs"
                     :version "1.0.0"}
              :tags [{:name "simulations", :description "basic functionality for simulations"}]
              :produces ["application/json"]}}}

   (context "/simulations" []
            :tags ["simulations"]
            ;
            ; (GET "/test" []
            ;      ; :return Test
            ;      :summary "get all simulations"
            ;      (let [board (dino/create-board)]
            ;        (ok {:data {:id (:id board)
            ;                    :identifier (:identifier board)
            ;                    :currentState (dino/format-board board)}
            ;             :message "test"})))

            (GET "/" []
                 :return BoardList
                 :summary "get all simulations"
                 (let [board-list (dino/get-all-boards)
                       got-list (dino/format-board-list)]
                   (if (empty? board-list)
                     (ok {:data {:simulations_list []}
                          :message "Simulation list was returned successfully"})
                     (if (nil? got-list)
                       (bad-request "The simulation list could not be retrieved")
                       (ok {:data {:simulations_list board-list}
                            :message "Simulation list was returned successfully"})))))

            (GET "/:simulationId" []
                 :return FormattedBoard
                 :summary "get a simulation given its ID"
                 :path-params [simulationId :- (describe Long "Simulation ID")]
                 (let [got-board (dino/get-board simulationId)]
                   (if (nil? got-board)
                     (not-found "Simulation does not exists")
                     (ok {:data {:id (:id got-board)
                                 :identifier (:identifier got-board)
                                 :currentState (dino/format-board got-board)}
                          :message "Simulation was returned successfully"}))))

            (GET "/:simulationId/elements/:col/:row" []
                 :return Element
                 :summary "get the element on given position inside the simulation"
                 :path-params [simulationId :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be retrieved"), row :- (describe Long "Row to be retrieved")]
                 (let [got-board (dino/get-board simulationId)
                       got-element (dino/get-element col row got-board)]
                   (if (nil? got-board)
                     (not-found "Simulation does not exists")
                     (if (nil? got-element)
                       (bad-request "The given position is invalid")
                       (ok {:data {:identifier (str "s" simulationId "-col" col "-row" row) :position_state got-element}
                            :message "Element was returned successfully"})))))

            (POST "/" []
                  :return FormattedBoard
                  :summary "create a simulation"
                  (let [created-board (dino/create-board)
                        added-board (dino/add-board created-board)]
                    (if (nil? created-board)
                      (bad-request "Simulation could not be created")
                      (if (nil? added-board)
                        (bad-request "The simulation could not be added to our simulation list")
                        (ok {:data {:id (:id added-board)
                                    :identifier (:identifier added-board)
                                    :currentState (dino/format-board added-board)}
                             :message "Simulation created with success"})))))

            (POST "/:simulationId/dinos/:col/:row" []
                  :return FormattedBoard
                  :path-params [simulationId :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :summary "create a dino inside an existing simulation"
                  (let [got-board (dino/get-board simulationId)
                        added-dino (dino/add-dino col row got-board)
                        updated (dino/update-board added-dino)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? added-dino)
                        (bad-request "Position is invalid, empty or already taken")
                        (if (nil? updated)
                          (bad-request "The simulation could not be updated")
                          (ok {:data {:id (:id added-dino)
                                      :identifier (:identifier added-dino)
                                      :currentState (dino/format-board added-dino)}
                               :message "Dino created with success"}))))))

            (POST "/:simulationId/robots/:col/:row" []
                  :return FormattedBoard
                  :path-params [simulationId :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :query-params [{direction :- (describe (s/enum "Looking up" "Looking down" "Looking left" "Looking right") "Direction it will be facing. If empty, it defaults to **Looking up**") "Looking up"}]
                  :summary "create a robot inside an existing simulation"
                  (let [dir-map {"Looking up" :T, "Looking down" :B, "Looking left" :L, "Looking right" :R}
                        got-board (dino/get-board simulationId)
                        added-robot (dino/add-robot col row (dir-map direction) got-board)
                        updated (dino/update-board added-robot)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? added-robot)
                        (bad-request "Position is invalid, empty or already taken")
                        (if (nil? updated)
                          (bad-request "The simulation could not be updated")
                          (ok {:data {:id (:id added-robot)
                                      :identifier (:identifier added-robot)
                                      :currentState (dino/format-board added-robot)}
                               :message "Robot created with success"}))))))


            (POST "/:simulationId/instructions/:col/:row" []
                  :return FormattedBoard
                  :path-params [simulationId :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be accessed"), row :- (describe Long "Row to be accessed")]
                  :query-params [{instruction :- (describe (s/enum "goForward" "goBackwards" "turnLeft" "turnRight") "Action to be executed. If empty, it defaults to **goForward**") "goForward"}]
                  :summary "move/rotate a robot inside an existing simulation"
                  (let [dir-map {"goForward" :F, "goBackwards" :B, "turnLeft" :L, "turnRight" :R}
                        got-board (dino/get-board simulationId)
                        action-robot (dino/take-action col row (dir-map instruction) got-board)
                        updated (dino/update-board action-robot)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? action-robot)
                        (bad-request "Action is invalid")
                        (if (nil? updated)
                          (bad-request "The simulation could not be updated")
                          (ok {:data {:id (:id action-robot)
                                      :identifier (:identifier action-robot)
                                      :currentState (dino/format-board action-robot)}
                               :message "Action was executed successfully"}))))))


            (POST "/:simulationId/attacks/:col/:row" []
                  :return FormattedBoard
                  :path-params [simulationId :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be accessed"), row :- (describe Long "Row to be accessed")]
                  :query-params [{attackDirection :- (describe (s/enum "up" "down" "toTheLeft" "toTheRight") "Direction in which the Robot will attack. If empty, it defaults to **up**") "up"}]
                  :summary "send an attack command to a robot inside an existing simulation"
                  (let [dir-map {"up" :T, "down" :B, "toTheLeft" :L, "toTheRight" :R}
                        got-board (dino/get-board simulationId)
                        attack-robot (dino/take-action col row :A (dir-map attackDirection) got-board)
                        updated-att (dino/update-board attack-robot)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? attack-robot)
                        (bad-request "Action is invalid")
                        (if (nil? updated-att)
                          (bad-request "The simulation could not be updated")
                          (ok {:data {:id (:id attack-robot)
                                      :identifier (:identifier attack-robot)
                                      :currentState (dino/format-board attack-robot)}
                               :message "Action was executed successfully"}))))))

            (DELETE "/:simulationId" []
                 :summary "delete a simulation given its ID"
                 :path-params [simulationId :- (describe Long "Simulation ID")]
                 (let [deleted-board (dino/delete-board simulationId)]
                   (if (nil? deleted-board)
                     (not-found "Simulation does not exists")
                     (ok {:data {:identifier (str "simulation-" simulationId)}
                          :message "Simulation was deleted successfully"}))))

            (DELETE "/:simulationId/elements/:col/:row" []
                 :summary "delete an element inside an existent simulation"
                 :path-params [simulationId :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be accessed"), row :- (describe Long "Row to be accessed")]
                 (let [got-board (dino/get-board simulationId)
                       removed-element (dino/remove-element col row got-board)
                       updated (dino/update-board removed-element)]
                   (if (nil? got-board)
                     (not-found "Simulation does not exists")
                     (if (nil? removed-element)
                       (bad-request "Position is invalid or empty")
                       (if (nil? updated)
                         (bad-request "The simulation could not be updated")
                         (ok {:data updated
                              :message "Element was deleted successfully"})))))))))
