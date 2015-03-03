express = require('express');
http = require('http');
path = require('path');
fs = require('fs')

app = express();

global.config = require('yaml-config').readConfig('./config/config.yaml', app.settings.env)

db_model = require("./logic/model")

# controllers
controller = require('./routes/controller')
app.use(express.bodyParser());
app.use(app.router)
# all environments
app.set('port', process.env.PORT || 5000);
app.use(express.static(path.join(__dirname, 'public')));
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');

app.post '/track/add', controller.user_push_track

app.get '/', controller.devices
app.get '/track/:objectId', controller.device_id
app.get '/track/info/:objectId', controller.get_coordinates

#app.post '/home/user/:user_id/replay', services.user_push

http.createServer(app).listen app.get('port'), ()->
  console.log 'Express server listening on port ' + app.get('port')
