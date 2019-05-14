(ns robot-vs-dino.core-test
  (:require [midje.sweet :refer :all]
            [robot-vs-dino.core :as dino]))

;; Definitions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def board-dimension dino/board-dimension)
(def board (dino/create-board))

;; Core Functions Tests
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(facts "Unit testing for core functions"
  (fact "Check board dimensions and elements on initialization"
    (count (:currentState board)) => (* board-dimension board-dimension)
    (set (:currentState board)) => '#{"⛶"})

  (fact "Check if board was added to board list"
    (not (nil? (dino/get-board (:id (dino/add-board board))))) => true)

  (fact "Check if board was deleted from board list"
    (do (dino/add-board board)
        (dino/delete-board  1)
        (dino/get-board 1)) => nil
    (do (dino/add-board board)
        (dino/add-board board)
        (dino/delete-board 1)
        (:currentState (dino/get-board 3)) => (:currentState board)))

  (fact "Check if the board list and IDs are being reset"
    (do (dino/add-board board)
        (dino/add-board board)
        (dino/reset-all-boards)
        (and
         (zero? @dino/id-seq)
         (empty? @dino/board-list))) => true)

  (fact "Check if board was deleted from board list"
    (do (dino/add-board board)
        (dino/delete-board 1)
        (:currentState (dino/get-board  1))) => nil

    (do (dino/add-board board)
        (dino/add-board board)
        (dino/delete-board 2)
        (dino/get-board 2)) => nil)

  (fact "The transformation from collumn and row to vector position"
    (dino/get-element-pos 1 1) => 0
    (dino/get-element-pos board-dimension board-dimension) => (dec (* board-dimension board-dimension)))

  (fact "The transformation from collumn and row to vector position"
      (dino/get-element 1 1 board) => "⛶"
      (dino/get-element 3 1 (dino/add-robot 3 1 board)) => "T")

  (fact "The position provided is inside the board"
    (dino/inside-board? -1 0) => nil
    (dino/inside-board? 0 0) => nil
    (dino/inside-board? 1 1) => true
    (dino/inside-board? board-dimension board-dimension) => true
    (dino/inside-board? (inc board-dimension) board-dimension) => nil
    (dino/inside-board? board-dimension (inc board-dimension)) => nil)

  (fact "The position is an empty space"
    (dino/is-space-available? 1 1 board) => true
    (dino/is-space-available? 3 4 (dino/add-dino 3 4 board)) => nil
    (dino/is-space-available? 3 4 (dino/add-robot 3 4 board)) => nil
    (dino/is-space-available? (inc board-dimension) 4 board) => nil)

  (fact "The position is related to a robot"
    (dino/is-robot? 2 3 (dino/add-robot 2 3 board)) => true
    (dino/is-robot? 3 2 (dino/add-dino 3 2 board)) => nil
    (dino/is-robot? 3 2 (dino/add-robot 4 4 board)) => nil
    (dino/is-robot? 1 (inc board-dimension) (dino/add-dino 2 3 board)) => nil)

  (fact "Add a robot to the simulation"
    (dino/get-element 2 3 (dino/add-robot 2 3 :R board)) => "R"
    (dino/get-element 3 2 (dino/add-robot 3 2 :B board)) => "B"
    (dino/get-element 4 4 (dino/add-robot 4 4 board)) => "T"
    (dino/add-robot 1 2 (dino/add-robot 1 2 board)) => nil)

  (fact "Add a dino to the simulation"
    (dino/get-element 2 3 (dino/add-dino 2 3 board)) => "D"
    (dino/get-element 3 2 (dino/add-dino 3 2 board)) => "D"
    (dino/get-element 4 5 (dino/add-dino 4 4 board)) => "⛶"
    (dino/add-dino 1 2 (dino/add-dino 1 2 board)) => nil)

  (fact "Remove non-empty element from simulation"
    (dino/get-element 2 3 (dino/remove-element 2 3 (dino/add-dino 2 3 board))) => "⛶"
    (dino/get-element 4 3 (dino/remove-element 4 3 (dino/add-robot 4 3 board))) => "⛶"
    (dino/remove-element 2 3 board) => nil
    (dino/remove-element 2 (inc board-dimension) board) => nil)

  (fact "The element is being moved to a new valid position"
    [(dino/get-element 2 2 (dino/move-element 2 3 2 2 (dino/add-robot 2 3 :T board)))
     (dino/get-element 2 3 (dino/move-element 2 3 2 2 (dino/add-robot 2 3 :T board)))] => ["T", "⛶"]

     [(dino/get-element (inc board-dimension) 2 (dino/move-element board-dimension 3 (inc board-dimension) 3 (dino/add-robot board-dimension 3 :T board)))
      (dino/get-element board-dimension 2 (dino/move-element board-dimension 3 (inc board-dimension) 3 (dino/add-robot board-dimension 3 :T board)))] => [nil nil])

  (fact "The element is being moved to a new valid position"
    (dino/get-element 2 3 (dino/turn-element 2 3 ["T" :R] (dino/add-robot 2 3 :T board))) => "R"
    (dino/get-element 5 5 (dino/turn-element 5 5 ["R" :L] (dino/add-robot 5 5 :R board))) => "T")

  (fact "A robot is attacking a valid empty space, a dino or outside the simulation"
    (dino/robot-attack 2 3 3 3 (dino/add-robot 2 3 :T board)) => (dino/add-robot 2 3 :T board)
    (dino/get-element 4 2 (dino/robot-attack 4 1 4 2 (dino/add-dino 4 2 (dino/add-robot 4 1 :R board)))) => "⛶"
    (dino/robot-attack board-dimension 3 (inc board-dimension) 3 (dino/add-robot board-dimension 3 :L board)) => (dino/add-robot board-dimension 3 :L board))

  (fact "Send any of the following actions: turn left, turn right, move forward and move backwards"
    (dino/get-element 2 2 (dino/take-action 2 3 :F (dino/add-robot 2 3 :T board))) => "T"
    (dino/get-element 2 3 (dino/take-action 3 3 :B (dino/add-robot 3 3 :R board))) => "R"
    (dino/get-element 4 2 (dino/take-action 4 1 :A :B (dino/add-dino 4 2 (dino/add-robot 4 1 :R board)))) => "⛶"))
