(ns gifrosenstock.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [gifrosenstock.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-dark.bg-primary
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "gifrosenstock.com"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Home" :home collapsed?]
         [nav-link "#/about" "About" :about collapsed?]]]])))


;----------------------------------
;HOME PAGE GIF https://thumbs.gfycat.com/DisastrousCleverHippopotamus-size_restricted.gif

(def gifmap {:0 "https://media.giphy.com/media/3o7TKJcneY8JkZNYBi/giphy.gif"
             :1 "http://i.makeagif.com/save/v0GwnN"
             :2 "https://media.giphy.com/media/3o7TKyohNLnEhOIVkA/giphy.gif"
             :3 "https://thumbs.gfycat.com/DisastrousCleverHippopotamus-size_restricted.gif"
             :4 "https://thumbs.gfycat.com/SatisfiedDemandingHoneycreeper-size_restricted.gif"})

(def currentmap gifmap)

(defn new-gif
  "returns a random gif from the gifmap"
  [] (currentmap (keyword (str (rand-int 5)))))




(defn home-page []
  (let [gif (r/atom (new-gif))]
  [:div.container
   [:div.row
    [:div.col-md-12
     [:h1.page-title "GIFROSENSTOCK.COM!!!!!!" ]]]
   [:div.row
    [:div.col-md-12.maingif
     [:img {:src (str js/context @gif)}]]]
   [:div.row
    [:div.col-md-12
     [:input.gifbutton {:type "button" :value "NEW GIF!!!"
              :on-click #(reset! gif new-gif)}] ]]]))


;----------------------------------------

(defn about-page []
  [:div.container
   (when-let [docs (session/get :docs)]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])


(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
