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
  {:body (list [3 0] [2 0] [1 0] [0 0])
   :direction [1 0]
   :type :snake
   :color (Color. 15 150 70)})

(defn create-apple []
  {:location [(rand-int field-width) (rand-int field-height)]
   :color (Color. 230 50 90)
   :type :appy})

(defn point-to-screen-rect [[pt-x pt-y]]
  [(* pt-x point-size) (* pt-y point-size) point-size point-size])
  ; the syntax/destructuring on the arguments is hazy to me ATM

(defn move [{:keys [body direction] :as snake} & grow]
  (assoc snake :body
    (cons
      (let [[head-x head-y] (first body)
            [dir-x dir-y] direction]
        [(+ head-x dir-y) (+ head-y dir-y)])
      (if grow body (butlast body)))))






