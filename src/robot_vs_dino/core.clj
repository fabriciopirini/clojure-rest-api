(ns robot-vs-dino.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn board
  "Generates {size}x{size} board"
  [size] (vec (repeat (* size size) "O")))

(defn inside-board?
  "Returns if the position is inside the board"
  [pos board]
  (if (or (>= pos (count board)) (neg? pos))
   false
   true))

(defn get-board-size
  "Returns the board size"
  [board]
  (Math/sqrt (count board)))

(defn get-pos
  "Returns the element position on vector due to its collumn and row numbers
  being X for collumn and Y for row."
  [X Y board]
  (int (+ X(* (dec Y) (get-board-size board)))))

(defn row-num
  "Returns which row the position belongs to: pos 1 in row 1,
  pos 51 in row 2, etc"
  [pos board]
  (when (inside-board? pos board)
   (int (inc (quot (dec pos) (get-board-size board))))))

(defn col-num
  "Returns which collumn the position belongs to: pos 1 in row 1,
  pos 50 in col 50, pos 51 in col 1 again, etc"
  [pos board]
  (when (inside-board? pos board)
   (int (inc (rem (dec pos) (get-board-size board))))))

(defn is-space-available?
  "Returns if the position on the board is free"
  [pos board]
  (when (and (inside-board? pos board) (= '("O") (get board pos)))
   true))

(defn add-robot
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not."
  ([X Y board] (add-robot X Y "U" board))
  ([X Y facing board]
   (when is-space-available?
    (assoc-in board [(dec (get-pos X Y board))] facing))))
