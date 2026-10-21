-- Preenche uma busca, confirma com Enter e escolhe a primeira sugestão.

let nndl = ../dhall/package.dhall

let query = nndl.var "query"

in  nndl.Nndl::{
    , entryStep = Some "search"
    , steps =
      [ nndl.Step::{
        , name = "search"
        , elements =
          [ nndl.Element::{ name = "search", matchExp = "input#q" }
          , nndl.Element::{ name = "suggestion", matchExp = "li.suggestion" }
          , nndl.Element::{
            , name = "menu"
            , matchExp = "button.menu"
            , ignoreRoot = True
            }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.goto "https://notes.example"
            , landmarks = nndl.all [ nndl.elementLandmark "search" ]
            }
          , nndl.Action::{ kind = nndl.clearInput "search" }
          , nndl.Action::{
            , kind = nndl.fillInput "search" query
            , landmarks = nndl.all [ nndl.elementLandmark "search" ]
            }
          , nndl.Action::{
            , kind =
                nndl.sendKey
                  nndl.SendKey::{ key = "ENTER", targetElement = Some "search" }
            }
          , nndl.Action::{
            , kind = nndl.hover "menu"
            , waitCondition = [ "displayed" ]
            , requirement = Some nndl.Requirement::{ type = "nonRequired" }
            }
          , nndl.Action::{
            , kind =
                nndl.elementMark
                  nndl.ElementMark::{ element = "suggestion", mark = "candidate" }
            }
          , nndl.Action::{
            , kind =
                nndl.elementPaint
                  nndl.ElementPaint::{ element = "suggestion", color = "#ff00aa" }
            }
          , nndl.Action::{
            , kind = nndl.elementClick "suggestion"
            , timeout = Some 15
            , waitCondition = [ "clickable" ]
            , limitRequirement = True
            , positionBefore = Some nndl.Position::{ x = Some 0, y = Some 80 }
            , landmarks =
                nndl.all
                  [ nndl.Landmark.Element
                      { element = "suggestion"
                      , timeout = Some 10
                      , waitCondition = [ "visible" ]
                      }
                  ]
            }
          ]
        }
      ]
    }
