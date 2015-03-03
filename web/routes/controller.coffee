mongoose = require('mongoose')
moment = require('moment')
db_model = require("../logic/model")
__ = require("underscore")
mongoose.connect(config.db.connection)

exports.user_push_track = (req, res)->
	if Math.abs(req.body.lat)<90 && Math.abs(req.body.lon)<180
		db_model.User.findOne({device_id: req.body.device_id}).exec (err, user)->
			if user
				is_repeat = __.find(user.track, (c_res)->
					c_res.time_seconds == parseInt(req.body.time)
				)
				if is_repeat
					res.json 
						status: "Error. Already exist"
				else
					user.track.push {lon: req.body.lon, lat: req.body.lat, time: req.body.time, time_seconds: req.body.time}
					user.save (err) ->
						console.log err  
						res.json
							status: 200
			else
				user = new db_model.User
					device_id: req.body.device_id
				user.track.push {lon: req.body.lon, lat: req.body.lat, time: req.body.time, time_seconds: req.body.time}
				user.save (err) ->
					console.log err  
					res.json
						status: 200
	else 
		res.json 
			status: "Error. Incorrect latitude or longitude."

exports.devices = (req, res)->
	db_model.User.find().exec (err, dev)->
		model = {}
		model.devices = dev
		res.render "devices", model

exports.device_id = (req, res)->
	console.log req.params.objectId
	db_model.User.findOne({device_id: req.params.objectId}).exec (err, dev)->
		device = dev.toObject()
		for d in device.track
			d.time = moment(d.time).format('LLLL')
		model = {}
		model.device = device
		res.render "device_details", model
		
exports.get_coordinates = (req, res)->
	db_model.User.findOne({device_id: req.params.objectId}).exec (err, dev)->
		res.json 
			dev:dev.track