(ns robot-vs-dino.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [robot-vs-dino.core :as dino]))

(deftest row-num-test
  (testing "Check the row number of a position"
    (is (= 1 (dino/row-num 1 50)))
    (is (= 1 (dino/row-num 50 50)))
    (is (= 2 (dino/row-num 51 50)))
    (is (= 5 (dino/row-num 213 50)))))

(deftest col-num-test
  (testing "Check the collumn number of a position"
    (is (= 1 (dino/col-num 1 50)))
    (is (= 50 (dino/col-num 50 50)))
    (is (= 1 (dino/col-num 51 50)))
    (is (= 5 (dino/col-num 205 50)))))

(deftest check-board
  (testing "Check board dimensions and number elements"
    (is (= 2500 (count (dino/board 50))))
    (is (= '(0) (take 1 (dino/board 50))))
    (is (= '(2499) (take-last 1 (dino/board 50))))))
