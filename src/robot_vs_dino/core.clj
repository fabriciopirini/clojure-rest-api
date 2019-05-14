(ns robot-vs-dino.core)

;; Definitions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce board-dimension 50)
(defonce board-total-size (* board-dimension board-dimension))

(defonce id-seq (atom 0))
(defonce board-list (atom (array-map)))


;; Board utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare inside-board? get-board add-board)

(defn create-board
  "Generates 50x50 board"
  []
  {:currentState (vec (repeat board-total-size "⛶"))})

(defn add-board
  "Add board to board list"
  [new-board]
  (when-not (nil? new-board)
    (let [id (swap! id-seq inc)
          board (assoc new-board :id id :identifier (str "simulation-" id))]
      (swap! board-list assoc (:id board) board)
      (get-board (:id board)))))

(defn update-board
  "Update existing board on board list"
  [board]
  (when-not (nil? board)
    (swap! board-list assoc (:id board) board)
    (get-board (:id board))))

(defn delete-board
  "Delete board if provided valid ID. If it is not, returns nil"
  [id]
  (when-not (nil? (get-board id))
    (swap! board-list dissoc id)))


(defn reset-all-boards
  "Delete all simulations and reset the ID"
  []
  (reset! id-seq  0)
  (reset! board-list (array-map))
  nil)

(defn get-board
  "Get board if provided valid ID. If it is not, returns nil"
  [id]
  (when-not (nil? id)
    (@board-list id)))

(defn get-all-boards
  "Get list of all running boards. If none, returns nil"
  []
  (-> board-list deref vals reverse))

(defn get-element-pos
  "Returns the element position on vector due to its column and row numbers"
  [col row]
  (dec (int (+ col (* (dec row) board-dimension)))))

(defn get-element
  "Returns the element from board due to its column and row numbers"
  [col row board]
  (when (inside-board? col row)
    (get (:currentState board) (get-element-pos col row))))

(defn get-symbol
  "Returns the element symbol to be placed on board"
  [letter]
  (def letter-map {:T "T"
                   :B "B"
                   :L "L"
                   :R "R"})
  (get letter-map letter))

(defn inside-board?
  "Returns if the position is inside the board"
  [col row]
  (when (and (pos? col) (pos? row) (<= col board-dimension) (<= row board-dimension))
    true))

(defn is-space-available?
  "Returns if the position on the board is free"
  [col row board]
  (when (and (inside-board? col row) (= "⛶" (get (:currentState board) (get-element-pos col row))))
    true))

(defn print-board
  "Print the current board"
  [board]
  (let [board (:currentState board)]
    (if (nil? board)
      "Invalid board"
      (doseq [i (range board-total-size)]
        (if (= (rem i board-dimension) (dec board-dimension))
          (println (get board i))
          (print (str (get board i) " ")))))))

;; Simulation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-robot?
  "Returns if element positioned on X column and Y row is a robot or not"
  [col row board]
  (let [element (get-element col row board)]
    (when (and (inside-board? col row)
               (and (not= "D" element)) (not= "⛶" element))
      true)))

(defn add-robot
  "Returns board with new element positioned on X column and Y row,
  if successful; return nil, if not"
  ([col row board] (add-robot col row :T board))
  ([col row facing board]
   (when (and (is-space-available? col row board)
              (not (nil? (get-symbol facing))))
     (assoc-in board [:currentState]
               (assoc-in (:currentState board) [(get-element-pos col row)] (get-symbol facing))))))

(defn add-dino
  "Returns board with new element positioned on X column and Y row,
  if successful; return nil, if not"
  [col row board]
  (when (is-space-available? col row board)
    (assoc-in board [:currentState]
              (assoc-in (:currentState board) [(get-element-pos col row)] "D"))))

(defn remove-element
  "Returns board with non-empty element positioned on X column and Y row replaced by an empty one, if successful; return nil, if not"
  [col row board]
  (when (and (inside-board? col row)
             (not= "⛶" (get-element col row board)))
    (assoc-in board [:currentState]
              (assoc-in (:currentState board) [(get-element-pos col row)] "⛶"))))

(defn move-element
  "Move non-empty element from simulation to a new position, if successful; return nil, if not"
  [col row new-col new-row board]
  (when (is-space-available? new-col new-row board)
    (assoc-in board [:currentState]
      (assoc-in
        (assoc-in (:currentState board) [(get-element-pos new-col new-row)] (get-element col row board)) [(get-element-pos col row)] "⛶"))))

(defn turn-element
  "Turn non-empty element from simulation to a new direction, if successful; return nil, if not"
  [col row dir-tuple board]
  (let [direction-map {["T" :R] :R, ["B" :L] :R, ["T" :L] :L, ["B" :R] :L, ["L" :R] :T, ["R" :L] :T, ["L" :L] :B, ["R" :R] :B}]
    (assoc-in board [:currentState]
              (assoc-in (:currentState board) [(get-element-pos col row)] (get-symbol (direction-map dir-tuple))))))

(defn robot-attack
  "Robot attack the first position on a certain direction. If it has a dino, it is destroyed; If not, nothing happens and the board is returned"
  [col row att-col att-row board]
  (if (and (inside-board? att-col att-row) (= "D" (get-element att-col att-row board)))
    (assoc-in board [:currentState]
              (assoc-in (:currentState board) [(get-element-pos att-col att-row)] "⛶"))
    board))

(defn take-action
  "Returns board with new element positioned after action, if successful; return nil, if not"
  ([col row action board]
   (when (is-robot? col row board)
     (let [cur-direction (get-element col row board)
           dir-tuple [cur-direction action]]
       (when-not (nil? cur-direction)
         (case dir-tuple
           (["T" :F] ["B" :B]) (move-element col row col (dec row) board)
           (["T" :B] ["B" :F]) (move-element col row col (inc row) board)
           (["L" :F] ["R" :B]) (move-element col row (dec col) row board)
           (["L" :B] ["R" :F]) (move-element col row (inc col) row board)
           (["T" :R] ["B" :L] ["T" :L] ["B" :R] ["L" :R] ["R" :L] ["L" :L] ["R" :R]) (turn-element col row dir-tuple board))))))
  ([col row attack direction board]
   (when (is-robot? col row board)
     (let [att-tuple [attack direction]]
       (case att-tuple
         ([:A :T]) (robot-attack col row col (dec row) board)
         ([:A :B]) (robot-attack col row col (inc row) board)
         ([:A :L]) (robot-attack col row (dec col) row board)
         ([:A :R]) (robot-attack col row (inc col) row board))))))
