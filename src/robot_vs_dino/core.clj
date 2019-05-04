(ns robot-vs-dino.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn board
  "Generates 50x50 board"
  [size] (range (* size size)))

(defn inside-board?
  "Returns if the position is inside the board"
  [pos size]
  (if (or (> pos (* size size)) (< pos 0))
   false
   true))

(defn row-num
  "Returns which row the position belongs to: pos 1 in row 1,
  pos 51 in row 2, etc"
  [pos size]
  (if inside-board?
   (inc (quot (- pos 1) size))
   nil))

(defn col-num
  "Returns which collumn the position belongs to: pos 1 in row 1,
  pos 50 in col 50, pos 51 in col 1 again, etc"
  [pos size]
  (if inside-board?
   (inc (rem (- pos 1) size))
   nil))
