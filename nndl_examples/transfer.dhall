-- Baixa um relatório, envia o arquivo de volta e segue para done ou retry.
-- O step watch roda em paralelo.

let nndl = ../dhall/package.dhall

in  nndl.Nndl::{
    , entryStep = Some "fetch"
    , asynchronousSteps = [ "watch" ]
    , steps =
      [ nndl.Step::{
        , name = "fetch"
        , elements =
          [ nndl.Element::{
            , name = "saveButton"
            , matchExp = "button.save"
            , shadowPath = [ "#app", "files-panel" ]
            }
          , nndl.Element::{ name = "fileInput", matchExp = "input[type=file]" }
          , nndl.Element::{ name = "savedMark", matchExp = ".saved" }
          , nndl.Element::{ name = "errorMark", matchExp = ".error" }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://files.example/inbox"
            , landmarks = nndl.all [ nndl.elementLandmark "saveButton" ]
            }
          , nndl.Action::{
            , kind = nndl.downloadFile "https://files.example/report.csv"
            , landmarks = nndl.all [ nndl.cooldown 2000 ]
            }
          , nndl.Action::{
            , kind =
                nndl.uploadFile
                  "fileInput"
                  "https://files.example/inbox"
            }
          , nndl.Action::{
            , kind = nndl.actionTriggerer "inbox.refresh" [ "report.csv" ]
            }
          , nndl.Action::{
            , kind = nndl.elementClick "saveButton"
            , landmarks =
                nndl.oneOf
                  [ nndl.fork "savedMark" "done"
                  , nndl.fork "errorMark" "retry"
                  ]
            }
          ]
        }
      , nndl.Step::{
        , name = "done"
        , elements =
          [ nndl.Element::{ name = "close", matchExp = "button.close" } ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.elementClick "close"
            , landmarks = nndl.all [ nndl.elementLandmark "close" ]
            }
          ]
        }
      , nndl.Step::{
        , name = "retry"
        , elements =
          [ nndl.Element::{ name = "errorMark", matchExp = ".error" } ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://files.example/inbox"
            , landmarks = nndl.all [ nndl.elementLandmark "errorMark" ]
            }
          ]
        }
      , nndl.Step::{
        , name = "watch"
        , elements =
          [ nndl.Element::{ name = "status", matchExp = "#status" } ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://files.example/status"
            , landmarks = nndl.all [ nndl.cooldown 5000 ]
            }
          ]
        }
      ]
    }
