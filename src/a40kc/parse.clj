(ns a40kc.parse
  (:require
   [clojure.zip :as zip]
   [clojure.xml :as xml]
   [clojure.java.io :refer [file] :as io]

   [clojure.data.zip :as zd]
   [clojure.data.zip.xml :as zx])
   (:import (java.io File)
           [java.util.zip   ZipInputStream]
           )

   )

(defn unzip-file
  "uncompress zip archive.
  `input` - name of zip archive to be uncompressed.
  `output` - name of folder where to output."
  [input output]
  (with-open [stream (-> input io/input-stream ZipInputStream.)]
    (loop [entry (.getNextEntry stream)]
      (if entry
        (let [save-path (str "./" output)
              out-file (File. save-path)]
          (if (.isDirectory entry)
            (if-not (.exists out-file)
              (.mkdirs out-file))
            (let [parent-dir (File. (.substring save-path 0 (.lastIndexOf save-path (int File/separatorChar))))]
              (if-not (.exists parent-dir) (.mkdirs parent-dir))
              (clojure.java.io/copy stream out-file)))
          (recur (.getNextEntry stream))))))
  (slurp output)
  )


(defn parse-char [char]
  (cond
    (and (> (count char) 1) (clojure.string/includes? char "-")) (- (read-string (str (clojure.string/replace char "-" ""))))
    (clojure.string/includes? char "+") (read-string (str (clojure.string/replace char "+" "")))
    :else
    char))

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

(defn weapons [model]
  (let [weapons (zx/xml->
                 model
                 :selections
                 :selection
                 :profiles
                 :profile
                 (zx/attr= :typeName "Weapon"))]
    ;; (map (fn [e]
    ;;        {:name (attrs-name (first e))
    ;;         :chars
    ;;         (->
    ;;          (map #(attrs-and-content (first %)) (zx/xml-> e :characteristics :characteristic))
    ;;          (keywordize-chars))}) weapons)

    (reduce (fn [result w]
              (conj result {:name (attrs-name (first w))
                            :chars
                            (->
                             (map #(attrs-and-content (first %)) (zx/xml-> w :characteristics :characteristic))
                             (keywordize-chars))

                            })

              )


            []
            weapons
            )

    ;;(map #(attrs-name (first %)) weapons)

   ))

(defn characteristics [model]
  (let [chars (zx/xml-> model
                        :profiles
                        :profile
                        :characteristics
                        :characteristic)]
    (->
     (map #(attrs-and-content (first %)) chars)
     (keywordize-chars))))

(defn unit-models [unit]
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

(defn models [force]
  (zx/xml->
   force
   :selections
   :selection
   (zx/attr= :type "model")))


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


(defn edn [forces]
  (for [f forces]
    {:force-name (attrs-name  (first f))
     :models (for [m (models f)]
               {:name    (attrs-name (first m))
                :number  (read-string (:number (:attrs (first m))))
                :chars   (characteristics  m)
                :weapons (weapons m)})

     :units      (for [u (units f)]
                   {:name
                    (attrs-name (first u))
                    :models (for [m (unit-models u)]
                              {:name    (attrs-name (first m))
                               :number  (read-string (:number (:attrs (first m))))
                               :chars   (characteristics  u)
                               :weapons (weapons m)})})}))

(defn file->edn [file]
  (-> file
   zipper
   forces
   edn))


(defn parse [file-rosz]
  ;; TODO: check extension file
  (let [new-filename (clojure.string/join "" (drop-last file-rosz))
        file (unzip-file file-rosz new-filename)]
    (file->edn file)))


(comment

  (parse "spacemarines.rosz")



  (unzip-file "spacemarines.rosz" "spacemarines.ros")

  (clojure.string/join "" (drop-last "hello"))

  (def file (slurp "spacemarines.ros"))

  (def zipper (zipper file))

  (def forces (forces zipper))


  (parse "spacemarines.rosz")


 ,)
