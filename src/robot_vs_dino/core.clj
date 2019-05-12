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
  (let [id (swap! id-seq inc)]
    {:id id :identifier (str "simulation-" id) :simulation_state (vec (repeat board-total-size "⛶"))}))

(defn add-board
  "Add board to board list"
  [new-board]
  (when-not (nil? new-board)
    (swap! board-list assoc (:id new-board) new-board)
    (get-board (:id new-board))))
    ; (assoc new-board :id id)))

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
  "Returns the element position on vector due to its collumn and row numbers"
  [col row]
  (dec (int (+ col (* (dec row) board-dimension)))))

(defn get-element
  "Returns the element from board due to its collumn and row numbers"
  [col row board]
  (when (inside-board? col row)
    (get (:simulation_state board) (get-element-pos col row))))

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
  [col row]
  (when (and (pos? col) (pos? row) (<= col board-dimension) (<= row board-dimension))
    true))

(defn is-space-available?
  "Returns if the position on the board is free"
  [col row board]
  (when (and (inside-board? col row) (= "⛶" (get (:simulation_state board) (get-element-pos col row))))
    true))

(defn print-board
  "Print the current board"
  [board]
  (let [board (:simulation_state board)]
    (if (nil? board)
      "Invalid board"
      (doseq [i (range board-total-size)]
        (if (= (rem i board-dimension) (dec board-dimension))
          (println (get board i))
          (print (str (get board i) " ")))))))

; (defn format-board
;   "Format the current board to send it as a response"
;   [board]
;   (if (nil? board)
;     "Invalid board"
;     (let [board-str-let ""]
;       (loop [i 0
;              board-str board-str-let]
;         (if (= i board-total-size)
;           board-str
;           (if (= (rem i board-dimension) (dec board-dimension))
;             (recur (inc i) (str board-str (get board i) "\n"))
;             (recur (inc i) (str board-str (get board i) " "))))))))


;; Simulation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-robot?
  "Returns if element positioned on X collumn and Y row is a robot or not"
  [col row board]
  (let [element (get-element col row board)]
    (when (and (inside-board? col row)
               (and (not= "🄳" element)) (not= "⛶" element))
      true)))

(defn add-robot
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not"
  ([col row board] (add-robot col row :T board))
  ([col row facing board]
   (when (and (is-space-available? col row board)
              (not (nil? (get-symbol facing))))
     (assoc-in board [:simulation_state]
               (assoc-in (:simulation_state board) [(get-element-pos col row)] (get-symbol facing))))))

(defn add-dino
  "Returns board with new element positioned on X collumn and Y row,
  if successful; return nil, if not"
  [col row board]
  (when (is-space-available? col row board)
    (assoc-in board [:simulation_state]
              (assoc-in (:simulation_state board) [(get-element-pos col row)] "🄳"))))

(defn remove-element
  "Returns board with non-empty element positioned on X collumn and Y row replaced by an empty one, if successful; return nil, if not"
  [col row board]
  (when (and (inside-board? col row)
             (not= "⛶" (get-element col row board)))
    (assoc-in board [:simulation_state]
              (assoc-in (:simulation_state board) [(get-element-pos col row)] "⛶"))))

(defn move-element
  "Move non-empty element from simulation to a new position, if successful; return nil, if not"
  [col row new-col new-row board]
  (when (is-space-available? new-col new-row board)
    (assoc-in board [:simulation_state]
      (assoc-in
        (assoc-in (:simulation_state board) [(get-element-pos new-col new-row)] (get-element col row board)) [(get-element-pos col row)] "⛶"))))

(defn turn-element
  "Turn non-empty element from simulation to a new direction, if successful; return nil, if not"
  [col row dir-tuple board]
  (let [direction-map {["🅃" :R] :R, ["🄱" :L] :R, ["🅃" :L] :L, ["🄱" :R] :L, ["🄻" :R] :T, ["🅁" :L] :T, ["🄻" :L] :B, ["🅁" :R] :B}]
    (assoc-in board [:simulation_state]
              (assoc-in (:simulation_state board) [(get-element-pos col row)] (get-symbol (direction-map dir-tuple))))))

(defn robot-attack
  "Robot attack the first position on a certain direction. If it has a dino, it is destroyed; If not, nothing happens and the board is returned"
  [col row att-col att-row board]
  (if (and (inside-board? att-col att-row) (= "🄳" (get-element att-col att-row board)))
    (assoc-in board [:simulation_state]
              (assoc-in (:simulation_state board) [(get-element-pos att-col att-row)] "⛶"))
    board))

(defn take-action
  "Returns board with new element positioned after action, if successful; return nil, if not"
  ([col row action board]
   (when (is-robot? col row board)
     (let [cur-direction (get-element col row board)
           dir-tuple [cur-direction action]]
       (when-not (nil? cur-direction)
         (case dir-tuple
           (["🅃" :F] ["🄱" :B]) (move-element col row col (dec row) board)
           (["🅃" :B] ["🄱" :F]) (move-element col row col (inc row) board)
           (["🄻" :F] ["🅁" :B]) (move-element col row (dec col) row board)
           (["🄻" :B] ["🅁" :F]) (move-element col row (inc col) row board)
           (["🅃" :R] ["🄱" :L]
                      ["🅃" :L] ["🄱" :R]
                      ["🄻" :R] ["🅁" :L]
                      ["🄻" :L] ["🅁" :R]) (turn-element col row dir-tuple board))))))
  ([col row attack direction board]
   (when (is-robot? col row board)
     (let [att-tuple [attack direction]]
       (case att-tuple
         ([:A :T]) (robot-attack col row col (dec row) board)
         ([:A :B]) (robot-attack col row col (inc row) board)
         ([:A :L]) (robot-attack col row (dec col) row board)
         ([:A :R]) (robot-attack col row (inc col) row board))))))
