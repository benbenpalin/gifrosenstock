(ns gifrosenstock.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [gifrosenstock.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            ;[cljsjs.clipboard :as c]
            )
  (:import goog.History))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri} title]])

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-dark.bg-primary
       [:ul.nav.navbar-nav.nav-font
        [nav-link "#/" " Home" :home]
        [nav-link "#/about" " About" :about]]])))

(def gifmap {:0 "https://media.giphy.com/media/3o7TKJcneY8JkZNYBi/giphy.gif"
             :1 "https://j.gifs.com/Wnx2Lx.gif"
             :2 "https://media.giphy.com/media/3o7TKyohNLnEhOIVkA/giphy.gif"
             :3 "https://thumbs.gfycat.com/DisastrousCleverHippopotamus-size_restricted.gif"
             :4 "https://thumbs.gfycat.com/SatisfiedDemandingHoneycreeper-size_restricted.gif"
             :5 "https://media.giphy.com/media/7fTu979vElNLy/giphy.gif"
             :6 "https://media.giphy.com/media/jyxMqAX4iEf2E/giphy.gif"
             :7 "https://media.giphy.com/media/s2GNMNR46c1Ik/giphy.gif"
             :8 "https://media.giphy.com/media/2GmIZ1o9W1l2U/giphy.gif"
             :9 "https://media.giphy.com/media/aMOaQQDui4fUQ/giphy.gif"
             :10 "https://media.giphy.com/media/KU0GfYFWUr5Cg/giphy.gif"
             :11 "https://media.giphy.com/media/mqL2zmcTRTAA0/giphy.gif"
             :12 "https://media.giphy.com/media/UpY0Ozp2urdOE/giphy.gif"
             :13 "https://media.giphy.com/media/V4hfThqxCTxgk/giphy.gif"
             :14 "https://media.giphy.com/media/ZaT7bNOCAnwju/giphy.gif"
             :15 "https://media.giphy.com/media/fG8MnBSXg00yA/giphy.gif"
             :16 "https://media.giphy.com/media/gVpHfVQfed0kg/giphy.gif"
             :17 "https://media.giphy.com/media/tT4CUC4jgCHkc/giphy.gif"})

(def keyvec (shuffle (range (count gifmap)))) ;random vector of keys to the GIFs

(defn new-gif
  "returns a random gif from the gifmap"
  [n] (if (< n (count keyvec))
        (gifmap (keyword (str (get keyvec n))))
        (gifmap (keyword (str (get keyvec (rem n (count keyvec))))))))

(def gif-num (r/atom 0)) ;initial atom for gif num

(defn home-page []
  [:div.container
   [:div.row
    [:h1.page-title "GIFROSENSTOCK.COM!!!!!!" ]]
   [:div.row
    [:img {:src (new-gif @gif-num)}]]
   [:div.row.gif-row {:style {:padding-top "30px"}}
    [:input.gifbutton {:type "button" :value "NEW GIF!!!"
                       :on-click #(swap! gif-num inc)}]]])

;----------------------------------------

(defn about-page []
  [:div.container.about-page
   [:div.row>div.col-sm-12
    [:h1.page-title "About Jeff!"]]
   [:div.row
    [:p "Jeff Rosenstock is a living punk rock legend. He began as the singer/songwriter of the early 2000's ska
     band "
     [:a.about-link {:href "http://quoteunquoterecords.com/qur023.htm"} "The Arrogant Songs Of Bitches."]
     " After feeling overwhelmed by the capitalist hellhole that is the music
     industry, Jeff began making his own music as "
     [:a.about-link {:href "http://quoteunquoterecords.com/qur002.htm"} "Bomb The Music Industry!"]
     " and releasing it for free. BTMI!
     garnered critical acclaim and a dedicated fanbase, and continued to rise in popularity until their
     breakup in 2014. From start to finish, they released all albums for free, and alway played all ages shows
     that cost less than $10. After BTMI! broke up, Jeff went solo. "
     [:a.about-link {:href "http://www.quoteunquoterecords.com/qur088.htm"} "Jeff Rosenstock"]
     " has released 3 albums, two
     of which are full band affairs, and continues to get massive love from citics and fans alike."]]
   [:br]
   [:div.row>div.col-sm-12
    [:h1.page-title "About Ben!"][:br]]
   [:div.row
    [:p "Ben Palin is a clojure developer and punk fan currently living in Chicago. He works at Guaranteed Rate and
     regularly goes to see Jeff play. Check out his "
     [:a.about-link {:href "https://github.com/benbenpalin"} "github"]
     " where you can find the code for this site, as well as many of his other projects."]]
   [:br]
   [:div.row>div.col-sm-12
    [:h1.page-title "About GIF!"][:br]]
   [:div.row
    [:p "GIF is a file type that stands for Graphic Interchange Format. There are great GIFs all over the internet, start"
     [:a.about-link {:href "https://giphy.com/create/gifmaker"} " GIFing!"]]]])

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
