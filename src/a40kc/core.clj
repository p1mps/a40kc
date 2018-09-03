(ns a40kc.core)

(require '[clojure.xml :as xml])
(require '[clojure.java.io :as io])
(require '[clj-xpath.core :as xpath])

(def xml-file (slurp "Mordians.ros"))

;; (defn parse-xml
;;   []
;;   (->
;;    (io/file xml-file)
;;    (xml/parse)
;;    (xml-seq)))

;; (def xml-file-parsed (parse-xml))

;; (defn get-attrs
;;   [xml]
;;   (get-in xml [:tag]))

;; (defn parse-xpath []
;;   (xpath/xml->doc (slurp "Mordians.ros")))

(defn parse-xml []
  (xpath/$x "//selection[@type='model']//characteristic" xml-file))

(defn -main
  "I don't do a whole lot."
  [& args]
  (print (map #(println %) (parse-xml))))
