(ns sdf-clojure.ch2)

((comp
  (fn [x] (list 'foo x))
  (fn [x] (list 'bar x)))
 'z)

(defn my-iterate [n]
      (fn [f]
          (if (= n 0)
            identity
            (comp f ((my-iterate (dec n)) f)))))

(((my-iterate 3) (fn [x] (* x x))) 5)

(defn my-iterate-2 [n f]
      (if (= n 0)
            identity
            (comp f (my-iterate-2 (dec n) f))))

((my-iterate-2 3 #(* % %)) 5)

(defn parallel-combine [h f g]
      (fn [& args]
          (h (apply f args) (apply g args))))

((parallel-combine list
                   (fn [x y z] (list 'foo x y z))
                   (fn [u v w] (list 'bar u v w)))
 'a 'b 'c)

;; ----


(defn get-arity [f]
      (-> f meta :arity))

(defn restrict-arity [f n]
      (with-meta f {:arity n}))

(defn spread-combine [h f g]
      (let [n (get-arity f)]
           (fn [& args]
               (h (apply f (take n args))
                  (apply g (drop n args))))))

(defn spread-combine-2 [h f g]
      (let [n (get-arity f)
            m (get-arity g)
            t (+ n m)
            result-fn (fn [& args]
                   (h (apply f (take n args))
                      (apply g (drop n args))))]
               (restrict-arity result-fn t)))


(defn spread-combine-3 [h f g]
      (let [n (get-arity f)
            m (get-arity g)
            t (+ n m)
            the-combination (fn [& args]
                   (assert (= (count args) t))
                   (h (apply f (take n args))
                      (apply g (drop n args))))]
               (restrict-arity the-combination t)))

((spread-combine-3 list
                   (restrict-arity (fn [x y] (list 'foo x y)) 2)
                   (restrict-arity (fn [u v w] (list 'bar u v w)) 3))
 'a 'b 'c 'd 'e)

(defn -main [& args]
      (let [result ((spread-combine-3 list
                                      (restrict-arity (fn [x y] (list 'foo x y)) 2)
                                      (restrict-arity (fn [u v w] (list 'bar u v w)) 3))
                    'a 'b 'c 'd 'e)]
           (println result)))