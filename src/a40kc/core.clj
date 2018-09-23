(ns a40kc.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.xml :as xml])
(require '[clojure.zip :as zip])
(require '[clojure.data.zip.xml :as zip-xml])
(use 'clojure.data.zip.xml)

(def data (zip/xml-zip (xml/parse "test-1.ros")))

(def selections
  (xml-> data
         :forces
         :force
         :selections
         :selection
         (attr= :type "model")))

(defn extract-attributes [node]
  (for [a ["T" "BS" "Save"]]
    (xml-> node
           :profiles
           :profile
           (attr= :profileTypeName "Unit")
           :characteristics
           :characteristic
           (attr= :name a))))

(defn extract-name[node]
  (:name (:attrs node)))

(defn extract-weapons [node]
  (for [a ["Range" "Type" "S" "AP" "D"]]
    (xml-> node
             :selections
             :selection
             :profiles
             :profile
             (attr= :profileTypeName "Weapon")
             :characteristics
             :characteristic
             (attr= :name a))
    ))

(defn map-attr [f node]
  (for [s (f node)]
    (map
     #(select-keys (get-in (first %) [:attrs]) [:name :value])
     s)))

(defn extract-characteristics [node]
  (for [n node]
    (do
      (let
          [name (:name (:attrs (zip/node n)))
           weapons (map-attr extract-weapons n)
           attributes (map-attr extract-attributes n)]
        {:name name :weapons weapons :attributes attributes}))))

(defn -main
  "I don't do a whole lot."
  [& args]
  (extract-characteristics selections))
