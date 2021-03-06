(ns a40kc.fight)

(def spacemarines
  {:name "Tactical Squad",
    :models
    (list {:name "Space Marine",
      :number 4,
      :chars {:ws "3+"
              :ld 7
              :w 2
              :m "6\""
              :save "3+"
              :s 4
              :bs "3+"
              :t 4
              :a 1},
      :weapons
      [{:name "Boltgun",
        :chars
        {:range "24\"",
         :type "Rapid Fire 1",
         :s 4,
         :ap 0,
         :d 1,
         :abilities -}}
       {:name "Frag grenades",
        :chars
        {:range "6\"",
         :type "Grenade D6",
         :s 3,
         :ap 0,
         :d 1,
         :abilities "Blast."}}
       {:name "Krak grenades",
        :chars
        {:range "6\"",
         :type "Grenade 1",
         :s 6,
         :ap -1,
         :d "D3",
         :abilities -}}
       {:name "Bolt pistol",
        :chars
        {:range "12\"",
         :type "Pistol 1",
         :s 4,
         :ap 0,
         :d 1,
         :abilities -}}]})})

(def conscripts
  {:name "Conscripts",
   :models
   (list {:name   "Conscript",
     :number 20,
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
  (println char)
  (read-string (str (clojure.string/replace char "+" ""))))

(defn hit-probs [u]
  (-> u :chars :bs parse-char d6-odds))

;;woundRatio = targetToughness / weaponStrength;
(defn odds-wounding [S T]
  (let [comparison (- S T)]
    (d6-odds (- 4 comparison))))

(defn wound-probs [w u]
  (let [strength (-> w :chars :s parse-char)
        toughess (-> u :chars :t parse-char)]
    (odds-wounding strength toughess)))

(defn save-prob [u w]
  (-> u :chars :s parse-char (- (:ap (:chars w)))  d6-odds))


(def fight
  {:attacker "Conscripts"
   :defender "Tactical Squad"
   :wounds [{:weapon      "lasgun"
             :wounds      0
             :wounds-rf   0
             :wounds-frsf 0}]})


;; TODO: fight units and fight models
(defn fight-units [u1 u2]
  {:attacker (:name u1)
   :defender (:name u2)
   :weapons (flatten (for [m (:models u1)]
                       (for [m2 (:models u2)]
                         (for [w (:weapons m)]
                           {:weapon (:name w)
                            ;; todo: count attacks per weapons
                            :wounds (format "%.2f"
                                            (* (:number m)
                                               (hit-probs m)
                                               (wound-probs w m2)
                                               (- 1 (save-prob m2 w))))}))))})


(comment

  ;; Wounds Inflicted = Attacks * (Hit Probability) * (Wound Probability) * (Save Probability)
  ;; *1D6=3.5 *1D3=2 *2D6 pick highest= 4.47 (For Fussion damage at half range)
  (defn wounds-inflicted [u1 u2]
    (for [w (:weapons u1)]
      {:weapon            (:name w)
       :number            (:number u1)
       :hits-prob         (hit-probs u1)
       :wound-probs       (wound-probs w u2)
       :save-probs        (- 1 (save-prob u2 w))
       :wounds            (format "%.2f" (* (:number u1) (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))
       :wounds-rapid-fire (format "%.2f" (* (* 2 (:number u1)) (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))
       :wounds-ffs        (format "%.2f" (* (* 4 (:number u1)) (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))
       :wounds-grenades   (format "%.2f" (* 6 (hit-probs u1) (wound-probs w u2) (- 1 (save-prob u2 w))))}))

  (def model1 (first (:models spacemarines)))
  (def model2 (first (:models conscripts)))

  (def weapon (first (:weapons model1)))





  (* (:number model1) (hit-probs model1) (wound-probs weapon model2) (- 1 (save-prob model2 weapon)))


  (fight-units spacemarines conscripts)

  )
