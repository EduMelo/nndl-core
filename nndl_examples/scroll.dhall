-- Percorre os cards de uma busca e extrai título e preço de cada um.

let nndl = ../dhall/package.dhall

let titleFactory = "dev.example.catalog.TitleFactory"

let priceFactory = "dev.example.catalog.PriceFactory"

in  nndl.Nndl::{
    , entryStep = Some "results"
    , steps =
      [ nndl.Step::{
        , name = "results"
        , elements =
          [ nndl.Element::{ name = "card", matchExp = "#results .card" }
          , nndl.Element::{ name = "next", matchExp = "button.next" }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://catalog.example/search?q=notebook"
            , landmarks = nndl.all [ nndl.elementLandmark "card" ]
            }
          , nndl.Action::{
            , kind =
                nndl.loop
                  nndl.Loop::{
                  , execute = "readCard"
                  , iterationScope = nndl.pageElement "card"
                  , scrollCount = Some 8
                  , limit = Some 30
                  , ignoreMaxLoopCountException = True
                  }
            , landmarks = nndl.all [ nndl.elementLandmark "next" ]
            }
          ]
        , subSteps =
          [ nndl.NestedStep::{
            , name = "readCard"
            , elements =
              [ nndl.Element::{ name = "title", matchExp = "h2" }
              , nndl.Element::{ name = "price", matchExp = ".price" }
              ]
            , actions =
              [ nndl.Action::{
                , kind =
                    nndl.extract
                      nndl.Extract::{
                      , factory = titleFactory
                      , targetElement = Some "title"
                      }
                , limitRequirement = True
                }
              , nndl.Action::{
                , kind =
                    nndl.extract
                      nndl.Extract::{
                      , factory = priceFactory
                      , targetElement = Some "price"
                      }
                , onEach = Some 2
                }
              ]
            }
          ]
        }
      ]
    }
