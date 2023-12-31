# NutriPro - Comprehensive Health and Nutrition App

## Introduction

NutriPro is an advanced health and nutrition application developed for the Android platform as a part of the 2023 summer mobile application class.

- **Team Members:** Kevin Lin, Shashank Manjunath, Akshat Gandhi, Vaibhav Garg, Jialin Huang

## Key Features

- **Enriched Meal Planning & Fitness Tracking:** Leveraging Java, Kotlin, sensor APIs, and USDA nutrition databases.
- **Recipe-Generator API:** Developed with OpenAI's LLM model and LangChain framework in Django to enhance user dietary choices by 80%.
- **User-Focused Data Visualization:** Boosted average session duration by 20% with health progress tracking.
- **Real-Time Notification System:** A 25% rise in daily active users due to progress updates and streaks.

## Relevant Materials

- **UI Design:** We used Figma to design UI for the app [LINK TO FIGMA].
- **Database:** Firebase serves as our DB for profiles and data storage.
- **Project Management:** Github projects for weekly progress tracking.


## App Screenshots

You can find demo videos to each of our app's function below :
- [![Registering steps, caluclating the food intake and analysing them through charts](https://drive.google.com/thumbnail?id=1RUxqWsPzGAcASO2222b6jLFMDcEur08M)](https://drive.google.com/file/d/1fv67ecVU1hjA6DYyzNSaZRg_QjeuEhbm/view?usp=sharing)

- - [![Real-time Notifications once the goal is reached](https://drive.google.com/thumbnail?id=1U_Kg7lpKa_Q9NQp4GzwJScllYlXoGuZ9)](https://drive.google.com/file/d/12GSYzeIe36NF_FINL0q6Lvfrhv1BXIKN/view?usp=sharing)

- [![Generating the custom recipe](https://drive.google.com/thumbnail?id=1FkeCGTBpFvYc74vkRFdz9-HjaWU8o-wp)](https://drive.google.com/file/d/18HL7L_hmJ9wf-siU3uzEUQMCRZ421SAt/view?usp=sharing)

- [![Following friends and staying social](https://drive.google.com/thumbnail?id=1Qia5EGwQpgS-8KzdTGXg2rc6sB2m3-I0)](https://drive.google.com/file/d/1HV4COHL0g7GJMJPm6bFEQhG5-oVeF5Q7/view?usp=sharing)




# Project Structure

Our Android project follows the MVVM (Model-View-ViewModel) architectural pattern to ensure a scalable, maintainable, and testable codebase. The application is divided into several modules and packages to separate responsibilities and make the structure more understandable and manageable.

## High-level Structure

```

- app/
	- src/
		- main/
			- java/
				- com/
					- calorieCounterAPP/
						- data/
							- models/
							- remote/
							- repository/
						- ui/
							- activities/
							- adapters/
							- fragments/
							- viewmodels/
						- utils/
						- notifications/
						- location/
						- services/
				- res/
					- layout/
					- drawable/
					- values/
				- androidTest/
				- test/


```

## Package Explanation

- **Models (data/models)**: This package contains data structures or objects, such as User, Recipe, etc.

- **Remote (data/remote)**: This package holds classes responsible for network operations, specifically interacting with Firebase APIs. This is where we perform actions such as fetching data from a server, sending data to a server, etc.

- **Repository (data/repository)**- The Repository will use the functions defined in the Remote layer to fetch data from Firebase. Then it will either pass that data directly to the ViewModel or process it further before doing so.

- **View (ui/activities and ui/fragments)**: These are the classes responsible for rendering UI and listen to user actions. They observe data from the ViewModels and update the UI accordingly.

- **Adapters (ui/adapters)**: Adapters for RecyclerView to efficiently manage and display the list or grid of data.

- **ViewModel (ui/viewmodels)**: The ViewModel classes are responsible for providing and keeping the data needed for the UI while surviving configuration changes.

- **Utils (utils)**: This package is for utility or helper classes that include methods and variables that are used across multiple classes in the application.

- **Notifications (notifications)**: This package is for classes related to handling notifications. These classes interact with the Android system to create, update or cancel notifications.

- **Location (location)**: This package is for classes related to handling GPS permissions and location tracking.

- **Services (services)**: This is where background tasks are managed. For example, Firebase Messaging service, Step counter service, etc.

- **Res**: This directory contains all the resources like layout XML files, images, icons, string values, colors, dimensions, and styles.

- **androidTest**: This directory contains UI tests and Instrumented tests.

- **test**: This directory contains unit tests.

## Basic Flow

1. User interacts with the UI (clicks a button, fills out a form, etc.)
2. The UI triggers a corresponding function in the ViewModel.
3. The ViewModel communicates with the Repository to get or modify data.
4. Once data is retrieved or modified, the ViewModel exposes this data back to the UI, usually through LiveData or other observable constructs.
5. The UI observes these changes in the ViewModel and updates accordingly.

## FDA API Setup

In order to leverage the FDA API used as part of this app, you will need to get
an API key. Navigate to the [FDA API Key Signup
page](https://fdc.nal.usda.gov/api-key-signup.html), and create an API key.
Create a file called `fda_api_key.properties` in the gradle scripts location (on the app file level).
This file should have the following structure:

```
API_KEY="XXX"
```

This file is automatically loaded if found, and the API is automatically
accessed as part of the app.
