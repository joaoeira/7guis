(ns roam-temp-converter
  (:require [reagent.core :as r]))



(def temp (r/atom {:celcius "", :fahrenheit ""}))

(defn to-celcius [x] (/ (- x 32) 1.8))
(defn to-fahrenheit [x] (+ (* x 1.8) 32))

(defn isNaN?
  "Check for NaN"
  [x]
  (true? (== x x))
)

(defn isValid? [evt]
  "Validade input value"
  (and 
    (isNaN? (js/Number (-> evt .-target .-value)))
    (false? (= (-> evt .-target .-value) ""))
    )
)


(defn set-temp-state [celcius fahrenheit]
  (reset! temp {:celcius celcius :fahrenheit fahrenheit})
)

(defn temp-converter 
  "Convert from one unit to the other"
  [evt]
  (if (isValid? evt)
    (if (= (-> evt .-target .-id) "celcius")
      (set-temp-state (-> evt .-target .-value) (to-fahrenheit (-> evt .-target .-value)))  
      (set-temp-state (to-celcius (-> evt .-target .-value)) (-> evt .-target .-value))  
    )
    (if (= (-> evt .-target .-value) "")
      (set-temp-state "" "")
      (set-temp-state (get @temp :celcius) (get @temp :fahrenheit))
  )
))

(defn converter-component []
  [:div
    [:input {:type "text",
             :value (get @temp :celcius),
             :id "celcius",
             :on-change (fn [evt] (temp-converter evt))
            }]
    [:p "Celcius"]
    [:input {:type "text",
             :value (get @temp :fahrenheit),
             :id "fahrenheit",
             :on-change (fn [evt] (temp-converter evt))
            }]
    [:p "Fahrenheit"]
  ]
)
