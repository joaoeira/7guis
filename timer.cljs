(ns roam-timer
  (:require [reagent.core :as r]))

(def elapsed (r/atom 0))
(def slider-value (r/atom 100))
(def meter (r/atom 0))
(def max-time (r/atom 100))
(defonce interval (atom 0))


(defn update-timer []
  (if (< @elapsed @max-time)
  (do
    (swap! elapsed inc)
    (reset! meter (/ @elapsed @max-time))
  ) nil)
)

(defn handle-slider [value]
  (do 
    
    (reset! slider-value value)
    (reset! max-time value)
    (if (= value "0")
        (reset! meter 1)
        (reset! meter (/ @elapsed @max-time))   
    )
  )

)

(defn reset []
  
    (js/clearInterval @interval)
    
    (reset! interval (js/setInterval update-timer 1000))
    (reset! elapsed 0)
    (if (= @slider-value "0")
      (reset! meter 1)
      (reset! meter 0)
    )
)

(defn timer []
  
  [:div 
  [:div {:style {:display "flex"}}
  [:p {:style {:margin-right "10px"}} "Elapsed Time:" ]
  [:meter {:id "meter", :value @meter}]]
  [:p#elapsed (str @elapsed "s")]
  [:input {:type "range", :min "0", :max 100, :value @slider-value :on-input (fn [evt] (handle-slider (-> evt .-target .-value)))}]
  [:button {:type "button" :on-click reset} "Reset"]
  ;;[:p @slider-value]
  ]
)
