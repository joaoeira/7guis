(ns roam-counter
  (:require
   [reagent.core :as r]))
   
(def counter-atom (r/atom 0))

(defn counter []
  [:div 
    @counter-atom
   	[:input {:type "button", :value "Count"
             :on-click #(swap! counter-atom inc)}]
])
