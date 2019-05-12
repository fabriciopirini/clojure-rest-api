(ns robot-vs-dino.handler
  (:require [compojure.api.sweet :refer [api context POST GET DELETE describe]]
            [ring.util.http-response :refer [ok not-found bad-request]]
            [robot-vs-dino.core :as dino]
            [schema.core :as s]))

(s/defschema Board
  {:id Long
   :identifier s/Str
   :simulation_state s/Str})

(def app
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Robot VS Dino API"
                   :description "Application to simulate a fight between Robots and Dinosaurs"}
            :tags [{:name "simulation", :description "simulation simple operations"}]}}}

   (context "/simulation" []
            :tags ["simulation"]

            (GET "/test" []
                 :return Board
                 :summary "get all simulations"
                 (let [board (dino/create-board)]
                   (ok {:id (:id board)
                        :identifier (:identifier board)
                        :simulation_state (dino/format-board board)})))



            (GET "/all" []
                 :summary "get all simulations"
                 (let [got-list (dino/get-all-boards)]
                   (if (nil? got-list)
                     (bad-request "The simulation list could not be retrieved")
                     (ok {:data {:simulations_list got-list}
                          :message "Simulation list was returned successfully"}))))

            (GET "/:simulation-id" []
                 :summary "get a simulation given its ID"
                 :path-params [simulation-id :- (describe Long "Simulation ID")]
                 (let [got-board (dino/get-board simulation-id)]
                   (if (nil? got-board)
                     (not-found "Simulation does not exists")
                     (ok {:data got-board
                          :message "Simulation was returned successfully"}))))

            (GET "/:simulation-id/:col/:row" []
                 :summary "get the element on given position inside the simulation"
                 :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be retrieved"), row :- (describe Long "Row to be retrieved")]
                 (let [got-board (dino/get-board simulation-id)
                       got-element (dino/get-element col row got-board)]
                   (if (nil? got-board)
                     (not-found "Simulation does not exists")
                     (if (nil? got-element)
                       (bad-request "The given position is invalid")
                       (ok {:data {:identifier (str "s" simulation-id "-col" col "-row" row) :position_state got-element}
                            :message "Element was returned successfully"})))))

            (POST "/" []
                  :summary "create a simulation"
                  (let [created-board (dino/create-board)
                        added-board (dino/add-board created-board)]
                    (if (nil? created-board)
                      (bad-request "Simulation could not be created")
                      (if (nil? added-board)
                        (bad-request "The simulation could not be added to our simulation list")
                        (ok {:data added-board :message "Simulation created with success"})))))

            (POST "/dino/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :summary "create a dino inside an existing simulation"
                  (let [got-board (dino/get-board simulation-id)
                        added-dino (dino/add-dino col row got-board)
                        updated (dino/update-board added-dino)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? added-dino)
                        (bad-request "Position is invalid, empty or already taken")
                        (if (nil? updated)
                          (bad-request "The simulation could not be updated")
                          (ok {:data updated :message "Dino created with success"}))))))

            (POST "/robot/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :query-params [{direction :- (describe (s/enum "Looking up" "Looking down" "Looking left" "Looking right") "Direction it will be facing. If empty, it defaults to **Looking up**") "Looking up"}]
                  :summary "create a robot inside an existing simulation"
                  (let [dir-map {"Looking up" :T, "Looking down" :B, "Looking left" :L, "Looking right" :R}
                        got-board (dino/get-board simulation-id)
                        added-robot (dino/add-robot col row (dir-map direction) got-board)
                        updated (dino/update-board added-robot)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? added-robot)
                        (bad-request "Position is invalid, empty or already taken")
                        (if (nil? updated)
                          (bad-request "The simulation could not be updated")
                          (ok {:data updated :message "Robot created with success"}))))))


            (POST "/instruction/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be accessed"), row :- (describe Long "Row to be accessed")]
                  :query-params [{instruction :- (describe (s/enum "Go forward" "Go backwards" "Turn left" "Turn right") "Action to be executed. If empty, it defaults to **Go forward**") "Go forward"}]
                  :summary "move/rotate a robot inside an existing simulation"
                  (let [dir-map {"Go forward" :F, "Go backwards" :B, "Turn left" :L, "Turn right" :R}
                        got-board (dino/get-board simulation-id)
                        action-robot (dino/take-action col row (dir-map instruction) got-board)
                        updated (dino/update-board action-robot)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? action-robot)
                        (bad-request "Action is invalid")
                        (if (nil? updated)
                          (bad-request "The simulation could not be updated")
                          (ok {:data updated :message "Action was executed successfully"}))))))


            (POST "/attack/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be accessed"), row :- (describe Long "Row to be accessed")]
                  :query-params [{attack-direction :- (describe (s/enum "Up" "Down" "To the left" "To the right") "Direction in which the Robot will attack. If empty, it defaults to **Up**") "Up"}]
                  :summary "send an attack command to a robot inside an existing simulation"
                  (let [dir-map {"Up" :T, "Down" :B, "To the left" :L, "To the right" :R}
                        got-board (dino/get-board simulation-id)
                        attack-robot (dino/take-action col row :A (dir-map attack-direction) got-board)
                        updated-att (dino/update-board attack-robot)]
                    (if (nil? got-board)
                      (not-found "Simulation does not exists")
                      (if (nil? attack-robot)
                        (bad-request "Action is invalid")
                        (if (nil? updated-att)
                          (bad-request "The simulation could not be updated")
                          (ok {:data updated-att :message "Action was executed successfully"}))))))

            (DELETE "/:simulation-id" []
                 :summary "delete a simulation given its ID"
                 :path-params [simulation-id :- (describe Long "Simulation ID")]
                 (let [deleted-board (dino/delete-board simulation-id)]
                   (if (nil? deleted-board)
                     (not-found "Simulation does not exists")
                     (ok {:data {:identifier (str "simulation-" simulation-id)}
                          :message "Simulation was deleted successfully"}))))

            (DELETE "/:simulation-id/:col/:row" []
                 :summary "delete an element inside an existent simulation"
                 :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be accessed"), row :- (describe Long "Row to be accessed")]
                 (let [got-board (dino/get-board simulation-id)
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
