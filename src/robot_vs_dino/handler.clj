(ns robot-vs-dino.handler
  (:require [compojure.api.sweet :refer [api context POST GET describe]]
            [ring.util.http-response :refer [ok not-found bad-request]]
            [robot-vs-dino.core :as dino]
            [schema.core :as s]))

; (defn get-board
;   [id]
;   (robot-vs-dino.core/get-board id))
;
; (defn add-dino
;   [col row board]
;   (robot-vs-dino.core/add-dino col row board))
;
; (defn update-board
;   [board]
;   (robot-vs-dino.core/update-board board))

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
                      (not-found "Board not found")
                      (if (nil? added-dino)
                        (bad-request "Position is invalid, empty or already taken")
                        (if (nil? updated)
                          (bad-request "The board could not be updated")
                          (ok {:data updated :message "Dino created with success"}))))))

            (POST "/robot/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :query-params [{direction :- (describe (s/enum :T :B :L :R) "Direction it will be facing: looking **T**op, **B**ottom, **L**eft or **R**ight. If empty, it defaults to Top") :T}]
                  :summary "create a robot inside an existing simulation"
                  (let [got-board (dino/get-board simulation-id)
                        added-robot (dino/add-robot col row direction got-board)
                        updated (dino/update-board added-robot)]
                    (if (nil? got-board)
                      (not-found "Board not found")
                      (if (nil? added-robot)
                        (bad-request "Position is invalid, empty or already taken")
                        (if (nil? updated)
                          (bad-request "The board could not be updated")
                          (ok {:data updated :message "Robot created with success"}))))))


            (POST "/move/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :query-params [{action :- (describe (s/enum :F :B :L :R) "Action it will be executed: move **F**orward or **B**ackwards, turn **L**eft or **R**ight and **A**ttack**. If empty, it defaults to move Forward") :F}]
                  :summary "move/rotate a robot inside an existing simulation"
                  (let [got-board (dino/get-board simulation-id)
                        action-robot (dino/take-action col row action got-board)
                        updated (dino/update-board action-robot)]
                    (if (nil? got-board)
                      (not-found "Board not found")
                      (if (nil? action-robot)
                        (bad-request "Action is invalid")
                        (if (nil? updated)
                          (bad-request "The board could not be updated")
                          (ok {:data updated :message "Action was executed successfully"}))))))


            (POST "/attack/:simulation-id/:col/:row" []
                  :path-params [simulation-id :- (describe Long "Simulation ID"), col :- (describe Long "Collumn to be added"), row :- (describe Long "Row to be added")]
                  :query-params [{attack-direction :- (describe (s/enum :T :B :L :R) "Direction in which the Robot will attack: **T**op, **B**ottom, **L**eft or **R**ight. If empty, it defaults to move Top.") :T}]
                  :summary "send an attack command to a robot inside an existing simulation"
                  (let [got-board (dino/get-board simulation-id)
                        attack-robot (dino/take-action col row :A attack-direction got-board)
                        updated-att (dino/update-board attack-robot)]
                    (if (nil? got-board)
                      (not-found "Board not found")
                      (if (nil? attack-robot)
                        (bad-request "Action is invalid")
                        (if (nil? updated-att)
                          (bad-request "The board could not be updated")
                          (ok {:data updated-att :message "Action was executed successfully"})))))))))

   ;          (POST "/minus" []
   ;                :return Total
   ;                :body-params [x :- Long, y :- Long]
   ;                :summary "x-y with body-parameters"
   ;                (ok {:total (- x y)}))
   ;
   ;          (GET "/times/:x/:y" []
   ;               :return Total
   ;               :path-params [x :- Long, y :- Long]
   ;               :summary "x*y with path-parameters"
   ;               (ok {:total (* x y)}))
   ;
   ;          (GET "/power" []
   ;               :return Total
   ;               :header-params [x :- Long, y :- Long]
   ;               :summary "x^y with header-parameters"
   ;               (ok {:total (long (Math/pow x y))})))
   ;
   ; (context "/echo" []
   ;          :tags ["echo"]
   ;
   ;          (GET "/request" req
   ;               (ok (dissoc req :body)))
   ;
   ;          (GET "/pizza" []
   ;               :return NewSingleToppingPizza
   ;               :query [pizza NewSingleToppingPizza]
   ;               :summary "get echo of a pizza"
   ;               (ok pizza))
   ;
   ;          (PUT "/anonymous" []
   ;               :return [{:secret Boolean s/Keyword s/Any}]
   ;               :body [body [{:secret Boolean s/Keyword s/Any}]]
   ;               (ok body))
   ;
   ;          (GET "/hello" []
   ;               :return String
   ;               :query-params [name :- String]
   ;               (ok (str "Hello, " name)))
   ;
   ;          (POST "/pizza" []
   ;                :return NewSingleToppingPizza
   ;                :body [pizza NewSingleToppingPizza]
   ;                :summary "post echo of a pizza"
   ;                (ok pizza)))
   ;
   ; (context "/pizzas" []
   ;          :tags ["pizza"]
   ;
   ;          (GET "/" []
   ;               :return [Pizza]
   ;               :summary "Gets all Pizzas"
   ;               (ok (get-pizzas)))
   ;
   ;          (POST "/" []
   ;                :return Pizza
   ;                :body [pizza NewPizza {:description "new pizza"}]
   ;                :summary "Adds a pizza"
   ;                (ok (add! pizza)))
   ;
   ;          (PUT "/" []
   ;               :return Pizza
   ;               :body [pizza Pizza]
   ;               :summary "Updates a pizza"
   ;               (ok (update! pizza)))
   ;
   ;          (GET "/:id" []
   ;               :return Pizza
   ;               :path-params [id :- Long]
   ;               :summary "Gets a pizza"
   ;               (ok (get-pizza id)))
   ;
   ;          (DELETE "/:id" []
   ;                  :path-params [id :- Long]
   ;                  :summary "Deletes a Pizza"
   ;                  (ok (delete! id))))))
