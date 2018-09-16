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

(def ATTRIBUTES ["T" "BS" "Save"])
(defn extract-attributes [node]
  (for [a ATTRIBUTES]
    (apply
     #(xml-> %
             :characteristics
             :characteristic
             (attr= :name a)
             zip/node)
     node)
     ))

(defn map-attr [node]
  (for [s (map #(zip/node %) (extract-attributes node))]
    (select-keys (get-in (first s) [:attrs]) [:name :value])))


(defn extract-characteristics [node] 
  [:name (:name (:attrs (zip/node (first node))))
   :attrs [(map-attr node)]])

(defn -main
  "I don't do a whole lot."
  [& args])
