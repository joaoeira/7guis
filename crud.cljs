(ns roam-crud
  (:require [reagent.core :as r]))

(def name-filter (r/atom ""))
(def active-id (r/atom ""))

(def active-name (r/atom ""))
(def active-surname (r/atom ""))

(def data (r/atom {
  :ilkajsd  {:name "Conor" :surname "White-Sullivan"} 
  :asdas {:name "Bardia" :surname "Pourvakil"}}))



;; https://gist.github.com/rboyd/5053955
;; wanted to use (random-uuid) from cljs but couldn't? 
(defn random-uuid [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defn update-input-fields [id]
  (reset! active-name (:name ((keyword id) @data)))
  (reset! active-surname (:surname ((keyword id) @data)))
  (reset! active-id id)
)

(defn select-values [data, name-filter]
  (map 
    (fn [entry] [:option 
      {:id (keys entry)
       :on-click (fn [evt] (update-input-fields (-> evt .-target .-id)))
      } 
      (str (:surname ((first (keys entry)) entry)) ", " (:name ((first (keys entry)) entry)) )])

    (for 
      [[k v] data 
      :when 
      (or 
        (clojure.string/includes? (:name v) name-filter)
        (clojure.string/includes? (:surname v) name-filter)
      )] 
      {k v})))

(defn update-entry [] 
  (if 
    
    (and (> (count ((keyword @active-id) @data)) 0)
    (and 
     (> (count @active-name) 0) 
     (> (count @active-surname) 0)))
  (reset! data (update-in @data [(keyword @active-id)] assoc :name @active-name :surname @active-surname))
  )

)
(defn create-entry [] 
  (if (and (> (count @active-name) 0) (> (count @active-surname) 0))
  (reset! data (update-in @data [(keyword (random-uuid (rand 100)))] assoc :name @active-name :surname @active-surname))
  )
  
)
(defn delete-entry [] 
  (reset! data (dissoc @data (keyword @active-id)))
)


(defn crud []
  
  [:div {:style {:width "500px"}}
  [:label {:for "filter" :style {:margin-right "15px"}} "Filter:"]
  [:input {:type "text" :id "filter" :on-change (fn [evt] (reset! name-filter (-> evt .-target .-value)))}]

  [:div {:style {:display "flex" :flex-flow "row" :margin-top "15px" :justify-content "space-between"}}
    [:select {:multiple "multiple" :size (count @data) :style {:width "45%"}}
      (select-values @data @name-filter)
    ]
  
    [:div {:style {:display "flex" :flex-flow "column"}}
      [:label {:for "name"} "Name:"]
      [:input {:type "text" :id "crud-name" :value @active-name :on-change (fn [evt] (reset! active-name (-> evt .-target .-value)))}]
      [:label {:for "surname"} "Surname:"]
      [:input {:type "text" :id "crud-surname" :value @active-surname :on-change (fn [evt] (reset! active-surname (-> evt .-target .-value)))}]
    ]
  ]
  
  
  [:div {:style {:display "flex" :flex-flow "row" :justify-content "center" :margin-top "20px"}}
    [:input {:type "button" :value "Create" :style {:margin "0 10px"} :on-click create-entry}]
    [:input {:type "button" :value "Update" :style {:margin "0 10px"} :on-click update-entry}]
    [:input {:type "button" :value "Delete" :style {:margin "0 10px"} :on-click delete-entry}]
  ]
  
  ]
)
