(ns tests.core-tests
    (:require [tests.assertions :as assertions])
    (:require [tests.utils :only [deftest]]))

(deftest try-tests
;  (assertions/assert-true (= nil (try)))

;  (assertions/assert-true (= 20 (try (py.bytecode/BINARY_ADD 10 10 ))))

  (assertions/assert-true (= 20
    (try (py.bytecode/BINARY_ADD 10 10)
      (finally (py/print "finally")))))

  (assertions/assert-true (= 20
          (try
            (py.bytecode/BINARY_ADD 10 10)
            (catch IllegalStateException e "py/print exception"))))

  (assertions/assert-true (= 20
    (try
      (py.bytecode/BINARY_ADD 10 10)
            (catch IllegalArgumentException iae "py/print illegalargument")
            (catch IllegalStateException e "py/print exception"))))
  )

(deftest if-not-tests
    (assertions/assert-true (if-not false true))
    (assertions/assert-false (if-not true true false))
    (assertions/assert-true (if-not true false true)))

(deftest and-tests
    (assertions/assert-true (and true true true))
    (assertions/assert-false (and true false true))
    (assertions/assert-false (and nil true)))

(deftest identical?-tests
    (assertions/assert-true (identical? 1 1))
    (assertions/assert-false (identical? 1 2))
    (assertions/assert-false (identical? '() '()))
    (let [tmp '()]
         (assertions/assert-true (identical? tmp tmp))))

(deftest compare-tests
    (assertions/assert-lesser (compare 0 1) 0)
    (assertions/assert-lesser (compare -1 0) 0))

(deftest or-tests
    (assertions/assert-true (or true false false))
    (assertions/assert-false (or false false false))
    (assertions/assert-true (or false true false)))

(deftest zero?-tests
    (assertions/assert-true (zero? 0))
    (assertions/assert-false (zero? 1)))

(deftest count-tests
    (assertions/assert-equal (count '()) 0)
    (assertions/assert-equal (count '(1)) 1))

(deftest int-tests
    (assertions/assert-equal (int "1") 1)
    (assertions/assert-equal (int "-1") -1))

(deftest <-tests
    (assertions/assert-true (< 1))
    (assertions/assert-true (< 1 2))
    (assertions/assert-true (< 1 2 3))
    (assertions/assert-true (< 1 2 3 4))
    (assertions/assert-false (< 1 3 2 4)))

(deftest reduce1-tests
    (assertions/assert-equal (reduce1 (fn [x y] (py.bytecode/BINARY_ADD x y))
                            '(1 2 3 4)) 10)
    (assertions/assert-equal (reduce1 (fn [x y] (py.bytecode/BINARY_ADD x y))
                            5 '(1 2 3 4)) 15))
(deftest reverse-tests
    (assertions/assert-equal (reverse [1 2 3 4 5]) [5 4 3 2 1]))

(deftest >1?-tests
    (assertions/assert-true (>1? 3))
    (assertions/assert-false (>1? 0)))

(deftest >0?-tests
    (assertions/assert-true (>0? 2))
    (assertions/assert-false (>0? -1)))


(deftest +-tests
    (assertions/assert-equal (+) 0)
    (assertions/assert-equal (+ 1) 1)
    (assertions/assert-equal (+ 1 2) 3)
    (assertions/assert-equal (+ 1 2 3 4) 10))

(deftest *-tests
    (assertions/assert-equal (*) 1)
    (assertions/assert-equal (* 1) 1)
    (assertions/assert-equal (* 1 2) 2)
    (assertions/assert-equal (* 1 2 3 4) 24))

(deftest /-tests
    (assertions/assert-equal (/ 1) 1)
    (assertions/assert-equal (/ 2 2) 1)
    (assertions/assert-equal (/ 20 5 2) 2))

(deftest /-tests
    (assertions/assert-equal (- 1) -1)
    (assertions/assert-equal (- 2 2) 0)
    (assertions/assert-equal (- 20 5 2) 13))

(deftest <=-tests
    (assertions/assert-true (<= 1))
    (assertions/assert-true (<= 1 2))
    (assertions/assert-true (<= 1 1 3 4))
    (assertions/assert-false (<= 2 1 1 1)))

(deftest >-tests
    (assertions/assert-true (> 4))
    (assertions/assert-true (> 4 3))
    (assertions/assert-true (> 4 3 2 1))
    (assertions/assert-false (> 4 3 2 2)))

(deftest >=-tests
    (assertions/assert-true (>= 4))
    (assertions/assert-true (>= 4 3))
    (assertions/assert-true (>= 4 3 3))
    (assertions/assert-false (>= 3 4 2 1)))

(deftest ==-tests
    (assertions/assert-true (== 1))
    (assertions/assert-true (== 1 1))
    (assertions/assert-true (== 1 1 1 1 1))
    (assertions/assert-false (== 1 2 1 1 1)))

(deftest max-tests
    (assertions/assert-equal (max 1) 1)
    (assertions/assert-equal (max 1 2) 2)
    (assertions/assert-equal (max 3 2 1) 3))

(deftest min-tests
    (assertions/assert-equal (min 1) 1)
    (assertions/assert-equal (min 1 2) 1)
    (assertions/assert-equal (min 3 2 1) 1))

(deftest pos?-tests
    (assertions/assert-true (pos? 1))
    (assertions/assert-false (pos? -1))
    (assertions/assert-false (pos? 0)))

(deftest neg?-tests
    (assertions/assert-true (neg? -1))
    (assertions/assert-false (neg? 1))
    (assertions/assert-false (neg? 0)))

(deftest quot-tests
    (assertions/assert-equal (quot 23 7) 3)
    (assertions/assert-equal (quot 4 2) 2)
    (assertions/assert-equal (quot 3 2) 1)
    (assertions/assert-equal (quot 6 4) 1)
    (assertions/assert-equal (quot 0 5) 0)
    
    ;(assertions/assert-equal (quot 2 1/2) 4)
    ;(assertions/assert-equal (quot 2/3 1/2) 1)
    ;(assertions/assert-equal (quot 1 2/3) 1)
    
    (assertions/assert-equal (quot 4.0 2.0) 2.0)
    (assertions/assert-equal (quot 4.5 2.0) 2.0)
    ; |num| > |div|, num != k * div
    (assertions/assert-equal (quot 42 5) 8)     ; (8 * 5) + 2 == 42
    (assertions/assert-equal (quot 42 -5) -8)   ; (-8 * -5) + 2 == 42
    (assertions/assert-equal (quot -42 5) -8)   ; (-8 * 5) + -2 == -42
    (assertions/assert-equal (quot -42 -5) 8)   ; (8 * -5) + -2 == -42
    ; |num| > |div|, num = k * div
    (assertions/assert-equal (quot 9 3) 3)
    (assertions/assert-equal (quot 9 -3) -3)
    (assertions/assert-equal (quot -9 3) -3)
    (assertions/assert-equal (quot -9 -3) 3)
    ; |num| < |div|
    (assertions/assert-equal (quot 2 5) 0)
    (assertions/assert-equal (quot 2 -5) 0)
    (assertions/assert-equal (quot -2 5) 0)
    (assertions/assert-equal (quot -2 -5) 0)

    ; num = 0, div != 0
    (assertions/assert-equal (quot 0 3) 0)
    (assertions/assert-equal (quot 0 -3) 0)
    )
    

(deftest rem-tests
    (assertions/assert-equal (rem 23 7) 2)
    (assertions/assert-equal (rem 4 2) 0)
    (assertions/assert-equal (rem 3 2) 1)
    (assertions/assert-equal (rem 6 4) 2)
    (assertions/assert-equal (rem 0 5) 0)

    ;(assertions/assert-equal (rem 2 1/2) 0)
    ;(assertions/assert-equal (rem 2/3 1/2) 1/6)
    ;(assertions/assert-equal (rem 1 2/3) 1/3)

    (assertions/assert-equal (rem 4.0 2.0) 0.0)
    (assertions/assert-equal (rem 4.5 2.0) 0.5)

    ; |num| > |div|, num != k * div
    (assertions/assert-equal (rem 42 5) 2)      ; (8 * 5) + 2 == 42
    (assertions/assert-equal (rem 42 -5) 2)     ; (-8 * -5) + 2 == 42
    (assertions/assert-equal (rem -42 5) -2)    ; (-8 * 5) + -2 == -42
    (assertions/assert-equal (rem -42 -5) -2)   ; (8 * -5) + -2 == -42

    ; |num| > |div|, num = k * div
    (assertions/assert-equal (rem 9 3) 0)
    (assertions/assert-equal (rem 9 -3) 0)
    (assertions/assert-equal (rem -9 3) 0)
    (assertions/assert-equal (rem -9 -3) 0)

    )

(deftest mod-tests
    (assertions/assert-equal (rem 23 7) 2)
    (assertions/assert-equal (mod 4 2) 0)
    (assertions/assert-equal (mod 3 2) 1)
    (assertions/assert-equal (mod 6 4) 2)
    (assertions/assert-equal (mod 0 5) 0)

    ;(assertions/assert-equal (mod 2 1/2) 0)
    ;(assertions/assert-equal (mod 2/3 1/2) 1/6)
    ;(assertions/assert-equal (mod 1 2/3) 1/3)

    (assertions/assert-equal (mod 4.0 2.0) 0.0)
    (assertions/assert-equal (mod 4.5 2.0) 0.5)

    ; |num| > |div|, num != k * div
    (assertions/assert-equal (mod 42 5) 2)      ; (42 / 5) * 5 + (42 mod 5)        = 8 * 5 + 2        = 42
    (assertions/assert-equal (mod 42 -5) -3)    ; (42 / -5) * (-5) + (42 mod -5)   = -9 * (-5) + (-3) = 42
    (assertions/assert-equal (mod -42 5) 3)     ; (-42 / 5) * 5 + (-42 mod 5)      = -9 * 5 + 3       = -42
    (assertions/assert-equal (mod -42 -5) -2)  ; (-42 / -5) * (-5) + (-42 mod -5) = 8 * (-5) + (-2)  = -42

    ; |num| > |div|, num = k * div
    (assertions/assert-equal (mod 9 3) 0)      ; (9 / 3) * 3 + (9 mod 3) = 3 * 3 + 0 = 9
    (assertions/assert-equal (mod 9 -3) 0)
    (assertions/assert-equal (mod -9 3) 0)
    (assertions/assert-equal (mod -9 -3) 0)

    ; |num| < |div|
    (assertions/assert-equal (mod 2 5) 2)       ; (2 / 5) * 5 + (2 mod 5)        = 0 * 5 + 2          = 2
    (assertions/assert-equal (mod 2 -5) -3)     ; (2 / -5) * (-5) + (2 mod -5)   = (-1) * (-5) + (-3) = 2
    (assertions/assert-equal (mod -2 5) 3)      ; (-2 / 5) * 5 + (-2 mod 5)      = (-1) * 5 + 3       = -2
    (assertions/assert-equal (mod -2 -5) -2)    ; (-2 / -5) * (-5) + (-2 mod -5) = 0 * (-5) + (-2)    = -2

    ; num = 0, div != 0
    (assertions/assert-equal (mod 0 3) 0)       ; (0 / 3) * 3 + (0 mod 3) = 0 * 3 + 0 = 0
    (assertions/assert-equal (mod 0 -3) 0)

    ; large args
    (assertions/assert-equal (mod 3216478362187432 432143214) 120355456)
)


(deftest bit-not-tests
    (assertions/assert-equal (bit-not 5) -6))

(deftest bit-and-tests
    (assertions/assert-equal (bit-and 5 4) 4)
    (assertions/assert-equal (bit-and 5 4 1) 0))

(deftest bit-or-tests
    (assertions/assert-equal (bit-or 6 5 4 2) 7))

(deftest bit-xor-tests
    (assertions/assert-equal (bit-xor 2 3 4) 5))

(deftest bit-and-not-tests
    (assertions/assert-equal (bit-and-not 3 1 2) 0))

(deftest bit-shift-left-tests
    (assertions/assert-equal (bit-shift-left 1 3) 8))

(deftest bit-shift-right-tests
    (assertions/assert-equal (bit-shift-right 8 3) 1))

(deftest bit-clear-tests
    (assertions/assert-equal (bit-clear 3 1) 1))

(deftest bit-set-tests
    (assertions/assert-equal (bit-set 0 1) 2))

(deftest bit-flip-tests
    (assertions/assert-equal (bit-flip 0 1) 2)
    (assertions/assert-equal (bit-flip 2 1) 0))

(deftest bit-flip-tests
    (assertions/assert-true (bit-test 3 1))
    (assertions/assert-false (bit-test 1 1)))

(deftest integer?-tests
    (assertions/assert-true (integer? 1))
    (assertions/assert-false (integer? "1")))

(deftest even?-tests
    (assertions/assert-true (even? 2))
    (assertions/assert-false (even? 1)))

(deftest odd?-tests
    (assertions/assert-true (odd? 1))
    (assertions/assert-false (odd? 2)))

(deftest complement-tests
    (assertions/assert-true ((complement (fn [] false))))
    (assertions/assert-true ((complement (fn [x] false)) 1))
    (assertions/assert-true ((complement (fn [x y] false)) 1 2))
    (assertions/assert-true ((complement (fn [x y z] false)) 1 2 3)))

(deftest constantly-tests
    (assertions/assert-equal ((constantly 1) 1 2 3 4 5) 1))

(deftest identityi-tests
    (assertions/assert-equal (identity 3) 3)
    (assertions/assert-equal (identity 4) 4))

(deftest peek-tests
    (assertions/assert-equal (peek '(1 2)) 1)
    (assertions/assert-equal (peek nil) nil))

(deftest pop-tests
    (assertions/assert-equal (pop '(1 2)) '(2))
    (assertions/assert-equal (pop nil) nil))

;;map stuff

(deftest contains?-tests
    (assertions/assert-true (contains? [4 4 4 4] 3))
    (assertions/assert-true (contains? {:a 1 :b 2} :a))
    (assertions/assert-false (contains? [1 1 1] 4))
    (assertions/assert-false (contains? {:a 4} :b)))

(deftest get-tests
    (assertions/assert-equal (get {:a 1} :a) 1)
    (assertions/assert-equal (get "abc" 1) "b"))

(deftest dissoc-tests
    (assertions/assert-equal (dissoc {:a 1 :b 2} :b) {:a 1})
    (assertions/assert-equal (dissoc {:a 1 :b 2} :a :b) {}))

(deftest disj-tests
    (assertions/assert-equal (disj #{:a :b :c} :a) #{:b :c}))

(deftest set-tests
    (assertions/assert-true (= #{} (set [])))
    (assertions/assert-true (= #{"foo"} (set ["foo"])))
    (assertions/assert-true (= #{1 2 3} #{1 3 2}))
    ; FIXME vector/map find (assertions/assert-true (= #{#{1 2 3} [4 5 6] {7 8} 9 10} #{10 9 [4 5 6] {7 8} #{1 2 3}}))
    (assertions/assert-true (= #{#{1 2 3} 9 10} #{10 9 #{1 2 3}}))
    (assertions/assert-true (not (= #{nil [] {} 0 #{}} #{})))
    (assertions/assert-true (= (count #{nil [] {} 0 #{}}) 5))
    (assertions/assert-true (= (conj #{1} 1) #{1}))
    (assertions/assert-true (= (conj #{1} 2) #{2 1}))
    ;FIXME (assertions/assert-true (= #{} (-empty #{1 2 3 4})))
    (assertions/assert-true (= (reduce + #{1 2 3 4 5}) 15))
    (assertions/assert-true (= 4 (get #{1 2 3 4} 4)))
    (assertions/assert-true (contains? #{1 2 3 4} 4))
    (assertions/assert-true (contains? #{[] nil 0 {} #{}} {}))
    (assertions/assert-true (contains? #{[1 2 3]} [1 2 3]))
    ;; FIXME
    ;; (assertions/assert-false (= [] {}))
    (assertions/assert-false (= () #{}))
    (assertions/assert-true (= () []))
    (assertions/assert-true (= [] ()))
    (assertions/assert-true (= #{1 2 3} #{1 2 3}))
    (assertions/assert-true (= #{#{1 2 3}} #{#{1 2 3}}))
    (assertions/assert-true (= #{[4 5 6]} #{[4 5 6]}))
    
)

(deftest find-tests
    (assertions/assert-equal (.getKey (find {:a 1} :a)) :a)
    (assertions/assert-equal (.getValue (find {:a 1} :a)) 1))

(deftest select-keys-tests
    (assertions/assert-equal (select-keys {:a 1 :b 2 :c 3} [:a]) {:a 1}))

(deftest keys-tests
    (assertions/assert-equal (keys {:a 1}) [:a]))

(deftest vals-tests
    (assertions/assert-equal (vals {:a 1}) [1]))

(deftest key-tests
    (assertions/assert-equal (key (find {:a 1 :b 2} :b)) :b))

(deftest val-tests
    (assertions/assert-equal (val (find {:a 1 :b 2} :b)) 2))

(deftest name-tests
    (assertions/assert-equal (name 'Foo) "Foo")
    (assertions/assert-equal (name "Foo") "Foo"))

(deftest namespace-tests
    (assertions/assert-equal (namespace 'baz/Foo) "baz")
    (assertions/assert-equal (namespace 'Foo) nil))

; broken need to fix
;(deftest dot-dot-tests
;    (assertions/assert-equal (.. :foo (.-sym) (.-name)) ":foo"))

(deftest ->-tests
    (assertions/assert-equal (-> " baz " (.rstrip) (.lstrip)) "baz"))

;(deftest ->>-tests ; haven't a clue how to test this
;    )


;;; var stuff


;;; if-let and when-let tests are from
;;; http://blog.jayfields.com/2011/03/clojure-if-let-and-when-let.html

(deftest if-let-tests
	(assertions/assert-equal (if-let [a 4] (+ a 4) (+ 10 10)) 8)
	(assertions/assert-equal (if-let [a nil] (+ a 4) (+ 10 10)) 20))


(deftest when-let-tests
    (assertions/assert-equal (when-let [a 9] (+ a 4))  13)
    (assertions/assert-equal (when-let [a nil] (+ a 4)) nil))

;;; functional stuff

(deftest comp-tests
    (assertions/assert-equal ((comp str +) 8 8 8) "24"))

(deftest juxt-tests
    (assertions/assert-equal ((juxt :a :b :c :d) {:a 1 :b 2 :c 3 :d 4}) [1 2 3 4]))

(deftest partial-tests
    (assertions/assert-equal ((partial + 1) 1) 2))

;;; sequence stuff

(deftest sequence-tests
    (assertions/assert-equal (sequence [1 2 3]) '(1 2 3)))

(deftest every?-tests
    (assertions/assert-true (every? even? '(2 4 6)))
    (assertions/assert-false (every? even? '(1 4 6))))

(deftest every?-tests
    (assertions/assert-false (not-every? even? '(2 4 6)))
    (assertions/assert-true (not-every? even? '(1 4 6))))

(deftest some-tests
    (assertions/assert-true (some even? '(1 2 3 4)))
    (assertions/assert-equal (some even? '(1 3 5 7)) nil))

(deftest not-any?-tests
    (assertions/assert-true (not-any? odd? '(2 4 6)))
    (assertions/assert-false (not-any? odd? '(1 2 3))))

;(deftest dotimes-tests
;    (dotimes [n 5] (assertions/assert-true (and (>= n 0) (< n 5)))))

(deftest map-tests
    (assertions/assert-equal (map inc [1 2 3 4 5]) (seq [2 3 4 5 6])))

(deftest mapcat-tests
    (assertions/assert-equal (mapcat reverse [[3 2 1 0] [6 5 4] [9 8 7]]) [0 1 2 3 4 5 6 7 8 9]))
 
(deftest filter-tests
    (assertions/assert-equal (filter even? [1 2 3 4 5]) [2 4]))

(deftest remove-tests
    (assertions/assert-equal (remove even? [1 2 3 4 5]) [1 3 5]))

(deftest take-tests
    (assertions/assert-equal (take 2 [1 2 3 4]) [1 2]))

(deftest take-while-tests
    (assertions/assert-equal (take-while even? [2 2 1 1]) [2 2]))

(deftest drop-tests
    (assertions/assert-equal (drop 1 [1 2 3]) [2 3]))

(deftest drop-last-tests
    (assertions/assert-equal (drop-last 2 [1 2 3 4]) [1 2]))

(deftest take-last-tests
    (assertions/assert-equal (take-last 3 [1 2 3 4]) [2 3 4]))

(deftest drop-while-tests
    (assertions/assert-equal (drop-while even? [2 4 6 1 2 3]) [1 2 3]))

(deftest cycle-tests
    (assertions/assert-equal (take 6 (cycle [1 2 3])) [1 2 3 1 2 3]))

(deftest split-at-tests
    (assertions/assert-equal (split-at 3 [1 2 3 4 5]) [[1 2 3] [4 5]]))

(deftest split-with-tests
    (assertions/assert-equal (split-with odd? [1 1 1 1 2 2 2 2]) [[1 1 1 1] [2 2 2 2]]))

(deftest repeat-tests
    (assertions/assert-equal (repeat 3 1) [1 1 1]))

(deftest interate-tests
    (assertions/assert-equal (take 3 (iterate inc 0)) [0 1 2]))

(deftest range-tests
    (assertions/assert-equal (range 0 8 2) [0 2 4 6]))

(deftest merge-tests
    (assertions/assert-equal (merge {:a 1 :b 2} {:a 3 :c 4}) {:a 3 :b 2 :c 4}))

(deftest merge-with-tests
    (assertions/assert-equal (merge-with + 
                   {:a 1  :b 2}
                   {:a 9  :b 98 :c 0})
                  {:c 0, :a 10, :b 100}))


(deftest zipmap-tests
    (assertions/assert-equal (zipmap [:a :b :c :d :e] [1 2 3 4 5])
                    {:e 5, :d 4, :c 3, :b 2, :a 1}))


(deftest sort-tests
    (assertions/assert-equal (sort [3 1 2 4]) [1 2 3 4])
    (assertions/assert-equal (sort > (vals {:foo 5, :bar 2, :baz 10})) [10 5 2]))

(deftest sort-by-tests
    (assertions/assert-equal (sort-by first > [[1 2] [2 2] [2 3]]) [[2 2] [2 3] [1 2]]))

(deftype Accum [i]
    ISeq  ; Bit of a hack until we get definterface implemented
    (inc [self] (py/setattr self "i" (inc i))))


(deftest dorun-tests
    (let [accum (Accum 0)]
         (dorun (map (fn [x] (.inc accum))
                     (range 10)))
         (assertions/assert-equal (.-i accum) 10)))

(deftest nthnext-tests
    (assertions/assert-equal (nthnext (range 10) 3) '(3 4 5 6 7 8 9)))

(deftest nth-tests
    (assertions/assert-equal (nth (list 1 2 3) 1) 2)
    (assertions/assert-equal (nth [1 2 3] 1) 2))

(deftest partition-tests
    (assertions/assert-equal (partition 4 (range 20)) 
                  '((0 1 2 3) (4 5 6 7) (8 9 10 11) (12 13 14 15) (16 17 18 19)))
    (assertions/assert-equal (partition 4 6 ["a" "b" "c" "d"] (range 20))
                  '((0 1 2 3) (6 7 8 9) (12 13 14 15) (18 19 "a" "b"))))

(deftest eval-tests
    (assertions/assert-equal (eval '(+ 1 2)) 3))

(deftest doseq-tests
    (doseq [x [1 2 3]
                          y [1 2 3]]
                         (py/print (* x y))))
;; prints      
;;[1 2 3 2 4 6 3 6 9]

(deftest do-times
    (let [accum (Accum 0)]
         (dotimes [i 5]
             (assertions/assert-equal (.-i accum) i)
             (.inc accum))))

(deftest class-tests
    (assertions/assert-equal (class "f") py/str))

(deftest num-tests
    (assertions/assert-equal (num "1") 1))

(deftest num-tests
    (assertions/assert-true (float? (num "inf"))))

(deftest number?-tests
    (assertions/assert-true (number? 1)))

(deftest read-string-tests
    (assertions/assert-equal (read-string "12") 12))

(deftest subvec-tests
    (assertions/assert-equal (subvec [1 2 3 4] 1 3) [2 3]))

(deftest doto-tests
    (assertions/assert-equal (doto (py/list) (.append "foo") (.append "bar")) ["foo" "bar"]))

(deftest memfn-tests
    (assertions/assert-equal (let [f (memfn join ch)]
                       (f "," ["1" "2"])) 
                  "1,2"))

(deftest find-ns-tests
    (assertions/assert-true (not (nil? (find-ns 'clojure.core)))))

(deftest create-ns-tests
    (assertions/assert-true (identical? (find-ns 'clojure.core) (create-ns 'clojure.core)))
    (assertions/assert-true (not (nil? (create-ns 'foo.bar)))))

(deftest ns-name-tests
    (assertions/assert-equal (ns-name 'clojure.core) 'clojure.core))

(deftest let-tests
    (let [[x & y] [1 2 3]]
         (assertions/assert-equal x 1)
         (assertions/assert-equal y [2 3])))

(deftest let-tests
    ((fn td [[x & y]]
         (assertions/assert-equal x 1)
         (assertions/assert-equal y [2 3])) [ 1 2 3]))

(deftest loop-tests
    (loop [[x & y] [1 2 3]]
         (assertions/assert-equal x 1)
         (assertions/assert-equal y [2 3])))

(deftest when-first-tests
    (assertions/assert-equal (when-first [a [1 2 3]] a) 1)
    (assertions/assert-equal (when-first [a []] a) nil)
    (assertions/assert-equal (when-first [a nil] a) nil))

(deftest lazy-cat-tests
    (assertions/assert-equal (lazy-cat [1 2 3] [4 5 6]) [1 2 3 4 5 6]))

(deftest for-tests
    (assertions/assert-equal (for [x [1 2 3]] x) [1 2 3]))

(deftest destructure-tests
    (assertions/assert-equal (map (fn [[k v]] k) {:1 1}) [:1])
    (assertions/assert-equal (map (fn [[k v]] v) {:1 1}) [1]))

(deftest map-entry-tests
    (assertions/assert-equal (-> {:1 :2} first first) :1)
    (assertions/assert-equal (-> {:1 :2} first second) :2))
    

(deftest reduce-tests
    (assertions/assert-equal (reduce + '(1 2 3 4)) 10)
    (assertions/assert-equal (reduce + 5 '(1 2 3 4)) 15))

(deftest empty?-tests
    (assertions/assert-true (empty? []))
    (assertions/assert-false (empty? [1])))



(deftest do-tests
    (assertions/assert-equal (do) nil)
    (assertions/assert-equal (do 1) 1)
    (assertions/assert-equal (do 1 2) 2))

(deftest lazy-seq-tests
    (.more (range 1))) ; would throw an error before fix to Issue #45

(deftest comment-tests
    (comment (assertions/assert-true false)))

(deftest vec-tests
    (assertions/assert-equal ((fn [& y] (vec y)) 1 2) [1 2]))

(deftest reify-tests
    (let [f (fn [y] (reify ISeq
                           (seq [self] self)
                           (first [self] y)))]
         (assertions/assert-equal (first (f 42)) 42)))

(deftest defprotocol-tests
    (defprotocol Foo "Foo Protocol"
        (foo [self] "Foo_foo")))

(deftest truthiness-tests
    (assertions/assert-true (if true true false))
    (assertions/assert-true (if 0 true false))
    (assertions/assert-true (if '() true false))
    (assertions/assert-true (if '(1) true false))
    (assertions/assert-true (if [] true false))
    (assertions/assert-true (if [1] true false))
    (assertions/assert-true (if {} true false))
    (assertions/assert-true (if {1 2} true false))
    (assertions/assert-true (if #{} true false))
    (assertions/assert-true (if #{1} true false))
    (assertions/assert-true (if "" true false))
    (assertions/assert-true (if 1 true false))
    (assertions/assert-true (if :spam true false))
    (assertions/assert-false (if false true false))
    (assertions/assert-false (if nil true false)))


(deftest defrecord-tests
    (defrecord FooRecord [x y] IDeref (deref [self] 42))
    (let [foo (FooRecord 1 2)]
         (assertions/assert-equal (:x foo) 1)
         (assertions/assert-equal (:y foo) 2)
         (assertions/assert-equal (get foo "x") 1)         
         (assertions/assert-equal (get foo 'x) 1)
         (assertions/assert-equal (vec (keys foo)) ["x" "y"])         
         (assertions/assert-equal (count foo) 2)
         (assertions/assert-equal (:x (.without foo "x")) nil)
         (assertions/assert-equal (deref foo) 42)
         (assertions/assert-true  (= (FooRecord 1 2) foo))
         (assertions/assert-false (= (FooRecord 2 2) foo))
         (assertions/assert-true  (= (py/hash (FooRecord 1 2)) 
         	 	             (py/hash foo)))
         (assertions/assert-false (= (py/hash (FooRecord 2 2)) 
         	 	             (py/hash foo)))))
         

(deftest extend-tests
    (extend py/int ISeq {:seq (fn [self] 42)})
    (assertions/assert-equal (seq 1) 42))
         
(deftest binding-tests
    (def x 0)
    (def y 0)
    (binding [x 1 y 2]
        (assertions/assert-equal (+ x y) 3))
    (assertions/assert-equal (+ x y) 0))

(deftest var-tests
    (assertions/assert-true (py/hasattr #'cons "deref")))

(deftest doc-tests
    (defn baz "This is a test" [] nil)
    (doc baz))

(definterface IClosable
        (__enter__ [self])
        (__exit__ [self]))

(deftype MutatableCloser [state]
        IClosable
        (__enter__ [self] 
            (py/setattr self "state" :enter))
        (__exit__ [self]
            (py/setattr self "state" :exit)))
    
(deftest with-open-tests
    (let [mc (MutatableCloser :unknown)]
        (assertions/assert-equal (.-state mc) :unknown)
        (with-open [obj mc]
             (assertions/assert-equal (.-state obj) :enter))
        (assertions/assert-equal (.-state mc) :exit)))

(deftest generator-seq-tests
    (let [gen (fn [times]
                (dotimes [x times]
                    (py.bytecode/YIELD_VALUE x)))]
         (assertions/assert-equal (seq (gen 3)) [0 1 2])))
        
