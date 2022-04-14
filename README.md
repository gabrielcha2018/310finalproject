### How to run code
1. Pull repository though VS Code
2. Download models file from https://1drv.ms/u/s!AidBCfmtc0EDhtZiCroSVeDgESCegg?e=hZWT4m and put in Jars folder
3. Unzip dict.zip and replace folder in Dictionaries (For some reason the file gets corrupted when pulled through github)
4. Run Launcher.java


### Documentation
#### Class Structure
* Chatbot
    * Main method for the bot program. Once instance is created, this class handles the processing of the user input
* GUI
    * Creates a GUI and passes user input into the Chatbot class for processing
* Response
    * The "response" object
    * Stores information of a certain response that the chatbot could provide to the user
* Flow
    * The "flow" object
    * Stores information on a certain series of messages that must be entered in a specific order
    * Takes care of situations where multiple lines of user input is needed to be stored or processed at the same time
* Dict
    * Handles correcting spelling of inputs
* SentimentAnalysis
    * Handles analysing user input for sentiment when required
* Testing
    * Unit tests
* Translator
   * using the translator api auto translate the any languages in english
* Staticmaps
   * showing the static image for specific location 

 
#### Assignment 4 new features




#### Assignment 3 new features
* Created GUI for better user experience
* Added extra topic to agent (Now can answer questions about the fictional company's games as well as troubleshoot issues)
* Added ability for bot to propose to the user potential answer if the bot doesn't find a match in the current branch (a.k.a "Did you mean this?") - Reduces number of times that users are required to reset program
    * YOU: I want to return the headet
    * AGENT: I understand that you seem to be having some problems with refunds of your purchased headset. Does the "status" section of your refund applciation on your account says "approved"?
    * YOU: Actually nevermind. How do I factory reset my headset?
    * AGENT: I'm sorry, I don't recognise your input for the current branch. Did you want information on "Factory Reset" instead?
* Implemented general solution "Flows" to allow for validation of user input and for the input to be processed as a batch (E.g. For leaving a review, for getting user account information, for getting user shipment information, getting user email/phone number, etc.)
* Implemented sentiment analysis for categorizing user input when required
* Implemented ability for user to leave reviews which are automatically sorted by sentiment (Discussed with professor Du for marks)
    * YOU: Can I leave a review?
    * AGENT: Thank you for your interest in providing us feedback! Please provide the following information to leave a review
    * AGENT: Please enter your name:
    * YOU: Howard Stark
    * AGENT: Please enter your email:
    * YOU: hoho@starkindustries.ca
    * AGENT: Thank you for your contact information, please enter your feedback now :)
    * YOU: I think that the products you made are really good and I really like your games. Keep up the good work!
    * AGENT: Thank you for providing that information!
    * AGENT: Review saved successfully. Thank you for your feedback!
* Implemented spelling correction for user inputs - Greatly improves recognition of keywords for Chatbot
    * YOU: I go some issues with teh sofater
    * AGENT: If you're having some trouble with out PC software, please let me know if you're experiencing trouble while installing it or if you're getting performance issues.
* Implemented synonym recognition - helps recognition by bot of keywords during search
* Implemented POS tagging - Reduces chance of spelling correction producing extra/invalid output by limiting it to certain word types.
* Implemented multithreading to prevent GUI lag while Chatbot processes input.

####
* List of 5 features that can be extracted as an API
    * Flows can be extracted as a way for others to validate batches of data quickly then send the data package for processing elsewhere
    * The "Chatbot" object is a general method that gets all of it's information from a txt file. This can be extracted and then used by others simply by changing the txt file so that it can handle answering questions in any topic
    * The GUI we made is also generalized and can take input from any program by simply changing a few parameters
    * Response objects can be extracted and used as object to store data for keyword bots
    * Modified spelling correction can be extracted as API for various functions
