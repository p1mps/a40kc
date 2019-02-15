(ns a40kc.core
  (:gen-class))

(use '[clj-xpath.core])

(def xml-file1 (slurp "test.ros"))

(def xml-file2 (slurp "test.ros"))

(def data1
  (xml->doc xml-file1))

(def data2
  (xml->doc xml-file2))

(defn get-xml-values [node]
  (select-keys (:attrs node) [:name :value]))

(defn get-list-xml-values [list]
  (map
   #(select-keys (:attrs %) [:name :value]) list))

(defn parse-weapons [xml]
  (map
   (fn [item]
     {:weapon-name (get-xml-values item)
      :values (get-list-xml-values ($x ".//characteristic" item))})
   ($x
    "./selections/selection/profiles/profile[@profileTypeName='Weapon']"
    xml)))

(defn parse-data [data]
   (map
    (fn [item]
      {:unit-name
       (get-xml-values item)
       :unit-characteristics
       (get-list-xml-values
        ($x "./profiles/profile[@profileTypeName='Unit']//characteristic" item))
       :unit-weapons
       (parse-weapons item)
       })
    ($x "//selection[@type='model']" data)))

(defn read-file [file]
  (parse-data (slurp file)))

;;((6 - BS + 1) * 1/6) * 100
(defn odds-shooting [BS]
  (int (* 100 (* (+ (- 6 BS) 1) (/ 1 6.0)))))


(def data-file
  (read-file "test.ros"))

(defn get-unit-characteristic [unit name]
  (:value (first (filter #(= (:name %) name) (:unit-characteristics unit)))))

(map
 (fn [unit]
   (do
     (pprint (:unit-name unit))
     (pprint (get-unit-characteristic unit "BS"))
     (pprint (odds-shooting (get-unit-characteristic unit "BS")))))
 data-file)

;; ( -main
;;   "I don't do a whole lot."
;;   [& args])
