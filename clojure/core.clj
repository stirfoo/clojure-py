;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
(ns* ^{:doc "The core Clojure language."
       :author "Rich Hickey"}
  clojure.core)

(def unquote)
(def unquote-splicing)

(def
 ^{:arglists '([& items])
   :doc "Creates a new list containing the items."
   :added "1.0"}
  list clojure.lang.persistentlist/creator)

(def
 ^{:arglists '([& items])
   :doc "Creates a new vector containing the items."
   :added "1.0"}
  vector clojure.lang.rt/vector)

(def 
  ^{:static true}
  extend clojure.lang.protocol/extend)

(def
 ^{:arglists '([& items])
   :doc "Creates a new hashmap containing the items."
   :added "1.0"}
  hashmap clojure.lang.rt/map)

(def
 ^{:arglists '([x seq])
    :doc "Returns a new seq where x is the first element and seq is
    the rest."}
 cons (fn* cons [x seq] (clojure.lang.rt/cons x seq)))

;during bootstrap we don't have destructuring let, loop or fn, will redefine later
(def
  ^{:macro true
    :added "1.0"}
  let (fn* let [&form &env & decl] (cons 'let* decl)))

(def
 ^{:macro true
   :added "1.0"}
 loop (fn* loop [&form &env & decl] (cons 'loop* decl)))

(def
 ^{:macro true
   :added "1.0"}
 fn (fn* fn [&form &env & decl] 
         (cons 'fn* decl)))

(def
  ^{:macro true
    :added "1.0"
    :arglists '([pred val] [pred val else])}
  if (fn* if [ form env & decl] (cons 'if* decl)))

(def
    ^{:arglists '([& args])
      :doc "Clojure version of RT.assoc"
      :added "1.0"}
 _assoc (fn* assoc [col k v]
                   (py/if col
                      (.assoc col k v)
                      (clojure.lang.rt/map k v))))

(def
 ^{:arglists '(^clojure.lang.ISeq [coll])
   :doc "Returns a seq on the collection. If the collection is
    empty, returns nil.  (seq nil) returns nil. seq also works on
    Strings, native Python lists and any objects
    that implement __getitem__."
   :tag clojure.lang.ISeq
   :added "1.0"
   :static true}
 seq (fn seq [coll] (. clojure.lang.rt (seq coll))))

(def
 ^{:arglists '([^Class c x])
   :doc "Evaluates x and tests if it is an instance of the class
    c. Returns true or false"
   :added "1.0"}
 instance? (fn instance? [c x] (py/isinstance x c)))

(def
 ^{:arglists '([x])
   :doc "Return true if x implements ISeq"
   :added "1.0"
   :static true}
 seq? (fn seq? [x] (instance? clojure.lang.iseq/ISeq x)))

(def
 ^{:arglists '([coll])
   :doc "Returns the first item in the collection. Calls seq on its
    argument. If coll is nil, returns nil."
   :added "1.0"
   :static true}
 first (fn first [s]
	   (py/if (py.bytecode/COMPARE_OP "is not" s nil)
	     (py/if (instance? ISeq s)
	       (.first s)
	       (let [s (seq s)]
	            (py/if (py.bytecode/COMPARE_OP "is not" s nil)
	                   (.first s)
			   nil)))
	    nil)))

(def
 ^{:arglists '([coll])
   :tag clojure.lang.ISeq
   :doc "Returns a seq of the items after the first. Calls seq on its
  argument.  If there are no more items, returns nil."
   :added "1.0"
   :static true}  
 next (fn next [s]
 	 			 (py/if (is? nil s)
 	 			 	 nil
					 (py/if (instance? ISeq s)
						 (.next s)
						 (let [s (seq s)]
							  (.next s))))))

(def
 ^{:arglists '([coll])
   :tag clojure.lang.ISeq
   :doc "Returns a possibly empty seq of the items after the first. Calls seq on its
  argument."
   :added "1.0"
   :static true}  
 rest (fn rest [x] (py/if (py/isinstance x ISeq)
                       (.more x)
                       (let [s (seq x)]
                           (py/if s
                               (.more s)
                               clojure.lang.persistentlist/EMPTY)))))

(def
 ^{:doc "Same as (first (next x))"
   :arglists '([x])
   :added "1.0"
   :static true}
 second (fn second [x] (first (next x))))

(def
 ^{:doc "Same as (first (first x))"
   :arglists '([x])
   :added "1.0"
   :static true}
 ffirst (fn ffirst [x] (first (first x))))

(def
 ^{:doc "Same as (next (first x))"
   :arglists '([x])
   :added "1.0"
   :static true}
 nfirst (fn nfirst [x] (next (first x))))

(def
 ^{:doc "Same as (first (next x))"
   :arglists '([x])
   :added "1.0"
   :static true}
 fnext (fn fnext [x] (first (next x))))

(def
 ^{:doc "Same as (next (next x))"
   :arglists '([x])
   :added "1.0"
   :static true}
 nnext (fn nnext [x] (next (next x))))

(def
 ^{:arglists '([x])
   :doc "Return true if x is a String"
   :added "1.0"
   :static true}
 string? (fn string? [x] (instance? py/str x)))

(def
 ^{:arglists '([x])
   :doc "Return true if x implements IPersistentMap"
   :added "1.0"
   :static true}
 map? (fn ^:static map? [x] (instance? clojure.lang.ipersistentmap/IPersistentMap x)))

(def
 ^{:arglists '([x])
   :doc "Return true if x implements IPersistentVector"
   :added "1.0"
   :static true}
 vector? (fn vector? [x] (instance? clojure.lang.ipersistentvector/IPersistentVector x)))

(def
 ^{:arglists '([map key val] [map key val & kvs])
   :doc "assoc[iate]. When applied to a map, returns a new map of the
    same (hashed/sorted) type, that contains the mapping of key(s) to
    val(s). When applied to a vector, returns a new vector that
    contains val at index. Note - index must be <= (count vector)."
   :added "1.0"}
 assoc
 (fn assoc
   ([map key val] (_assoc map key val))
   ([map key val & kvs]
    (let [ret (assoc map key val)]
      (py/if kvs
        (recur ret (first kvs) (second kvs) (nnext kvs))
        ret)))))

;;;;;;;;;;;;;;;;; metadata ;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def
 ^{:arglists '([obj])
   :doc "Returns the metadata of obj, returns nil if there is no metadata."
   :added "1.0"}
 meta (fn meta [x]
        (py/if (py/hasattr x "meta")
          (.meta x))))

(def
 ^{:arglists '([obj m])
   :doc "Returns an object of the same type and value as obj, with
    map m as its metadata."
   :added "1.0"}
 with-meta (fn with-meta [x m]
             (. x (withMeta m))))

;;;;;;;;;;;;;;;;

(def 
 ^{:arglists '([coll])
   :doc "Return the last item in coll, in linear time"
   :added "1.0"}
 last (fn last [s]
        (py/if (next s)
          (recur (next s))
          (first s))))

(def nil?
 ^{:tag Boolean
   :doc "Returns true if x is nil, false otherwise."
   :added "1.0"
   :static true}
  (fn nil? [x] (is? x nil)))

(def
 ^{:arglists '([& args])
   :doc "Clojure version of RT.conj"
   :added "1.0"}
 _conj (fn _conj [coll x] (py/if (nil? coll)
                            clojure.lang.persistentlist/EMPTY
                            (.cons coll x))))


(def
 ^{:arglists '([coll x] [coll x & xs])
   :doc "conj[oin]. Returns a new collection with the xs
    'added'. (conj nil item) returns (item).  The 'addition' may
    happen at different 'places' depending on the concrete type."
   :added "1.0"}
 conj (fn conj 
        ([coll x] (_conj coll x))
        ([coll x & xs]
         (py/if (nil? xs)
             (conj coll x)
             (recur (conj coll x) (first xs) (next xs))))))



(def 
 ^{:arglists '([coll])
   :doc "Return a seq of all but the last item in coll, in linear time"
   :added "1.0"}
 butlast (fn butlast [s]
           (loop [ret [] s s]
             (py/if (nil? (next s))
               (seq ret)  
               (recur (conj ret (first s)) (next s))))))

 		
(def set-macro 
    (fn set-macro [f]
        (py/setattr f "macro?" true)
        f))	 
 	 
 	 
(def ^{:private true :dynamic true}
  assert-valid-fdecl (fn [fdecl]))

(def
 ^{:private true}
 sigs
 (fn [fdecl]
   (assert-valid-fdecl fdecl)
   (let [asig 
         (fn [fdecl]
           (let [arglist (first fdecl)
                 ;elide implicit macro args
                 arglist (py/if (.__eq__ '&form (first arglist)) 
                           (clojure.lang.rt/subvec arglist 2 (py/len arglist))
                           arglist)
                 body (next fdecl)]
             (py/if (map? (first body))
               (py/if (next body)
                 (with-meta arglist (conj (py/if (meta arglist) (meta arglist) {}) (first body)))
                 arglist)
               arglist)))]
     (py/if (seq? (first fdecl))
       (loop [ret [] fdecls fdecl]
         (py/if fdecls
           (recur (conj ret (asig (first fdecls))) (next fdecls))
           (seq ret)))
       (list (asig fdecl))))))



(def 

 ^{:doc "Same as (def name (fn [params* ] exprs*)) or (def
    name (fn ([params* ] exprs*)+)) with any doc-string or attrs added
    to the var metadata. prepost-map defines a map with optional keys
    :pre and :post that contain collections of pre or post conditions."
   :arglists '([name doc-string? attr-map? [params*] prepost-map? body]
                [name doc-string? attr-map? ([params*] prepost-map? body)+ attr-map?])
   :added "1.0"}
 defn (fn defn [&form &env name & fdecl]
        (let [m (py/if (string? (first fdecl))
                  {:doc (first fdecl)}
                  {})
              fdecl (py/if (string? (first fdecl))
                      (next fdecl)
                      fdecl)
              m (py/if (map? (first fdecl))
                  (conj m (first fdecl))
                  m)
              fdecl (py/if (map? (first fdecl))
                      (next fdecl)
                      fdecl)
              fdecl (py/if (vector? (first fdecl))
                      (list fdecl)
                      fdecl)
              m (py/if (map? (last fdecl))
                  (conj m (last fdecl))
                  m)
              fdecl (py/if (map? (last fdecl))
                      (butlast fdecl)
                      fdecl)
              m (conj {:arglists (list 'quote (sigs fdecl))} m)
              m (let [inline (:inline m)
                      ifn (first inline)
                      iname (second inline)]
                  ;; same as: (py/if (and (= 'fn ifn) (not (symbol? iname))) ...)
                  (py/if (py/if (.__eq__ 'fn ifn)
                        (py/if (instance? clojure.lang.symbol/Symbol iname) false true))
                    ;; inserts the same fn name to the inline fn if it does not have one
                    (assoc m :inline (cons ifn (cons (clojure.lang.symbol/symbol (.concat (.getName name) "__inliner"))
                                                     (next inline))))
                    m))
              m (conj (py/if (meta name) (meta name) {}) m)
              ]
          (list 'def (with-meta name m)
                ;;todo - restore propagation of fn name
                ;;must figure out how to convey primitive hints to self calls first
                (cons `fn fdecl)))))

(set-macro defn)


(defn vec
  "Creates a new vector containing the contents of coll."
  {:added "1.0"
   :static true}
  ([coll]
    (py/if (nil? coll)
        nil
        (clojure.lang.persistentvector/vec coll))))

(def
 ^{:doc "Like defn, but the resulting function name is declared as a
  macro and will be used as a macro by the compiler when it is
  called."
   :arglists '([name doc-string? attr-map? [params*] body]
                 [name doc-string? attr-map? ([params*] body)+ attr-map?])
   :added "1.0"}
 defmacro (fn [&form &env
                name & args]
             (let [prefix (loop [p (list name) args args]
                            (let [f (first args)]
                              (py/if (string? f)
                                (recur (cons f p) (next args))
                                (py/if (map? f)
                                  (recur (cons f p) (next args))
                                  p))))
                   fdecl (loop [fd args]
                           (py/if (string? (first fd))
                             (recur (next fd))
                             (py/if (map? (first fd))
                               (recur (next fd))
                               fd)))
                   fdecl (py/if (vector? (first fdecl))
                           (list fdecl)
                           fdecl)
                   add-implicit-args (fn [fd]
                             (let [args (first fd)]
                               (cons (vec (cons '&form (cons '&env args))) (next fd))))
                   add-args (fn [acc ds]
                              (py/if (nil? ds)
                                acc
                                (let [d (first ds)]
                                  (py/if (map? d)
                                    (conj acc d)
                                    (recur (conj acc (add-implicit-args d)) (next ds))))))
                   fdecl (seq (add-args [] fdecl))
                   decl (loop [p prefix d fdecl]
                          (py/if p
                            (recur (next p) (cons (first p) d))
                            d))]
               (list 'do
                     (cons `defn decl)
                     (list 'set-macro name)
                     (list 'py/setattr name "_macro-form" (list 'quote decl))
                     name))))


(set-macro defmacro)


(defmacro when
  "Evaluates test. If logical true, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  (list 'if test (cons 'do body)))

(defmacro when-not
  "Evaluates test. If logical false, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
    (list 'if test nil (cons 'do body)))

(defn false?
  "Returns true if x is the value false, false otherwise."
  {:added "1.0"}
  [x] (.__eq__ x false))

(defn true?
  "Returns true if x is the value true, false otherwise."
  {:added "1.0"}
  [x] (.__eq__ x true))

(defn not
  "Returns true if x is logical false, false otherwise."
  {:added "1.0"}
  [x] (py/if x false true))

(defn println
  "Pass through one string to Python print.  This is just for debugging,
   and is not a proper final solution."
  {}
  [x] (py/print x))

(defn str
  "With no args, returns the empty string. With one arg x, returns
  x.__str__().  (str nil) returns the empty string. With more than
  one arg, returns the concatenation of the str values of the args."
  {:added "1.0"}
  ([] "")
  ([x]
   (py/if (nil? x) "" (py/str x)))
  ([x & ys]
     (let [lst (py/list (py/str x))
           lst (loop [remain ys]
                 (py/if remain
                   (do (.append lst (py/str (first remain)))
                       (recur (next remain)))
                   lst))]
           (.join "" lst))))

(defn symbol?
  "Return true if x is a Symbol"
  {:added "1.0"}
  [x] (instance? clojure.lang.symbol/Symbol x))

(defn var?
  "Return true if x is a Var"
  {:static true}
  [x] (instance? clojure.lang.var/Var x))

(defn keyword?
  "Return true if x is a Keyword"
  {:added "1.0"}
  [x] (instance? clojure.lang.cljkeyword/Keyword x))

(defn symbol
  "Returns a Symbol with the given namespace and name."
  {:tag clojure.lang.Symbol
   :added "1.0"}
  ([name] (py/if (symbol? name) name (clojure.lang.symbol/symbol name)))
  ([ns name] (clojure.lang.symbol/symbol ns name)))


(defn inc
  "Returns a number one greater than num. Does not auto-promote
  longs, will throw on overflow. See also: inc'"
  {:added "1.2"
   :static true}
  [x] (py.bytecode/BINARY_ADD x 1))

(defn +
  [x y] (py.bytecode/BINARY_ADD x y))

(defn hash-map
  "keyval => key val
  Returns a new hash map with supplied mappings."
  {:added "1.0"}
  ([] {})
  ([& keyvals]
      (let [coll {}]
          (loop [keyvals (seq keyvals) coll coll]
              (py/if (nil? keyvals)
                  coll
                  (do (py/if (py.bytecode/COMPARE_OP "==" (py/len keyvals) 1)
                          (throw (py/Exception "Even number of args required to hash-map")))
                      (py/if (py.bytecode/COMPARE_OP "in" (first keyvals) coll)
                          (throw (py/Exception "Duplicate keys found in hash-map")))
                      (recur (nnext keyvals) 
                             (.assoc coll 
                                    (first keyvals)
                                    (fnext keyvals)))))))))
          
      


(defn gensym
  "Returns a new symbol with a unique name. If a prefix string is
  supplied, the name is prefix# where # is some unique number. If
  prefix is not supplied, the prefix is 'G__'."
  {:added "1.0"}
  ([] (gensym "G__"))
  ([prefix-string] (. clojure.lang.symbol (symbol (str prefix-string (py/str (. clojure.lang.rt (nextID))))))))


(defmacro cond
  "Takes a set of test/expr pairs. It evaluates each test one at a
  time.  If a test returns logical true, cond evaluates and returns
  the value of the corresponding expr and doesn't evaluate any of the
  other tests or exprs. (cond) returns nil."
  {:added "1.0"}
  [& clauses]
    (when clauses
      (list 'py/if (first clauses)
            (py/if (next clauses)
                (second clauses)
                (throw (IllegalArgumentException
                         "cond requires an even number of forms")))
            (cons 'clojure.core/cond (next (next clauses))))))


(defn spread
  {:private true}
  [arglist]
  (cond
   (nil? arglist) nil
   (nil? (next arglist)) (seq (first arglist))
   :else (cons (first arglist) (spread (next arglist)))))

(defn list*
  "Creates a new list containing the items prepended to the rest, the
  last of which will be treated as a sequence."
  {:added "1.0"}
  ([args] (seq args))
  ([a args] (cons a args))
  ([a b args] (cons a (cons b args)))
  ([a b c args] (cons a (cons b (cons c args))))
  ([a b c d & more]
     (cons a (cons b (cons c (cons d (spread more)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn =
  "Equality. Returns true if x equals y, false if not. Same as
  Java x.equals(y) except it also works for nil, and compares
  numbers and collections in a type-independent manner.  Clojure's immutable data
  structures define equals() (and thus =) as a value, not an identity,
  comparison."
  {:added "1.0"}
  ([x] true)
  ([x y] (py.bytecode/COMPARE_OP "==" x y))
  ([x y & more]
   (py/if (py.bytecode/COMPARE_OP "==" x y)
     (py/if (next more)
       (recur y (first more) (next more))
       (py.bytecode/COMPARE_OP "==" y (first more)))
     false)))


(defn not=
  "Same as (not (= obj1 obj2))"
  {:added "1.0"}
  ([x] false)
  ([x y] (not (= x y)))
  ([x y & more]
   (not (apply = x y more))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn make-init
    "Creates a __init__ method for use in deftype"
    [fields]
    (loop [fields fields
           args ['self]
           body []]
           (py/if (not fields)
               (cons 'fn (cons '__init__ (cons args body)))
               (let [newargs (conj args (first fields))
                     newbody (conj body (list 'py/setattr
                                              'self 
                                              (py/str (first fields))
                                              (first fields)))]
                     (recur (next fields) newargs newbody)))))

(defn make-props
    [fields selfname]
    (loop [remain (seq fields)
        props []]
       (py/if (nil? remain)
           props
           (recur (next remain) 
                  (conj (conj props (first remain))
                        (list 'py/getattr selfname (.-name (first remain))))))))

(defn prop-wrap-fn
    [name members f]
    (list 'fn 
 	  (clojure.lang.symbol/symbol (str name "_" (first f))) 
	  (second f)
          (list* 'let-macro 
		(make-props members 
			    (first (fnext f)))
		(next (next f)))))

(defn prop-wrap-multi
    [name members f]
    (let [name (clojure.lang.symbol/symbol (str name "_" (first f))) 
          f (next f)
          wrapped (loop [remain f
                         wr []]
			(py/if remain
			    (let [cur (first remain)
				   args (first cur)
				   body (next cur)]
				  (recur (next remain) 
 			               (cons (list args
					          (list* 'let-macro
						      (make-props members
								  (first args))
						      body))
					     wr)))
			    wr))]
	(list* 'fn name wrapped)))
                        
         
(defn prop-wrap
    [name members f]
    (if (vector? (fnext f))
	(prop-wrap-fn name members f)
	(prop-wrap-multi name members f)))


(defmacro deftype
    [name fields & specs]
    (loop [specs (seq specs)
           inherits []
           fns (if (= (py/len fields) 0) {} {"__init__" (make-init fields)})]
          (cond (not specs)
                   (list 'def name (list 'py/type (.-name name) 
                                                   (list 'py/tuple 
                                                         (conj inherits py/object))
                                                   (list '.toDict fns)))
                (symbol? (first specs))
                    (recur (next specs) 
                           (conj inherits (first specs))
                           fns)
                (instance? clojure.lang.ipersistentlist/IPersistentList
                           (first specs))
                    (recur (next specs)
                           inherits
                           (assoc fns (py/str (ffirst specs))
                           	   	      (prop-wrap name fields (first specs)))))))
(def definterface deftype)
(set-macro definterface)

;;;;;;;;;;;;;;;;;Lazy Seq and Chunked Seq;;;;;;;;;;;;;;;;


(definterface IPending []
	(isRealized [self] nil))

(deftype LazySeq [fnc sv s _meta]
	(withMeta [self meta]
		(LazySeq nil nil (.seq self) meta))
	(sval [self]
		(when (not (nil? fnc))
			  (py/setattr self "sv" (fnc))
			  (py/setattr self "fnc" nil))
		(py/if (not (nil? sv))
			sv
		s))
	clojure.lang.iseq/ISeq
	(seq [self]
		(.sval self)
		(when (not (nil? sv))
		      (let [ls sv]
		           (py/setattr self "sv" nil)
          		   (py/setattr self "s"
       		 	 	   (loop [ls ls]
					         (py/if (instance? LazySeq ls)
					                (recur (.sval ls))
					                (seq ls))))))
		s)
	(__len__ [self]
	    (loop [c 0
	           s (.seq self)]
	          (py/if (nil? s)
	              c
	              (recur (inc c) (next s)))))
	(__eq__ [self other]
	    (loop [s (seq self)
	           o (seq other)]
	           (if (nil? s)
	               (if (nil? o)
	                   true
	                   false)
	               (if (nil? o)
	                   false
	                   (if (py.bytecode/COMPARE_OP "==" (first s) (first o))
	                       (recur (next s) (next o))
	                       false)))))
	(__iter__ [self]
	    (loop [s (seq self)]
	          (when s
	              (py.bytecode/YIELD_VALUE (first s))
	              (recur (next s)))))
	(__repr__ [self]
	    (loop [c []
	           s (seq self)
	           cnt 0]
	           (if (not (nil? s))
                   (if (py.bytecode/COMPARE_OP "<" cnt 10)
                       (recur (conj c (str (first s))) 
                              (next s)
                              (inc cnt))
                       (recur (conj c "...")
                              nil
                              11)))
               (str "[" (.join " " c) "]")))
	(first [self]
	    (.seq self)
	    (py/if (nil? s)
	        nil
	        (.first s)))
	(next [self]
	    (.seq self)
	    (py/if (nil? s)
	        nil
	        (.next s)))
	(more [self]
	    (.seq self)
	    (py/if (nil? s)
	        (list)
	        (.more s)))
	(cons [self o]
	    (cons o (.seq self)))
	(empty [self]
	    (list))
        ;; IPrintable protocol
        ;; These methods realize the entire sequence as Clojure does. But
        ;; __repr__ does not. Is the intent to prevent spamming the repl?
        clojure.lang.iprintable/IPrintable
        (writeAsString [self writer]
          (.write writer "(")
          (loop [s (.seq self)]
            (when s
              (clojure.protocols/writeAsString (.first s) writer)
              (when (.next s)
                (.write writer " "))
              (recur (.next s))))
          (.write writer ")"))
        (writeAsReplString [self writer]
          (.write writer "(")
          (loop [s (.seq self)]
            (when s
              (clojure.protocols/writeAsReplString (.first s) writer)
              (when (.next s)
                (.write writer " "))
              (recur (.next s))))
          (.write writer ")")))


(clojure.lang.protocol/extendForAllSubclasses clojure.lang.iseq/ISeq)
(clojure.lang.protocol/extendForAllSubclasses
 clojure.lang.iprintable/IPrintable)


                        

(defmacro lazy-seq
  "Takes a body of expressions that returns an ISeq or nil, and yields
  a Seqable object that will invoke the body only the first time seq
  is called, and will cache the result and return it on all subsequent
  seq calls. See also - realized?"
  {:added "1.0"}
  [& body]
  (list 'clojure.core/LazySeq (list* '^{:once true} fn* [] body) nil nil nil))    

(extend clojure.lang.pytypes/pyTypeGenerator
    Seqable
    {:seq (fn generator-seq [self]
               (lazy-seq
                   (try
                       (let [result (.next self)]
                           (cons result (generator-seq self)))
                       (catch py/StopIteration e nil))))})


(definterface IChunkedSeq [] 
	clojure.lang.sequential/Sequential

	clojure.lang.iseq/ISeq
	(chunkedFirst [self] nil)
	(chunkedNext [self] nil)
	(chunkedMore [self] nil))



(deftype ArrayChunk [array off end]
    (__getitem__ 
        ([self i]
          (py.bytecode/BINARY_SUBSCR array (py.bytecode/BINARY_ADD off i)))
        ([self i not-found]
          (if (py.bytecode/COMPARE_OP ">=" i 0)
              (if (py.bytecode/COMPARE_OP "<" i (py/len self))
                  (py.bytecode/BINARY_SUBSCR array i)
                  not-found)
              not-found)))

    (__len__ [self]
        (py.bytecode/BINARY_SUBTRACT end off))

    (dropFirst [self]
        (if (= off end)
            (throw (IllegalStateException "dropFirst of empty chunk")))
        (ArrayChunk array (inc off) end))

    (reduce [self f start]
        (loop [ret (f start (py.bytecode/BINARY_SUBSCR array off))
               x (inc off)]
             (if (py.bytecode/COMPARE_OP "<" x end)
             (recur (f ret (py.bytecode/BINARY_SUBSCR array x)) 
                (inc x))
             ret))))


(deftype ChunkBuffer [buffer end]
    (add [self o]
        (py.bytecode/STORE_SUBSCR o buffer end)
        (py/setattr self "end" (inc end)))
    (chunk [self]
        (let [ret (ArrayChunk buffer 0 end)]
             (py/setattr self "buffer" nil)
             ret))
    (__len__ [self] end))

(deftype ChunkedCons [_meta chunk _more]

	clojure.lang.aseq/ASeq

	(first [self]
	       (py.bytecode/BINARY_SUBSCR chunk 0))
	(withMeta [self meta]
	  (if (py.bytecode/COMPARE_OP "is not" meta _meta)
	        (ChunkedCons meta chunk _more)
		self))
	
	(meta [self] _meta)

	(next [self]
	  (if (py.bytecode/COMPARE_OP ">" (py/len chunk) 1)
	      (ChunkedCons nil (.dropFirst chunk) _more)
	      (.chunkedNext self)))

	(more [self]
	  (cond (py.bytecode/COMPARE_OP ">" (py/len chunk) 1)
		        (ChunkedCons nil (.dropFirst chunk) _more)
		    (py.bytecode/COMPARE_OP "is" _more nil)
		        '()
		    :else
		        _more))

	IChunkedSeq
	(chunkedFirst [self] chunk)

	(chunkedNext [self]
	  (.seq (.chunkedMore self)))

	(chunkedMore [self]
	  (if (is? _more nil)
	        '()
		_more)))




(defn chunk-buffer [capacity]
     (ChunkBuffer (py.bytecode/BINARY_MULTIPLY (py/list [nil]) capacity)
		  0))
(defn chunk-append [b x]
     (.add b x))

(defn chunk [b]
     (.chunk b))

(defn chunk-first [s]
     (.chunkedFirst s))

(defn chunk-rest [s]
     (.chunkedMore s))

(defn chunk-next [s]
     (.chunkedNext s))

(defn chunk-cons [chunk rest]
     (if (= (py/len chunk) 0)
	 rest
	 (ChunkedCons nil chunk rest)))

(defn chunked-seq? [s]
     (instance? IChunkedSeq s))


(defn concat
  "Returns a lazy seq representing the concatenation of the elements in the supplied colls."
  {:added "1.0"}
  ([] (lazy-seq nil))
  ([x] (lazy-seq x))
  ([x y]
    (lazy-seq
      (let [s (seq x)]
        (if s
          (if (chunked-seq? s)
            (chunk-cons (chunk-first s) (concat (chunk-rest s) y))
            (cons (first s) (concat (rest s) y)))
          y))))
  ([x y & zs]
     (let [cat (fn cat [xys zs]
                 (lazy-seq
                   (let [xys (seq xys)]
                     (if xys
                       (if (chunked-seq? xys)
                         (chunk-cons (chunk-first xys)
                                     (cat (chunk-rest xys) zs))
                         (cons (first xys) (cat (rest xys) zs)))
                       (when zs
                         (cat (first zs) (next zs)))))))]
       (cat (concat x y) zs))))


(defmacro if-not
  "Evaluates test. If logical false, evaluates and returns then expr, 
  otherwise else expr, if supplied, else nil."
  {:added "1.0"}
  ([test then] `(if-not ~test ~then nil))
  ([test then else]
   `(if (not ~test) ~then ~else)))

(defmacro and
  "Evaluates exprs one at a time, from left to right. If a form
  returns logical false (nil or false), and returns that value and
  doesn't evaluate any of the other expressions, otherwise it returns
  the value of the last expr. (and) returns true."
  {:added "1.0"}
  ([] true)
  ([x] x)
  ([x & next]
   `(let [and# ~x]
      (if and# (and ~@next) and#))))



(defn identical?
  "Tests if 2 arguments are the same object"
  {:added "1.0"}
  ([x y] (py.bytecode/COMPARE_OP "is" x y)))

(defn compare
  "Comparator. Returns a negative number, zero, or a positive number
  when x is logically 'less than', 'equal to', or 'greater than'
  y. Same as Java x.compareTo(y) except it also works for nil, and
  compares numbers and collections in a type-independent manner. x
  must implement Comparable"
  {:added "1.0"}
  [x y] (py/cmp x y))

(defmacro or
  "Evaluates exprs one at a time, from left to right. If a form
  returns a logical true value, or returns that value and doesn't
  evaluate any of the other expressions, otherwise it returns the
  value of the last expression. (or) returns nil."
  {:added "1.0"}
  ([] nil)
  ([x] x)
  ([x & next]
      `(let [or# ~x]
         (if or# or# (or ~@next)))))

(defn zero?
  "Returns true if num is zero, else false"
  {:added "1.0"}
  [x] (py.bytecode/COMPARE_OP "==" x 0))

(defn count
  "Returns the number of items in the collection. (count nil) returns
  0.  Also works on strings, arrays, and Java Collections and Maps"
  {:added "1.0"}
  [coll] (py/len coll))


(defn int
  "Coerce to int"
  {:added "1.0"}
  [x] (py/int x))

(defn <
  "Returns non-nil if nums are in monotonically increasing order,
  otherwise false."
  {:added "1.0"
   :static true}
  ([x] true)
  ([x y] (py.bytecode/COMPARE_OP "<" x y))
  ([x y & more]
   (if (< x y)
     (if (next more)
       (recur y (first more) (next more))
       (< y (first more)))
     false)))

;; reduce is defined again later after InternalReduce loads
(defn reduce1
       ([f coll]
             (let [s (seq coll)]
               (if s
         (reduce1 f (first s) (next s))
                 (f))))
       ([f val coll]
          (let [s (seq coll)]
            (if s
              (if (chunked-seq? s)
                (recur f 
                       (.reduce (chunk-first s) f val)
                       (chunk-next s))
                (recur f (f val (first s)) (next s)))
         val))))

(defn reverse
  "Returns a seq of the items in coll in reverse order. Not lazy."
  {:added "1.0"}
  [coll]
    (reduce1 conj () coll))

(defn >1? [n] (py.bytecode/COMPARE_OP ">" n 1))
(defn >0? [n] (py.bytecode/COMPARE_OP ">" n 0))

(defn +
  "Returns the sum of nums. (+) returns 0. Does not auto-promote
  longs, will throw on overflow. See also: +'"
  {:added "1.2"}
  ([] 0)
  ([x] x)
  ([x y] (py.bytecode/BINARY_ADD x y))
  ([x y & more]
     (reduce1 + (+ x y) more)))

(defn *
  "Returns the product of nums. (*) returns 1. Does not auto-promote
  longs, will throw on overflow. See also: *'"
  {:added "1.2"}
  ([] 1)
  ([x] x)
  ([x y] (py.bytecode/BINARY_MULTIPLY x y))
  ([x y & more]
     (reduce1 * (* x y) more)))

(defn /
  "If no denominators are supplied, returns 1/numerator,
  else returns numerator divided by all of the denominators."
  {:added "1.0"}
  ([x] (/ 1 x))
  ([x y] (py.bytecode/BINARY_DIVIDE x y))
  ([x y & more]
   (reduce1 / (/ x y) more)))

(defn -
  "If no ys are supplied, returns the negation of x, else subtracts
  the ys from x and returns the result. Does not auto-promote
  longs, will throw on overflow. See also: -'"
  {:added "1.2"}
  ([x] (py.bytecode/UNARY_NEGATIVE x))
  ([x y] (py.bytecode/BINARY_SUBTRACT x y))
  ([x y & more]
     (reduce1 - (- x y) more)))

(defn <=
  "Returns non-nil if nums are in monotonically non-decreasing order,
  otherwise false."
  {:added "1.0"}
  ([x] true)
  ([x y] (py.bytecode/COMPARE_OP "<=" x y))
  ([x y & more]
   (if (<= x y)
     (if (next more)
       (recur y (first more) (next more))
       (<= y (first more)))
     false)))

(defn >
  "Returns non-nil if nums are in monotonically decreasing order,
  otherwise false."
  {:added "1.0"}
  ([x] true)
  ([x y] (py.bytecode/COMPARE_OP ">" x y))
  ([x y & more]
   (if (> x y)
     (if (next more)
       (recur y (first more) (next more))
       (> y (first more)))
     false)))

(defn >=
  "Returns non-nil if nums are in monotonically non-increasing order,
  otherwise false."
  {:added "1.0"}
  ([x] true)
  ([x y] (py.bytecode/COMPARE_OP ">=" x y))
  ([x y & more]
   (if (>= x y)
     (if (next more)
       (recur y (first more) (next more))
       (>= y (first more)))
     false)))

(defn ==
  "Returns non-nil if nums all have the equivalent
  value (type-independent), otherwise false"
  {:added "1.0"}
  ([x] true)
  ([x y] (py.bytecode/COMPARE_OP "==" x y))
  ([x y & more]
   (if (== x y)
     (if (next more)
       (recur y (first more) (next more))
       (== y (first more)))
     false)))

(defn dec
  "Returns a number one less than num. Does not auto-promote
  longs, will throw on overflow. See also: dec'"
  {:added "1.2"}
  [x] (py.bytecode/BINARY_SUBTRACT x 1))

(defn max
  "Returns the greatest of the nums."
  {:added "1.0"}
  ([x] x)
  ([x y] (py/max x y))
  ([x y & more]
   (reduce1 max (max x y) more)))

(defn min
  "Returns the least of the nums."
  {:added "1.0"}
  ([x] x)
  ([x y] (py/min x y))
  ([x y & more]
   (reduce1 min (min x y) more)))

(defn pos?
  "Returns true if num is greater than zero, else false"
  {:added "1.0"}
  [x] (> x 0))

(defn neg?
  "Returns true if num is less than zero, else false"
  {:added "1.0"}
  [x] (< x 0))

(defn quot
  "quot[ient] of dividing numerator by denominator."
  {:added "1.0"}
  [num div]
    (let [q (py.bytecode/BINARY_FLOOR_DIVIDE num div)]
      (if (>= q 0)
        q
        (if (= 0 (py.bytecode/BINARY_MODULO num div))
          q
          (inc q)))))        

(defn mod
  "modulus of dividing numerator by denominator. Truncates toward negative infinity."
  {:added "1.0"}
  [num div]
    (py.bytecode/BINARY_MODULO num div))

(defn rem
  "remainder of dividing numerator by denominator."
  {:added "1.0"
   :static true}
  [num div] 
  (let [m (quot num div)]   
      (- num (* m div))))

(defn bit-not
  "Bitwise complement"
  {:added "1.0"}
  [x] (py.bytecode/UNARY_INVERT x))

(defn bit-and
  "Bitwise and"
   {:added "1.0"}
   ([x y] (py.bytecode/BINARY_AND x y))
   ([x y & more]
      (reduce1 bit-and (bit-and x y) more)))

(defn bit-or
  "Bitwise or"
  {:added "1.0"}
  ([x y] (py.bytecode/BINARY_OR x y))
  ([x y & more]
    (reduce1 bit-or (bit-or x y) more)))

(defn bit-xor
  "Bitwise exclusive or"
  {:added "1.0"}
  ([x y] (py.bytecode/BINARY_XOR x y))
  ([x y & more]
    (reduce1 bit-xor (bit-xor x y) more)))

(defn bit-and-not
  "Bitwise and with complement"
  {:added "1.0"}
  ([x y] (py.bytecode/BINARY_AND  x (py.bytecode/UNARY_NOT y)))
  ([x y & more]
    (reduce1 bit-and-not (bit-and-not x y) more)))

(defn bit-shift-left
  "Bitwise shift left"
  {:added "1.0"}
  [x n] (py.bytecode/BINARY_LSHIFT x n))

(defn bit-shift-right
  "Bitwise shift right"
  {:added "1.0"}
  [x n] (py.bytecode/BINARY_RSHIFT x n))

(defn bit-clear
  "Clear bit at index n"
  {:added "1.0"}
  [x n] (bit-and x (bit-not (bit-shift-left 1 n))))

(defn bit-set
  "Set bit at index n"
  {:added "1.0"}
  [x n] (bit-or x (bit-shift-left 1 n)))

(defn bit-flip
  "Flip bit at index n"
  {:added "1.0"}
  [x n] (bit-xor x (bit-shift-left 1 n)))

(defn bit-test
  "Test bit at index n"
  {:added "1.0"}
  [x n] (py.bytecode/COMPARE_OP "==" (bit-and (bit-shift-right x n) 1) 1))

(defn integer?
  "Returns true if n is an integer"
  {:added "1.0"}
  [n]
  (or (instance? py/int n)))

(defn even?
  "Returns true if n is even, throws an exception if n is not an integer"
  {:added "1.0"}
   [n] (if (integer? n)
        (zero? (bit-and n 1))
        (throw (py/TypeError (str "Argument must be an integer: " n)))))

(defn odd?
  "Returns true if n is odd, throws an exception if n is not an integer"
  {:added "1.0"}
  [n] (not (even? n)))

(defn complement
  "Takes a fn f and returns a fn that takes the same arguments as f,
  has the same effects, if any, and returns the opposite truth value."
  {:added "1.0"}
  [f] 
  (fn 
    ([] (not (f)))
    ([x] (not (f x)))
    ([x y] (not (f x y)))
    ([x y & zs] (not (apply f x y zs)))))

(defn constantly
  "Returns a function that takes any number of arguments and returns x."
  {:added "1.0"}
  [x] (fn [& args] x))

(defn identity
  "Returns its argument."
  {:added "1.0"}
  [x] x)

;; list stuff
(defn peek
  "For a list or queue, same as first, for a vector, same as, but much
  more efficient than, last. If the collection is empty, returns nil."
  {:added "1.0"}
  [coll] (if (nil? coll) 
             nil
             (.peek coll)))

(defn pop
  "For a list or queue, returns a new list/queue without the first
  item, for a vector, returns a new vector without the last item. If
  the collection is empty, throws an exception.  Note - not the same
  as next/butlast."
  {:added "1.0"}
  [coll] (if (nil? coll)
             nil
             (.pop coll)))

;; map stuff

(defn coll?
  "Returns true if x implements IPersistentCollection"
  {:added "1.0"}
  [x] (instance? clojure.lang.ipersistentcollection/IPersistentCollection x))

(defn list?
  "Returns true if x implements IPersistentList"
  {:added "1.0"}
  [x] (instance? clojure.lang.ipersistentlist/IPersistentList
 x))

(defn set?
  "Returns true if x implements IPersistentSet"
  {:added "1.0"}
  [x] (instance? clojure.lang.ipersistentset/IPersistentSet x))

(defn pylist?
  "Returns true if coll is a native python list"
  [coll] (instance? py/list coll))

(defn tuple?
  "Returns true if toll is a native python tuple"
  [coll] (instance? py/tuple coll))

(defn contains?
  "Returns true if key is present in the given collection, otherwise
  returns false.  Note that for numerically indexed collections like
  vectors and Java arrays, this tests if the numeric key is within the
  range of indexes. 'contains?' operates constant or logarithmic time;
  it will not perform a linear search for a value.  See also 'some'."
  {:added "1.0"}
  [coll key] (cond (or (vector? coll)
                        (list? coll)
                        (tuple? coll)
                        (pylist? coll)
                        (string? coll)
                        (and (not (py/hasattr coll "__contains__"))
                             (py/hasattr coll "__len__")))
                     (and (>= key 0) (< key (count coll)))
                    (nil? coll) false
                    :else
                     (py.bytecode/COMPARE_OP "in" key coll)))    


(defn get
  "Returns the value mapped to key, not-found or nil if key not present."
  {:added "1.0"}
  ([map key]
   (get map key nil))
  ([map key not-found]
   (cond (instance? clojure.lang.ilookup/ILookup map)
           (.valAt map key not-found)
         
         :else
           (if (contains? map key)
               (py.bytecode/BINARY_SUBSCR map key)
               not-found))))

(defn dissoc
  "dissoc[iate]. Returns a new map of the same (hashed/sorted) type,
  that does not contain a mapping for key(s)."
  {:added "1.0"
   :static true}
  ([map] map)
  ([map key]
   (if (nil? map) nil (.without map key)))
  ([map key & ks]
   (let [ret (dissoc map key)]
     (if ks
       (recur ret (first ks) (next ks))
       ret))))

(defn disj
  "disj[oin]. Returns a new set of the same (hashed/sorted) type, that
  does not contain key(s)."
  {:added "1.0"
   :static true}
  ([set] set)
  ([set key]
   (when set
     (. set (disjoin key))))
  ([set key & ks]
   (when set
     (let [ret (disj set key)]
       (if ks
         (recur ret (first ks) (next ks))
         ret)))))

(defn find
  "Returns the map entry for key, or nil if key not present."
  {:added "1.0"}
  [map key] 
    (cond (nil? map)
           nil
          (instance? clojure.lang.associative/Associative map)
           (.entryAt map key)
          :else
           (if (contains? map key)
               (clojure.lang.mapentry/MapEntry key (get map key))
               nil)))

(defn select-keys
  "Returns a map containing only those entries in map whose key is in keys"
  {:added "1.0"}
  [map keyseq]
    (loop [ret {} keys (seq keyseq)]
      (if keys
        (let [entry (find map (first keys))]
          (recur
           (if entry
             (conj ret entry)
             ret)
           (next keys)))
        ret)))

(defn keys
  "Returns a sequence of the map's keys."
  {:added "1.0"
   :static true}
  [map] 
    (if (map? map)
         (clojure.lang.apersistentmap/createKeySeq (seq map))
         (seq (.keys map))))

(defn vals 
  "Returns a sequence of the map's keys."
  {:added "1.0"
   :static true}
  [map] 
    (if (map? map)
         (clojure.lang.apersistentmap/createValueSeq (seq map))
         (seq (.items map))))

(defn key
  "Returns the key of the map entry."
  {:added "1.0"
   :static true}
  [e]
    (.getKey e))

(defn val
  "Returns the value in the map entry."
  {:added "1.0"
   :static true}
  [e]
    (. e (getValue)))

(defn unicode?
  "Returns true if x is unicode"
  [x]
  (instance? py/unicode x))

(def 
  ^{:doc "Returns the name String of a string, symbol or keyword."
    :static true}
  name
  clojure.lang.rt/name)

(def 
  ^{:doc "Returns the namespace String of a symbol or keyword, or nil if not present."
   :added "1.0"
   :static true}
  namespace
  clojure.lang.rt/namespace)

(defmacro ..
  "form => fieldName-symbol or (instanceMethodName-symbol args*)

  Expands into a member access (.) of the first member on the first
  argument, followed by the next member on the result, etc. For
  instance:

  (.. System (getProperties) (get \"os.name\"))

  expands to:

  (. (. System (getProperties)) (get \"os.name\"))

  but is easier to write, read, and understand."
  {:added "1.0"}
  ([x form] `(. ~x ~form))
  ([x form & more] `(.. (. ~x ~form) ~@more)))

(defmacro ->
  "Threads the expr through the forms. Inserts x as the
  second item in the first form, making a list of it if it is not a
  list already. If there are more forms, inserts the first form as the
  second item in second form, etc."
  {:added "1.0"}
  ([x] x)
  ([x form] (if (seq? form)
              (with-meta `(~(first form) ~x ~@(next form)) (meta form))
              (list form x)))
  ([x form & more] `(-> (-> ~x ~form) ~@more)))

(defmacro ->>
  "Threads the expr through the forms. Inserts x as the
  last item in the first form, making a list of it if it is not a
  list already. If there are more forms, inserts the first form as the
  last item in second form, etc."
  {:added "1.1"} 
  ([x form] (if (seq? form)
              (with-meta `(~(first form) ~@(next form)  ~x) (meta form))
              (list form x)))
  ([x form & more] `(->> (->> ~x ~form) ~@more)))


;;; var stuff


(defmacro assert-args
  [& pairs]
  `(do (when-not ~(first pairs)
         (throw (py/Exception
                  (str (first ~'&form) " requires " ~(second pairs) " in " ~'__name__ ":" (:line (meta ~'&form))))))
     ~(let [more (nnext pairs)]
        (when more
          (list* `assert-args more)))))


(defmacro if-let
  "bindings => binding-form test

  If test is true, evaluates then with binding-form bound to the value of 
  test, if not, yields else"
  {:added "1.0"}
  ([bindings then]
   `(if-let ~bindings ~then nil))
  ([bindings then else & oldform]
   (assert-args
     (and (vector? bindings) (nil? oldform)) "a vector for its binding"
     (= 2 (count bindings)) "exactly 2 forms in binding vector")
   (let [form (bindings 0) tst (bindings 1)]
     `(let [temp# ~tst]
        (if temp#
          (let [~form temp#]
            ~then)
          ~else)))))

(defmacro when-let
  "bindings => binding-form test

  When test is true, evaluates body with binding-form bound to the value of test"
  {:added "1.0"}
  [bindings & body]
  (assert-args
     (vector? bindings) "a vector for its binding"
     (= 2 (count bindings)) "exactly 2 forms in binding vector")
   (let [form (bindings 0) tst (bindings 1)]
    `(let [temp# ~tst]
       (when temp#
         (let [~form temp#]
           ~@body)))))

;;; functional stuff

(defn comp
  "Takes a set of functions and returns a fn that is the composition
  of those fns.  The returned fn takes a variable number of args,
  applies the rightmost of fns to the args, the next
  fn (right-to-left) to the result, etc."
  {:added "1.0"}
  ([] identity)
  ([f] f)
  ([f g] 
     (fn 
       ([] (f (g)))
       ([x] (f (g x)))
       ([x y] (f (g x y)))
       ([x y z] (f (g x y z)))
       ([x y z & args] (f (apply g x y z args)))))
  ([f g h] 
     (fn 
       ([] (f (g (h))))
       ([x] (f (g (h x))))
       ([x y] (f (g (h x y))))
       ([x y z] (f (g (h x y z))))
       ([x y z & args] (f (g (apply h x y z args))))))
  ([f1 f2 f3 & fs]
    (let [fs (reverse (list* f1 f2 f3 fs))]
      (fn [& args]
        (loop [ret (apply (first fs) args) fs (next fs)]
          (if fs
            (recur ((first fs) ret) (next fs))
            ret))))))


(defn juxt 
  "Takes a set of functions and returns a fn that is the juxtaposition
  of those fns.  The returned fn takes a variable number of args, and
  returns a vector containing the result of applying each fn to the
  args (left-to-right).
  ((juxt a b c) x) => [(a x) (b x) (c x)]"
  {:added "1.1"}
  ([f] 
     (fn
       ([] [(f)])
       ([x] [(f x)])
       ([x y] [(f x y)])
       ([x y z] [(f x y z)])
       ([x y z & args] [(apply f x y z args)])))
  ([f g] 
     (fn
       ([] [(f) (g)])
       ([x] [(f x) (g x)])
       ([x y] [(f x y) (g x y)])
       ([x y z] [(f x y z) (g x y z)])
       ([x y z & args] [(apply f x y z args) (apply g x y z args)])))
  ([f g h] 
     (fn
       ([] [(f) (g) (h)])
       ([x] [(f x) (g x) (h x)])
       ([x y] [(f x y) (g x y) (h x y)])
       ([x y z] [(f x y z) (g x y z) (h x y z)])
       ([x y z & args] [(apply f x y z args) (apply g x y z args) (apply h x y z args)])))
  ([f g h & fs]
     (let [fs (list* f g h fs)]
       (fn
         ([] (reduce1 #(conj %1 (%2)) [] fs))
         ([x] (reduce1 #(conj %1 (%2 x)) [] fs))
         ([x y] (reduce1 #(conj %1 (%2 x y)) [] fs))
         ([x y z] (reduce1 #(conj %1 (%2 x y z)) [] fs))
         ([x y z & args] (reduce1 #(conj %1 (apply %2 x y z args)) [] fs))))))


(defn partial
  "Takes a function f and fewer than the normal arguments to f, and
  returns a fn that takes a variable number of additional args. When
  called, the returned function calls f with args + additional args."
  {:added "1.0"}
  ([f arg1]
   (fn [& args] (apply f arg1 args)))
  ([f arg1 arg2]
   (fn [& args] (apply f arg1 arg2 args)))
  ([f arg1 arg2 arg3]
   (fn [& args] (apply f arg1 arg2 arg3 args)))
  ([f arg1 arg2 arg3 & more]
   (fn [& args] (apply f arg1 arg2 arg3 (concat more args)))))


;;;;;;;;;;;;;;;;;;; sequence fns  ;;;;;;;;;;;;;;;;;;;;;;;
(defn sequence
  "Coerces coll to a (possibly empty) sequence, if it is not already
  one. Will not force a lazy seq. (sequence nil) yields ()"
  {:added "1.0"}
  [coll]
   (if (seq? coll) coll
    (or (seq coll) ())))

(defn every?
  "Returns true if (pred x) is logical true for every x in coll, else
  false."
  {:added "1.0"}
  [pred coll]
  (cond
   (nil? (seq coll)) true
   (pred (first coll)) (recur pred (next coll))
   :else false))

(def
 ^{:doc "Returns false if (pred x) is logical true for every x in
  coll, else true."
   :added "1.0"}
 not-every? (comp not every?))


(defn some
  "Returns the first logical true value of (pred x) for any x in coll,
  else nil.  One common idiom is to use a set as pred, for example
  this will return :fred if :fred is in the sequence, otherwise nil:
  (some #{:fred} coll)"
  {:added "1.0"}
  [pred coll]
    (when (seq coll)
      (or (pred (first coll)) (recur pred (next coll)))))

(def
 ^{:doc "Returns false if (pred x) is logical true for any x in coll,
  else true."
   :added "1.0"}
 not-any? (comp not some))

;will be redefed later with arg checks
(defmacro dotimes
  "bindings => name n

  Repeatedly executes body (presumably for side-effects) with name
  bound to integers from 0 through n-1."
  {:added "1.0"}
  [bindings & body]
  (let [i (first bindings)
        n (second bindings)]
    `(let [n# ~n]
       (loop [~i 0]
         (when (< ~i n#)
           ~@body
           (recur (inc ~i)))))))



(defn map
  "Returns a lazy sequence consisting of the result of applying f to the
  set of first items of each coll, followed by applying f to the set
  of second items in each coll, until any one of the colls is
  exhausted.  Any remaining items in other colls are ignored. Function
  f should accept number-of-colls arguments."
  {:added "1.0"
   :static true}
  ([f coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (if (chunked-seq? s)
        (let [c (chunk-first s)
              size (int (count c))
              b (chunk-buffer size)]
          (dotimes [i size]
              (chunk-append b (f (.__getitem__ c i))))
          (chunk-cons (chunk b) (map f (chunk-rest s))))
        (cons (f (first s)) (map f (rest s)))))))
  ([f c1 c2]
   (lazy-seq
    (let [s1 (seq c1) s2 (seq c2)]
      (when (and s1 s2)
        (cons (f (first s1) (first s2))
              (map f (rest s1) (rest s2)))))))
  ([f c1 c2 c3]
   (lazy-seq
    (let [s1 (seq c1) s2 (seq c2) s3 (seq c3)]
      (when (and  s1 s2 s3)
        (cons (f (first s1) (first s2) (first s3))
              (map f (rest s1) (rest s2) (rest s3)))))))
  ([f c1 c2 c3 & colls]
   (let [step (fn step [cs]
                 (lazy-seq
                  (let [ss (map seq cs)]
                    (when (every? identity ss)
                      (cons (map first ss) (step (map rest ss)))))))]
     (map #(apply f %) (step (conj colls c3 c2 c1))))))


(defn mapcat
  "Returns the result of applying concat to the result of applying map
  to f and colls.  Thus function f should return a collection."
  {:added "1.0"
   :static true}
  [f & colls]
    (apply concat (apply map f colls)))

(defn filter
  "Returns a lazy sequence of the items in coll for which
  (pred item) returns true. pred must be free of side-effects."
  {:added "1.0"
   :static true}
  ([pred coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (if (chunked-seq? s)
        (let [c (chunk-first s)
              size (count c)
              b (chunk-buffer size)]
          (dotimes [i size]
              (when (pred (.nth c i))
                (chunk-append b (.nth c i))))
          (chunk-cons (chunk b) (filter pred (chunk-rest s))))
        (let [f (first s) r (rest s)]
          (if (pred f)
            (cons f (filter pred r))
            (filter pred r))))))))


(defn remove
  "Returns a lazy sequence of the items in coll for which
  (pred item) returns false. pred must be free of side-effects."
  {:added "1.0"
   :static true}
  [pred coll]
  (filter (complement pred) coll))


(defn take
  "Returns a lazy sequence of the first n items in coll, or all items if
  there are fewer than n."
  {:added "1.0"}
  [n coll]
  (lazy-seq
   (when (pos? n) 
     (when-let [s (seq coll)]
      (cons (first s) (take (dec n) (rest s)))))))

(defn take-while
  "Returns a lazy sequence of successive items from coll while
  (pred item) returns true. pred must be free of side-effects."
  {:added "1.0"}
  [pred coll]
  (lazy-seq
   (when-let [s (seq coll)]
       (when (pred (first s))
         (cons (first s) (take-while pred (rest s)))))))

(defn drop
  "Returns a lazy sequence of all but the first n items in coll."
  {:added "1.0"}
  [n coll]
  (let [step (fn [n coll]
               (let [s (seq coll)]
                 (if (and (pos? n) s)
                   (recur (dec n) (rest s))
                   s)))]
    (lazy-seq (step n coll))))

(defn drop-last
  "Return a lazy sequence of all but the last n (default 1) items in coll"
  {:added "1.0"}
  ([s] (drop-last 1 s))
  ([n s] (map (fn [x _] x) s (drop n s))))



(defn take-last
  "Returns a seq of the last n items in coll.  Depending on the type
  of coll may be no better than linear time.  For vectors, see also subvec."
  {:added "1.1"}
  [n coll]
  (loop [s (seq coll), lead (seq (drop n coll))]
    (if lead
      (recur (next s) (next lead))
      s)))

(defn drop-while
  "Returns a lazy sequence of the items in coll starting from the first
  item for which (pred item) returns nil."
  {:added "1.0"}
  [pred coll]
  (let [step (fn [pred coll]
               (let [s (seq coll)]
                 (if (and s (pred (first s)))
                   (recur pred (rest s))
                   s)))]
    (lazy-seq (step pred coll))))

(defn cycle
  "Returns a lazy (infinite!) sequence of repetitions of the items in coll."
  {:added "1.0"}
  [coll] (lazy-seq 
          (when-let [s (seq coll)] 
              (concat s (cycle s)))))

(defn split-at
  "Returns a vector of [(take n coll) (drop n coll)]"
  {:added "1.0"}
  [n coll]
    [(take n coll) (drop n coll)])

(defn split-with
  "Returns a vector of [(take-while pred coll) (drop-while pred coll)]"
  {:added "1.0"}
  [pred coll]
    [(take-while pred coll) (drop-while pred coll)])

(defn repeat
  "Returns a lazy (infinite!, or length n if supplied) sequence of xs."
  {:added "1.0"}
  ([x] (lazy-seq (cons x (repeat x))))
  ([n x] (take n (repeat x))))



(defn iterate
  "Returns a lazy sequence of x, (f x), (f (f x)) etc. f must be free of side-effects"
  {:added "1.0"}
  [f x] (cons x (lazy-seq (iterate f (f x)))))

(defn range 
  "Returns a lazy seq of nums from start (inclusive) to end
  (exclusive), by step, where start defaults to 0, step to 1, and end
  to infinity."
  {:added "1.0"}
  ([] (range 0 (py/float "inf")))
  ([end] (range 0 end 1))
  ([start end] (range start end 1))
  ([start end step]
   (lazy-seq
    (let [b (chunk-buffer 32)
          comp (if (pos? step) < >)]
      (loop [i start]
        (if (and (< (count b) 32)
                 (comp i end))
          (do
            (chunk-append b i)
            (recur (+ i step)))
          (chunk-cons (chunk b) 
                      (when (comp i end) 
                        (range i end step)))))))))

(defn merge
  "Returns a map that consists of the rest of the maps conj-ed onto
  the first.  If a key occurs in more than one map, the mapping from
  the latter (left-to-right) will be the mapping in the result."
  {:added "1.0"}
  [& maps]
  (when (some identity maps)
    (reduce1 #(conj (or %1 {}) %2) maps)))


(defn merge-with
  "Returns a map that consists of the rest of the maps conj-ed onto
  the first.  If a key occurs in more than one map, the mapping(s)
  from the latter (left-to-right) will be combined with the mapping in
  the result by calling (f val-in-result val-in-latter)."
  {:added "1.0"}
  [f & maps]
  (when (some identity maps)
    (let [merge-entry (fn [m e]
			(let [k (key e) v (val e)]
			  (if (contains? m k)
			    (assoc m k (f (get m k) v))
			    (assoc m k v))))
          merge2 (fn [m1 m2]
		   (reduce1 merge-entry (or m1 {}) (seq m2)))]
      (reduce1 merge2 maps))))


(defn zipmap
  "Returns a map with the keys mapped to the corresponding vals."
  {:added "1.0"}
  [keys vals]
    (loop [map {}
           ks (seq keys)
           vs (seq vals)]
      (if (and ks vs)
        (recur (assoc map (first ks) (first vs))
               (next ks)
               (next vs))
        map)))


(defn line-seq
  "Returns the lines of text from rdr as a lazy sequence of strings.
  rdr must implement .readline"
  {:added "1.0"}
  [rdr]
  (let [line (.readline rdr)]
    (when-not (= line "")
              (cons line (lazy-seq (line-seq rdr))))))

(defn comparator
  "Returns an implementation of java.util.Comparator based upon pred."
  {:added "1.0"
   :static true}
  [pred]
    (fn [x y]
      (cond (pred x y) -1 (pred y x) 1 :else 0)))

(defn wrap-fn-for-compare
  [f]
    (fn [x y]
        (let [ret (f x y)]
             (if (instance? py/bool ret)
                 (if ret -1 1)
                 ret))))

(defn sort
  "Returns a sorted sequence of the items in coll. If no comparator is
  supplied, uses compare. comparator must
  implement java.util.Comparator."
  {:added "1.0"}
  ([coll]
   (sort compare coll))
  ([comp coll]
   (if (seq coll)
       (seq (py/sorted coll (wrap-fn-for-compare comp)))
       ())))

(defn sort-by
  "Returns a sorted sequence of the items in coll, where the sort
  order is determined by comparing (keyfn item).  If no comparator is
  supplied, uses compare. comparator must
  implement java.util.Comparator."
  {:added "1.0"}
  ([keyfn coll]
   (sort-by keyfn compare coll))
  ([keyfn comp coll]
   (seq (py/sorted coll (wrap-fn-for-compare comp) keyfn))))

(defn dorun
  "When lazy sequences are produced via functions that have side
  effects, any effects other than those needed to produce the first
  element in the seq do not occur until the seq is consumed. dorun can
  be used to force any effects. Walks through the successive nexts of
  the seq, does not retain the head and returns nil."
  {:added "1.0"}
  ([coll]
   (when (seq coll)
     (recur (next coll))))
  ([n coll]
   (when (and (seq coll) (pos? n))
     (recur (dec n) (next coll)))))



(defn nthnext
  "Returns the nth next of coll, (seq coll) when n is 0."
  {:added "1.0"}
  [coll n]
    (loop [n n xs (seq coll)]
      (if (and xs (pos? n))
        (recur (dec n) (next xs))
        xs)))

(defn nthrest
  "Returns the nth rest of coll, coll when n is 0."
  {:added "1.3"}
  [coll n]
    (loop [n n xs coll]
      (if (and (pos? n) (seq xs))
        (recur (dec n) (rest xs))
        xs)))

(defn doall
  "When lazy sequences are produced via functions that have side
  effects, any effects other than those needed to produce the first
  element in the seq do not occur until the seq is consumed. doall can
  be used to force any effects. Walks through the successive nexts of
  the seq, retains the head and returns it, thus causing the entire
  seq to reside in memory at one time."
  {:added "1.0"}
  ([coll]
   (dorun coll)
   coll)
  ([n coll]
   (dorun n coll)
   coll))


(defn partition
  "Returns a lazy sequence of lists of n items each, at offsets step
  apart. If step is not supplied, defaults to n, i.e. the partitions
  do not overlap. If a pad collection is supplied, use its elements as
  necessary to complete last partition upto n items. In case there are
  not enough padding elements, return a partition with less than n items."
  {:added "1.0"}
  ([n coll]
     (partition n n coll))
  ([n step coll]
     (lazy-seq
       (when-let [s (seq coll)]
         (let [p (doall (take n s))]
           (when (= n (count p))
             (cons p (partition n step (nthrest s step))))))))
  ([n step pad coll]
     (lazy-seq
       (when-let [s (seq coll)]
         (let [p (doall (take n s))]
           (if (= n (count p))
             (cons p (partition n step pad (nthrest s step)))
             (list (take n (concat p pad)))))))))

;;
(defn eval
  "Evaluates the form data structure (not text!) and returns the result."
  {:added "1.0"
   :static true}
  [form] (clojure.lang.compiler/evalForm form __name__))



(defmacro doseq
  "Repeatedly executes body (presumably for side-effects) with
  bindings and filtering as provided by \"for\".  Does not retain
  the head of the sequence. Returns nil."
  {:added "1.0"}
  [seq-exprs & body]
  (assert-args
     (vector? seq-exprs) "a vector for its binding"
     (even? (count seq-exprs)) "an even number of forms in binding vector")
  (let [step (fn step [recform exprs]
               (if-not exprs
                 [true `(do ~@body)]
                 (let [k (first exprs)
                       v (second exprs)]
                   (if (keyword? k)
                     (let [steppair (step recform (nnext exprs))
                           needrec (steppair 0)
                           subform (steppair 1)]
                       (cond
                         (= k :let) [needrec `(let ~v ~subform)]
                         (= k :while) [false `(when ~v
                                                ~subform
                                                ~@(when needrec [recform]))]
                         (= k :when) [false `(if ~v
                                               (do
                                                 ~subform
                                                 ~@(when needrec [recform]))
                                               ~recform)]))
                     (let [seq- (gensym "seq_")
                           chunk- (with-meta (gensym "chunk_")
                                             {:tag 'clojure.lang.IChunk})
                           count- (gensym "count_")
                           i- (gensym "i_")
                           recform `(recur (next ~seq-) nil 0 0)
                           steppair (step recform (nnext exprs))
                           needrec (steppair 0)
                           subform (steppair 1)
                           recform-chunk 
                             `(recur ~seq- ~chunk- ~count- (inc ~i-))
                           steppair-chunk (step recform-chunk (nnext exprs))
                           subform-chunk (steppair-chunk 1)]
                       [true
                        `(loop [~seq- (seq ~v), ~chunk- nil,
                                ~count- 0, ~i- 0]
                           (if (< ~i- ~count-)
                             (let [~k (.__getitem__ ~chunk- ~i-)]
                               ~subform-chunk
                               ~@(when needrec [recform-chunk]))
                             (when-let [~seq- (seq ~seq-)]
                               (if (chunked-seq? ~seq-)
                                 (let [c# (chunk-first ~seq-)]
                                   (recur (chunk-rest ~seq-) c#
                                          (int (count c#)) (int 0)))
                                 (let [~k (first ~seq-)]
                                   ~subform
                                   ~@(when needrec [recform]))))))])))))]
    (.__getitem__ (step nil (seq seq-exprs)) 1)))


(defmacro dotimes
  "bindings => name n

  Repeatedly executes body (presumably for side-effects) with name
  bound to integers from 0 through n-1."
  {:added "1.0"}
  [bindings & body]
  (assert-args
     (vector? bindings) "a vector for its binding"
     (= 2 (count bindings)) "exactly 2 forms in binding vector")
  (let [i (first bindings)
        n (second bindings)]
    `(let [n# ~n]
       (loop [~i 0]
         (when (< ~i n#)
           ~@body
           (recur (inc ~i)))))))

;redef into with batch support
(defn ^:private into1
  "Returns a new coll consisting of to-coll with all of the items of
  from-coll conjoined."
  {:added "1.0"
   :static true}
  [to from]
    (reduce1 conj to from))


(defmacro import*
  ([module syms] 
    (let [module (.-name module)
          copies (map #(list 'py.bytecode/STORE_GLOBAL
                             (name %)
                             (list 'py/getattr 'itms (name %)))
                        syms)
          symnames (list* (map name syms))]
                  
         `(let [~'itms (py/__import__ (name ~module)
						     (py/globals)
						     (py/locals)
					  	     (py/list (list ~@symnames))
						     -1)]
		       ~@copies ))))

(defmacro import 
  "import-list => (package-symbol class-name-symbols*)

  For each name in class-name-symbols, adds a mapping from name to the
  class named by package.name to the current namespace. Use :import in the ns
  macro in preference to calling this directly."
  {:added "1.0"}
  [& import-symbols-or-lists]
  (let [specs (map #(if (and (seq? %) (= 'quote (first %))) (second %) %) 
                   import-symbols-or-lists)]
   `(do ~@(map #(list 'clojure.core/import* (first %) (next %))
                specs))))

(defn class
  "Returns the Class of x"
  {:added "1.0"}
  [x] (py/type x))

(defn num
  "Coerce to Number"
  {:added "1.0"}
  [x] (py/float x))

(defn float?
  "Returns true if n is a floating point number"
  {:added "1.0"
   :static true}
  [n]
  (instance? py/float n))

(defn int?
  "Returns true if n is a floating point number"
  {:added "1.0"
   :static true}
  [n]
  (instance? py/int n))

(defn number?
  "Returns true if x is a Number"
  {:added "1.0"
   :static true}
  [x]
  (or (float? x) (int? x)))

(defn keyword
  "Returns a keyword for the given string"
  [x]
  (clojure.lang.cljkeyword/keyword (symbol x)))

(defn nth
  ([coll x]
      (if (py/hasattr coll "__getitem__")
          (py.bytecode/BINARY_SUBSCR coll x)
          (first (drop x coll))))
  ([coll x default]
      (if (contains? coll x)
          (nth coll x)
          default)))

(import '(clojure.lang.lispreader readString))

(defn read-string
  "Reads one object from the string s"
  {:added "1.0"}
  [s] (readString s))


(defn subvec
  "Returns a persistent vector of the items in vector from
  start (inclusive) to end (exclusive).  If end is not supplied,
  defaults to (count vector). This operation is O(1) and very fast, as
  the resulting vector shares structure with the original and no
  trimming is done."
  {:added "1.0"}
  ([v start]
   (subvec v start (count v)))
  ([v start end]
   (. clojure.lang.rt (subvec v start end))))

(defmacro doto
  "Evaluates x then calls all of the methods and functions with the
  value of x supplied at the front of the given arguments.  The forms
  are evaluated in order.  Returns x.

  (doto (new java.util.HashMap) (.put \"a\" 1) (.put \"b\" 2))"
  {:added "1.0"}
  [x & forms]
    (let [gx (gensym)]
      `(let [~gx ~x]
         ~@(map (fn [f]
                  (if (seq? f)
                    `(~(first f) ~gx ~@(next f))
                    `(~f ~gx)))
                forms)
         ~gx)))

(defmacro memfn
  "Expands into code that creates a fn that expects to be passed an
  object and any args and calls the named instance method on the
  object passing the args. Use when you want to treat a Java method as
  a first-class fn."
  {:added "1.0"}
  [name & args]
  `(fn [target# ~@args]
     (. target# (~name ~@args))))

;(import '(time time))
;(def pytime time)

;(defmacro time
;  "Evaluates expr and prints the time it took.  Returns the value of
; expr."
;  {:added "1.0"}
;  [expr]
;  `(let [start# (pytime)
;         ret# ~expr]
;     (py/print (str "Elapsed time: " (* (- (pytime) start#) 1000) " msecs"))
;     ret#))

(defn set
  "Returns a set of the distinct elements of coll."
  {:added "1.0"
   :static true}
  [coll] (clojure.lang.persistenthashset/create (seq coll)))

(defn find-ns
  "Returns the namespace named by the symbol or nil if it doesn't exist."
  {:added "1.0"}
  [sym] (clojure.lang.namespace/find sym))

(defn create-ns
  "Create a new namespace named by the symbol if one doesn't already
  exist, returns it or the already-existing namespace of the same
  name."
  {:added "1.0"}
  [sym] (clojure.lang.namespace/findOrCreate sym))

(defn remove-ns
  "Removes the namespace named by the symbol. Use with caution.
  Cannot be used to remove the clojure namespace."
  {:added "1.0"}
  [sym] (clojure.lang.namespace/remove sym))



(defn all-ns
  "Returns a sequence of all namespaces."
  {:added "1.0"
   :static true}
  [] (seq (py/tuple (.itervalues sys/modules))))

(defn the-ns
  "If passed a namespace, returns it. Else, when passed a symbol,
  returns the namespace named by it, throwing an exception if not
  found."
  {:added "1.0"}
  [x]
  (if (instance? new/module x)
    x
    (or (find-ns x) (throw (py/Exception (str "No namespace: " x " found"))))))

(defn ns-name
  "Returns the name of the namespace, a symbol."
  {:added "1.0"
   :static true}
  [ns]
  (symbol (.-__name__ (the-ns ns))))


(defn take-nth
  "Returns a lazy seq of every nth item in coll."
  {:added "1.0"
   :static true}
  [n coll]
    (lazy-seq
     (when-let [s (seq coll)]
       (cons (first s) (take-nth n (drop n s))))))

(defn interleave
  "Returns a lazy seq of the first item in each coll, then the second etc."
  {:added "1.0"
   :static true}
  ([c1 c2]
     (lazy-seq
      (let [s1 (seq c1) s2 (seq c2)]
        (when (and s1 s2)
          (cons (first s1) (cons (first s2) 
                                 (interleave (rest s1) (rest s2))))))))
  ([c1 c2 & colls] 
     (lazy-seq 
      (let [ss (map seq (conj colls c2 c1))]
        (when (every? identity ss)
          (concat (map first ss) (apply interleave (map rest ss))))))))



;redefine let and loop  with destructuring
(defn destructure [bindings]
  (let [bents (partition 2 bindings)
        pb (fn pb [bvec b v]
               (let [pvec
                     (fn [bvec b val]
                       (let [gvec (gensym "vec__")]
                         (loop [ret (-> bvec (conj gvec) (conj val))
                                n 0
                                bs b
                                seen-rest? false]
                           (if (seq bs)
                             (let [firstb (first bs)]
                               (cond
                                (= firstb '&) (recur (pb ret (second bs) (list `nthnext gvec n))
                                                     n
                                                     (nnext bs)
                                                     true)
                                (= firstb :as) (pb ret (second bs) gvec)
                                :else (if seen-rest?
                                        (throw (py/Exception "Unsupported binding form, only :as can follow & parameter"))
                                        (recur (pb ret firstb  (list `nth gvec n nil))
                                               (inc n)
                                               (next bs)
                                               seen-rest?))))
                             ret))))
                     pmap
                     (fn [bvec b v]
                       (let [gmap (or (:as b) (gensym "map__"))
                             defaults (:or b)]
                         (loop [ret (-> bvec (conj gmap) (conj v)
                                        (conj gmap) (conj `(if (seq? ~gmap) (apply hash-map ~gmap) ~gmap)))
                                bes (reduce1
                                     (fn [bes entry]
                                       (reduce1 #(assoc %1 %2 ((val entry) %2))
                                               (dissoc bes (key entry))
                                               ((key entry) bes)))
                                     (dissoc b :as :or)
                                     {:keys #(keyword (str %)), :strs str, :syms #(list `quote %)})]
                           (if (seq bes)
                             (let [bb (key (first bes))
                                   bk (val (first bes))
                                   has-default (contains? defaults bb)]
                               (recur (pb ret bb (if has-default
                                                   (list `get gmap bk (defaults bb))
                                                   (list `get gmap bk)))
                                      (next bes)))
                             ret))))]
                 (cond
                  (symbol? b) (-> bvec (conj b) (conj v))
                  (vector? b) (pvec bvec b v)
                  (map? b) (pmap bvec b v)
                  :else (throw (py/Exception (str "Unsupported binding form: " b))))))
        process-entry (fn [bvec b] (pb bvec (first b) (second b)))]
    (if (every? symbol? (map first bents))
      bindings
      (reduce1 process-entry [] bents))))

(defmacro let
  "binding => binding-form init-expr

  Evaluates the exprs in a lexical context in which the symbols in
  the binding-forms are bound to their respective init-exprs or parts
  therein."
  {:added "1.0", :special-form true, :forms '[(let [bindings*] exprs*)]}
  [bindings & body]
  (assert-args
     (vector? bindings) "a vector for its binding"
     (even? (count bindings)) "an even number of forms in binding vector")
  `(let* ~(destructure bindings) ~@body))

(defn ^{:private true}
  maybe-destructured
  [params body]
  (if (every? symbol? params)
    (cons params body)
    (loop [params params
           new-params []
           lets []]
      (if params
        (if (symbol? (first params))
          (recur (next params) (conj new-params (first params)) lets)
          (let [gparam (gensym "p__")]
            (recur (next params) (conj new-params gparam)
                   (-> lets (conj (first params)) (conj gparam)))))
        `(~new-params
          (let ~lets
            ~@body))))))

;redefine fn with destructuring and pre/post conditions
(defmacro fn
  "params => positional-params* , or positional-params* & next-param
  positional-param => binding-form
  next-param => binding-form
  name => symbol

  Defines a function"
  {:added "1.0", :special-form true,
   :forms '[(fn name? [params* ] exprs*) (fn name? ([params* ] exprs*)+)]}
  [& sigs]
    (let [name (if (symbol? (first sigs)) (first sigs) nil)
          sigs (if name (next sigs) sigs)
          sigs (if (vector? (first sigs)) (list sigs) sigs)
          psig (fn* [sig]
                 (let [[params & body] sig
                       conds (when (and (next body) (map? (first body))) 
                                           (first body))
                       body (if conds (next body) body)
                       conds (or conds (meta params))
                       pre (:pre conds)
                       post (:post conds)                       
                       body (if post
                              `((let [~'% ~(if (< 1 (count body)) 
                                            `(do ~@body) 
                                            (first body))]
                                 ~@(map (fn* [c] `(assert ~c)) post)
                                 ~'%))
                              body)
                       body (if pre
                              (concat (map (fn* [c] `(assert ~c)) pre) 
                                      body)
                              body)]
                   (maybe-destructured params body)))
          new-sigs (map psig sigs)]
        (if name
          (list* 'fn* name new-sigs)
          (cons 'fn* new-sigs))))

(defmacro loop
  "Evaluates the exprs in a lexical context in which the symbols in
  the binding-forms are bound to their respective init-exprs or parts
  therein. Acts as a recur target."
  {:added "1.0", :special-form true, :forms '[(loop [bindings*] exprs*)]}
  [bindings & body]
    (assert-args
      (vector? bindings) "a vector for its binding"
      (even? (count bindings)) "an even number of forms in binding vector")
    (let [db (destructure bindings)]
      (if (= db bindings)
        `(loop* ~bindings ~@body)
        (let [vs (take-nth 2 (drop 1 bindings))
              bs (take-nth 2 bindings)
              gs (map (fn [b] (if (symbol? b) b (gensym))) bs)
              bfs (reduce1 (fn [ret [b v g]]
                            (if (symbol? b)
                              (conj ret g v)
                              (conj ret g v b g)))
                          [] (map vector bs vs gs))]
          `(let ~bfs
             (loop* ~(vec (interleave gs gs))
               (let ~(vec (interleave bs gs))
                 ~@body)))))))


(defn ^{:private true
   :static true}
  filter-key [keyfn pred amap]
    (loop [ret {} es (seq amap)]
      (if es
        (if (pred (keyfn (first es)))
          (recur (assoc ret (key (first es)) (val (first es))) (next es))
          (recur ret (next es)))
        ret)))

(defn ns-map
  "Returns a map of all the mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (clojure.lang.rt/map (.-__dict__ (the-ns ns))))

(defn ns-publics
  "Returns a map of the public intern mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (let [ns (the-ns ns)]
    (filter-key val (fn [v] (and (instance? clojure.lang.var/Var v)
                                 (= ns (.-ns v))
                                 (.isPublic v)))
                (ns-map ns))))

(defn ns-imports
  "Returns a map of the import mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (filter-key val (partial instance? py/type) (ns-map ns)))

(defn ns-interns
  "Returns a map of the intern mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (let [ns (the-ns ns)]
    (filter-key val (fn [v] (and (instance? clojure.lang.var/Var v)
                                 (= ns (.-ns v))))
                (ns-map ns))))

(defn refer-var
    "Adds the var to the given namespace with the given name"
    [ns nm v]
    (py/setattr ns (name nm) v))

(defn refer
  "refers to all public vars of ns, subject to filters.
  filters can include at most one each of:

  :exclude list-of-symbols
  :only list-of-symbols
  :rename map-of-fromsymbol-tosymbol

  For each public interned var in the namespace named by the symbol,
  adds a mapping from the name of the var to the var to the current
  namespace.  Throws an exception if name is already mapped to
  something else in the current namespace. Filters can be used to
  select a subset, via inclusion or exclusion, or to provide a mapping
  to a symbol different from the var's name, in order to prevent
  clashes. Use :use in the ns macro in preference to calling this directly."
  {:added "1.0"}
  [ns-sym & filters]
    (let [ns (or (find-ns ns-sym) (throw (py/Exception (str "No namespace: " ns-sym))))
          fs (apply hash-map filters)
          nspublics (ns-publics ns)
          rename (or (:rename fs) {})
          exclude (set (:exclude fs))
          to-do (or (:only fs) (keys nspublics))]
      (doseq [sym to-do]
        (when-not (exclude sym)
          (let [v (nspublics sym)]
            (when-not v
              (throw (py/Exception
                          (if (get (ns-interns ns) sym)
                            (str sym " is not public")
                            (str sym " does not exist")))))
            (refer-var (the-ns __name__) 
                       (or (rename sym) sym) 
                       (get nspublics sym)))))))
    
(defn ns-refers
  "Returns a map of the refer mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (let [ns (the-ns ns)]
    (filter-key val (fn [v] (and (instance? clojure.lang.var/Var v)
                                 (not= ns (.-ns v))))
                (ns-map ns))))


(defn ns-aliases
  "Returns a map of the aliases for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (let [ns (the-ns ns)]
       (if (py/hasattr ns "__aliases__")
           (.-__aliases__ ns)
           {})))

(defn alias
  "Add an alias in the given namespace to another
  namespace. Arguments are thre symbols: the alias to be used,
  the symbolic name of the target namespace, and the destination namespace. 
  Use :as in the ns macro in preference to calling this directly."
  {:added "1.0"
   :static true}
  [alias namespace-sym to-ns]
  (let [from-ns (the-ns namespace-sym)
        to-ns (the-ns to-ns)]
        (py/setattr to-ns 
                    "__aliases__" 
                    (assoc (ns-aliases to-ns)
                           alias
                           from-ns))))

(defn ns-unalias
  "Removes the alias for the symbol from the namespace."
  {:added "1.0"
   :static true}
  [ns sym]
  (let [to-ns (the-ns ns)]
       (py/setattr to-ns
                   "__aliases__"
                   (dissoc (ns-aliases to-ns)
                           sym))))

(defmacro when-first
  "bindings => x xs

  Same as (when (seq xs) (let [x (first xs)] body))"
  {:added "1.0"}
  [bindings & body]
  (assert-args
     (vector? bindings) "a vector for its binding"
     (= 2 (count bindings)) "exactly 2 forms in binding vector")
  (let [[x xs] bindings]
    `(when (seq ~xs)
       (let [~x (first ~xs)]
         ~@body))))

(defmacro lazy-cat
  "Expands to code which yields a lazy sequence of the concatenation
  of the supplied colls.  Each coll expr is not evaluated until it is
  needed. 

  (lazy-cat xs ys zs) === (concat (lazy-seq xs) (lazy-seq ys) (lazy-seq zs))"
  {:added "1.0"}
  [& colls]
  `(concat ~@(map #(list `lazy-seq %) colls)))

(defmacro for
  "List comprehension. Takes a vector of one or more
   binding-form/collection-expr pairs, each followed by zero or more
   modifiers, and yields a lazy sequence of evaluations of expr.
   Collections are iterated in a nested fashion, rightmost fastest,
   and nested coll-exprs can refer to bindings created in prior
   binding-forms.  Supported modifiers are: :let [binding-form expr ...],
   :while test, :when test.

  (take 100 (for [x (range 100000000) y (range 1000000) :while (< y x)] [x y]))"
  {:added "1.0"}
  [seq-exprs body-expr]
  (assert-args
     (vector? seq-exprs) "a vector for its binding"
     (even? (count seq-exprs)) "an even number of forms in binding vector")
  (let [to-groups (fn [seq-exprs]
                    (reduce1 (fn [groups [k v]]
                              (if (keyword? k)
                                (conj (pop groups) (conj (peek groups) [k v]))
                                (conj groups [k v])))
                            [] (partition 2 seq-exprs)))
        err (fn [& msg] (throw (IllegalArgumentException. ^String (apply str msg))))
        emit-bind (fn emit-bind [[[bind expr & mod-pairs]
                                  & [[_ next-expr] :as next-groups]]]
                    (let [giter (gensym "iter__")
                          gxs (gensym "s__")
                          do-mod (fn do-mod [[[k v :as pair] & etc]]
                                   (cond
                                     (= k :let) `(let ~v ~(do-mod etc))
                                     (= k :while) `(when ~v ~(do-mod etc))
                                     (= k :when) `(if ~v
                                                    ~(do-mod etc)
                                                    (recur (rest ~gxs)))
                                     (keyword? k) (err "Invalid 'for' keyword " k)
                                     next-groups
                                      `(let [iterys# ~(emit-bind next-groups)
                                             fs# (seq (iterys# ~next-expr))]
                                         (if fs#
                                           (concat fs# (~giter (rest ~gxs)))
                                           (recur (rest ~gxs))))
                                     :else `(cons ~body-expr
                                                  (~giter (rest ~gxs)))))]
                      (if next-groups
                        #_"not the inner-most loop"
                        `(fn ~giter [~gxs]
                           (lazy-seq
                             (loop [~gxs ~gxs]
                               (when-first [~bind ~gxs]
                                 ~(do-mod mod-pairs)))))
                        #_"inner-most loop"
                        (let [gi (gensym "i__")
                              gb (gensym "b__")
                              do-cmod (fn do-cmod [[[k v :as pair] & etc]]
                                        (cond
                                          (= k :let) `(let ~v ~(do-cmod etc))
                                          (= k :while) `(when ~v ~(do-cmod etc))
                                          (= k :when) `(if ~v
                                                         ~(do-cmod etc)
                                                         (recur
                                                           (inc ~gi)))
                                          (keyword? k)
                                            (err "Invalid 'for' keyword " k)
                                          :else
                                            `(do (chunk-append ~gb ~body-expr)
                                                 (recur (inc ~gi)))))]
                          `(fn ~giter [~gxs]
                             (lazy-seq
                               (loop [~gxs ~gxs]
                                 (when-let [~gxs (seq ~gxs)]
                                   (if (chunked-seq? ~gxs)
                                     (let [c# (chunk-first ~gxs)
                                           size# (int (count c#))
                                           ~gb (chunk-buffer size#)]
                                       (if (loop [~gi (int 0)]
                                             (if (< ~gi size#)
                                               (let [~bind (py.bytecode/BINARY_SUBSCR c# ~gi)]
                                                 ~(do-cmod mod-pairs))
                                               true))
                                         (chunk-cons
                                           (chunk ~gb)
                                           (~giter (chunk-rest ~gxs)))
                                         (chunk-cons (chunk ~gb) nil)))
                                     (let [~bind (first ~gxs)]
                                       ~(do-mod mod-pairs)))))))))))]
    `(let [iter# ~(emit-bind (to-groups seq-exprs))]
        (iter# ~(second seq-exprs)))))

(defn format
  "Formats a string"
  {:added "1.0"
   :static true}
  [fmt & args]
  (py.bytecode/BINARY_MODULO fmt args))

(defn throw-if
  "Throws an exception with a message if pred is true"
  [pred fmt & args]
  (when pred
    (let [message (apply format fmt args)
          exception (py/Exception. message)]
      (throw exception))))


(defn load-all
  "Loads a lib given its name and forces a load of any libs it directly or
  indirectly loads. If need-ns, ensures that the associated namespace
  exists after loading. If require, records the load so any duplicate loads
  can be skipped."
  [lib]
  (py/__import__ (name lib)))

(defn map-ns-vals [from to opts]
    (let [from-ns (the-ns from)
          to-ns (the-ns to)
          only (if (:only opts) (set (map name (:only opts))))
          exclude (if (:exclude opts) (set (map name (:exclude opts))))
          filterfn (fn filterfn [s]
                       (cond (.startswith (name s) "_")
                             false
                             exclude
                             (not (contains? exclude s))
                             only 
                             (contains? only s)
                             :else
                             true))]
          (doseq [x (filter filterfn (py/dir from-ns))]
                 (py/setattr to-ns 
                             (name x)
                             (py/getattr from-ns (name x))))))
                           
          

(defn load-lib
  "Loads a lib with options"
  [to-ns lib & options]
  (let [opts (apply hash-map options)
        {:keys [as reload reload-all require use verbose]} opts
        loaded (contains? sys/modules lib)
        need-ns (or as use)
        filter-opts (select-keys opts '(:exclude :only :rename))]
       (load-all lib)
       (cond (not options)
              (py/setattr (the-ns (name to-ns)) 
                          (name lib) 
                          (load-all lib))
             as
              (alias (:as opts) lib to-ns)
             :else 
              (map-ns-vals lib to-ns opts))))

(defmacro require
    [& options]
    `(load-lib ~'__name__ ~@options))


(defmacro ns
  "Sets *ns* to the namespace named by name (unevaluated), creating it
  if needed.  references can be zero or more of: (:refer-clojure ...)
  (:require ...) (:use ...) (:import ...) (:load ...) (:gen-class)
  with the syntax of refer-clojure/require/use/import/load/gen-class
  respectively, except the arguments are unevaluated and need not be
  quoted. (:gen-class ...), when supplied, defaults to :name
  corresponding to the ns name, :main true, :impl-ns same as ns, and
  :init-impl-ns true. All options of gen-class are
  supported. The :gen-class directive is ignored when not
  compiling. If :gen-class is not supplied, when compiled only an
  nsname__init.class will be generated. If :refer-clojure is not used, a
  default (refer 'clojure) is used.  Use of ns is preferred to
  individual calls to in-ns/require/use/import:

  (ns foo.bar
    (:refer-clojure :exclude [ancestors printf])
    (:require (clojure.contrib sql sql.tests))
    (:use (my.lib this that))
    (:import (java.util Date Timer Random)
             (java.sql Connection Statement)))"
  {:arglists '([name docstring? attr-map? references*])
   :added "1.0"}
  [name & references]
  (let [
       quote-args (fn [arg kname]
                  `(~(symbol "clojure.core" (clojure.core/name kname))
                    ~@(map #(list 'quote %) arg)))
       
       process-reference
       (fn [[kname & args]]
           (if (vector? (first args))
               `(do ~@(map quote-args args (repeat kname)))
               (quote-args args kname)))
               
        docstring  (when (string? (first references)) (first references))
        references (if docstring (next references) references)
        metadata   (when (map? (first references)) (first references))
        references (if metadata (next references) references)
        gen-class-clause (first (filter #(= :gen-class (first %)) references))
        gen-class-call
          (when gen-class-clause
            (list* `gen-class :name (.replace (str name) "-" "_") :impl-ns name :main true (next gen-class-clause)))
        references (remove #(= :gen-class (first %)) references)
        ;ns-effect (clojure.core/in-ns name)
        ]
    `(do
       (~'ns* ~name)
       ~@(map process-reference references))))


(def reduce reduce1)

(defn empty?
  "Returns true if coll has no items - same as (not (seq coll)).
  Please use the idiom (seq x) rather than (not (empty? x))"
  {:added "1.0"
   :static true}
  [coll] (not (seq coll)))

(defn deref
  "Also reader macro: @ref/@agent/@var/@atom/@delay/@future/@promise. Within a transaction,
  returns the in-transaction-value of ref, else returns the
  most-recently-committed value of ref. When applied to a var, agent
  or atom, returns its current state. When applied to a delay, forces
  it if not already forced. When applied to a future, will block if
  computation not complete. When applied to a promise, will block
  until a value is delivered.  The variant taking a timeout can be
  used for blocking references (futures and promises), and will return
  timeout-val if the timeout (in milliseconds) is reached before a
  value is available. See also - realized?."
  {:added "1.0"
   :static true}
  ([ref] (.deref ref))
  ([ref timeout-ms timeout-val] (.deref ref timeout-ms timeout-val)))

(defn ns-resolve
  "Returns the var or Class to which a symbol will be resolved in the
  namespace (unless found in the environement), else nil.  Note that
  if the symbol is fully qualified, the var/Class to which it resolves
  need not be present in the namespace."
  {:added "1.0"
   :static true}
  ([ns sym]
    (clojure.lang.namespace/findItem (the-ns ns) sym)))

(defmacro resolve
  "same as (ns-resolve *ns* symbol) or (ns-resolve *ns* &env symbol)"
  {:added "1.0"}
  ([sym] `(ns-resolve ~'__name__ ~sym)))

                
(defmacro comment
  "Ignores body, yields nil"
  {:added "1.0"}
  [& body])

(defn aclone
  "Returns a clone of the Python list. Works on any type implementing slices and lists."
  [l] (.__getitem__ l (py/slice 0 (py/len l))))

(defn aset [l i item]
  "Equivalent to l[i] = item in Pytyon"
  (.__setitem__ l i item))

(defn hash-combine
  "Creates a new hash value from two other hashes"
  [seed hash]
  (bit-and 0xFFFFFFFF (bit-xor seed (+ hash 0x9e3779b9 (bit-shift-left seed 6) (bit-shift-left seed 2)))))

(require 'clojure.core-deftype :only ['deftype 'reify 'definterface 'defprotocol 'defrecord])

; FIXME: Am I polluting the namespace by requiring those?!
(require 'numbers :only ['Number])

(defn number? [n]
    "Returns true if n is a number. Works for int, long, Decimal, Fractions and anything else that implements the Number Abstract Base Class"
    (instance? Number n))

(defn boolean [x] (if x true false))

;; ======================================================================
;; Printing
;; newline, flush, pr, and prn, print, println.
;; It's in place for live IPrintable testing.
;;
;; None instead of nil is printed after each call because the repl is
;; printing the raw Python result of pr, print, etc.
;;
;; None of this is final.
;; ======================================================================

;; Need binding
;; (def ^:dynamic *flush-on-newline* false)
;; (def ^:dynamic *print-dup* false)
;; (def ^:dynamic *print-meta* false)
;; (def ^:dynamic *print-readably* true)
(def ^:dynamic *out* sys/stdout)

(defn newline [] (.write *out* "\n") nil)
(defn flush [] (.flush *out*) nil)

(defn pr
  "readable, no newline"
  ([] nil)
  ([x]
     (clojure.protocols/writeAsReplString x *out*))
  ([x & more]
     (pr x)
     (.write *out* " ")
     (if-let [nmore (next more)]
       (recur (first more) nmore)
       (apply pr more))))

(defn prn
  "readable, newline follows"
  [& more]
  (apply pr more)
  (newline))

;; XXX: for testing only
(defn print-pr
  ([] nil)
  ([x]
     (clojure.protocols/writeAsString x *out*))
  ([x & more]
     (print-pr x)
     (.write *out* " ")
     (if-let [nmore (next more)]
       (recur (first more) nmore)
       (apply print-pr more))))

(defn print
  "unreadable-ish, no newline"
  [& more]
  (apply print-pr more))

;; XXX: for testing only
(defn print-prn
  [& more]
  (apply print-pr more)
  (newline))

(defn debug [x]
    (py/print x)
    x)

(defn println
  "unreadable-ishishly, newline follows"
  [& more]
  (apply print-prn more))

(defmacro binding
  "binding => var-symbol init-expr

  Creates new bindings for the (already-existing) vars, with the
  supplied initial values, executes the exprs in an implicit do, then
  re-establishes the bindings that existed before.  The new bindings
  are made in parallel (unlike let); all init-exprs are evaluated
  before the vars are bound to their new values."
  {:added "1.0"}
  [bindings & body]
  (assert-args binding
    (vector? bindings) "a vector for its binding"
    (even? (count bindings)) "an even number of forms in binding vector")
  (let [var-ize (fn [var-vals]
                  (loop [ret [] vvs (seq var-vals)]
                    (if vvs
                      (recur  (conj (conj ret `(resolve (symbol ~(name (first vvs))))) (second vvs))
                             (next (next vvs)))
                      (seq ret))))]
   `(let []
       (clojure.lang.var/pushThreadBindings (hash-map ~@(var-ize bindings)))
       (~'try
         ~@body
         (~'finally
           (clojure.lang.var/popThreadBindings))))))

(defmacro var
    [itm]
    `(clojure.lang.var/find (symbol ~'__name__ ~(name itm))))


(defmacro doc
    [itm]
   `(let [itm# (clojure.core/var ~itm)]
         (cond (and (meta itm#) (:doc (meta itm#)))
                (py/print (:doc (meta itm#)))
               (py/hasattr itm# "__doc__")
                (py/print (py/getattr itm# "__doc__")))))

(defn when-attr

    "If attr exists on obj, it calls (apply (py/getattr obj attr) args) otherwise

     nil is returned"

    [obj attr & args]

    (when (py/hasattr obj attr)

          (apply (py/getattr obj attr) args)))

    



(defmacro with-open

  "bindings => [name init ...]



  First, with-open calls (.__enter__ name) on all bindings if the attribute

  exists. Next it evaluates body in a try expression with names bound to the 

  values of the inits, and a finally clause that calls (.__exit__ name) and/or 

  (.close name) on each name in reverse order."

  {:added "1.0"}

  [bindings & body]

  (assert-args with-open

     (vector? bindings) "a vector for its binding"

     (even? (count bindings)) "an even number of forms in binding vector")

  (cond

    (= (count bindings) 0) `(do ~@body)

    (symbol? (bindings 0)) `(let ~(subvec bindings 0 2)

                              (when-attr ~(bindings 0) "__enter__")

                              (~'try

                                (with-open ~(subvec bindings 2) ~@body)

                                (~'finally

                                  (do

                                   (when-attr ~(bindings 0) "__exit__")

                                   (when-attr ~(bindings 0) "close")))))

    :else (throw (IllegalArgumentException.

                   "with-open only allows Symbols in bindings"))))




