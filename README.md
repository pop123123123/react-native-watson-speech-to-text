
# react-native-watson-speech-to-text

Based on [react-native-watson](https://github.com/pwcremin/react-native-watson).
Adapted to recent watson SDKs.

This project is not actively maintained. Feel free to contribute.

## Getting started

`$ npm install react-native-watson-speech-to-text --save`

### Mostly automatic installation

`$ react-native link react-native-watson-speech-to-text`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativeWatsonSpeechToTextPackage;` to the imports at the top of the file
  - Add `new RNReactNativeWatsonSpeechToTextPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-watson-speech-to-text'
  	project(':react-native-watson-speech-to-text').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-watson-speech-to-text/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-watson-speech-to-text')
  	```

## Usage
```javascript
import { SpeechToText } from 'react-native-watson-speech-to-text';

SpeechToText.initialize(
	"WATSON_IAM_KEY",
	"https://stream-fra.watsonplatform.net/speech-to-text/api",// endpoint
	"fr-FR_BroadbandModel");// model

function callback (error, {text, confidence}) {
	console.log(text, confidence);
}

SpeechToText.startStreaming(callback);

// ...

SpeechToText.stopStreaming();
```
  
