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

(defonce board-dimension 5) ; Less than 5 will break some tests due to hardcoded values
(defonce board-total-size (* board-dimension board-dimension))

(defonce id-seq (atom 0))
(defonce board-list (atom (array-map)))

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
    (swap! board-list assoc id new-board)
    new-board))

(defn delete-board
  "Delete board if provided valid ID. If it is not, returns nil"
  [id]
  (swap! board-list dissoc id)
  nil)

(defn reset-all-boards
  "Delete all simulations and reset the ID"
  []
  (reset! id-seq  0)
  (reset! board-list (array-map))
  nil)

(defn get-board
  "Get board if provided valid ID. If it is not, returns nil"
  [id]
  (@board-list id))

(defn get-all-boards
  "Get list of all running boards. If none, returns nil"
  []
  (-> board-list deref vals reverse))

; (defn get-board-pos
;   "Returns the element position on board due to its collumn and row numbers"
;   [col row]
;   (int (+ col(* (dec row) board-dimension))))

(defn get-element-pos
  "Returns the element position on vector due to its collumn and row numbers"
  [col row]
  (dec (int (+ col(* (dec row) board-dimension)))))

(defn get-element
  "Returns the element from board due to its collumn and row numbers"
  [col row board]
  (get board (get-element-pos col row)))

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
  (when (and (pos? (inc pos)) (< (inc pos) board-total-size))
   true))

(defn is-space-available?
  "Returns if the position on the board is free"
  [pos board]
  ; (do (println "Is the pos 'O'?" (= "O" (get board (dec pos)))))
  ; (do (println "Is it inside?" (inside-board? pos board)))
  (when (and (inside-board? pos) (= "⛶" (get board pos)))
   true))

(defn print-board
  "Print the current board"
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
  (let [pos (get-element-pos col row)
        element (get board pos)]
    (when (and (inside-board? pos)
               (and (not= "🄳" element)) (not= "⛶" element))
      true)))

(defn add-robot
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not"
  ([col row board] (add-robot col row :T board))
  ([col row facing board]
   (when (and (is-space-available? (get-element-pos col row) board)
              (not (nil? (get-symbol facing))))
     (assoc-in board [(get-element-pos col row)] (get-symbol facing)))))

(defn add-dino
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not"
  [col row board]
  (when (is-space-available? (get-element-pos col row) board)
    (assoc-in board [(get-element-pos col row)] "🄳")))

(defn remove-element
  "Returns board with non-empty element positioned on X collumn and Y row replaced by an empty one, if successful; return nil, if not"
  [col row board]
  (when (and (inside-board? (get-element-pos col row))
             (not= "⛶" (get-element col row board)))
    (assoc-in board [(get-element-pos col row)] "⛶")))

(defn move-element
  "Move non-empty element from simulation to a new position, if successful; return nil, if not"
  [col row new-col new-row board]
  (when (is-space-available? (get-element-pos new-col new-row) board)
    (assoc-in
      (assoc-in board [(get-element-pos new-col new-row)] (get-element col row board)) [(get-element-pos col row)] "⛶")))

(defn turn-element
  "Turn non-empty element from simulation to a new direction, if successful; return nil, if not"
  [col row dir-tuple board]
  (let [direction-map {["🅃" :R] :R, ["🄱" :L] :R, ["🅃" :L] :L, ["🄱" :R] :L, ["🄻" :R] :T, ["🅁" :L] :T, ["🄻" :L] :B, ["🅁" :R] :B}]
    (assoc-in board [(get-element-pos col row)] (get-symbol (direction-map dir-tuple)))))

(defn take-action
  "Returns board with new element positioned after action, if successful; return nil, if not"
  [col row action board]
  (when (is-robot? col row board)
    (let [cur-direction (get-element col row board)
          dir-tuple [cur-direction action]]
      (case dir-tuple
            (["🅃" :F] ["🄱" :B]) (move-element col row col (dec row) board)
            (["🅃" :B] ["🄱" :F]) (move-element col row col (inc row) board)
            (["🄻" :F] ["🅁" :B]) (move-element col row (dec col) row board)
            (["🄻" :B] ["🅁" :F]) (move-element col row (inc col) row board)
            (["🅃" :R] ["🄱" :L] ["🅃" :L] ["🄱" :R] ["🄻" :R] ["🅁" :L]
              ["🄻" :L] ["🅁" :R]) (turn-element col row dir-tuple board)))))
