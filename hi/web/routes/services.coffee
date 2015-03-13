mongoose = require('mongoose')
moment = require('moment')
db_model = require("../logic/model")
manager = require("../logic/manager")
__ = require("underscore")


exports.fb_sign_in = (req, res)->
  errors = {}
  # validate empty fields
  if __.isEmpty req.body.fb_id?.trim()
    errors["fb_id"] =
      message: "fb_id is empty"
      path: "fb_id"
  if __.isEmpty req.body.gender?.trim()
    errors["gender"] =
      message: "gender is empty"
      path: "gender"
  if __.isEmpty req.body.name?.trim()
    errors["name"] =
      message: "name is empty"
      path: "name"
  if __.isEmpty req.body.image_url?.trim()
    errors["image_url"] =
      message: "image_url is empty"
      path: "image_url"

  if not __.isEmpty errors
    res.json
      status: "error"
      errors: errors
  else
    console.log req.body
    manager.save_or_update_fb_user req.body, (err, user)->
      if err
        console.log "fb error:", err
        res.json
          status: 444
          errors: err.errors
      else
        console.log "return user", user
        res.json
          status: 200
          result:
            user: user