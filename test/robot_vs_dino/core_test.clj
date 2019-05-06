(ns robot-vs-dino.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [robot-vs-dino.core :as dino]))

(def size 5)
(def board (dino/board size))

(deftest row-num-test
  (testing "Check the row number of a position"
    (is (= 1 (dino/row-num 1 board)))
    (is (= 1 (dino/row-num size board)))
    (is (= 2 (dino/row-num (inc size) board)))
    (is (= 4 (dino/row-num (dec (* 4 size)) board)))))

(deftest col-num-test
  (testing "Check the collumn number of a position"
    (is (= 1 (dino/col-num 1 board)))
    (is (= 1 (dino/col-num (inc size) board)))
    (is (= size (dino/col-num size board)))
    (is (= 1 (dino/col-num (inc size) board)))
    (is (= (dec size) (dino/col-num (dec (* 4 size)) board)))))

(deftest check-board
  (testing "Check board dimensions and elements on initialization"
    (is (= (* size size) (count (dino/board size))))
    (is (= '#{"O"} (set (dino/board size))))))

(deftest add-robot
  (testing "Add a robot to the simulation"
    (is (= "R" (get (dino/add-robot 2 3 "R" board) (+ (dec 2)(* (dec 3) size)))))
    (is (= "D" (get (dino/add-robot 3 2 "D" board) (+ (dec 3)(* (dec 2) size)))))
    (is (= "U" (get (dino/add-robot 4 4 board) (+ (dec 4)(* (dec 4) size)))))
    (is (nil? (dino/add-robot 1 2 (dino/add-robot 1 2 board))))))
