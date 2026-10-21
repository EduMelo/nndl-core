let nndl = ../dhall/package.dhall

let user = nndl.var "userHandle"

let password = nndl.var "userPassword"

let storer
    = "dev.edumelo.grfnkl.socialplataforms.services.reddit.RedditUserCookiesStorer"

let retriever
    = "dev.edumelo.grfnkl.socialplataforms.services.reddit.RedditUserCookiesRetriever"

in  nndl.Nndl::{
    , steps =
      [ nndl.Step::{
        , name = "storeCookies"
        , elements =
          [ nndl.Element::{
            , name = "loginMark"
            , matchExp = "#USER_DROPDOWN_ID #email-collection-tooltip-id svg"
            }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.storeCookies storer [ user ]
            , landmarks = nndl.all [ nndl.elementLandmark "loginMark" ]
            }
          ]
        }
      , nndl.Step::{
        , name = "bootstrap"
        , elements =
          [ nndl.Element::{ name = "inputLogin", matchExp = "#loginUsername" }
          , nndl.Element::{
            , name = "loginMark"
            , matchExp = "#USER_DROPDOWN_ID #email-collection-tooltip-id svg"
            }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://www.reddit.com/settings/"
            , landmarks = nndl.all [ nndl.elementLandmark "inputLogin" ]
            }
          , nndl.Action::{
            , kind = nndl.loadCookies retriever [ user ]
            }
          , nndl.Action::{
            , kind = nndl.goto "https://www.reddit.com/settings/"
            , landmarks = nndl.all [ nndl.cooldown 15000 ]
            }
          , nndl.Action::{
            , kind = nndl.goto "https://www.reddit.com/settings/"
            , landmarks =
                nndl.oneOf
                  [ nndl.fork "loginMark" "loginSuccessfull"
                  , nndl.fork "inputLogin" "login"
                  ]
            }
          ]
        }
      , nndl.Step::{
        , name = "login"
        , elements =
          [ nndl.Element::{ name = "inputLogin", matchExp = "#loginUsername" }
          , nndl.Element::{
            , name = "inputPassword"
            , matchExp = "#loginPassword"
            }
          , nndl.Element::{
            , name = "submitButton"
            , matchExp = ".AnimatedForm__submitButton"
            }
          , nndl.Element::{
            , name = "loginButton"
            , matchExp = "a[href^='https://www.reddit.com/login']"
            }
          , nndl.Element::{
            , name = "loginMark"
            , matchExp = "#USER_DROPDOWN_ID #email-collection-tooltip-id svg"
            }
          , nndl.Element::{
            , name = "invalidLogin"
            , matchExp = "fieldset.m-invalid"
            }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://www.reddit.com/login"
            , landmarks = nndl.all [ nndl.elementLandmark "inputLogin" ]
            }
          , nndl.Action::{
            , kind = nndl.fillInput "inputLogin" user
            , landmarks = nndl.all [ nndl.elementLandmark "inputLogin" ]
            }
          , nndl.Action::{
            , kind = nndl.fillInput "inputPassword" password
            , landmarks = nndl.all [ nndl.elementLandmark "inputPassword" ]
            }
          , nndl.Action::{
            , kind = nndl.elementClick "submitButton"
            , timeout = Some 200
            , landmarks =
                nndl.oneOf [ nndl.fork "loginMark" "loginSuccessfull" ]
            }
          ]
        }
      ]
    }
