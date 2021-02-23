(ns a40kc.core
  (:require [a40kc.parse :as parse])
  (:gen-class))


(def file (slurp "spacemarines.ros"))

(def zipper (parse/zipper file))

(def forces (parse/forces zipper))

(defn edn [forces]
  (for [f forces]
    {:force-name (parse/attrs-name  (first f))
     :units      (for [u (parse/units f)]
                   {:name
                    (parse/attrs-name (first u))
                    :models (for [m (parse/models u)]
                              {:name    (parse/attrs-name (first m))
                               :number  (:number (:attrs (first m)))
                               :chars   (parse/characteristics  m)
                               :weapons (parse/weapons m)})})}))

(defn file->edn [file]
  (-> (slurp file)
   parse/zipper
   parse/forces
   edn))


(comment



  (count (first ))
  (map :tag (flatten (parse/forces zipper)))






  (parse/units (first forces))

  (parse/models zipper)

  (for [f forces]
    (for [u (parse/units f)]
      f)
    )




  (def u1
    (parse-units (slurp "roster.ros")))

  (def u2
    (parse-units (slurp "roster.ros")))

  (fight u1 u2)
  )
