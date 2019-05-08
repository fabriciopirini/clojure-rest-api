(ns robot-vs-dino.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [robot-vs-dino.core :as dino]))

(def board-dimension dino/board-dimension)
(def board (dino/create-board))

(deftest create-board-test
  (testing "Check board dimensions and elements on initialization"
    (is (= (* board-dimension board-dimension) (count board)))
    (is (= '#{"⛶"} (set board)))))

(deftest get-pos-test
  (testing "Get element in a defined position"
    (is (= 1 (dino/get-board-pos 1 1)))
    (is (= (int (+ (- board-dimension 3)(* (- board-dimension 3) board-dimension)))
           (dino/get-board-pos (- board-dimension 3) (- board-dimension 2))))))

(deftest row-num-test
  (testing "Check the row number of a position"
    (is (= 1 (dino/get-row-num 1)))
    (is (= 1 (dino/get-row-num board-dimension)))
    (is (= 2 (dino/get-row-num (inc board-dimension))))
    (is (= 4 (dino/get-row-num (dec (* 4 board-dimension)))))))

(deftest col-num-test
  (testing "Check the collumn number of a position"
    (is (= 1 (dino/get-col-num 1)))
    (is (= 1 (dino/get-col-num (inc board-dimension))))
    (is (= board-dimension (dino/get-col-num board-dimension)))
    (is (= 1 (dino/get-col-num (inc board-dimension))))
    (is (= (dec board-dimension) (dino/get-col-num (dec (* 4 board-dimension)))))))

(deftest is-robot?-test
  (testing "If the position is related to a robot")
  (is (true? (dino/is-robot? 2 3 (dino/add-robot 2 3 board))))
  (is (nil? (dino/is-robot? 3 2 (dino/add-dino 3 2 board))))
  (is (nil? (dino/is-robot? 3 2 (dino/add-robot 4 4 board))))
  (is (nil? (dino/is-robot? 1 (inc board-dimension) (dino/add-dino 2 3 board)))))

(deftest add-robot-test
  (testing "Add a robot to the simulation"
    (is (= "🅁" (get (dino/add-robot 2 3 :R board) (dino/get-vector-pos 2 3))))
    (is (= "🄱" (get (dino/add-robot 3 2 :B board) (dino/get-vector-pos 3 2))))
    (is (= "🅃" (get (dino/add-robot 4 4 board) (dino/get-vector-pos 4 4))))
    (is (nil? (dino/add-robot 1 2 (dino/add-robot 1 2 board))))))

(deftest add-dino-test
  (testing "Add a dino to the simulation"
    (is (= "🄳" (get (dino/add-dino 2 3 board) (dino/get-vector-pos 2 3))))
    (is (= "🄳" (get (dino/add-dino 3 2 board) (dino/get-vector-pos 3 2))))
    (is (= "⛶" (get (dino/add-dino 4 4 board) (dino/get-vector-pos 4 5))))
    (is (nil? (dino/add-dino 1 2 (dino/add-dino 1 2 board))))))


; (deftest move-action-test
;   (testing "Send any of the following actions: turn left, turn right,
;            move forward and move backwards"
;     (is (= "🅁" (get (dino/move-action 2 3 :R (dino/add-robot 2 3 :T board)) (dino/get-vector-pos 2 3))))
;     (is (= "🄱" (get (dino/move-action 2 3 :R (dino/add-robot 2 3 :R board)) (dino/get-vector-pos 2 3))))
;     (is (and (= "🅁" (get (dino/move-action 4 3 :F (dino/add-robot 4 3 :R board)) (dino/get-vector-pos 5 3)))
;              (= "⛶" (get (dino/move-action 4 3 :F (dino/add-robot 4 3 :R board)) (dino/get-vector-pos 4 3)))))
;     (is (nil? (dino/move-action 2 board-dimension :F (dino/add-robot 2 board-dimension :B board))))))
