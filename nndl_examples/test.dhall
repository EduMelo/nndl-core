let nndl = ../dhall/package.dhall

in  nndl.Nndl::{
    , entryStep = Some "main"
    , steps =
      [ nndl.Step::{
        , name = "main"
        , elements =
          [ nndl.Element::{ name = "dolar", matchExp = "#root h2" } ]
        , actions =
          [ nndl.Action::{
            , kind =
                nndl.goto
                  "https://site.nomadglobal.com/lps/currency-converter/?currency=USD&initialValue=100&history=true"
            , landmarks = nndl.all [ nndl.elementLandmark "dolar" ]
            }
          , nndl.Action::{
            , kind =
                nndl.extract
                  nndl.Extract::{
                  , factory = "dev.edumelo.grfnkl.service.ValueFactory"
                  , targetElement = Some "dolar"
                  }
            }
          ]
        }
      ]
    }
