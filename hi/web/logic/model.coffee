mongoose = require('mongoose')


UserSchema = new mongoose.Schema
	device_id: { type: String }
	device_name: { type: String }
	title: { type: String }
	track: [
		{
			loc:
				lon: Number
				lat: Number
			time: Date
		}
	]


UserSchema.pre 'save', (next, done) ->
	if this.device_name
		this.title = this.device_name
	else
		this.title = this.device_id
	next()

UserSchema.set('toJSON', { getters: true, virtuals: true })
UserSchema.set('toObject', { getters: true, virtuals: true })
UserSchema.index({ 'track.loc': "2d" })
exports.User = mongoose.model 'User', UserSchema
