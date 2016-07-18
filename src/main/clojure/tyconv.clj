(ns tyconv.core
  (:require [clojure.walk :as walk])
  (:import [com.droitfintech.core BucketedMap]
           [com.droitfintech.core.regulatory Tenor]))

(defn- tenor-like? [s]
  (or (and (map? s)
           (contains? s "multiplier")
           (contains? s "period")
           (number? (get s "multiplier"))
           (contains? #{"D" "W" "M" "Y" "T"} (get s "period")))
      (and (string? s)
           (re-matches #"[0-9]+[DWMYT]" s))))

(defn- make-tenor [s]
  (cond (map? s)
        (Tenor. (get s "multiplier")
                (get s "period"))
        (string? s)
        (Tenor/makeTenor s)))

(defn- date-like? [s]
  (and (string? s)
       (not (nil? (re-seq #"\d{4}-\d{2}-\d{2}" s)))))

(defn- read-date [s]
  (if-let [[_ v] (re-find #"^(.+)[+]\d+$" s)]
    (clojure.instant/read-instant-date v)
    (clojure.instant/read-instant-date s)))

(defn- number-like? [s]
  (and (string? s)
       (not (nil? (re-matches #"[0-9]+" s)))))

(defn- bool-like? [s]
  (and (string? s)
       (or (= s "true")
           (= s "false"))))

(defn- clojure-to-java [m]
  (walk/postwalk
   #(cond (tenor-like? %)
          (make-tenor %)
          (map? %)
          (java.util.HashMap. %)
          (keyword? %)
          (name %)
          (date-like? %)
          (read-date %)
          (number-like? %)
          (Integer/parseInt %)
          (bool-like? %)
          (read-string %)
          :else
          %)
   m))

(defn- java-to-clojure [o]
  (walk/prewalk
   #(cond (instance? java.util.Map %)
          (into {} %)
          (instance? java.util.Set %)
          (into #{} %)
          (instance? java.util.ArrayList %)
          (into [] %)
          (instance? java.util.LinkedList %)
          (into '() %)
          :else
          %)
   o))

(defn convert [o]
  (-> o java-to-clojure clojure-to-java))

(defn make-bucketed-map [input-map]
  (letfn [(filter-non-map [m]
            (into {} (filter (fn [[k v]] (map? v)) m)))]
    (doto (BucketedMap.)
      (.setUnderlyingMap
       (-> input-map filter-non-map clojure-to-java)))))

(defn un-bucketed-map [hash-map]
  (walk/postwalk (fn [x]
                   (if (= java.util.HashMap (class x))
                     (into {} x)
                     x))
                 hash-map))
