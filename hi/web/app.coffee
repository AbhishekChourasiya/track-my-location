express = require('express')
path = require('path')
favicon = require('serve-favicon')
logger = require('morgan')
cookieParser = require('cookie-parser')
bodyParser = require('body-parser')
mongoose = require('mongoose')
controller = require('./routes/controller')
services = require('./routes/services')
http = require('http');
app = express()
# view engine setup

global.config = require('yaml-config').readConfig('./config/config.yaml', app.settings.env)
mongoose.connect(config.db.connection)
db_model = require("./logic/model")
app.set 'views', path.join(__dirname, 'views')
app.set 'view engine', 'jade'
# uncomment after placing your favicon in /public
#app.use(favicon(__dirname + '/public/favicon.ico'));
app.use logger('dev')
app.set('port', process.env.PORT || 4000);
app.use bodyParser.json()
app.use bodyParser.urlencoded(extended: false)
app.use cookieParser()
app.use express.static(path.join(__dirname, 'public'))

#--------------------------------------------------------------------------------
# Mobile API
#--------------------------------------------------------------------------------
app.post '/track/add', controller.user_push_track
app.post '/_s/sign_in/fb', services.fb_sign_in
app.get '/test', controller.send_push1
#--------------------------------------------------------------------------------
# Web
#--------------------------------------------------------------------------------

app.get '/', controller.devices
app.get '/track/:objectId', controller.device_id
app.get '/track/info/:objectId', controller.get_coordinates
app.get '/track/delete/:objectId', controller.delete_device
# catch 404 and forward to error handler
app.use (req, res, next) ->
  err = new Error('Not Found')
  err.status = 404
  next err
  return
# error handlers
# development error handler
# will print stacktrace
if app.get('env') == 'development'
  app.use (err, req, res, next) ->
    res.status err.status or 500
    res.render 'error',
      message: err.message
      error: err
    return
# production error handler
# no stacktraces leaked to user


http.createServer(app).listen app.get('port'), ()->
  console.log 'Express server listening on port ' + app.get('port')
