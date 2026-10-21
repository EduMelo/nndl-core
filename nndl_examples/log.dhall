-- Registra uma mensagem depois que cada action termina.
-- `var` deixa `${img}` para o runner substituir.

let nndl = ../dhall/package.dhall

let img = nndl.var "img"

in  nndl.Nndl::{
    , entryStep = Some "oldPoster"
    , steps =
      [ nndl.Step::{
        , name = "oldPoster"
        , elements =
          [ nndl.Element::{ name = "imagePost", matchExp = "button.image-post" }
          , nndl.Element::{ name = "fileInput", matchExp = "input[type=file]" }
          ]
        , actions =
          [ nndl.Action::{
            , kind = nndl.elementClick "imagePost"
            , log = Some "Clicked on image post button"
            }
          , nndl.Action::{
            , kind = nndl.uploadFile "fileInput" img
            , log = Some ("File uploaded successfully: " ++ img)
            }
          ]
        }
      ]
    }
