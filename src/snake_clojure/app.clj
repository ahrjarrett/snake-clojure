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

; initializing functions
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
  (assoc snake :body
    (cons
      (let [[head-x head-y] (first body)
            [dir-x dir-y] direction]
        [(+ head-x dir-y) (+ head-y dir-y)])
      (if grow body (butlast body)))))






