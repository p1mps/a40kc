(ns a40kc.core
  (:gen-class))

(require '[clojure.java.io :as io])
(require '[clojure.xml :as xml])
(require '[clojure.zip :as zip])
(require '[clojure.data.zip.xml :as zip-xml])
(use 'clojure.data.zip.xml)

(def data (zip/xml-zip (xml/parse "test.ros")))

(def selections
  (xml-> data
         :forces
         :force
         :selections
         :selection
         (attr= :type "model")
         ))

(defn extract-attributes [node]
  (for [a ["T" "BS" "Save"]]
    (apply
     #(xml-> %
             :profiles
             :profile
             (attr= :profileTypeName "Unit")
             :characteristics
             :characteristic
             (attr= :name a)
             zip/node)
     node)
     ))

(defn extract-weapons [node]
  (for [a ["Range" "Type" "S" "AP" "D"]]
    (apply
     #(xml-> %
             :selections
             :selection
             :profiles
             :profile
             (attr= :profileTypeName "Weapon")
             :characteristics
             :characteristic
             (attr= :name a)
             zip/node)
     node)
    ))

(defn map-attr [f node]
  (for [s (f node)]
    (select-keys (get-in (first s) [:attrs]) [:name :value])))

(defn extract-characteristics [node]
  [:name (:name (:attrs (zip/node (first node))))
   :attrs [(map-attr extract-attributes node)]
   :wepons [(map-attr extract-weapons node)]
   ]
  )

(defn -main
  "I don't do a whole lot."
  [& args]
  (extract-characteristics selections))
