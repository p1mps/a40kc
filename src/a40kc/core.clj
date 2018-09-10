(ns a40kc.core
  (:gen-class))

(require '[clojure.xml :as xml])
(require '[clojure.java.io :as io])
(require '(xenopath [xpath :as xpath] [dom :as dom]))

(def xml-file (slurp "test.ros"))

(def models (xpath/lookup-nodeset "//selection[@type='model']" xml-file))
(def weapons (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Weapon']" xml-file)))
(def weapons-stats (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Weapon']//characteristic" xml-file)))

(for [x (xml-seq
         (xml/parse (java.io.File. "test.ros" )))
      :when (= :selection (:tag x))
      :when (= "model" (:type (:attrs x)))]
  (:type (:attrs x)))

(doseq [x models] (println x))
(doseq [x weapons] (println (:name x)))
(doseq [x weapons-stats] (println (:name x) (:value x)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (print parse-xml))
