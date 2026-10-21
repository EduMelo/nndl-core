-- Types for an NNDL recipe.
--
-- `var "userHandle"` normalizes to the text `${userHandle}`. The runner still
-- substitutes that text from a ContextAdapter when the steps are loaded.
-- Dhall would otherwise treat `${...}` inside a double-quoted string as
-- interpolation.
--
-- `subSteps` is one level deep. A nested step has no further subSteps.
--
-- dhall-to-json drops the name of a union alternative and emits only the
-- payload. Each action payload therefore carries `type`, and the helpers
-- below set it. A landmark list needs no tag: a fork has `fork`, a cooldown
-- has `millis`, and an element landmark has `element`.

let var
    : Text -> Text
    = \(name : Text) -> "\${" ++ name ++ "}"

let Element =
      { Type =
          { name : Text
          , matchExp : Text
          , ignoreRoot : Bool
          , shadowPath : List Text
          }
      , default = { ignoreRoot = False, shadowPath = [] : List Text }
      }

let Landmark =
      < Element :
          { element : Text
          , timeout : Optional Natural
          , waitCondition : List Text
          }
      | Cooldown : { millis : Natural }
      >

let Fork =
      { Type =
          { element : Text
          , fork : Text
          , timeout : Optional Natural
          , waitCondition : List Text
          }
      , default =
          { timeout = None Natural, waitCondition = [] : List Text }
      }

let Landmarks = < All : List Landmark | OneOf : List Fork.Type >

let IterationScope =
      < PageElement : { element : Text } | Values : { values : List Text } >

let Loop =
      { Type =
          { execute : Text
          , iterationScope : IterationScope
          , observerClass : Optional Text
          , fill : Optional Text
          , executeConditions : Optional Text
          , autoScroll : Bool
          , scrollCount : Optional Natural
          , limit : Optional Natural
          , ignoreMaxLoopCountException : Bool
          , throwTimeout : Optional Bool
          , timeoutWait : Optional Natural
          }
      , default =
          { observerClass = None Text
          , fill = None Text
          , executeConditions = None Text
          , autoScroll = True
          , scrollCount = None Natural
          , limit = None Natural
          , ignoreMaxLoopCountException = False
          , throwTimeout = None Bool
          , timeoutWait = None Natural
          }
      }

let SendKey =
      { Type =
          { key : Text, targetElement : Optional Text, ignoreRoot : Bool }
      , default = { targetElement = None Text, ignoreRoot = False }
      }

let Extract =
      { Type =
          { factory : Text
          , targetElement : Optional Text
          , fromClipboard : Bool
          }
      , default = { targetElement = None Text, fromClipboard = False }
      }

let ElementPaint =
      { Type = { element : Text, color : Text, ignoreRoot : Bool }
      , default = { ignoreRoot = False }
      }

let ElementMark =
      { Type = { element : Text, mark : Text, ignoreRoot : Bool }
      , default = { ignoreRoot = False }
      }

let ActionKind =
      < Goto : { type : Text, url : Text }
      | StoreCookies : { type : Text, storer : Text, storerParams : List Text }
      | LoadCookies :
          { type : Text, retriever : Text, retrieverParams : List Text }
      | FillInput : { type : Text, element : Text, value : Text }
      | ClearInput : { type : Text, element : Text }
      | ElementClick : { type : Text, element : Text }
      | Hover : { type : Text, element : Text }
      | SendKey : SendKey.Type //\\ { type : Text }
      | Loop : Loop.Type //\\ { type : Text }
      | Extract : Extract.Type //\\ { type : Text }
      | ElementPaint : ElementPaint.Type //\\ { type : Text }
      | ElementMark : ElementMark.Type //\\ { type : Text }
      | ActionTriggerer : { type : Text, id : Text, triggerParam : List Text }
      | DownloadFile : { type : Text, url : Text }
      | UploadFile : { type : Text, input : Text, url : Text }
      >

let Requirement =
      { Type =
          { type : Text
          , retries : Optional Natural
          , stepTreatment : Optional Text
          , restartCount : Optional Natural
          , fallback : Optional Text
          }
      , default =
          { retries = None Natural
          , stepTreatment = None Text
          , restartCount = None Natural
          , fallback = None Text
          }
      }

let Position =
      { Type = { x : Optional Natural, y : Optional Natural }
      , default = { x = None Natural, y = None Natural }
      }

let Condition = { className : Text }

