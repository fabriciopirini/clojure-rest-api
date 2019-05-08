(ns robot-vs-dino.core
  (:gen-class))

;; Schemas
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; (s/defschema Pizza {:id    Long
;                     :name  String
;                     :price Double
;                     :hot   Boolean
;                     (s/optional-key :description) String
;                     :toppings #{Topping}})

;; Definitions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce board-dimension 5)
(defonce board-total-size (* board-dimension board-dimension))

(defonce id-seq (atom 0))
(defonce board_list (atom (array-map)))

; (defn -main
;   "I don't do a whole lot ... yet."
;   [& args]
;   (println "Hello, World!"))


;; Board utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare inside-board?)

(defn create-board
  "Generates 50x50 board"
  []
  (vec (repeat board-total-size "⛶")))

(defn add-board
  "Add board to simulation list"
  [new-board]
  (let [id (swap! id-seq inc)]
        ; simulation (coerce! Pizza (assoc new-pizza :id id))]
    (swap! board_list assoc id new-board)
    new-board))

(defn delete-board
  ""
  [id]
  (swap! board_list dissoc id)
  nil)

(defn get-board
  "Get board if provided id. If the ID is not valid, returns nil"
  [id]
  (@board_list id))

(defn get-all-boards
  "Get list of all running boards. If none, returns nil"
  []
  (-> board_list deref vals reverse))

(defn get-board-pos
  "Returns the element position on board due to its collumn and row numbers"
  [col row]
  (int (+ col(* (dec row) board-dimension))))

(defn get-vector-pos
  "Returns the element position on vector due to its collumn and row numbers"
  [col row]
  (dec (get-board-pos col row)))

(defn get-row-num
  "Returns which row the position belongs to: pos 1 in row 1,
  pos 51 in row 2, etc"
  [pos]
  (when (inside-board? pos)
   (int (inc (quot (dec pos) board-dimension)))))

(defn get-col-num
  "Returns which collumn the position belongs to: pos 1 in row 1,
  pos 50 in col 50, pos 51 in col 1 again, etc"
  [pos]
  (when (inside-board? pos)
   (int (inc (rem (dec pos) board-dimension)))))

(defn get-symbol
  "Returns the element symbol to be placed on board"
  [letter]
  (def letter-map {:T "🅃"
                   :B "🄱"
                   :L "🄻"
                   :R "🅁"})
  (get letter-map letter))

(defn inside-board?
  "Returns if the position is inside the board"
  [pos]
  (when (and (pos? pos) (< pos board-total-size))
   true))

(defn is-space-available?
  "Returns if the position on the board is free"
  [pos board]
  ; (do (println "Is the pos 'O'?" (= "O" (get board (dec pos)))))
  ; (do (println "Is it inside?" (inside-board? pos board)))
  (when (and (inside-board? pos) (= "⛶" (get board (dec pos))))
   true))

(defn print-board
  "Print the actual board"
  [board]
  (if (nil? board)
    "Invalid board"
    (doseq [i (range board-total-size)]
     (if (= (rem i board-dimension) (dec board-dimension))
      (println (get board i))
      (print (str (get board i) " "))))))

;; Simulation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-robot?
  "Returns if element positioned on X collumn and Y row is a robot or not"
  [col row board]
  (let [pos (get-vector-pos col row)
        element (get board pos)]
    (when (and (inside-board? pos)
               (and (not= "🄳" element)) (not= "⛶" element))
      true)))

(defn add-robot
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not"
  ([col row board] (add-robot col row :T board))
  ([col row facing board]
   (when (and (is-space-available? (get-board-pos col row) board)
              (not (nil? (get-symbol facing))))
     (assoc-in board [(get-vector-pos col row)] (get-symbol facing)))))

(defn add-dino
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not"
  [col row board]
  (when (is-space-available? (get-board-pos col row) board)
    (assoc-in board [(get-vector-pos col row)] "🄳")))

; (defn move-action
;   "Returns board with new element positioned after action,
;   if successful; return nil, if not"
;   [col row direction board]
;   (when (is-robot? col row board)))
