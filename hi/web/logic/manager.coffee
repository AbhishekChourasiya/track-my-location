mongoose = require('mongoose')
moment = require('moment')
db_model = require("../logic/model")
__ = require("underscore")


exports.save_or_update_fb_user = (params, callback)->
	console.log "METHOD - Manager save_or_update_fb_user"
	console.log "save or update user", params
	db_model.User.findOne({"fb_id":params.fb_id}).exec (err, user)->
		if user
			console.log "fb_login:", "update user"
			user.gender = params.gender
			user.name = params.name      
			user.save (err) ->
				console.log err
				callback err, user
		else
			# new user
			user = new db_model.User
				gender: params.gender
				name: params.name
				fb_id: params.fb_id
			user.save (err) ->
				console.log err
				callback err, user