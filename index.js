import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

let {
    RNSpeechToText,
} = NativeModules

module.exports = {
    SpeechToText: {
        speechToTextEmitter: new NativeEventEmitter(RNSpeechToText),

        initialize: function (apiKey, url, model) {
            RNSpeechToText.initialize(apiKey, url, model);
        },

        startStreaming(callback) {
            this.subscription = this.speechToTextEmitter.addListener(
                'StreamingText',
                (text) => callback(null, text)
            );

            RNSpeechToText.startStreaming(callback)
        },

        stopStreaming() {
            this.subscription.remove()

            RNSpeechToText.stopStreaming()
        }
    },
}
