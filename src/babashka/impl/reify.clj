(ns babashka.impl.reify
  {:no-doc true}
  (:require [clojure.math.combinatorics :as combo]
            [sci.impl.types]))

(set! *warn-on-reflection* false)

(defmacro gen-reify-combos
  "Generates pre-compiled reify combinations"
  [methods]
  (let [subsets (rest (combo/subsets (seq methods)))]
    (reduce (fn [opts [classes protocols?]]
              (let [prelude '[reify]
                    prelude (if protocols?
                              (conj prelude
                                    'sci.impl.types.IReified
                                    '(getInterfaces [this]
                                                    interfaces)
                                    '(getMethods [this]
                                                 methods)
                                    '(getProtocols [this]
                                                   protocols))
                              prelude)]
                (assoc opts
                       (cond-> (set (map #(list 'quote %)
                                         (map first classes)))
                         protocols?
                         (conj (list 'quote 'sci.impl.types.IReified)))
                       (list 'fn ['interfaces 'methods 'protocols]
                             (concat prelude
                                     (mapcat
                                      (fn [[clazz methods]]
                                        (cons clazz
                                              (mapcat
                                               (fn [[meth arities]]
                                                 (map
                                                  (fn [arity]
                                                    (list meth arity
                                                          (list*
                                                           (list 'get 'methods (list 'quote meth))
                                                           arity)))
                                                  arities))
                                               methods)))
                                      classes))))))
            {}
            (concat (map (fn [subset bool]
                           [subset bool])
                         subsets
                         (repeat true))
                    (map (fn [subset bool]
                           [subset bool])
                         subsets
                         (repeat false))))))

#_(prn (macroexpand '(gen-reify-combos
                      {java.io.FileFilter {accept [[this f]]}})))

#_:clj-kondo/ignore
(def reify-opts
  (gen-reify-combos
   {java.nio.file.FileVisitor {preVisitDirectory [[this p attrs]]
                               postVisitDirectory [[this p attrs]]
                               visitFile [[this p attrs]]}
    java.io.FileFilter {accept [[this f]]}
    java.io.FilenameFilter {accept [[this f s]]}
    clojure.lang.ILookup {valAt [[this k] [this k default]]}
    clojure.lang.IFn {applyTo [[this arglist]]
                      invoke [[this]
                              [this a1]
                              [this a1 a2]
                              [this a1 a2 a3]
                              [this a1 a2 a3 a4]
                              [this a1 a2 a3 a4 a5]
                              [this a1 a2 a3 a4 a5 a6]
                              [this a1 a2 a3 a4 a5 a6 a7]
                              [this a1 a2 a3 a4 a5 a6 a7 a8]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16 a17]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16 a17 a18]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16 a17 a18 a19]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16 a17 a18 a19 a20]
                              [this a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16 a17 a18 a19 a20 varargs]]}
    clojure.lang.Associative {containsKey [[this k]]
                              entryAt [[this k]]
                              assoc [[this k v]]}}))
