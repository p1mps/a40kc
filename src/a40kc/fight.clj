(ns a40kc.fight)

(def spacemarine
  {:model "Space marine",
   :number 9
  :model-chars
  {:ws "3+",
   :ld "6",
   :w "1",
   :m "6\"",
   :save "3+",
   :s "4",
   :bs "3+",
   :t "4",
   :a "1"},
  :weapons
  (list {:name "Bolter",
    :chars
    {:range "24",
     :type "Rapid fire 1",
     :s "4",
     :ap "0",
     :d "1",
     :abilities "-"}}
   )})

(def conscripts
  {:name "Conscripts",
   :models
   (list {:name   "Conscript",
     :number "20",
     :chars
     {:ws 5, :ld 4, :w 1, :m 6, :save 5, :s 3, :bs 5, :t 3, :a 1},
     :weapons
     [{:name "Lasgun",
       :chars
       {:range     24,
        :type      "Rapid",
        :s         3,
        :ap        0,
        :d         1,
        :abilities -}}]})})


(defn d6-odds [n]
  (double (/ (- 7 n) 6)))

(defn parse-char [char]
  (read-string (str (clojure.string/replace char "+" ""))))

(defn hit-probs [u]
  (-> u :model-chars :bs parse-char d6-odds))

;;woundRatio = targetToughness / weaponStrength;
(defn odds-wounding [S T]
  (let [comparison (- S T)]
    (d6-odds (- 4 comparison))))

(defn wound-probs [w u]
  (let [strength (-> w :chars :s parse-char)
        toughess (-> u :model-chars :t parse-char)]
    (odds-wounding strength toughess)))

(defn save-prob [u w]
  (-> u :model-chars :s parse-char (- (:ap (:chars w)))  d6-odds))


;; Wounds Inflicted = Attacks * (Hit Probability) * (Wound Probability) * (Save Probability)
;; *1D6=3.5 *1D3=2 *2D6 pick highest= 4.47 (For Fussion damage at half range)
(defn wounds-inflicted [u1 u2]
  (for [w (:weapons u1)]
    {:weapon (:name w)
     :number (:number u1)
     :hits-prob (hit-probs u1)
     :wound-probs (wound-probs w u2)
     :save-probs (- 1 (save-prob u2 w))
     :wounds (format "%.2f" (* (:number u1) (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))
     :wounds-rapid-fire (format "%.2f" (* (* 2 (:number u1)) (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))
     :wounds-ffs (format "%.2f" (* (* 4 (:number u1)) (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))
     :wounds-grenades (format "%.2f" (* 6 (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))}))

(defn fight [list1 list2]
  (for [u1 list1]
    (for [u2 list2]
      {:unit (:unit u1)
       :u1 (:model u1)
       :u2 (:model u2)
       :wounds (wounds-inflicted u1 u2)})))
