(ns a40kc.parse
  (:require
   [clojure.zip :as zip]
   [clojure.xml :as xml]
   [clojure.data.zip :as zd]
   [clojure.data.zip.xml :as zx]))

(defn parse-char [char]
  (read-string (str (clojure.string/replace char "+" ""))))

(defn attrs-name [e]
    (:name (:attrs e)))

(defn attrs-and-content [e]
  [(:name (:attrs e)) (first (:content e))])

(defn content [e]
  (first (:content e)))

(defn keywordize [string]
  (keyword (clojure.string/lower-case string)))


(defn keywordize-chars [chars]
  (reduce (fn [result value]

            (assoc result (keywordize (first value)) (parse-char (second value))))
          {}
          chars))




;; (defn parse-weapons [u]
;;   (concat
;;    (for [weapon (zx/xml-> u :profiles :profile)]
;;                   (let [chars (zx/xml-> weapon :characteristics :characteristic)]
;;                     {:name  (attrs-name (first weapon))
;;                      :chars (keywordize-chars (map #(attrs-and-content (first %)) chars))}))


;;    (for [weapon (zx/xml-> u :selections :selection (zx/attr= :type "upgrade") :selections :selection)]
;;                   (let [chars (zx/xml-> weapon :profiles :profile :characteristics :characteristic)]
;;                     {:name  (attrs-name (first weapon))
;;                      :chars (keywordize-chars (map #(attrs-and-content (first %)) chars))}))))


;; (defn parse-units [file]
;;   (first (for [u (units file)]
;;            (for [m (zx/xml-> u :selections :selection)]
;;              {:unit        (attrs-name (first u))
;;               :model       (attrs-name (first m))
;;               :number      (read-string (:number (:attrs (first m))))
;;               :model-chars (keywordize-chars
;;                             (map #(attrs-and-content (first %)) (zx/xml-> m :profiles :profile :characteristics :characteristic)))
;;               :weapons     (parse-weapons u)
;;               }))))

(defn characteristics [model]
  (let [chars (zx/xml-> model
                        :profiles
                        :profile
                        :characteristics
                        :characteristic)]
    (->
     (map #(attrs-and-content (first %)) chars)
     (keywordize-chars))
   )

  )

(defn models [unit]
  (zx/xml->
   unit
   :selections
   :selection
   ))

(defn upgrades [unit]
  (zx/xml->
   unit
   :selections
   :selection
   (zx/attr= :type "upgrade")))


(defn units [force]
  (zx/xml->
   force
   :selections
   :selection
   (zx/attr= :type "unit")))


(defn forces [zipper]
  (zx/xml->
   zipper
   :roster
   :forces
   :force))


(defn zipper [file]
  (-> (.getBytes file)
      java.io.ByteArrayInputStream.
      xml/parse
      zip/xml-zip))
