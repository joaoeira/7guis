(ns roam-flight-booker
  (:require
   [reagent.core :as r]))
 
(def outgoing-date (r/atom "")) 
(def returning-date (r/atom "" ))

(def outgoing-bg (r/atom "white"))
(def returning-bg (r/atom "white"))

(def outgoing-date-valid? (r/atom false))
(def returning-date-valid? (r/atom false))
(def button-disabled? (r/atom true))
(def return-flight-disabled? (r/atom true))

(def booking-confirmation (r/atom ""))

(defn length-validation [input]
  (= (map count (clojure.string/split input #"\.")) '(2 2 4))
)

(defn character-validation 
;; a pure clojure option would use read-string and number? 
[input]
  (=
    (count 
      (filter #(js/isNaN (js/Number %))
        (filter #(and (not= %".")(not= %""))
          (clojure.string/split input #""))))
          0))

(defn time-travel? [outgoing, returning]
  (let [
    outgoing-parsed (clojure.string/split outgoing #"\.")
    returning-parsed (clojure.string/split returning #"\.")

    day [(get outgoing-parsed 0) (get returning-parsed 0)]
    month [(get outgoing-parsed 1) (get returning-parsed 1)]
    year [(get outgoing-parsed 2) (get returning-parsed 2)]
  ]
  (or
    (> (js/Number (get day 0)) (js/Number (get day 1)))
    (or 
      (> (js/Number (get month 0)) (js/Number (get month 1)))
      (> (js/Number (get year 0)) (js/Number (get year 1)))))
  )
)

(defn activate-button? []
  
  (if (true? @return-flight-disabled?)
      (reset! button-disabled? (false? @outgoing-date-valid?))
      (reset! button-disabled? (false? (and 
        (and 
          @outgoing-date-valid? 
          @returning-date-valid?) 
          (false? (time-travel? @outgoing-date @returning-date)))))
  )
)

(defn change-date-validity-state [id, value]
  
  (if (= id "outgoing")
              (reset! outgoing-date-valid? value)
              (reset! returning-date-valid? value))
)

(defn set-bg-color [id, color]
(if (= id "outgoing")
  (reset! outgoing-bg color)
  (reset! returning-bg color)
)
)
;; function to set valid date state, too many repeats
(defn validate-date [evt]
  (let [
    input (-> evt .-target .-value)
    id (-> evt .-target .-id)
  ]
  
  (if (= id "outgoing")
      (reset! outgoing-date input)
      (reset! returning-date input)
  )
  
  (if (character-validation input)
      (do
        (set-bg-color id "white")
        (if (length-validation input)
          (change-date-validity-state id true)
          (change-date-validity-state id false))
      )
      (do
        (change-date-validity-state id false)
        (set-bg-color id "red")
        )
  )
  )
)

(defn display-message [] 
  
  (if (true? @return-flight-disabled? )
    (reset! booking-confirmation (str "You have booked a one-way flight on " @outgoing-date))  
    (reset! booking-confirmation (str "You have booked a one-way flight from " @outgoing-date " to " @returning-date)) 
  )
)

(defn flight-booker []
  
  [:div {:style {:display "flex" :flex-flow "column" :width "250px"}}
    [:select {:id "option" :on-change #(do (reset! return-flight-disabled? (= @return-flight-disabled? false)) (activate-button?))}
      [:option {:value "one-way"} "One-way flight"]
      [:option {:value "two-way"} "Return flight"]]
    [:input {
      :type "text", 
      :id "outgoing", 
      :style {:backgroundColor @outgoing-bg} 
      :value @outgoing-date 
      :on-change (fn [evt] (validate-date evt) (activate-button?))
      }]
    
    [:input {
      :type "text",
      :id "returning",
      :style {:backgroundColor @returning-bg} 
      :value @returning-date 
      :disabled @return-flight-disabled? 
      :on-change (fn [evt] (validate-date evt) (activate-button?))
    }]
    
    [:input {:type "button" :value "Book"  :disabled @button-disabled? :on-click display-message}]
    [:p#booking-confirmation @booking-confirmation]
  ]
)
