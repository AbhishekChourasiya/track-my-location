mongoose = require('mongoose')
moment = require('moment')
db_model = require("../logic/model")
__ = require("underscore")


exports.user_push_track = (req, res)->
	if req.body.track[0]&&req.body.device_id
		db_model.User.findOne({device_id: req.body.device_id}).exec (err, user)->
			if user
				add_track req, user, (result)->
					res.json
						status: 200
						message: "Data recorded!"
						result: result
			else
				user = new db_model.User
					device_id: req.body.device_id
				add_track req, user, (result)->
					res.json
						status: 200
						message: "Data recorded!"
						result: result
	else
		res.json
			status: 444
			message: "Missing data!"


exports.devices = (req, res)->
	db_model.User.find().exec (err, dev)->
		model = {}
		model.devices = dev
		model.title = dev.title
		res.render "devices", model

exports.device_id = (req, res)->
	db_model.User.findOne({device_id: req.params.objectId}).exec (err, dev)->
		device = dev.toObject()
		for d in device.track
			d.time = moment(d.time).format('LLLL')
		model = {}
		model.device = device
		res.render "device_details", model

exports.delete_device = (req, res)->
	db_model.User.remove({device_id: req.params.objectId}).exec (err)->
		res.redirect '/'

exports.get_coordinates = (req, res)->
	db_model.User.findOne({device_id: req.params.objectId}).exec (err, dev)->
		res.json
			dev:dev.track
add_track = (req, user, callback)->
	if req.body.device_name
		user.device_name = req.body.device_name
	for track in req.body.track
		if track.loc.lat&&track.loc.lon&&track.time
			is_repeat = __.find(user.track, (c_res)->
								Math.abs(c_res.time.getTime()-track.time)<60000
					)
			if !is_repeat&&Math.abs(track.loc.lat)<90 && Math.abs(track.loc.lon)<180 && parseInt(track.time)>0
				loc = {lon: track.loc.lon, lat: track.loc.lat}
				user.track.push {loc:loc, time: track.time}
				user.save (err) ->
					console.log err
	user.save (err) ->
		console.log err
		find_friends req.body.device_id, (result)->
			callback result

find_friends = (device_id, callback)->
	query = [{ '$match': {'device_id' : device_id}},{ $unwind: '$track' },{ $sort: {'track.time': -1}}]
	db_model.User.aggregate query, {}, (err, result)->
		if result[0]
			geoNear = 
				near: [parseFloat(result[0].track.loc.lon), parseFloat(result[0].track.loc.lat)],
				distanceField: "distance",
				spherical: true,
				distanceMultiplier: 6371 
			time = new Date(new Date().getTime() - 3600000)
			geoNear.maxDistance = 1/6371 
			match = {'track': { '$elemMatch': {'time': {'$gte': time } }} }
			query =  [{'$geoNear':geoNear}, {'$match':match}]
			db_model.User.aggregate query, {}, (err, friends)->
				friends_array=[]
				for friend in friends
					if friend.device_id != device_id
						friends_array.push friend
				callback friends_array
		else
			callback "no friends"
