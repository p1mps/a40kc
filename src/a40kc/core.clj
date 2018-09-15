(ns a40kc.core
  (:gen-class)
  (:require [clojure.zip :as zip]))

;; (require '[clojure.xml :as xml])
;; (require '[clojure.java.io :as io])
;; (require '(xenopath [xpath :as xpath] [dom :as dom]))
(require '[clojure.java.io :as io])
(require '[clojure.xml :as xml])
(require '[clojure.zip :as zip])
(require '[clojure.data.zip.xml :as zip-xml])
(use 'clojure.data.zip.xml)

(def xml-file (slurp "test.ros"))

(def data (zip/xml-zip (xml/parse "test.ros")))

(def root (-> "test.ros" xml/parse zip/xml-zip))


(def selections
  (xml-> data
         :forces
         :force
         :selections
         :selection
         :profiles
         :profile
         (attr= :profileTypeName "Unit")
         ))

(extract-characteristics selections "T")

(defn extract-characteristics [node c] 
  [:name (:name (:attrs (zip/node (first node))))
   (select-keys (:attrs (first (apply #(xml-> % :characteristics :characteristic (attr= :name c) zip/node) node))) [:name :value])])



(defn extract-characteristics
  [value]
  (xml->
   selections
   :characteristics
   :characteristic
   ))

(for [selection selections
      :let [
            c (xml->
               selection
               :characteristics
               :characteristic
               (attr= :name "WS")
               zip/node)
            ]]
  c)

(def map
  {:units
   [
    {
     :name (get-in (first selections) [:attrs :name])
     :weapons (get-in (:content selections) [:tag])
     }
    ]
   })

(defn print-characteristic [x]
  (for [characteristic (:characteristc x)]
    (pprint characteristic)))

(println "porco")

(doall (pprint selections))

(map #(pprint %) selections)

(for [selection selections]
      (pprint selection))


;; (def models (xpath/lookup-nodeset "//selection[@type='model']" xml-file))
;; (def weapons (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Weapon']" xml-file)))
;; (def weapons-stats (map dom/attributes (xpath/lookup-nodeset "//selection[@type='model']//profile[@profileTypeName='Weapon']//characteristic" xml-file)))

;; (def data (for [x (xml-seq
;;                    (xml/parse (java.io.File. "test.ros" )))
;;                 :when (= :selections (:tag x))]
;;             (first (get-in x [:content]))))

;; (doseq [x models] (println x))
;; (doseq [x weapons] (println (:name x)))
;; (doseq [x weapons-stats] (println (:name x) (:value x)))

(defn -main
  "I don't do a whole lot."
  [& args])
