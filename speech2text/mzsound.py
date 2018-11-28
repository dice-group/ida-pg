import speech_recognition as sr
import webbrowser
while(1):

    r = sr.Recognizer()
    with sr.Microphone() as source:
        print("Speak:")
        audio = r.listen(source)

    try:
        print("You said " + r.recognize_google(audio))
        S = r.recognize_google(audio)
        if S=="Google":
            webbrowser.open('http://google.com')
    except sr.UnknownValueError:
        print("Could not understand audio")
    except sr.RequestError as e:
        print("Could not request results; {0}".format(e))