let Action =
      { Type =
          { kind : ActionKind
          , timeout : Optional Natural
          , waitCondition : List Text
          , landmarks : Optional Landmarks
          , requirement : Optional Requirement.Type
          , onEach : Optional Natural
          , limitRequirement : Bool
          , positionBefore : Optional Position.Type
          , positionAfter : Optional Position.Type
          , condition : Optional Condition
          }
      , default =
          { timeout = None Natural
          , waitCondition = [] : List Text
          , landmarks = None Landmarks
          , requirement = None Requirement.Type
          , onEach = None Natural
          , limitRequirement = False
          , positionBefore = None Position.Type
          , positionAfter = None Position.Type
          , condition = None Condition
          }
      }

let NestedStep =
      { Type =
          { name : Text
          , elements : List Element.Type
          , actions : List Action.Type
          }
      , default =
          { elements = [] : List Element.Type
          , actions = [] : List Action.Type
          }
      }

let Step =
      { Type =
          { name : Text
          , elements : List Element.Type
          , actions : List Action.Type
          , subSteps : List NestedStep.Type
          }
      , default =
          { elements = [] : List Element.Type
          , actions = [] : List Action.Type
          , subSteps = [] : List NestedStep.Type
          }
      }

let Nndl =
      { Type =
          { entryStep : Optional Text
          , asynchronousSteps : List Text
          , steps : List Step.Type
          }
      , default =
          { entryStep = None Text
          , asynchronousSteps = [] : List Text
          , steps = [] : List Step.Type
          }
      }

let elementLandmark =
      \(element : Text) ->
        Landmark.Element
          { element, timeout = None Natural, waitCondition = [] : List Text }

let cooldown =
      \(millis : Natural) -> Landmark.Cooldown { millis }

let all =
      \(conditions : List Landmark) -> Some (Landmarks.All conditions)

let fork =
      \(element : Text) ->
      \(step : Text) ->
        Fork::{ element, fork = step }

let oneOf =
      \(forks : List Fork.Type) -> Some (Landmarks.OneOf forks)

let goto =
      \(url : Text) -> ActionKind.Goto { type = "gotoUrl", url }

let storeCookies =
      \(storer : Text) ->
      \(storerParams : List Text) ->
        ActionKind.StoreCookies { type = "storeCookies", storer, storerParams }

let loadCookies =
      \(retriever : Text) ->
      \(retrieverParams : List Text) ->
        ActionKind.LoadCookies
          { type = "loadCookies", retriever, retrieverParams }

let fillInput =
      \(element : Text) ->
      \(value : Text) ->
        ActionKind.FillInput { type = "fillInput", element, value }

let clearInput =
      \(element : Text) -> ActionKind.ClearInput { type = "clearInput", element }

let elementClick =
      \(element : Text) ->
        ActionKind.ElementClick { type = "elementClick", element }

let hover =
      \(element : Text) -> ActionKind.Hover { type = "hover", element }

let sendKey =
      \(body : SendKey.Type) ->
        ActionKind.SendKey (body /\ { type = "sendKey" })

let loop =
      \(body : Loop.Type) -> ActionKind.Loop (body /\ { type = "loop" })

let extract =
      \(body : Extract.Type) ->
        ActionKind.Extract (body /\ { type = "extract" })

let elementPaint =
      \(body : ElementPaint.Type) ->
        ActionKind.ElementPaint (body /\ { type = "elementPaint" })

let elementMark =
      \(body : ElementMark.Type) ->
        ActionKind.ElementMark (body /\ { type = "elementMark" })

let actionTriggerer =
      \(id : Text) ->
      \(triggerParam : List Text) ->
        ActionKind.ActionTriggerer
          { type = "actionTriggerer", id, triggerParam }

let downloadFile =
      \(url : Text) -> ActionKind.DownloadFile { type = "downloadFile", url }

let uploadFile =
      \(input : Text) ->
      \(url : Text) ->
        ActionKind.UploadFile { type = "uploadFile", input, url }

let pageElement =
      \(element : Text) -> IterationScope.PageElement { element }

let values =
      \(values : List Text) -> IterationScope.Values { values }

in  { var
    , Element
    , Landmark
    , Fork
    , Landmarks
    , IterationScope
    , Loop
    , SendKey
    , Extract
    , ElementPaint
    , ElementMark
    , ActionKind
    , Requirement
    , Position
    , Condition
    , Action
    , NestedStep
    , Step
    , Nndl
    , elementLandmark
    , cooldown
    , all
    , fork
    , oneOf
    , goto
    , storeCookies
    , loadCookies
    , fillInput
    , clearInput
    , elementClick
    , hover
    , sendKey
    , loop
    , extract
    , elementPaint
    , elementMark
    , actionTriggerer
    , downloadFile
    , uploadFile
    , pageElement
    , values
    }
