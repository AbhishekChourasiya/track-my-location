mongoose = require('mongoose')
moment = require('moment')
db_model = require("../logic/model")
__ = require("underscore")


exports.user_push_track = (req, res)->
	if req.body.track&&req.body.device_id
		db_model.User.findOne({device_id: req.body.device_id}).exec (err, user)->
			if user
				for track in req.body.track
					if track.lat&&track.lon&&track.time
						the_time = new Date(parseInt(track.time))
						is_repeat = __.find(user.track, (c_res)->
											c_res.time.toString() == the_time.toString()
								)
						if !is_repeat&&Math.abs(track.lat)<90 && Math.abs(track.lon)<180 && parseInt(track.time)>0
							user.track.push {lon: track.lon, lat: track.lat, time: track.time}
							user.save (err) ->
								console.log err
				res.json
					status: 200
					message: "Data recorded!"
			else
				user = new db_model.User
					device_id: req.body.device_id
				for track in req.body.track
					if track.lat&&track.lon&&track.time
						the_time = new Date(parseInt(track.time))
						is_repeat = __.find(user.track, (c_res)->
											c_res.time.toString() == the_time.toString()
								)
						if !is_repeat&&Math.abs(track.lat)<90 && Math.abs(track.lon)<180 && parseInt(track.time)>0
							user.track.push {lon: track.lon, lat: track.lat, time: track.time}
							user.save (err) ->
								console.log err
				res.json
					status: 200
					message: "Data recorded!"
	else
		res.json
			status: 444
			message: "Missing data!"


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
