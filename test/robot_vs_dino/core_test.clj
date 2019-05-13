(ns robot-vs-dino.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [robot-vs-dino.core :as dino]))

(def board-dimension dino/board-dimension)
(def board (dino/create-board))

(deftest create-board-test
  (testing "Check board dimensions and elements on initialization"
    (is (= (* board-dimension board-dimension) (count (:currentState board))))
    (is (= '#{"⛶"} (set (:currentState board))))))

(deftest add-board-test
  (testing "Check if board was added to board list"
    (is (not= nil? (@dino/board-list (:id (dino/add-board board)))))))

(deftest delete-board-test
  (testing "Check if board was deleted from board list"
    (is (nil? (do (dino/reset-all-boards)
                  (dino/add-board board)
                  (dino/delete-board  1)
                  (dino/get-board 1))))
    (is (= (:currentState board)
           (do (dino/reset-all-boards)
               (dino/add-board (dino/create-board))
               (dino/add-board (dino/create-board))
               (dino/delete-board  1)
               (:currentState (dino/get-board 2)))))))

(deftest reset-all-boards-test
  (testing "Check if the board list and IDs are being reset"
    (is (true? (do (dino/reset-all-boards)
                   (dino/add-board board)
                   (dino/add-board board)
                   (dino/reset-all-boards)
                   (and
                    (zero? @dino/id-seq)
                    (empty? @dino/board-list)))))))

(deftest get-board-test
  (testing "Check if board was deleted from board list"
    (is (= board
           (do (dino/reset-all-boards)
               (dino/add-board board)
               (dino/get-board  1))))
    (is (nil? (do (dino/reset-all-boards)
                  (dino/add-board board)
                  (dino/get-board  2))))))

(deftest get-element-pos-test
  (testing "The transformation from collumn and row to vector position"
    (is (zero? (dino/get-element-pos 1 1)))
    (is (= (dec (* board-dimension board-dimension)) (dino/get-element-pos board-dimension board-dimension)))))

(deftest get-element-test
  (testing "The transformation from collumn and row to vector position"
    (is (= "⛶" (dino/get-element 1 1 board)))
    (is (= "T" (dino/get-element 3 1 (dino/add-robot 3 1 board))))))

(deftest inside-board?-test
  (testing "The position provided is inside the board")
  (is (nil? (dino/inside-board? -1 0)))
  (is (nil? (dino/inside-board? 0 0)))
  (is (true? (dino/inside-board? 1 1)))
  (is (true? (dino/inside-board? board-dimension board-dimension)))
  (is (nil? (dino/inside-board? (inc board-dimension) board-dimension)))
  (is (nil? (dino/inside-board? board-dimension (inc board-dimension)))))

(deftest is-space-available?-test
  (testing "The position is an empty space")
  (is (true? (dino/is-space-available? 1 1 board)))
  (is (nil? (dino/is-space-available? 3 4 (dino/add-dino 3 4 board))))
  (is (nil? (dino/is-space-available? 3 4 (dino/add-robot 3 4 board))))
  (is (nil? (dino/is-space-available? (inc board-dimension) 4 board))))

(deftest is-robot?-test
  (testing "The position is related to a robot")
  (is (true? (dino/is-robot? 2 3 (dino/add-robot 2 3 board))))
  (is (nil? (dino/is-robot? 3 2 (dino/add-dino 3 2 board))))
  (is (nil? (dino/is-robot? 3 2 (dino/add-robot 4 4 board))))
  (is (nil? (dino/is-robot? 1 (inc board-dimension) (dino/add-dino 2 3 board)))))

(deftest add-robot-test
  (testing "Add a robot to the simulation"
    (is (= "R" (dino/get-element 2 3 (dino/add-robot 2 3 :R board))))
    (is (= "B" (dino/get-element 3 2 (dino/add-robot 3 2 :B board))))
    (is (= "T" (dino/get-element 4 4 (dino/add-robot 4 4 board))))
    (is (nil? (dino/add-robot 1 2 (dino/add-robot 1 2 board))))))

(deftest add-dino-test
  (testing "Add a dino to the simulation"
    (is (= "D" (dino/get-element 2 3 (dino/add-dino 2 3 board))))
    (is (= "D" (dino/get-element 3 2 (dino/add-dino 3 2 board))))
    (is (= "⛶" (dino/get-element 4 5 (dino/add-dino 4 4 board))))
    (is (nil? (dino/add-dino 1 2 (dino/add-dino 1 2 board))))))

(deftest remove-element-test
  (testing "Remove non-empty element from simulation"
    (is (= "⛶" (dino/get-element 2 3 (dino/remove-element 2 3 (dino/add-dino 2 3 board)))))
    (is (= "⛶" (dino/get-element 4 3 (dino/remove-element 4 3 (dino/add-robot 4 3 board)))))
    (is (nil? (dino/remove-element 2 3 board)))
    (is (nil? (dino/remove-element 2 (inc board-dimension) board)))))

(deftest move-element-test
  (testing "The element is being moved to a new valid position"
    (is (= ["T", "⛶"]
           [(dino/get-element 2 2 (dino/move-element 2 3 2 2 (dino/add-robot 2 3 :T board)))
            (dino/get-element 2 3 (dino/move-element 2 3 2 2 (dino/add-robot 2 3 :T board)))]))
    (is (= [nil nil]
           [(dino/get-element (inc board-dimension) 2 (dino/move-element board-dimension 3 (inc board-dimension) 3 (dino/add-robot board-dimension 3 :T board)))
            (dino/get-element board-dimension 2 (dino/move-element board-dimension 3 (inc board-dimension) 3 (dino/add-robot board-dimension 3 :T board)))]))))

(deftest turn-element-test
  (testing "The element is being moved to a new valid position"
    (is (= "R" (dino/get-element 2 3 (dino/turn-element 2 3 ["T" :R] (dino/add-robot 2 3 :T board)))))
    (is (= "T" (dino/get-element 5 5 (dino/turn-element 5 5 ["R" :L] (dino/add-robot 5 5 :R board)))))))

(deftest robot-attack-test
  (testing "A robot is attacking a valid empty space or a dino"
    (is (not (nil? (dino/robot-attack 2 3 3 3 (dino/add-robot 2 3 :T board)))))
    (is (= "⛶" (dino/get-element 4 2 (dino/robot-attack 4 1 4 2 (dino/add-dino 4 2 (dino/add-robot 4 1 :R board))))))
    (is (not (nil? (dino/robot-attack board-dimension 3 (inc board-dimension) 3 (dino/add-robot board-dimension 3 :L board)))))))

(deftest take-action-test
  (testing "Send any of the following actions: turn left, turn right,
           move forward and move backwards"
    (is (= "T" (dino/get-element 2 2 (dino/take-action 2 3 :F (dino/add-robot 2 3 :T board)))))
    (is (= "R" (dino/get-element 2 3 (dino/take-action 3 3 :B (dino/add-robot 3 3 :R board)))))
    (is (= "⛶" (dino/get-element 4 2 (dino/take-action 4 1 :A :B (dino/add-dino 4 2 (dino/add-robot 4 1 :R board))))))))
