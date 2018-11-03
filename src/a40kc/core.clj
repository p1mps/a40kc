(ns a40kc.core
  (:gen-class))

(use '[clj-xpath.core])


(def data (slurp "test-1.ros"))

(def xmldoc
  (xml->doc data))

(defn get-xml-values [node]
  (select-keys (:attrs node) [:name :value]))

(defn get-list-xml-values [list]
  (map
   #(select-keys (:attrs %) [:name :value]) list))

(defn parse-weapons [xml]
  (map
   (fn [item] {:weapon_name
         (get-xml-values item)
         :values (get-list-xml-values ($x ".//characteristic" item))})
   ($x
    "./selections/selection/profiles/profile[@profileTypeName='Weapon']"
    xml)))

(defn create-data []
  (map
   (fn [item]
     {:unit
      (get-xml-values
       item)
      :unit-characteristic
      (get-list-xml-values
       ($x "./profiles/profile[@profileTypeName='Unit']//characteristic" item))
      :unit-weapons
      (parse-weapons item)
      })
   ($x "//selection[@type='model']" xmldoc)))

(create-data)

 (defn -main
   "I don't do a whole lot."
   [& args])
