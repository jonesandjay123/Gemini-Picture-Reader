# Gemini Picture Recognition

This project is an Android application built with Jetpack Compose that demonstrates a picture recognition feature with language support and Text-to-Speech (TTS) functionality. The app allows users to select an image from their local storage, perform AI recognition (to be implemented), and hear a synthesized voice description based on the selected language.

## Features

- **Image Selection**: Users can select an image from their device's gallery.
- **Language Selection**: Users can switch between English and Chinese languages. The app's UI and TTS will adapt based on the selected language.
- **Text-to-Speech (TTS)**: The app uses TTS to read out a sample sentence in the selected language. The TTS button will toggle between "Play" and "Stop" states during playback, automatically reverting to "Play" when the playback finishes.
- **AI Recognition Placeholder**: A button for triggering AI recognition functionality, which is planned for future implementation.

## Screenshots

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android device or emulator with API level 21 or above

### Installation

1. Clone this repository:

    ```bash
    bash
    複製程式碼
    git clone https://github.com/yourusername/gemini-picture-recognition.git
    
    ```

2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device.

### Usage

1. **Language Selection**: Use the language dropdown menu on the top right to select either English or Chinese.
2. **Image Selection**: Tap the image selection button to choose a picture from your gallery.
3. **AI Recognition**: After selecting an image, press the "AI Recognition" button (currently a placeholder) for future AI-based image recognition.
4. **Text-to-Speech**: After selecting an image, press the "Play" button to hear the sample text read aloud in the selected language. Press "Stop" to end the playback early.

### Code Overview

- **`PictureRecognizeScreen`**: Main screen with image selection, language selection, TTS playback, and AI recognition functionality.
- **`Text-to-Speech Integration`**: Uses Android's `TextToSpeech` API to handle speech synthesis, with language support based on user selection.
- **`StringResources`**: Helper class for managing language-based string resources.

### Future Improvements

- **AI Recognition**: Implement AI-powered image recognition using Gemini API or other relevant AI models.
- **Enhanced TTS**: Expand TTS functionality to handle dynamic text based on image recognition results.
- **UI Enhancements**: Improve UI and add animations for a smoother user experience.

##