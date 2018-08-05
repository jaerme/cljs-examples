(ns app.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

;; elm-architecture-tutorial/examples/01-button.elm
(def counter (r/atom 0))

(defn button []
  [:div
   [:button {:on-click #(swap! counter dec)} "-"]
   [:div @counter]
   [:button {:on-click #(swap! counter inc)} "+"]])

;; elm-architecture-tutorial/examples/02-field.elm
(def content (r/atom ""))

(defn field []
  [:div
   [:input {:placeholder "Text to reverse"
            :on-change #(reset! content (-> % .-target .-value))}]
   [:div (reverse @content)]])

;; elm-architecture-tutorial/examples/03-form.elm
(def model (r/atom {:password "" :password-again ""}))

(defn view-validation []
  (let [{:keys [password password-again]} @model
        [color message] (if (= password password-again)
                          ["green" "OK"]
                          ["red" "Passwords do not match!"])]
    [:div {:style {:color color}} message]))

(defn form []
  [:div
   [:input {:type "text" :placeholder "Name"}]
   [:input {:type "password" :placeholder "Password" :on-change #(swap! model assoc :password (-> % .-target .-value))}]
   [:input {:type "password" :placeholder "Re-enter Password" :on-change #(swap! model assoc :password-again (-> % .-target .-value))}]
   [view-validation]])

;; elm-architecture-tutorial/examples/04-random.elm
(def die-face (r/atom 1))

(defn get-random []
  (-> (range 1 7)
      (rand-nth)))

(defn random []
  [:div
   [:h1 @die-face]
   [:button {:on-click #(swap! die-face get-random)} "Roll"]])

;; elm-architecture-tutorial/examples/05-http.elm
(def random-gif (r/atom {:topic "cats" :image-url ""}))

(defn get-random-gif [topic]
  (go (let [response (<! (http/get (str "https://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC&tag=" topic)
                                   {:with-credentials? false}))
            image (:image_url (:data (:body response)))]
        (swap! random-gif assoc :image-url image))))

(defn http []
  (let [{:keys [topic image-url]} @random-gif]
    [:div
     [:h2 topic]
     [:button {:on-click #(get-random-gif topic)} "More Please!"]
     [:br]
     [:img {:src image-url :alt "waiting.gif"}]]))

;; elm-architecture-tutorial/examples/06-clock.elm
(def timer (r/atom (js/Date.)))
(def time-updater (js/setInterval
                   #(reset! timer (js/Date.)) 1000))

(defn clock []
  (let [time-str (-> @timer .toTimeString (clojure.string/split " ") first)
        seconds (-> time-str (clojure.string/split ":") last)
        handX (+ 50 (* 40 (js/Math.cos seconds)))
        handY (+ 50 (* 40 (js/Math.sin seconds)))]
    [:svg {:view-box "0 0 100 100" :width "300px"}
     [:circle {:cx "50" :cy "50" :r "45" :fill "#0B79CE"}]
     [:line {:x1 "50" :y1 "50" :x2 handX :y2 handY  :stroke "#023963"}]]))

(defn examples []
  [:div
   [button]
   [:br]
   [field]
   [:br]
   [form]
   [:br]
   [random]
   [:br]
   [http]
   [:br]
   [clock]])

(defn ^:export main []
  (r/render-component [examples] (js/document.getElementById "app")))
