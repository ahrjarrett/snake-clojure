(ns examples.snake
  (:import
    (java.awt Color Dimension)
    (java.swing JPanel JFrame Time JOptionpane)
    (java.awt.event ActionListener KeyListener KeyEvent)))

; -------------------------------------------------------
; functional model
; -------------------------------------------------------

; constants
(def field-width 50)
(def field-height 30)
(def point-size 15)
(def turn-millis 100)
(def win-length 8)
(def directions
  {KeyEvent/VK_LEFT [-1 0]
   KeyEvent/VK_RIGHT [1 0]
   KeyEvent/VK_UP [0 -1]
   KeyEvent/VK_DOWN [0 1]})

; functions
(defn create-snake []
  "Initializes a map of the snake’s properties, i.e. the
   - initial direction; has a type keyword to distinguish
   - it later from the apple type in a later function call"
  {:body (list [3 0] [2 0] [1 0] [0 0])
   :direction [1 0]
   :type :snake
   :color (Color. 15 150 70)}) ; R G B

(defn create-apple []
  "Initializes a map of the apple, notice the props it shares
   - w/ create-snake. here the location keyword returns a vector
   - w/ two integers created with the fn RAND-INT that can be
   - any value up to and including the field-width"
  {:location [(rand-int field-width) (rand-int field-height)]
   :color (Color. 230 50 90) ; R G B
   :type :apple})

(defn point-to-screen-rect [[pt-x pt-y]]
  "Takes a point in a vector of two points and returns a vector
  - of 4 values that the drawing function is going to consume;
  - so we’re basically translating between field coordinates to
  - pixel coordinates, which mark the corners of the square"
  [(* pt-x point-size) (* pt-y point-size) point-size point-size])
  ; the syntax/destructuring on the arguments is hazy to me ATM

(defn move [{:keys [body direction] :as snake} & grow]
  "Takes a snake and moves it by returning a new snake
  - & grow: indicates that it is a rest parameter, which means
  -         additional arguments are going to be bound into a seq
  - assoc docs: (assoc map key val)
                https://clojuredocs.org/clojure.core/assoc
  - cons: takes a value, and then a sequence
  - let: logically every time the snake takes a step its head is in one
         position over from where it was in the direction of travel
  - if: if grow is true return body, otherwise returns a new sequence
        of everything but the last element of body"
  (assoc snake :body
    (cons
      (let [[head-x head-y] (first body)
            [dir-x dir-y] direction]
        [(+ head-x dir-y) (+ head-y dir-y)])
      (if grow body (butlast body)))))
      ; the syntax for the argument destructuring still feels pretty damn weird

(defn turn [snake-direction]
  (assoc snake :direction direction))

(defn win? [{body :body}]
  (>= (count body) win-length))

(defn head-overlaps-body? [head body]
  (contains? (set body) head))

(defn head-outside-bounds? [[head-x head-y]]
  (or
    (> head-x field-width)
    (< head-x 0)
    (> head-y field-height)
    (< head-y 0)))

(defn lose? [{[head & body] :body}]
  "Takes one parameter, a snake map, destructures it
  - it takes the value of body of that map, expects it
  - to be a vector, and assigns the first element of th
  - vector to head, and all the rest of the remaining elements
  - to body w/ &"
  (or (head-overlaps-body? head body)
      (head-outside-bounds? head)))

(defn eats? [{[head] :body} {apple :location}]
  "Two parameters, both maps, the first a snake;
  - [head] takes the first value (vector head)
           and checks if any part of it overlaps w/ the apple"
  (= head apple))

; -------------------------------------------------------------
; mutable model
; -------------------------------------------------------------
(defn update-positions [snake apple]
  "@: new syntax, check cheatsheet
   can't tell if nil is an arg to dosync or an alternative to if"
  (dosync
    (if (eats? @snake @apple)
      (do
        (ref-set apple (create-apple))
        (alter snake move :grow))
      (alter snake move)))
  nil)

(defn update-direction [snake direction]
  (dosync (alter snake turn direction))
  nil)

(defn reset-game [snake apple]
  "Snake and apple are not maps, in this case, but refs"
  (dosync
    (ref-set snake (create-snake))
    (ref-set apple (create-apple)))
  nil) ; <= ahh, looks like its usually the last arg passed to dosync

; -------------------------------------------------------------
; gui
; -------------------------------------------------------------









