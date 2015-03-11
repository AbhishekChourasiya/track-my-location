mongoose = require('mongoose')


UserSchema = new mongoose.Schema
	device_id: { type: String }
	track: [
		{lon: Number, lat: Number, time: Date}
	]


UserSchema.pre 'save', (next, done) ->
	next()

UserSchema.set('toJSON', { getters: true, virtuals: true })
UserSchema.set('toObject', { getters: true, virtuals: true })
exports.User = mongoose.model 'User', UserSchema
