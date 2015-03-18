mongoose = require('mongoose')
moment = require('moment')
db_model = require("../logic/model")
__ = require("underscore")
async = require("async")
send_push = (fb_id, friends)->
  #send push
  Parse = require("parse").Parse
  Parse.initialize config.parse_app_id, config.parse_js_key
  # send message to Parse (android)
  queryAndroid = new Parse.Query(Parse.Installation)
  queryAndroid.equalTo "channels", "hi_" + fb_id
  queryAndroid.equalTo "deviceType", "android"
  Parse.Push.send
    where: queryAndroid 
    data:
      friends: friends
  ,
    success: ->
      console.log "push sent"
      
    error: (error) ->
      console.log "push error: "
      console.log error

    queryIOS = new Parse.Query(Parse.Installation)
    queryIOS.equalTo "channels", "hi_" + fb_id
    queryIOS.equalTo "deviceType", "ios"      
    Parse.Push.send
      where: queryIOS 
      data:
        friends: friends

exports.send_push1 = (user)->
  #send push
  Parse = require("parse").Parse
  Parse.initialize config.parse_app_id, config.parse_js_key
  # send message to Parse (android)
  #hi_141352234229247
  queryAndroid = new Parse.Query(Parse.Installation)
  queryAndroid.equalTo "channels", "hi_1413522342292477"
  queryAndroid.equalTo "deviceType", "android"
  db_model.User.findOne({fb_id: 1413522342292477}).exec (err, user)->
    console.log user
    Parse.Push.send
      where: queryAndroid 
      data:
        msg:  " is near you."
        header: user
    ,
      success: ->
        console.log "push sent"
        
      error: (error) ->
        console.log "push error: "
        console.log error

    queryIOS = new Parse.Query(Parse.Installation)
    queryIOS.equalTo "channels", "hi_" + fb_id
    queryIOS.equalTo "deviceType", "ios"      
    Parse.Push.send
      where: queryIOS 
      data:
        friends: friends



exports.user_push_track = (req, res)->
  if req.body.track[0]&&req.body.fb_id
    db_model.User.findOne({fb_id: req.body.fb_id}).exec (err, user)->
      if user
        add_track req, user, (result)->
          res.json
            status: 200
            message: "Data recorded!"
            result: result
      else
        res.json
          status: 444
          message: "User not exist!"
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
  db_model.User.findOne({fb_id: req.params.objectId}).exec (err, dev)->
    device = dev.toObject()
    for d in device.track
      d.time = moment(d.time).format('LLLL')
    model = {}
    model.device = device
    res.render "device_details", model

exports.delete_device = (req, res)->
  db_model.User.remove({fb_id: req.params.objectId}).exec (err)->
    res.redirect '/'

exports.get_coordinates = (req, res)->
  db_model.User.findOne({fb_id: req.params.objectId}).exec (err, dev)->
    res.json
      dev:dev.track
add_track = (req, user, callback)->
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
    console.log new Date().getTime()
    time = new Date(new Date().getTime() - 3600000)
    query = [{ '$match': {'fb_id' : user.fb_id}},{ $unwind: '$track' },{ $match: {'track.time': {'$gte': time }}},{ $sort: {'track.time': -1}}]
    db_model.User.aggregate query, {}, (err, result)->
      if result[0]
        geoNear = 
          near: [parseFloat(result[0].track.loc.lon), parseFloat(result[0].track.loc.lat)],
          distanceField: "distance",
          spherical: true,
          distanceMultiplier: 6371 
        geoNear.maxDistance = 1/6371 
        match = {'track': { '$elemMatch': {'time': {'$gte': time } }} }
        query =  [{'$geoNear':geoNear}, {'$match':match}]
        db_model.User.aggregate query, {}, (err, friends)->
          if friends[0]
            me_array = []
            me_object = {}
            me_object.name = user.name
            me_object.image_url = user.image_url
            me_object.fb_id = user.fb_id
            me_object.gender = user.gender
            me_array.push me_object
            friends_array = []
            friend_object = {}
            async.each friends, (friend, next)->
              if friend.fb_id != user.fb_id
                check_hi_history user._id, friend._id, (count)->
                  if count == 0
                    console.log "+"
                    friend_object.name = friend.name
                    friend_object.image_url = friend.image_url
                    friend_object.fb_id = friend.fb_id
                    friend_object.gender = friend.gender
                    friends_array.push friend_object
                    friend_object = {}
                    push_hi_history user._id, friend._id
                    send_push friend.fb_id, me_array
                    next()  
                  else
                    next()   
              else
                next()
            , (err)->
              if friends_array[0]
                send_push user.fb_id, friends_array
              callback friends_array
          else
            callback "no friends"
      else
        callback "outdated data"


push_hi_history = (user1, user2, callback)->
  hi = new db_model.Hi
    user1: user1
    user2: user2
  hi.save (err) ->
    console.log err

check_hi_history = (user1, user2, callback)->
  currentData = new Date(new Date().getTime() - 86400000)
  db_model.Hi.count({'$or':[{'$and':[{'user1':user1},{'user2': user2},{'time':{'$gte': currentData}}]},{'$and':[{'user2':user1},{'user1': user2},{'time':{'$gte': currentData}}]}]}).exec (err, count)->
    console.log count
    callback count

