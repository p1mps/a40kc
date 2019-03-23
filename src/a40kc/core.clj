(ns a40kc.core
  (:gen-class))
;; TODO parse by category: HQ, Troops, Elite, Heavy Support
;; TODO handle characteristics "*" (vehicles/monsters)

(use '[clj-xpath.core])

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
       ($x ".//profile[@profileTypeName='Unit']//characteristic" item))
       :unit-weapons
       (parse-weapons item)
       })
    ;; troops "number" of units
    (concat
     ($x "//selection[@type='model']" data)
     ($x "//selection[@type='unit']" data))))

(defn read-file [file]
  (parse-data (slurp file)))

(read-file "roster.ros")

(def data-file
  (read-file "roster.ros"))

(defn get-unit-characteristic [unit name]
  (:value (first (filter #(= (:name %) name) (:unit-characteristics unit)))))

(defn clean-characteristic [value]
  (if value
    (read-string (clojure.string/replace value "+" ""))))

;;((6 - BS + 1) * 1/6) * 100
(defn odds-characteristic [BS]
  (let [cleaned-bs (clean-characteristic BS)]
    (println cleaned-bs)
    (if (integer? cleaned-bs)
      (int (* 100 (* (+ (- 6 cleaned-bs) 1) (/ 1 6.0)))))))


(defn odds-wounding [S T]
  (let [comparison (- S T)]
    (- 4 comparison)))

(odds-wounding 4 4)

(odds-characteristic (odds-wounding 3 4))

(clean-characteristic "*")

(map
 (fn [unit]
   {
    :name (:name (:unit-name unit))
    :BS (get-unit-characteristic unit "BS")
    :shooting (odds-characteristic (get-unit-characteristic unit "BS"))
    })
   data-file)

;; (data-file)

;; ( -main
;;   "I don't do a whole lot."
;;   [& args])
