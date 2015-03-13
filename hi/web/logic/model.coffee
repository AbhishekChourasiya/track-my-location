mongoose = require('mongoose')


UserSchema = new mongoose.Schema
	name: {type: String, trim: true}
	fb_id: {type: String, unique: true, sparse: true}
	gender: {type: String, trim: true, 'enum': ["male", "female"]}
	image_url: {type: String, trim: true}
	device_id: { type: String }
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
	if this.name
		this.title = this.name
	else
		this.title = this.fb_id
	next()

UserSchema.set('toJSON', { getters: true, virtuals: true })
UserSchema.set('toObject', { getters: true, virtuals: true })
UserSchema.index({ 'track.loc': "2d" })
exports.User = mongoose.model 'User', UserSchema
