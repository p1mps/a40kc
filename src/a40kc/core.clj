(ns a40kc.core
   (:require
   [clojure.zip :as zip]
   [clojure.xml :as xml]
   [clojure.data.zip.xml :as zx]
   )
   (:gen-class))

(def file (slurp "roster.ros"))

(def zipper
  (-> (.getBytes file)
      java.io.ByteArrayInputStream.
      xml/parse zip/xml-zip))

(defn attrs-name [e]
    (:name (:attrs e)))

(defn attrs-and-content [e]
  [(:name (:attrs e)) (:content e)])

(defn content [e]
  (first (:content e)))

(def units
  (zx/xml->
   zipper
   :roster
   :forces
   :force
   :selections
   :selection
   (zx/attr= :type "unit")))


(defn parse-weapons [u]
  (concat
   (for [weapon (zx/xml-> u :profiles :profile)]
                  (let [chars (zx/xml-> weapon :characteristics :characteristic)]
                    {:name  (attrs-name (first weapon))
                     :chars (map #(attrs-and-content (first %)) chars)}))


   (for [weapon (zx/xml-> u :selections :selection (zx/attr= :type "upgrade") :selections :selection )]
                  (let [chars (zx/xml-> weapon :profiles :profile :characteristics :characteristic)]
                    {:name  (attrs-name (first weapon))
                     :chars (map #(attrs-and-content (first %)) chars)}))
   ))


(defn parse-units []
  (first (for [u units]
           (for [m (zx/xml-> u :selections :selection)]
             {:model       (attrs-name (first m))
              :model-chars (map #(attrs-and-content (first %)) (zx/xml-> m :profiles :profile :characteristics :characteristic))
              :weapons     (parse-weapons u)
              }))))


(defn unit->weapons-all-squad [loc]
  (-> loc
      zip/down
      zip/down
      ))


(defn unit->models [loc]
  (-> loc
      zip/down
      zip/right
      zip/children

      ))



(defn chars-weapons-all-squad [loc]
  (some-> loc zip/down zip/children))


(defn keywordize [string]
  (keyword (clojure.string/lower-case string)))

(defn parse [loc]
  (reduce (fn [result value]
            (let [name (:name (:attrs value))
                  content (:content value)]
              (when (and name content)
                (assoc result (keywordize name)  (first content)))))
          {}
          loc))



(defn parse-name [loc]
  (remove nil? (map #(:name (:attrs %)) loc)))





(defn parse-chars [chars]
  (reduce (fn [result value]
            (assoc result (keywordize (attrs-name value)) (content value)))
          {}
          chars))

(defn parse-models [loc]
  (reduce (fn [result value]
            (let [chars (-> value zip/node)]

              (conj result chars)))
          []
          loc))


;; (let [models (unit->models u)]
;;        (for [m models]

;;          :name (attrs-name m)
;;          :weapons   (let [w       (unit->weapons-all-squad u)
;;                           w2      (chars-weapons-all-squad w)
;;                           weapons (-> u
;;                                       zip/down
;;                                       zip/node
;;                                       )]
;;                       {:name  (parse-name w)
;;                        :gg    weapons
;;                        :chars (parse w2)
;;                        })))

(defn analyse []
  (for [u units]
    {:unit-name (attrs-name (zip/node u))
     :models (parse-models (unit->models u))
     }))



(second (:content units))


(comment

  (def doc-xml
    (->
     file .getBytes java.io.ByteArrayInputStream. xml/parse))


  (->>
   doc-xml
   xml-seq
   (filter #(= "Weapon" (:typeName (:attrs %))))
   (map #(:name (:attrs %))))

  (def units
    (->>
     doc-xml
     xml-seq
     (filter #(= "unit" (:type (:attrs %))))))

  (defn attrs-and-content [e]
    [(:name (:attrs e) (:content e))])

  (defn weapon-stats [e]
    [{(keyword (clojure.string/lower-case (:name (:attrs e)))) (:content e)}])




  (defn content [e]
    (:content e))

  (defn unwrap [l]
    (reduce (fn [result value]
              (assoc result (first (first value)) (second (first value)))
              )

            {}
            l))

  (defn weapons [l]
    (mapcat weapon-stats
            (mapcat content
                    (mapcat content
                            (mapcat content
                                    (mapcat content
                                            (mapcat :content (filter #(=  (:tag %) :selections) l))))))))




  (defn weapons-and-characteristics [l]
    (map #(hash-map :name (attrs-name %) :weapons (weapons (:content %)))
         (mapcat :content (filter #(= (:tag %) :selections )
                                  (:content l)))))


  (reduce (fn [result value]
            (let [unit-name (:name (:attrs value))
                  sub-units (weapons-and-characteristics value)]
              (conj result {:unit-name unit-name :sub-units sub-units}))

            )
          []
          units
          )
  units

  (mapcat :content units)

  (->>
   doc-xml
   xml-seq

   (filter #(= "Weapon" (:typeName (:attrs %))))

   )


  (defn map-selection [l]
    (let [units (filter #(= (:type (:attrs %) "unit")) l)]
      units)
    )



  (def characteristics
    (zx/xml->
     zipper
     :forces
     :force
     :selections
     :selection
     (zx/attr= :type "unit")
     :profiles
     :profile
     (zx/attr= :typeName "Weapon")
     :characteristics
     (zx/children)

     zx/text
     ))

  (filter #(= (:type (:attrs %) "unit"))
          (:content (first (zx/xml1->
                            zipper
                            :forces
                            :force
                            :selections
                            xml-seq
                            ))))

  (for [force ]
    (for [selection (zx/xml1-> force :selections)]
      selection))



  ;; TODO parse by category: HQ, Troops, Elite, Heavy Support
  ;; TODO handle characteristics "*" (vehicles/monsters)

  (use '[clj-xpath.core])

  (defn get-xml-values [node]
    (select-keys (:attrs node) [:name :value]))

  (defn get-list-xml-values [l]

    (->>
     (map #(hash-map (:name (:attrs %)) (:text %))  l)
     (partition-all 6)

     )

    ;; ;; (map
    ;; ;;  #(cond
    ;; ;;     (= (:name %) "M") (assoc l :movement (:text %))



    ;; ;;    str (:name (:attrs %)) (:value %) (:text %)
    ;;     ))
    )

  (defn parse-weapons [xml]
    xml)


  (defn parse-data [data]
    (map
     (fn [item]
       {:unit-name
        (get-xml-values item)
        :unit-characteristics
        (map #(hash-map (:name (:attrs %)) (:text %))
             ($x ".//profile[@typeName='Unit']//characteristic" item))

        :unit-weapons
        (map #(hash-map (:name (:attrs %)) (:text %))
             ($x ".//profile[@typeName='Weapon']//characteristic" item))
        })
     ;; troops "number" of units
     (concat
      ($x "//selection[@type='model']" data)
      ($x "//selection[@type='unit']" data))))




  (:unit-characteristics (first (parse-data (slurp "roster.ros"))))


  (defn read-file [file]
    (parse-data (slurp file)))

  (first (read-file "roster.ros"))

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

  (first data-file)

  (map
   (fn [unit]
     {
      :name     (:name (:unit-name unit))
      :BS       (get-unit-characteristic unit "BS")
      :shooting (odds-characteristic (get-unit-characteristic unit "BS"))
      })
   data-file)

  ;; (data-file)

  ;; ( -main
  ;;   "I don't do a whole lot."
  ;;   [& args])
  )
