(ns a40kc.core
  (:gen-class))

(require '[clojure.xml :as xml])
(require '[clojure.java.io :as io])
(require '(xenopath [xpath :as xpath] [dom :as dom]))

(def xml-file (slurp "test.ros"))

(def models (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Unit']" xml-file)))
(def weapons (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Weapon']" xml-file)))
(def weapons-stats (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Weapon']//characteristic" xml-file)))

(doseq [x models] (println (:name x)))
(doseq [x weapons] (println (:name x)))
(doseq [x weapons-stats] (println (:name x) (:value x)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (print parse-xml))
