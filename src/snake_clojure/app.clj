(ns snake-clojure.app
  (:import
    (java.awt Color Dimension)
    (javax.swing JPanel JFrame Timer JOptionPane)
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
(defn create-snake
  "Initializes a map of the snake’s properties, i.e. the
   - initial direction; has a type keyword to distinguish
   - it later from the apple type in a later function call"
  []
  {:body (list [3 0] [2 0] [1 0] [0 0])
   :direction [1 0]
   :type :snake
   :color (Color. 15 150 70)}) ; R G B

(defn create-apple
  "Initializes a map of the apple, notice the props it shares
   - w/ create-snake. here the location keyword returns a vector
   - w/ two integers created with the fn RAND-INT that can be
   - any value up to and including the field-width"
  []
  {:location [(rand-int field-width) (rand-int field-height)]
   :color (Color. 230 50 90) ; R G B
   :type :apple})

(defn point-to-screen-rect
  "Takes a point in a vector of two points and returns a vector
  - of 4 values that the drawing function is going to consume;
  - so we’re basically translating between field coordinates to
  - pixel coordinates, which mark the corners of the square"
  [[pt-x pt-y]]
  [(* pt-x point-size) (* pt-y point-size) point-size point-size])
  ; the syntax/destructuring on the arguments is hazy to me ATM

(defn move
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
  [{:keys [body direction] :as snake} & grow]
  (assoc snake :body
    (cons
      (let [[head-x head-y] (first body)
            [dir-x dir-y] direction]
        [(+ head-x dir-x) (+ head-y dir-y)])
      (if grow body (butlast body)))))
      ; the syntax for the argument destructuring still feels pretty damn weird

(defn turn [snake direction]
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

(defn lose?
  "Takes one parameter, a snake map, destructures it
  - it takes the value of body of that map, expects it
  - to be a vector, and assigns the first element of th
  - vector to head, and all the rest of the remaining elements
  - to body w/ &"
  [{[head & body] :body}]
  (or (head-overlaps-body? head body)
      (head-outside-bounds? head)))

(defn eats?
  "Two parameters, both maps, the first a snake;
  - [head] takes the first value (vector head)
           and checks if any part of it overlaps w/ the apple"
  [{[head] :body} {apple :location}]
  (= head apple))

; -------------------------------------------------------------
; mutable model
; -------------------------------------------------------------
(defn update-positions
  "@: new syntax, check cheatsheet
      Note: it’s shorthand for the dref function
   dosync: can't tell if nil is an arg to dosync or an alternative to if
      Note: it’s an alt to dosync, making the side-effects explicit by returning nil"
  [snake apple]
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

(defn reset-game
  "Snake and apple are not maps, in this case, but refs"
  [snake apple]
  (dosync
    (ref-set snake (create-snake))
    (ref-set apple (create-apple)))
  nil) ; <= ahh, looks like its usually the last arg passed to dosync

; -------------------------------------------------------------
; gui
; -------------------------------------------------------------
(defn fill-point [g pt color]
  (let [[x y width height] (point-to-screen-rect pt)]
    (.setColor g color)
    (.fillRect g x y width height)))

(defmulti paint (fn [g object] (:type object)))

(defmethod paint :apple [g {:keys [location color]}]
  (fill-point g location color))

(defmethod paint :snake [g {:keys [body color]}]
  (doseq [point body]
    (fill-point g point color)))

(defn game-panel
  "Proxy: creates a one-off instace of a Java Class
          (basically an anonymous class)"
  [frame snake apple]
  (proxy [JPanel ActionListener KeyListener] []
    ; JPanel
    (paintComponent [g]
      (proxy-super paintComponent g)
      (paint g @apple)
      (paint g @snake))
    (getPreferredSize []
      (Dimension. (* (inc field-width) point-size)
        (* (inc field-height) point-size)))
    ; ActionListener
    (actionPerformed [e]
      (update-positions snake apple)
      (if (lose? @snake)
        (do
          (reset-game snake apple)
          (JOptionPane/showMessageDialog frame "You lose!")))
      (if (win? @snake)
        (do
          (reset-game snake apple)
          (JOptionPane/showMessageDialog frame "You win!")))
      (.repaint this))
    ; KeyListener
    (keyPressed [e]
      (let [direction (directions (.getKeyCode e))]
        (if direction (update-direction snake direction))))
    (keyReleased [e])
    (keyTyped [e])))

(defn game []
  (let [snake (ref (create-snake))
    apple (ref (create-apple))
    frame (JFrame. "Snake")
    panel (game-panel frame snake apple)
    timer (Timer. turn-millis panel)]
    (.setFocusable panel true)
    (.addKeyListener panel panel)

    (.add frame panel)
    (.pack frame)
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setVisible frame true)

    (.start timer)))

(game)
