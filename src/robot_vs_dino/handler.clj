(ns robot-vs-dino.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [robot-vs-dino.core :refer :all]
            [schema.core :as s]))

(def app
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Robot VS Dino API"
                   :description "Application to simulate a fight between Robots and Dinosaurs"}
            :tags [{:name "simulation", :description "simulation simple operations"}
                   {:name "echo", :description "request echoes"}
                   {:name "pizza", :description "pizza Api it is."}]}}}

   (context "/simulation" []
            :tags ["simulation"]

            (POST "/" []
                  ; :return
                 ; :query-params [x :- Long, y :- Long]
                  :summary "create a simulation"
                  (ok {:data (add-board (create-board))
                       :message "Simulation created with success"}))
                     ;(str "New Simulation state:\n\n" (format-board (create-board)))))))
            (GET "/" []
                 ; :return Total
                 ; :path-params [x :- Long, y :- Long]
                 :summary "get all simulations"
                 (ok {:data {:simulations_list
                             (get-all-boards)}
                      :message "Simulation created with success"})))))
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
