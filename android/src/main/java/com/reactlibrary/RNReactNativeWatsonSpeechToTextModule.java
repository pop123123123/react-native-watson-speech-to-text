package com.reactlibrary;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.Arguments;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ibm.cloud.sdk.core.security.AuthenticatorConfig;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;

import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.InputStream;


public class RNReactNativeWatsonSpeechToTextModule extends ReactContextBaseJavaModule {

  private ReactApplicationContext reactContext;
  private SpeechToText service;
  private MicrophoneInputStream capture;
  private Callback errorCallback;
  private String model = "en-US_BroadbandModel";
  private String url = "https://stream.watsonplatform.net/speech-to-text/api";

  public RNReactNativeWatsonSpeechToTextModule(ReactApplicationContext reactContext) {
    super(reactContext);

    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNSpeechToText";
  }

  @ReactMethod
  public void initialize(String apiKey, String url, String model) {
    if (model != null)
      this.model = model;
    if (url != null)
      this.url = url;
    IamOptions options = new IamOptions.Builder()
            .apiKey(apiKey)
            .url("https://iam.bluemix.net/identity/token")
            .build();
    service = new SpeechToText(options);
    service.setEndPoint(url);
  }

  @ReactMethod
  public void startStreaming(Callback errorCallback) {

    this.errorCallback = errorCallback;
    capture = new MicrophoneInputStream(true);

    try {
      //service.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate(reactContext, errorCallback));
      service.recognizeUsingWebSocket(getRecognizeOptions(), new MicrophoneRecognizeDelegate(reactContext, errorCallback));
    } catch (Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  @ReactMethod
  public void stopStreaming() {
    try {
      capture.close();
    }
    catch (Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  private RecognizeOptions getRecognizeOptions() {
    return new RecognizeOptions.Builder()
            .contentType(ContentType.OPUS.toString())
            .model(this.model)
            .audio(capture)
            .interimResults(true)
            .inactivityTimeout(1000)
	    .interimResults(true)
	    .inactivityTimeout(40)
	    .inactivityTimeout(120)
            .build();
  }

  private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {

    private ReactApplicationContext reactContext;
    private Callback errorCallback;

    public MicrophoneRecognizeDelegate(ReactApplicationContext reactContext, Callback errorCallback) {
      this.reactContext = reactContext;
      this.errorCallback = errorCallback;
    }


    @Override
    public void onTranscription(SpeechRecognitionResults speechResults) {
      if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
        String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
        //Double confidence = speechResults.getResults().get(0).getAlternatives().get(0).getConfidence();
        double confidence = speechResults.getResults().get(0).isFinalResults() ? 1 : 0;

        WritableMap params = Arguments.createMap();
        params.putString("text", text);
        params.putDouble("confidence", confidence);

        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("StreamingText", text);
      }
    }

    @Override public void onError(Exception e) {
      errorCallback.invoke(e.getMessage());
    }

    @Override public void onDisconnected() {

    }
  }
}
